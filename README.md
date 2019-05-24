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
