package com.stewardapostol.weatherapp.data.model
import kotlinx.serialization.Serializable

@Serializable
data class GeoLocationResponse(
    val ip: String,
    val success: Boolean,
    val type: String? = null,
    val message: String? = null,
    val continent: String? = null,
    val continent_code: String? = null,
    val country: String? = null,
    val country_code: String? = null,
    val region: String? = null,
    val region_code: String? = null,
    val city: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val is_eu: Boolean? = null,
    val postal: String? = null,
    val calling_code: String? = null,
    val capital: String? = null,
    val borders: String? = null,
    val flag: Flag? = null,
    val connection: Connection? = null,
    val timezone: Timezone? = null
)

@Serializable
data class Flag(
    val img: String? = null,
    val emoji: String? = null,
    val emoji_unicode: String? = null
)

@Serializable
data class Connection(
    val asn: Int? = null,
    val org: String? = null,
    val isp: String? = null,
    val domain: String? = null
)

@Serializable
data class Timezone(
    val id: String? = null,
    val abbr: String? = null,
    val is_dst: Boolean? = null,
    val offset: Int? = null,
    val utc: String? = null,
    val current_time: String? = null
)
