package eu.wojciechzurek.mattermostnbp

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.function.server.router

@SpringBootApplication
@EnableWebFlux
class MattermostNbpApplication{
	@Bean
	fun routes(handler: NBPHandler) = router {
		"/api".nest {
			accept(MediaType.APPLICATION_JSON).nest {
				GET("/nbp", handler::nbp)
			}
		}
	}
}

fun main(args: Array<String>) {
	runApplication<MattermostNbpApplication>(*args)
}

fun <T> loggerFor(clazz: Class<T>): Logger = LoggerFactory.getLogger(clazz)
