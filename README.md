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
