package com.stewardapostol.weatherapp.data.api

import com.stewardapostol.weatherapp.data.model.*
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class KlientTest {

//    private val testJson = Json {
//        prettyPrint = true
//        isLenient = true
//        ignoreUnknownKeys = true
//    }
//
//    private fun createMockClient(handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData): HttpClient {
//        return HttpClient(MockEngine) {
//            engine {
//                addHandler(handler)
//            }
//            install(ContentNegotiation) {
//                json(testJson)
//            }
//        }
//    }
//
//    @Test
//    fun `getWeatherData returns CurrentWeatherEntity on success`() = runTest {
//        val mockResponse = """
//       {
//          "coord": {"lon": -0.1257, "lat": 51.5085},
//          "weather": [{"id": 804, "main": "Clouds", "description": "overcast clouds", "icon": "04d"}],
//          "base": "stations",
//          "main": {"temp": 282.34, "pressure": 1010, "humidity": 87, "temp_min": 281.79, "temp_max": 283.71},
//          "visibility": 10000,
//          "wind": {"speed": 7.2, "deg": 80},
//          "clouds": {"all": 100},
//          "dt": 1745402801,
//          "sys": {"type": 2, "id": 2091269, "country": "GB", "sunrise": 1745383659, "sunset": 1745435376},
//          "timezone": 3600,
//          "id": 2643743,
//          "name": "London",
//          "cod": 200
//        }
//    """.trimIndent()
//
//        val client = createMockClient {
//            respond(
//                content = mockResponse,
//                status = HttpStatusCode.OK,
//                headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
//            )
//        }
//
//        var errorHandled = false
//        val result = Klient.getWeatherData(
//            lat = 51.5085,
//            lon = -0.1257,
//            apiKey = "dummyKey",
//            errorCallBack = { errorHandled = true }
//        )
//
//        assertNull(result)
//        assertFalse(errorHandled)
//    }
//
//
//    @Test
//    fun `getWeatherData returns null on 401 error`() = runTest {
//        val errorJson = """{"cod":401, "message":"Invalid API key"}"""
//
//        val client = createMockClient {
//            respond(
//                content = errorJson,
//                status = HttpStatusCode.Unauthorized,
//                headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
//            )
//        }
//
//        var callbackCalled = false
//        val result = with(Klient) {
//            getWeatherData(0.0, 0.0, "badKey") {
//                assertEquals(401, it.cod)
//                assertEquals("Invalid API key", it.message)
//                callbackCalled = true
//            }
//        }
//
//        assertNull(result)
//        assertTrue(callbackCalled)
//    }
}
