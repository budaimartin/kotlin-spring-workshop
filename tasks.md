# Tasks

This workshop is the continuation of our [WebFlux Workshop](https://github.com/budaimartin/webflux-workshop). We are going to rebuild the Stock trading system using Kotlin and Spring Boot and introduce new features along the way that includes Dynamo DB and Machine Learning.

## First part

Rebuild the services using Kotlin and Spring Boot.

Useful articles:
 * [Reactive web services using Kotlin and Spring Boot 2](https://amarszalek.net/blog/2018/04/02/reactive-web-services-kotlin-spring-boot-2/)
 * [Going Reactive with Spring, Coroutines and Kotlin Flow](https://spring.io/blog/2019/04/12/going-reactive-with-spring-coroutines-and-kotlin-flow)

### Create Quotes application

* Generate the project in IntelliJ IDEA. Take a look at the archetype selection and choose one that is closest to the Kotlin+SpringBoot world.
* Make sure that the project parent is the `spring-boot-starter-parent`.
* Include following dependencies:
  * `kotlin-stdlib`
  * `kotlinx-coroutines-core`
  * `spring-boot-starter-webflux`
  * `jackson-module-kotlin` (Jackson is having a hard time deserializing Kotlin data classes without this)
* Modify the `server.port` property to 8081

#### Create RestController

* `GET /hello-world` returns only the String "hello"
* `POST /echo` returns the String received in the request body

#### Quote domain class

* As seen in the [WebFlux Workshop](https://github.com/budaimartin/webflux-workshop/blob/master/demostock-quotes/src/main/java/io/spring/workshop/demostockquotes/domain/Quote.java), but use Kotlin-style!

#### Quote generator class

* See the [previous implementation](https://github.com/budaimartin/webflux-workshop/blob/master/demostock-quotes/src/main/java/io/spring/workshop/demostockquotes/generator/QuoteGenerator.java), but:
  * Make the class more Kotlin-like! ;)
  * Do not use `Flux`, but build a `Sequence` instead! _Hint: use Kotlin DSL for building the sequence and delaying!_

#### Expose quotes

* `GET /quotes?size={size}` returns _size_ quotes (default value is 10).
  * MIME type is `application/json`
* `GET /quotes` returns the stream of quotes
  * MIME type is `application/stream+json`
  * _Hint: maybe there is an extension function to make a Flux from Sequence?_

#### Replace RestController with RouterFunction and Handler

RestController is fun, but let's take a look at RouterFunctions too.

* Create a RouterFunction for routing instead of the RestController. Use [Kotlin DSL](https://docs.spring.io/spring/docs/5.2.0.M1/spring-framework-reference/languages.html#kotlin-web)!
* See [QuoteHandler](https://github.com/budaimartin/webflux-workshop/blob/master/demostock-quotes/src/main/java/io/spring/workshop/demostockquotes/resource/QuoteHandler.java) and [QuoteRouter](https://github.com/budaimartin/webflux-workshop/blob/master/demostock-quotes/src/main/java/io/spring/workshop/demostockquotes/resource/QuoteRouter.java) from the WebFlux workshop!

#### Testing

* Write some unit tests for `QuoteGenerator`.
* Write some integration tests for the application. See [DemostockQuotesApplicationTests](https://github.com/budaimartin/webflux-workshop/blob/master/demostock-quotes/src/test/java/io/spring/workshop/demostockquotes/DemostockQuotesApplicationTests.java) for hints!

### Create Trading Service application

_To be continued..._

## Second part (under planning)

Drop in-memory Mongo DB and replace it with Dynamo DB. Add some cool features using the new persistence.

## Third part (under planning)

Create a regression model based on stock prices and trends. The model should give hints for the user what to buy and sell.
