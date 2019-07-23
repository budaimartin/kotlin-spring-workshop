package hcom.mobile.workshop.demostockquotes

import org.assertj.core.api.Assertions.assertThat
import hcom.mobile.workshop.demostockquotes.domain.Quote
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.test.web.reactive.server.returnResult
import reactor.test.StepVerifier

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemostockQuotesApplicationTests {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun contextLoads() {
    }

    @Test
    fun `Test hello-world endpoint should return Hello`() {
        webTestClient.get()
                .uri("/hello-world")
                .exchange()
                .expectBody<String>()
                .isEqualTo("Hello")
    }

    @Test
    fun `Test Echo endpoint returns an echo`() {
        webTestClient.post()
                .uri("/echo")
                .syncBody("Echo this")
                .exchange()
                .expectBody<String>()
                .isEqualTo("Echo this")
    }

    @Test
    fun `Test quotes endpoint should return expected amount of quotes`() {
        webTestClient
                .get()
                .uri("/quotes?size=2")
                .exchange()
                .expectBodyList<Quote>()
                .hasSize(2)
    }

    @Test
    fun `Test quotes endpoint should return default amount of quotes`() {
        webTestClient
                .get()
                .uri("/quotes")
                .exchange()
                .expectBodyList<Quote>()
                .hasSize(5)
    }

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

    @Test
    fun `Test quotes endpoint with StepVerifier`() {
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
    }
}
