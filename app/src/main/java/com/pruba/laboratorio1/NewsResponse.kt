package com.pruba.laboratorio1
import com.google.gson.annotations.SerializedName


data class NewsResponse(
    @SerializedName("status") val status:String,
    @SerializedName("totalResulta") val totalResult:String,
    @SerializedName("articles") val articles:List<Articles>
    )
