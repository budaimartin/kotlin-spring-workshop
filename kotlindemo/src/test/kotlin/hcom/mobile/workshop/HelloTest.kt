package hcom.mobile.workshop

import hcom.mobile.workshop.domain.Quote
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.MediaType.TEXT_PLAIN
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
class HelloTest {

    @Autowired(required = false)
    lateinit var application: QuotesApplication

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun contextLoads() {
        assertThat(application).isNotNull
    }

    @Test
    fun helloEndpointTest() {
        webTestClient.get()
            .uri("/hello-world")
            .accept(TEXT_PLAIN)
            .exchange()
            .expectStatus().isOk
            .expectBody<String>() // https://discuss.kotlinlang.org/t/type-interference-issue-with-the-webflux-webtestclient-and-kotlin/3880/2
            .isEqualTo("hello")
    }

    @Test
    fun echoEndpointTest() {
        val testBody = "Árvíztűrő tükörfúrógép"
        webTestClient.post()
            .uri("/echo")
            .syncBody(testBody)
            .accept(TEXT_PLAIN)
            .exchange()
            .expectStatus().isOk
            .expectBody<String>()
            .isEqualTo(testBody)
    }

}
