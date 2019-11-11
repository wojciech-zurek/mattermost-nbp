package eu.wojciechzurek.mattermostnbp

import eu.wojciechzurek.mattermostnbp.api.NBPApiException
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest

@Component
class GlobalErrorAttributes : DefaultErrorAttributes() {

    override fun getErrorAttributes(request: ServerRequest, includeStackTrace: Boolean): Map<String, Any> {
        val map = super.getErrorAttributes(request, includeStackTrace)

        val throwable = getError(request)
        if (throwable is NBPApiException) {
            map["status"] = throwable.httpStatus.value()
            map["error"] = throwable.httpStatus.reasonPhrase
        }

        return map
    }
}