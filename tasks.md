# Tasks

This workshop is the continuation our [WebFlux Workshop](https://github.com/budaimartin/webflux-workshop). We are going to rebuild the Stock trading system using Kotlin and Spring Boot and introduce new features along the way that includes Dynamo DB and Machine Learning.

## First part

Rebuild the services using Kotlin and Spring Boot.

### Create Quotes application

* Generate the project in IntelliJ IDEA. Take a look at the archetype selection and choose one that is closest to the Kotlin+SpringBoot world.
* Make sure that the project parent is the `spring-boot-starter-parent`.
* Include following dependencies:
  * `kotlin-stdlib`
  * `kotlinx-coroutines-core`
  * `spring-boot-starter-webflux`
* Modify the `server.port` property to 8081

### Create the Quote Generator

#### Quote domain class

* As seen in the [WebFlux Workshop](https://github.com/budaimartin/webflux-workshop/blob/master/demostock-quotes/src/main/java/io/spring/workshop/demostockquotes/domain/Quote.java), but use Kotlin-style!

#### Quote generator class

* See the [previous implementation](https://github.com/budaimartin/webflux-workshop/blob/master/demostock-quotes/src/main/java/io/spring/workshop/demostockquotes/generator/QuoteGenerator.java), but:
  * Make the class more Kotlin-like! ;)
  * Do not use `Flux`, but build a `Sequence` instead! _Hint: use Kotlin DSL for building the sequence and delaying!_

### Create the router

## Second part (under planning)

Drop in-memory Mongo DB and replace it with Dynamo DB. Add some cool features using the new persistence.

## Third part (under planning)

Create a regression model based on stock prices and trends. The model should give hints for the user what to buy and sell.
