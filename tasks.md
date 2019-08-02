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

#### First steps

Generate the project with [Spring Initializr](https://start.spring.io) like the previous one. Make sure to include the following starters:
* Reactive Web
* Thymeleaf
* Reactive Mongo

Also, to be able to use the datastore, include this dependency for embedded Mongo DB:

```xml
<dependency>
  <groupId>de.flapdoodle.embed</groupId>
  <artifactId>de.flapdoodle.embed.mongo</artifactId>
</dependency>
```

#### Create the TradingUser entity class

* Like in [Java before](https://github.com/budaimartin/webflux-workshop/blob/master/trading-service/src/main/java/io/spring/workshop/tradingservice/TradingUser.java), but don't forget that you're using Kotlin!

#### Create repository for TradingUser

* Create an interface that extends `ReactiveMongoRepository`.
* Add a `findByUserName(String userName)` method that returns a single `TradingUser` instance.

#### Fill repository with initial data

* Create a `CommandLineRunner` or `ApplicationListener` that inserts some users to the database. Be creative at names! :)
  * NB! Remember that you should run it blocking to make effect!

#### Expose users

* `GET /users` endpoint returns all users in the repository.
* `GET /users/{username}` returns a single user with the given username, or `404 NOT_FOUND` when it doesn't exist.

#### Testing

* Write some integration tests for the application. See [UserControllerTest](https://github.com/budaimartin/webflux-workshop/blob/master/trading-service/src/test/java/io/spring/workshop/tradingservice/UserControllerTest.java) if you're stuck.

#### Prepare for HTML view rendering

* Add the following dependencies:

```xml
<dependency>
  <groupId>org.webjars</groupId>
  <artifactId>bootstrap</artifactId>
  <version>3.3.7</version>
</dependency>
<dependency>
  <groupId>org.webjars</groupId>
  <artifactId>highcharts</artifactId>
  <version>5.0.8</version>
</dependency>
```

* Put the following HTML templates in the `src/main/resources/templates` folder:
  * [index.html](https://raw.githubusercontent.com/budaimartin/kotlin-spring-workshop/own-solution/shared/index.html)
  * [quotes.html](https://raw.githubusercontent.com/budaimartin/kotlin-spring-workshop/own-solution/shared/quotes.html)

#### Render home page

* Implement the `GET /` (root) endpoint that should render the _index.html_ template.
  * MIME type is `text/html`
  * There is a model attribute with the name of `users` that contain every user located in the repository

#### Render quotes stream page

* Copy the `Quote` data class from the Quotes application.
* Implement the `GET /quotes` endpoint that renders the _quotes.html_ template.
  * MIME type is `text/html`
* Implement the `GET /quotes/feed` endpoint that feeds the quotes for the view.
  * MIME type is `text/event-stream`
  * Body should be the infinite stream returned by Quotes application's `/quotes` endpoint

#### Launch the app

* Launch both applications.
* `http://localhost:8080/` should show all users.
* Navigate to `http://localhost:8080/quotes` to see a fancy diagram of the quotes stream.

_This is the end of the part that follows the original Spring WebFlux workshop. The websocket part is omitted intentionally._

## Second part (under planning)

Drop in-memory Mongo DB and replace it with Dynamo DB. Add some cool features using the new persistence.

## Third part (under planning)

Create a regression model based on stock prices and trends. The model should give hints for the user what to buy and sell.
