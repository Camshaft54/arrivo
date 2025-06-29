package me.cameronshaw.amtraker.data.remote.api

import me.cameronshaw.amtraker.data.local.model.StationEntity
import me.cameronshaw.amtraker.data.model.Station
import me.cameronshaw.amtraker.data.remote.dto.StationDto

val expectedGACStationDomain = Station(
    code = "GAC",
    name = "Santa Clara - Great America"
)

val expectedGACStationEntity = StationEntity(
    code = "GAC",
    name = "Santa Clara - Great America"
)

val expectedGACStationDto = StationDto(
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

val expectedSJCStationDomain = Station(
    code = "SJC",
    name = "San Jose Diridon"
)

val expectedSJCStationEntity = StationEntity(
    code = "SJC",
    name = "San Jose Diridon"
)

val expectedSJCStationDto = StationDto(
    name = "San Jose Diridon",
    code = "SJC",
    tz = "America/Los_Angeles",
    lat = 37.329933,
    lon = -121.902388,
    hasAddress = true,
    address1 = "65 Cahill Street",
    address2 = " ",
    city = "San Jose",
    state = "CA",
    zip = 95110,
    trains = listOf(
        "11-29",
        "11-28",
        "14-28",
        "14-29",
        "732-29",
        "736-29",
        "741-29",
        "743-29"
    )
)