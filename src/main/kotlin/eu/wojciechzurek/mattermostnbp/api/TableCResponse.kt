package eu.wojciechzurek.mattermostnbp.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class TableCResponse(
        val effectiveDate: String,
        val rates: List<TableC>
)

data class TableC(
        val code: String,
        val currency: String,
        val ask: Float,
        val bid: Float
)