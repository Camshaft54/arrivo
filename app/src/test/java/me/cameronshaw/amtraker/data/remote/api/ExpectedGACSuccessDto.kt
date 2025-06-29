package me.cameronshaw.amtraker.data.remote.api

import me.cameronshaw.amtraker.data.remote.dto.StationDto

val expectedStation = StationDto(
    name = "Santa Clara - Great America",
    code = "GAC",
    tz = "America/Los_Angeles",
    lat = 37.406778,
    lon = -121.967003,
    hasAddress = true,
    address1 = "5099 Stars and Stripes Drive",
    address2 = " ",
    city = "Santa Clara",
    state = "CA",
    zip = 95054,
    trains = listOf(
        "724-29",
        "728-29",
        "729-29",
        "737-29"
    )
)