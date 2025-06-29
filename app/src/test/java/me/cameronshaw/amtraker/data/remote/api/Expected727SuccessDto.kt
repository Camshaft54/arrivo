package me.cameronshaw.amtraker.data.remote.api

import me.cameronshaw.amtraker.data.remote.dto.TrainDto

val expectedTrain = TrainDto(
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