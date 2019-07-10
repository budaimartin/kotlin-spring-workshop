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
