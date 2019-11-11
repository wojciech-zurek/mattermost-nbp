package eu.wojciechzurek.mattermostnbp.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class GoldResponse(
        @JsonProperty("cena")
        val price: Float,
        @JsonProperty("data")
        val date: String
)