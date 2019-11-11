package eu.wojciechzurek.mattermostnbp.api

data class Rate(
        val currency: String,
        val mid: Float? = null,
        val ask: Float? = null,
        val bid: Float? = null
)