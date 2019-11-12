package eu.wojciechzurek.mattermostnbp.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class TableAResponse(
        val effectiveDate: String,
        val rates: List<TableA>
)

data class TableA(
        val code: String,
        val currency: String,
        val mid: BigDecimal
)