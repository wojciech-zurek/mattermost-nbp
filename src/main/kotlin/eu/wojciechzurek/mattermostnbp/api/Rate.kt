package eu.wojciechzurek.mattermostnbp.api

import java.math.BigDecimal

data class Rate(
        val code: String,
        val currency: String,
        val mid: BigDecimal? = null,
        val ask: BigDecimal? = null,
        val bid: BigDecimal? = null
)