package eu.wojciechzurek.mattermostnbp

import eu.wojciechzurek.mattermostnbp.api.*
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono
import java.util.*
import java.util.stream.Collectors

@Component
class NBPHandler(
        private val messageSource: MessageSource
) {

    private val logger = loggerFor(this.javaClass)
    private val webClient = WebClient.builder()
            .baseUrl("http://api.nbp.pl/api/")
            .build()

    fun nbp(request: ServerRequest): Mono<ServerResponse> {

        val receiver = when (request.queryParam("text").orElse("ephemeral")) {
            "channel" -> "in_channel"
            "kanal" -> "in_channel"//polish channel
            else -> "ephemeral"
        }

        val gold = webClient
                .get()
                .uri("cenyzlota")
                .retrieve()
                .onStatus(HttpStatus::isError) {
                    it.bodyToMono<String>().subscribe { body -> logger.error(body) }
                    Mono.error(NBPApiException(it.statusCode(), "NBP Endpoint exception"))
                }
                .bodyToFlux(GoldResponse::class.java)
                .map { Rate("GLD", "złoto", it.price) }

        val tableAResponse = webClient
                .get()
                .uri("exchangerates/tables/A")
                .retrieve()
                .onStatus(HttpStatus::isError) {
                    it.bodyToMono<String>().subscribe { body -> logger.error(body) }
                    Mono.error(NBPApiException(it.statusCode(), "NBP Endpoint exception"))
                }
                .bodyToFlux(TableAResponse::class.java)
                .flatMapIterable { it.rates }
                .collectMap { it.code }

        val tableCResponse = webClient
                .get()
                .uri("exchangerates/tables/C")
                .retrieve()
                .onStatus(HttpStatus::isError) {
                    it.bodyToMono<String>().subscribe { body -> logger.error(body) }
                    Mono.error(NBPApiException(it.statusCode(), "NBP Endpoint exception"))
                }
                .bodyToFlux(TableCResponse::class.java)
                .flatMapIterable { it.rates }
                .collectMap { it.code }

        val tables = tableAResponse
                .zipWith(tableCResponse)
                .map { tuple ->
                    tuple.t1.map {
                        val tableCRate = tuple.t2[it.key]
                        Rate(
                                it.key,
                                it.value.currency,
                                it.value.mid,
                                tableCRate?.ask,
                                tableCRate?.bid
                        )
                    }
                }
                .flatMapIterable { it }
                .sort { o1, o2 -> naturalOrder<String>().compare(o1.currency.toLowerCase(), o2.currency.toLowerCase()) }


        return gold.concatWith(tables)
                .map {
                    messageSource.getMessage(
                            "theme.rates.row", arrayOf(it.code, it.currency, it.mid.toString(), it.bid?.toString()
                            ?: "", it.ask?.toString() ?: ""), Locale.getDefault())
                }
                .collect(Collectors.joining())
                .map { messageSource.getMessage("theme.rates", arrayOf(it), Locale.getDefault()) }
                .map { MattermostResponse(receiver, it) }
                .flatMap { ok().bodyValue(it) }
    }
}
