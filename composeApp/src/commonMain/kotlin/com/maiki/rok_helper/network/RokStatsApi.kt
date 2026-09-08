package com.maiki.rok_helper.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class RokStatsApi(private val client: HttpClient, private val apiKey: String) {
    suspend fun getGovernor(playerId: Long): GovernorResponse {
        return client.get("governors/$playerId") {
            parameter("apiKey", apiKey)
        }.body()
    }

    companion object {
        private const val BASE_URL = "https://app.rokstats.online/api/v1/"

        fun create(apiKey: String): RokStatsApi {
            val client = HttpClient {
                defaultRequest {
                    url(BASE_URL)
                }
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        coerceInputValues = true
                    })
                }
            }
            return RokStatsApi(client, apiKey)
        }
    }
}
