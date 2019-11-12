package eu.wojciechzurek.mattermostnbp.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class GoldResponse(
        @JsonProperty("cena")
        val price: BigDecimal,
        @JsonProperty("data")
        val date: String
)