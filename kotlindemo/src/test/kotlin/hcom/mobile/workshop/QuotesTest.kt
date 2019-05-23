package hcom.mobile.workshop

import hcom.mobile.workshop.domain.Quote
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.APPLICATION_STREAM_JSON
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.test.web.reactive.server.returnResult
import reactor.test.StepVerifier

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
class QuotesTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun testQuotesWithSize() {
        val size = 5
        webTestClient.get()
            .uri("/quotes?size=$size")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList<Quote>()
            .hasSize(size)
    }

    @Test
    fun testQuotesWithZeroSize() {
        webTestClient.get()
            .uri("/quotes?size=0")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList<Quote>()
            .hasSize(0)
    }

    @Test
    fun testQuotesWithoutSize() {
        webTestClient.get()
            .uri("/quotes")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList<Quote>()
            .hasSize(10)
    }

    @Test
    fun testQuotesStream() {
        val result = webTestClient.get()
            .uri("/quotes")
            .accept(APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult<Quote>()
            .responseBody.take(30)
            .collectList()
            .block()

        assertThat(result).allSatisfy {
            assertThat(it.price).isPositive()
        }
    }

    @Test
    fun testQuotesStreamWithStepVerifier() {
        StepVerifier.create(
            webTestClient.get()
                .uri("/quotes")
                .accept(APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk
                .returnResult<Quote>()
                .responseBody
        )
            .thenRequest(30)
            .thenConsumeWhile { it.price.signum() > 0 }
            .expectNextCount(30)
            .thenCancel()
    }
}
