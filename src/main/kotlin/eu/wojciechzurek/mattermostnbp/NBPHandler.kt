package eu.wojciechzurek.mattermostnbp

import eu.wojciechzurek.mattermostnbp.api.*
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux

@Component
class NBPHandler {

    private val logger = loggerFor(this.javaClass)
    private val webClient = WebClient.builder()
            .baseUrl("http://api.nbp.pl/api/")
            .build()

    fun nbp(request: ServerRequest): Mono<ServerResponse> {

        val gold = webClient
                .get()
                .uri("cenyzlota")
                .retrieve()
                .onStatus(HttpStatus::isError) {
                    it.bodyToMono<String>().subscribe { body -> logger.error(body) }
                    Mono.error(NBPApiException(it.statusCode(), "NBP Endpoint exception"))
                }
                .bodyToMono(GoldResponse::class.java)
                .map {
                    "gld" to Rate("złoto", it.price)
                }

        val tableAResponse = webClient
                .get()
                .uri("exchangerates/tables/A")
                .retrieve()
                .onStatus(HttpStatus::isError) {
                    it.bodyToMono<String>().subscribe { body -> logger.error(body) }
                    Mono.error(NBPApiException(it.statusCode(), "NBP Endpoint exception"))
                }
                .bodyToMono(TableAResponse::class.java)
                .flatMapIterable { it.rates }
                .map { it.code to Rate(it.currency, it.mid) }


        val tableCResponse = webClient
                .get()
                .uri("exchangerates/tables/C")
                .retrieve()
                .onStatus(HttpStatus::isError) {
                    it.bodyToMono<String>().subscribe { body -> logger.error(body) }
                    Mono.error(NBPApiException(it.statusCode(), "NBP Endpoint exception"))
                }
                .bodyToMono(TableCResponse::class.java)
                .flatMapIterable { it.rates }
                .map { it.code to Rate(it.currency, ask = it.ask, bid = it.bid) }

        tableAResponse.con(tableCResponse)

        //tableAResponse.zipWith(tableCResponse).map {

        //    }
        //  val y= goldResponse.concatWith(x)).
    }

}
