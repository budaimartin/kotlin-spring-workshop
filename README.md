# Readme

Based on the [Spring WebFlux Workshop](https://bclozel.github.io/webflux-workshop/). The tasks of this workshop can be found [here](tasks.md).

## Progress

### 2019.05.24.

#### [Create Quotes application](tasks.md#create-quotes-application)

* We used the Spring Initializr for creating the project instead of the archetypes in IntelliJ IDEA.
* We chose Spring Boot version 2.1.5, Kotlin language, and for dependencies, only WebFlux.
* Also, we increased Java version to 11 and Kotlin to 1.3.31 so we will be able to use new features.

*DemostockQuotesApplicatioin.kt*

```kotlin
@SpringBootApplication
class DemostockQuotesApplication

fun main(args: Array<String>) {
	runApplication<DemostockQuotesApplication>(*args)
}
```

* There is a `runApplication` extension function isntead of `SpringBootApplication::run`
* The spread operator (`*`) is used to convert `Array<String>` to varargs
 
#### [Create RestController](tasks.md#create-restcontroller)

* Just like we would do it in Java.
* We also tried if `@RequestBody input: String?` makes the body optional, but it still resulted in 400 BAD_REQUEST.

*HelloResource*

```kotlin
@RestController
class HelloResource {
    @GetMapping("/hello-world")
    fun hello() = "hello"
}
```

*EchoResource*

```kotlin
@RestController
class EchoResource {
    @PostMapping("/echo")
    fun echo(@RequestBody input: String) = input
}
```

#### [Quote domain class](tasks.md#quote-domain-class)

* We couldn't finish this task yet. So far we have this:
 
```kotlin
data class Quote(val ticker: String, val price: BigDecimal) {
    lateinit var instant: Instant
    companion object {
        private val MATH_CONTEXT = MathContext(2)
    }
}
```

* Domain class is defined as `data class` with its immutable fields.
* Mutable `instant` field is defined as `lateinit var`.
* Constant `MATH_CONTEXT` is defined in a `companion object`.
* We have some concerns about the mutable `instant` field, so it is possible that we will change this so it is set automatically to `Instant.now()`.
  * This way, `isntant` is not a property, so it is ignored by default `toString`, `equals` and `hashCode`.

```kotlin
val quote1 = Quote("hello", BigDecimal.ONE)
val quote2 = Quote("hello", BigDecimal.ONE)
quote1.instant = Instant.MIN
quote2.instant = Instant.MAX
println(quote1 == quote2)	// prints true
println(quote1)			// prints Quote(ticker=hello, price=1)

```

### 2019.05.31.

#### [Quote domain class (continued)](tasks.md#quote-domain-class)

* Final version looks like this:

```kotlin
data class Quote(val ticker: String, val price: BigDecimal, val instant: Instant = Instant.now()) {

    companion object {
        private val MATH_CONTEXT = MathContext(2)
    }

    constructor(ticker: String, price: Double) : this(ticker, BigDecimal(price, MATH_CONTEXT))
}
```

* `instant` is moved up to the primary constructor with default value
* Custom constructor calls the primary one, without passing an `Instant` to it

#### [Quote generator class](https://github.com/budaimartin/kotlin-spring-workshop/blob/master/tasks.md#quote-generator-class)

* No need for `new` keyword when instantiating a `Quote` !
* Instead of the mutable `ArrayList<Quote>` and filling it up in the constructor, we use the `listOf` factory method to create an immutable list:

```kotlin
private val prices = listOf(
    Quote("CTXS", 82.26),
    Quote("DELL", 32.26),
    Quote("GOOG", 62.26),
    // ...
)
```

* Instant is set in `Quotes`, so we don't need to set it
* Implicitly generated getters can be called by the name of the property
* Since the last parameter of `map` is a lambda, there's no need for brackets

```kotlin
fun generateQuotes() = prices.map {
    val newPrice = it.price.multiply(BigDecimal(0.05 * this.random.nextDouble()), this.mathContext)
    Quote(it.ticker, newPrice, it.instant)
}
```

* First naive solution for `ferchQuotes` used the `sequenceOf` factory method. It created a finite sequence without delays:

```kotlin
fun fetchQuotes() = sequenceOf(*(generateQuotes().toTypedArray()))
```

* Then we used the `sequence` builder function and an infinite loop to create an infinite sequence (still without delays, though):

```kotlin
fun fetchQuotes() = sequence {
    while (true) {
        yieldAll(generateQuotes())
    }
}
```

* Next time we will try [`generateSequence`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/generate-sequence.html) to avoid the ~good~ old `while(true)`, and find out something for delaying.

### 2019.06.14.

#### [Quote generator class (continued)](https://github.com/budaimartin/kotlin-spring-workshop/blob/master/tasks.md#quote-generator-class)

An idea came up that we should try [Flows instead of Sequence](https://medium.com/@elizarov/reactive-streams-and-kotlin-flows-bfd12772cda4), because we can use the `delay()` suspending function in it.

```kotlin
fun fetchQuotes() = flow {
    while (true) {
        generateQuotes().forEach {
            emit(it)
        }
        delay(1000)
    }
}
```

 * Unfortunately, there's no convenience method to emit collections, so we need `forEach` for that
 * Still no solution to avoid `while(true)`

#### [Expose quotes](https://github.com/budaimartin/kotlin-spring-workshop/blob/master/tasks.md#expose-quotes)

The `QuoteGenerator` class needs to be injected for example with `@Autowired`. But this way ugly `lateinit var` is needed again to let the compiler know that uninitialized variable will be given value by Spring, before using it.

```kotlin
@Autowired
lateinit var quoteGenerator: QuoteGenerator
```

Or we can use constructor injection:

```kotlin
class QuoteResource(val quoteGenerator: QuoteGenerator) { ... }
```

Currently, for the `/quotes/size={size}` endpoint, we have this:

```kotlin
@GetMapping("/quotes", produces = ["application/json"])
fun getQuotes(size: Int = 10) = quoteGenerator.fetchQuotes().take(size)
```

 * Nasty Kotlin doesn't let us set single values in annotations when the type is `Array<>`
 * We give a default value for the size _method_ parameter, however it fails with 500:

```
java.lang.IllegalStateException: Optional int parameter 'size' is present but cannot be translated into a null value due to being declared as a primitive type. Consider declaring it as object wrapper for the corresponding primitive type.
```

 * Also, response is empty JSON, because we don't collect the values of the Flow yet

### 2019.06.26.

#### [Expose quotes (continued)](https://github.com/budaimartin/kotlin-spring-workshop/blob/master/tasks.md#expose-quotes)

We raised the Spring Boot version to 2.2.0.M3 in order to get the experimental Flow support. However, we had to realize that default method parameters are currently not supported in Spring controllers.

Final version looks like this:

```kotlin
@GetMapping("/quotes?size={size}", produces = ["application/json"])
fun getQuotes(@RequestParam(required = false, name = "size", defaultValue = "10") size: Int) =
        quoteGenerator.fetchQuotes().take(size)
```

The streaming endpoint uses the infinite Flow, no need to convert to Flux!

```kotlin
@GetMapping("/quotes", produces = ["application/stream+json"])
fun getQuotesStream() = quoteGenerator.fetchQuotes()
```

#### [Replace RestController with RouterFunction and Handler](https://github.com/budaimartin/kotlin-spring-workshop/blob/master/tasks.md#replace-restcontroller-with-routerfunction-and-handler)

We managed to replace `EchoResource` and `HelloResource` with routers. Currently it looks like this:

```kotlin
@Bean
fun mainRouter() = router {
    POST("/echo") {
        ok().body(it.bodyToMono<String>())
    }
	
    GET("/hello-world") {
        ok().syncBody("Hello")
    }

    GET("/quotes").nest {
        accept(MediaType.APPLICATION_STREAM_JSON) {
            ok().body(qouteGenerator.fetchQuotes().asPublisher())
        }
    }
}
```

 * There is a generic `bodyToMono` extension function for `ServerRequest`
 * We are going to nest two `accept` branches under `GET("/quotes")` to choose between the streaming and batch `/quotes` endpoints
 * `asPublisher()` creates a Publisher from Flow, but when we tried it out, the browser didn't load. ~We assume that it does the transformation _eagerly_, which is not a good idea for potentially infinite data.~ _It turned out that it was caused by the missing `.contentType(MediaType.APPLICATION_STREAM_JSON)` part._

We are going to find out a solution to expose our infinite Flow on our next session.

### 2019.07.05.

#### [Replace RestController with RouterFunction and Handler (continued)](https://github.com/budaimartin/kotlin-spring-workshop/blob/master/tasks.md#replace-restcontroller-with-routerfunction-and-handler)

We added the other branch under the `"/quotes"` part, with the batch request of Quotes, but didn't find a solution for the Flow vs. Flux problem mentioned above.

```kotlin
GET("/quotes").nest {
    accept(MediaType.APPLICATION_JSON) {
        ok().body(qouteGenerator.fetchQuotes()
                .take(Integer.valueOf(it.queryParam("size")
                        .orElse("5")))
                .asPublisher())
    }
    accept(MediaType.APPLICATION_STREAM_JSON) {
        ok().contentType(MediaType.APPLICATION_STREAM_JSON)
		    .body(qouteGenerator.fetchQuotes().asPublisher())
    }
}
```

I have raised an [issue](https://github.com/Kotlin/kotlinx.coroutines/issues/1324) on the Kotlin coroutines repo and they informed me that this is not a Kotlin coroutines but a Spring issue, as it is reproducable with Flux. It turned out that it was caused by the missing `.contentType(MediaType.APPLICATION_STREAM_JSON)` part.

### 2019.07.12.

#### Experiments with Bean definition DSL (bonus task)

We decided to try out Spring's [bean definition DSL](https://docs.spring.io/spring-framework/docs/5.0.0.BUILD-SNAPSHOT/spring-framework-reference/kotlin.html#bean-definition-dsl) instead of good old annotation-based configuration.

_Beanz.kt_

```kotlin
fun beanz() = beans {
    bean<QuoteGenerator>() // <1>
    bean { // <2>
        router {
            POST("/echo") {
                ok().body(it.bodyToMono<String>())
            }
            GET("/hello-world") {
                ok().syncBody("Hello")
            }
            GET("/quotes").nest {
                accept(MediaType.APPLICATION_JSON) {
                    ok().body(ref<QuoteGenerator>().fetchQuotes() // <3>
                        .take(Integer.valueOf(it.queryParam("size")
                            .orElse("5")))
                        .asPublisher())
                }
                accept(MediaType.APPLICATION_STREAM_JSON) {
                    ok().contentType(MediaType.APPLICATION_STREAM_JSON)
                        .body(ref<QuoteGenerator>().fetchQuotes().asPublisher())
                }
            }
        }
    }
}
```

 1. If no further configuration needed, beans can be defined by the generic `bean<>()` function
 2. Otherwise, return type is infered, just like the return type of any other Kotlin lambda
 3. It is possible to reference other beans by type or by name using `ref<>()`

Since Spring Boot does not support this approach out of box, we need to implement and register an `ApplicationContextInitializer<GenericApplicationContext>`.

```kotlin
class QuotesApplicationInitializer : ApplicationContextInitializer<GenericApplicationContext> {
    override fun initialize(context: GenericApplicationContext) = beanz().initialize(context)
}
```

Retistering this listener can be done in `application.properties`:

```
context.initializer.classes=hcom.mobile.workshop.demostockquotes.resource.QuotesApplicationInitializer
```

##### Ambiguous bean problem

An idea came up about ambiguous beans. At first we forgot to remove the `@Controller` annotation from `QuoteGenerator`, and the application context was still able to build up despite of multiple bean definitions (the bean picked up by component scan and the one defined in the `beanz()` function).

It turned out that our solution is error prone: this problem is only revealed when we call the endpoint:

```
No qualifying bean of type 'hcom.mobile.workshop.demostockquotes.generator.QuoteGenerator' available: expected single matching bean but found 2: hcom.mobile.workshop.demostockquotes.generator.QuoteGenerator#0,quoteGenerator
```

When we extract `ref<QuoteGenerator>()` to a local variable, it is resolved eagerly, i.e. during startup time.

```kotlin
bean {
    val generator = ref<QuoteGenerator>()
    router {
        // ...
        accept(MediaType.APPLICATION_STREAM_JSON) {
            ok().contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(generator.fetchQuotes().asPublisher())
        }
    }
}
```

### 2019.07.23.

#### [Testing](https://github.com/budaimartin/kotlin-spring-workshop/blob/master/tasks.md#testing)

We have written some integration test for our application.

```kotlin
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemostockQuotesApplicationTests {
    @Autowired
    lateinit var webTestClient: WebTestClient
    // ...
}
```

 * We used JUnit and Spring Boot test, just like in Java
 * Since we are in a WebFlux environment, we automatically get a fully set-up `WebTestClient`, ready to be autowired

```kotlin
@Test
fun `Test Echo endpoint returns an echo`() {
    webTestClient.post()
       .uri("/echo")
       .syncBody("Echo this")
       .exchange()
       .expectBody<String>()
       .isEqualTo("Echo this")
}
```

 * Kotlin lets us use any string literal between backticks as identifier. This is very useful for nicely formatted test method names!
 * Kotlin is [having a hard time infering types](https://discuss.kotlinlang.org/t/type-interference-issue-with-the-webflux-webtestclient-and-kotlin/3880) when using `WebTestClient`. Fortunately there are extension methods for workaround! You can write `expectBody<String>()` instead of `expectBody(String::class)`.

```kotlin
@Test
fun `Test quotes endpoint should return expected amount of quotes`() {
    webTestClient.get()
        .uri("/quotes?size=2")
        .exchange()
        .expectBodyList<Quote>()
        .hasSize(2)
}
```

 * There is also `expectBodyList<>()`!
 
```kotlin
@Test
fun `Test quotes endpoint should return quotes`() {
    val collectMonoList = webTestClient
            .get()
            .uri("/quotes")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .returnResult<Quote>()
            .responseBody.take(30)
            .collectList()
            .block()

    assertThat(collectMonoList?.size).isEqualTo(30)
    assertThat(collectMonoList).allSatisfy {
        assertThat(it.price).isPositive()
    }
}
```

 * ...and also `returnResult<>()`!
 * We collected the `Flux` to a list first and did some AssertJ assertions on it.
 * Notice how beutiful the last one is with Kotlin-style lambda!
 * The assertion is also possible with `StepVerifier`, like below:

```kotlin
val result = webTestClient
       .get()
       .uri("/quotes")
       .accept(MediaType.APPLICATION_STREAM_JSON)
       .exchange()
       .returnResult<Quote>()
       .responseBody.take(30)
StepVerifier.create(result)
       .thenConsumeWhile { it.price.signum() > 0 }
       .verifyComplete()
```

### 2019.08.08.

#### [First steps](https://github.com/budaimartin/kotlin-spring-workshop/blob/master/tasks.md#first-steps)

We have created the application with the required dependencies. However, the Initializr still created the pom with Java 8 version and the UI didn't let us choose Kotlin version either. So we modified them to 11 and 1.3.31.

#### [Create the TradingUser entity class](https://github.com/budaimartin/kotlin-spring-workshop/blob/master/tasks.md#create-the-tradinguser-entity-class)

We could create this as an ordinary data class:

```kotlin
@Document
data class TradingUser(@Id var id: String?, var userName: String, var fullName: String) {
    constructor(userName: String, fullName: String) : this(null, userName, fullName)
}
```

* Property annotations, such as `@Id` must be before `var`
* Mongo DB will handle entity ID generation, so it must be nullable

#### [Create repository for TradingUser](https://github.com/budaimartin/kotlin-spring-workshop/blob/master/tasks.md#create-repository-for-tradinguser)

Nothing special here, just like we would do in Java:

```kotlin
interface TradingUserRepository : ReactiveMongoRepository<TradingUser, String> {
    fun findByUserName(name: String): Mono<TradingUser>
}
```

#### [Fill repository with initial data](https://github.com/budaimartin/kotlin-spring-workshop/blob/master/tasks.md#fill-repository-with-initial-data)

We decided to go with the `ApplicationListener` approach and chose the `ApplicationReadyEvent` type to make sure it runs when the context is fully initialized and it won't run again later.

```kotlin
@Component
class UserInitialiserListener(val userRepository: TradingUserRepository) : ApplicationListener<ApplicationReadyEvent> {
    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        userRepository.saveAll(
                listOf(TradingUser("Joco", "Pocok Joco"),
                        TradingUser("Joci", "Poci Joci")))
                .doOnNext { println(it) }
                .subscribe()
    }
}

```

* The `saveAll` method accepts an `Iterable` instance, so we could use Kotlin's `listOf` factory method.
* Subscribing to the Flux returned by `saveAll` makes sure that the insertion runs. An alternative for this could have been `blockLast` that blocks until the last element.

Console output made sure that our entities have been saved:

```
2019-08-08 12:41:40.191  INFO 14852 --- [ntLoopGroup-2-2] org.mongodb.driver.connection            : Opened connection [connectionId{localValue:3, serverValue:3}] to localhost:49286
TradingUser(id=5d4bfc64d947c237011e7dad, userName=Joco, fullName=Pocok Joco)
TradingUser(id=5d4bfc64d947c237011e7dae, userName=Joci, fullName=Poci Joci)
```

### 2019.08.15.

First we corrected `TradingUserRepositroy` so that it returns `Mono<TradingUser>`. We learned that in case of mismatching return type, a `ClassCastException` is thrown.

```
java.lang.ClassCastException: class reactor.core.publisher.MonoFlatMap cannot be cast to class hcom.mobile.workshop.tradingservice.domain.TradingUser
```

#### [Expose users](https://github.com/budaimartin/kotlin-spring-workshop/blob/master/tasks.md#expose-users)

Since we are using Spring Data, we get most CRUD operations out of box. Therefore, the `/users` endpoint is as simple as that almost-oneliner below.

```kotlin
@GetMapping("/users")
fun getUsers() = userRepository.findAll()
```

The other endpoint, however, needs some tweaking. We found out that in case of missing entity an empty Mono is returned by `TradingUserRepositroy`. We couldn't map it to `404 NOT_FOUND` yet, but so far we have this:

```kotlin
@GetMapping("/users/{userName:.+}")
fun getUserByUserName(@PathVariable userName: String) = userRepository.findByUserName(userName)
    .switchIfEmpty(Mono.error(Exception("Nincs Joco :(")))
```

* Notice `:.+` after the path parameter. It allows us to use special characters in the path variable.
* First, we tried [Mono.or()](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#or-reactor.core.publisher.Mono-), because it was quite similar to `Optional.or()` at first sight. But as a result, the endpoint always returned an error, because it emits the data of the first _completing_ Mono and it's always will be `Mono.error()`.
* The [Mono.switchIfEmpty()](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#switchIfEmpty-reactor.core.publisher.Mono-) does exactly what we need: wait for the completion of the Mono and emit an alternative when there was no data.

The current solution results in `500 INTERNAL_SERVER_ERROR`. On the next session we are going to map this exceptional case to a proper `404 NOT_FOUND`.

### 2019.08.23.

#### [Expose users (continued)](https://github.com/budaimartin/kotlin-spring-workshop/blob/master/tasks.md#expose-users)

The final version of the _getUserByUserName_ endpoint looks like this:

```kotlin
fun getUserByUserName(@PathVariable userName: String) = userRepository.findByUserName(userName)
        .switchIfEmpty(Mono.error(UserNotFoundException("User $userName not found! ¯\\_(ツ)_/¯")))
```

* Inside a String literal you can get the value of any variable by adding the `$` prefix to its name. (More complex expressions require the `${}` notation.)
* We have introduced a custom exception when the user is not found.

```kotlin
class UserNotFoundException(message: String) : Exception(message)
```

Then we could use it in an exception handler method, just like we would do in WebMVC:

```kotlin
@ExceptionHandler(UserNotFoundException::class)
@ResponseStatus(NOT_FOUND)
fun handleUserNotFound(exception: UserNotFoundException) = exception.message
```

* The `@ExceptionHandler` annotation should get the type of the exception that is wrapped inside the Mono.
* Error body will be the message of the exception.

#### [Testing](https://github.com/budaimartin/kotlin-spring-workshop/blob/master/tasks.md#testing-1)

We started the implementation of some integration tests for the application. We managed to create one:

```kotlin
@Test
fun `Get user should return a Joco if user name is Joco`() {
    webTestClient.get()
        .uri("/users/TestJoco")
        .exchange()
        .expectBody<TradingUser>()
        .isEqualTo(tradingUser)
}
```

We needed to make sure that when querying the database, the same entity is returned as `tradingUser`. Because ID-s are given by Mongo, we had two choices:

1. Mock `TradingUserRepository`
2. Since we use in-memory MongoDB, simply insert an instance beforehand and assign it to `tradingUser`

The first one is quite problematic. Because of `UserInitialiserListener`, we should stub the `saveAll` method as well or else we would get `NullPointerException`. But we cannot do that because the listener runs as soon as the context gets ready, i.e. before any stubbing could be done in the test. The solution would be a separate `@Configuration` class with a mocked `TradingUserRepository` bean, but that's just overkill.

Option _1/b_ was to use Spring profiles: make the `UserInitialiserListener` profile-dependent and launch the integration test with another one. We agreed that we should handle the listener as a first-class citizen in the application and shouldn't get rid of it in the tests.

Therefore, we went with the second option.

```kotlin
@Autowired
lateinit var tradingUserRepository: TradingUserRepository

lateinit var tradingUser : TradingUser

@Before
fun init() {
    tradingUser = tradingUserRepository.save(TradingUser("TestJoco", "Joco Test"))
        .block()!!
}
```

It works, but has some not so nice features.

* Line `.block()!!` smells both from Reactive and Kotlin perspective.
* Since the `@Before` method runs as many times as tests we have, the entity gets inserted multiple times.

We are going after these on the next session.
