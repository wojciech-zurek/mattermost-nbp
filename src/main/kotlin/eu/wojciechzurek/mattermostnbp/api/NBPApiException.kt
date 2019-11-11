package eu.wojciechzurek.mattermostnbp.api

import org.springframework.http.HttpStatus

class NBPApiException(val httpStatus: HttpStatus, message: String) : RuntimeException(message)