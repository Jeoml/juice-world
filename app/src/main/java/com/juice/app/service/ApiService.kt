package com.juice.app.service

import com.juice.app.model.Stall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiService {

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.BODY
        }
        defaultRequest {
            url("http://10.0.2.2:3000/")
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun getAllStalls(): List<Stall> {
        return client.get("stalls").body()
    }

    suspend fun getStall(id: Int): Stall {
        return client.get("stalls/$id").body()
    }

    suspend fun getNearbyStalls(lat: Double, lng: Double, radius: Double = 5.0): List<Stall> {
        return client.get("stalls/nearby") {
            parameter("lat", lat)
            parameter("lng", lng)
            parameter("radius", radius)
        }.body()
    }

    suspend fun createStall(stall: Stall): Stall {
        return client.post("stalls") {
            setBody(stall)
        }.body()
    }

    suspend fun updateStall(id: Int, stall: Stall): Stall {
        return client.put("stalls/$id") {
            setBody(stall)
        }.body()
    }

    suspend fun deleteStall(id: Int) {
        client.delete("stalls/$id")
    }
}
