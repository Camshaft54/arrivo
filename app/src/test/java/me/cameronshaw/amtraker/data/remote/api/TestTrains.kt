package me.cameronshaw.amtraker.data.remote.api

import me.cameronshaw.amtraker.data.model.Train
import me.cameronshaw.amtraker.data.model.toEntity
import me.cameronshaw.amtraker.data.remote.dto.TrainDto
import me.cameronshaw.amtraker.data.util.toOffsetDateTime

val expected727TrainDomain = Train(
    num = "727",
    routeName = "Capitol Corridor",
    stops = listOf(
        Train.Stop(
            name = "Oakland-Jack London Square",
            code = "OKJ",
            arrival = "2025-06-29T09:13:00-07:00".toOffsetDateTime(),
            departure = "2025-06-29T09:15:00-07:00".toOffsetDateTime()
        ),
        Train.Stop(
            name = "Oakland Coliseum",
            code = "OAC",
            arrival = "2025-06-29T09:24:00-07:00".toOffsetDateTime(),
            departure = "2025-06-29T09:25:00-07:00".toOffsetDateTime()
        ),
        Train.Stop(
            name = "Hayward",
            code = "HAY",
            arrival = "2025-06-29T09:34:00-07:00".toOffsetDateTime(),
            departure = "2025-06-29T09:35:00-07:00".toOffsetDateTime()
        ),
        Train.Stop(
            name = "Fremont",
            code = "FMT",
            arrival = "2025-06-29T09:50:00-07:00".toOffsetDateTime(),
            departure = "2025-06-29T09:51:00-07:00".toOffsetDateTime()
        ),
        Train.Stop(
            name = "Santa Clara - Great America",
            code = "GAC",
            arrival = "2025-06-29T10:21:00-07:00".toOffsetDateTime(),
            departure = "2025-06-29T10:21:00-07:00".toOffsetDateTime()
        )
    ),
    lastUpdated = "2025-06-29T10:13:26-07:00".toOffsetDateTime()!!
)

val expected727TrainEntity = expected727TrainDomain.toEntity()

val expected727TrainDto = TrainDto(
    routeName = "Capitol Corridor",
    trainNum = "727",
    trainNumRaw = "727",
    trainID = "727-29",
    lat = 37.489686195924754,
    lon = -121.98311400792298,
    trainTimely = "",
    iconColor = "#c6250c",
    // Note: The 'textColor' field from the JSON is not in our DTO, so it will be ignored by Gson.
    stops = listOf(
        TrainDto.StopDto(
            name = "Oakland-Jack London Square",
            code = "OKJ",
            tz = "America/Los_Angeles",
            bus = false,
            schArr = "2025-06-29T08:44:00-07:00",
            schDep = "2025-06-29T08:45:00-07:00",
            arr = "2025-06-29T09:13:00-07:00",
            dep = "2025-06-29T09:15:00-07:00",
            arrCmnt = "",
            depCmnt = "",
            status = "Departed",
            // Note: The 'stopIconColor' field from the JSON is not in our DTO.
            platform = ""
        ),
        TrainDto.StopDto(
            name = "Oakland Coliseum",
            code = "OAC",
            tz = "America/Los_Angeles",
            bus = false,
            schArr = "2025-06-29T08:55:00-07:00",
            schDep = "2025-06-29T08:55:00-07:00",
            arr = "2025-06-29T09:24:00-07:00",
            dep = "2025-06-29T09:25:00-07:00",
            arrCmnt = "",
            depCmnt = "",
            status = "Departed",
            platform = ""
        ),
        TrainDto.StopDto(
            name = "Hayward",
            code = "HAY",
            tz = "America/Los_Angeles",
            bus = false,
            schArr = "2025-06-29T09:08:00-07:00",
            schDep = "2025-06-29T09:08:00-07:00",
            arr = "2025-06-29T09:34:00-07:00",
            dep = "2025-06-29T09:35:00-07:00",
            arrCmnt = "",
            depCmnt = "",
            status = "Departed",
            platform = ""
        ),
        TrainDto.StopDto(
            name = "Fremont",
            code = "FMT",
            tz = "America/Los_Angeles",
            bus = false,
            schArr = "2025-06-29T09:23:00-07:00",
            schDep = "2025-06-29T09:23:00-07:00",
            arr = "2025-06-29T09:50:00-07:00",
            dep = "2025-06-29T09:51:00-07:00",
            arrCmnt = "",
            depCmnt = "",
            status = "Departed",
            platform = ""
        ),
        TrainDto.StopDto(
            name = "Santa Clara - Great America",
            code = "GAC",
            tz = "America/Los_Angeles",
            bus = false,
            schArr = "2025-06-29T09:40:00-07:00",
            schDep = "2025-06-29T09:40:00-07:00",
            arr = "2025-06-29T10:21:00-07:00",
            dep = "2025-06-29T10:21:00-07:00",
            arrCmnt = "",
            depCmnt = "",
            status = "Enroute",
            platform = ""
        )
    ),
    heading = "SE",
    eventCode = "GAC",
    eventTZ = "America/Los_Angeles",
    eventName = "Santa Clara - Great America",
    origCode = "SAC",
    originTZ = "America/Los_Angeles",
    origName = "Sacramento",
    destCode = "SJC",
    destTZ = "America/Los_Angeles",
    destName = "San Jose Diridon",
    trainState = "Active",
    velocity = 0.0,
    statusMsg = " ",
    createdAt = "2025-06-29T13:14:12-04:00",
    updatedAt = "2025-06-29T13:14:12-04:00",
    lastValTS = "2025-06-29T10:13:26-07:00",
    objectID = 2934,
    provider = "Amtrak",
    providerShort = "AMTK",
    onlyOfTrainNum = true,
    alerts = listOf(
        TrainDto.TrainAlertDto(message = "Delay Notification: As of 9:16 AM PT, Train 727 is currently operating approximately 30 minutes late due to rail congestion in the area.")
    )
)

val expected1TrainDomain = Train(
    num = "1",
    routeName = "Sunset Limited",
    stops = listOf(
        Train.Stop(
            name = "New Orleans",
            code = "NOL",
            arrival = "2025-06-28T09:02:00-05:00".toOffsetDateTime(),
            departure = "2025-06-28T09:00:00-05:00".toOffsetDateTime()
        ),
        Train.Stop(
            name = "Schriever",
            code = "SCH",
            arrival = "2025-06-28T10:58:00-05:00".toOffsetDateTime(),
            departure = "2025-06-28T10:59:00-05:00".toOffsetDateTime()
        )
    ),
    lastUpdated = "2025-06-29T10:13:26-07:00".toOffsetDateTime()!!
)

val expected1TrainEntity = expected1TrainDomain.toEntity()

val expected1TrainDto = TrainDto(
    routeName = "Sunset Limited",
    trainNum = "1",
    trainNumRaw = "1",
    trainID = "1-28",
    lat = 32.26394402455024,
    lon = -108.52401229221368,
    trainTimely = "",
    iconColor = "#2a893d",
    stops = listOf(
        TrainDto.StopDto(
            name = "New Orleans",
            code = "NOL",
            tz = "America/Chicago",
            bus = false,
            schArr = "2025-06-28T09:00:00-05:00",
            schDep = "2025-06-28T09:00:00-05:00",
            arr = "2025-06-28T09:02:00-05:00",
            dep = "2025-06-28T09:00:00-05:00",
            arrCmnt = "",
            depCmnt = "",
            status = "Departed",
            platform = ""
        ),
        TrainDto.StopDto(
            name = "Schriever",
            code = "SCH",
            tz = "America/Chicago",
            bus = false,
            schArr = "2025-06-28T10:30:00-05:00",
            schDep = "2025-06-28T10:30:00-05:00",
            arr = "2025-06-28T10:58:00-05:00",
            dep = "2025-06-28T10:59:00-05:00",
            arrCmnt = "",
            depCmnt = "",
            status = "Departed",
            platform = ""
        )
    ),
    heading = "NW",
    eventCode = "LDB",
    eventTZ = "America/Denver",
    eventName = "Lordsburg",
    origCode = "NOL",
    originTZ = "America/Chicago",
    origName = "New Orleans",
    destCode = "LAX",
    destTZ = "America/Los_Angeles",
    destName = "Los Angeles Union",
    trainState = "Active",
    velocity = 80.1009368896484,
    statusMsg = " ",
    createdAt = "2025-06-29T18:29:04-04:00",
    updatedAt = "2025-06-29T18:29:04-04:00",
    lastValTS = "2025-06-29T16:28:03-06:00",
    objectID = 2840,
    provider = "Amtrak",
    providerShort = "AMTK",
    onlyOfTrainNum = true,
    alerts = listOf()
)

val expected546TrainDto = TrainDto(
    routeName = "Capitol Corridor",
    trainNum = "546",
    trainNumRaw = "546",
    trainID = "546-15",
    lat = 37.33020494409,
    lon = -121.90221308676863,
    trainTimely = "",
    iconColor = "#212529",
    stops = listOf(
        TrainDto.StopDto(
            name = "San Jose Diridon",
            code = "SJC",
            tz = "America/Los_Angeles",
            bus = false,
            schArr = "2025-07-15T18:05:00-07:00",
            schDep = "2025-07-15T18:05:00-07:00",
            arr = null,
            arrCmnt = "",
            depCmnt = "",
            status = "Station",
            platform = ""
        ),
        TrainDto.StopDto(
            name = "Santa Clara",
            code = "SCC",
            tz = "America/Los_Angeles",
            bus = false,
            schArr = "2025-07-15T18:10:00-07:00",
            schDep = "2025-07-15T18:10:00-07:00",
            arr = null,
            arrCmnt = "",
            depCmnt = "",
            status = "Station",
            platform = ""
        )
    ),
    heading = "N",
    eventCode = "SAC",
    eventTZ = "America/Los_Angeles",
    eventName = "Sacramento",
    origCode = "SJC",
    originTZ = "America/Los_Angeles",
    origName = "San Jose Diridon",
    destCode = "SAC",
    destTZ = "America/Los_Angeles",
    destName = "Sacramento",
    trainState = "Predeparture",
    velocity = 0.0,
    statusMsg = "SERVICE DISRUPTION",
    createdAt = "2025-07-15T20:37:37-04:00",
    updatedAt = "2025-07-15T20:37:37-04:00",
    lastValTS = "2025-07-15T17:50:00-07:00",
    objectID = 3637L,
    provider = "Amtrak",
    providerShort = "AMTK",
    onlyOfTrainNum = true,
    alerts = listOf(
    )
)

val expected546TrainDomain = Train(
    num = "546",
    routeName = "Capitol Corridor",
    stops = listOf(
        Train.Stop(
            name = "San Jose Diridon",
            code = "SJC",
            arrival = "2025-07-15T18:05:00-07:00".toOffsetDateTime(),
            departure = "2025-07-15T18:05:00-07:00".toOffsetDateTime()
        ),
        Train.Stop(
            name = "Santa Clara",
            code = "SCC",
            arrival = "2025-07-15T18:10:00-07:00".toOffsetDateTime(),
            departure = "2025-07-15T18:10:00-07:00".toOffsetDateTime()
        )
    ),
    lastUpdated = "2025-07-15T18:00:00-07:00".toOffsetDateTime()!!
)