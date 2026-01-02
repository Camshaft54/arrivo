package me.cameronshaw.arrivo.data.remote.api

import me.cameronshaw.arrivo.data.amtrak.dto.AmtrakStationProperties
import me.cameronshaw.arrivo.data.amtraker.dto.StationDto
import me.cameronshaw.arrivo.data.local.model.StationEntity
import me.cameronshaw.arrivo.data.model.Station
import me.cameronshaw.arrivo.data.util.toOffsetDateTime

val expectedGACStationDomain = Station(
    code = "GAC",
    name = "Santa Clara - Great America",
    lastUpdated = "2025-06-29T10:13:26-07:00".toOffsetDateTime()!!
)

val expectedGACStationEntity = StationEntity(
    code = "GAC",
    name = "Santa Clara - Great America",
    lastUpdated = "2025-06-29T10:13:26-07:00"
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
    name = "San Jose Diridon",
    lastUpdated = "2025-06-29T10:13:26-07:00".toOffsetDateTime()!!
)

val expectedSJCStationEntity = StationEntity(
    code = "SJC",
    name = "San Jose Diridon",
    lastUpdated = "2025-06-29T10:13:26-07:00"
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

val expectedAmtrakApiStations: List<AmtrakStationProperties> = listOf(
    AmtrakStationProperties(
        name = "Albuquerque, NM",
        code = "ABQ",
        lat = 35.082061,
        lon = -106.647975,
        address1 = "320 1st Street SW",
        address2 = " ",
        city = "Albuquerque",
        state = "NM",
        zip = "87102"
    ),
    AmtrakStationProperties(
        name = "Antioch-Pittsburg, CA",
        code = "ACA",
        lat = 38.0177,
        lon = -121.816024,
        address1 = "100 I Street",
        address2 = " ",
        city = "Antioch",
        state = "CA",
        zip = "94509"
    ),
    AmtrakStationProperties(
        name = "Santa Clara-Great America, CA",
        code = "GAC",
        lat = 37.405321,
        lon = -121.977457,
        address1 = "5099 Stars and Stripes Drive",
        address2 = "",
        city = "Santa Clara",
        state = "CA",
        zip = "95054"
    ),
    AmtrakStationProperties(
        name = "Arcadia, MO",
        code = "ACD",
        lat = 37.592161,
        lon = -90.624403,
        address1 = "13700 Highway 21",
        address2 = null,
        city = "Arcadia",
        state = "MO",
        zip = "63621"
    )
)