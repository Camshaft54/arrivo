package me.cameronshaw.arrivo.ui.previewdata

import me.cameronshaw.arrivo.data.model.Train
import java.time.OffsetDateTime

class SampleData {
    companion object {
        private val referenceTime = OffsetDateTime.now()
        val stop1 = Train.Stop(
            code = "OKJ",
            name = "Oakland - Jack London",
            arrival = referenceTime,
            departure = referenceTime.plusMinutes(2),
            scheduledArrival = referenceTime,
            scheduledDeparture = referenceTime.plusMinutes(2)
        )

        val stop2 = Train.Stop(
            code = "GAC",
            name = "Santa Clara - Great America",
            arrival = referenceTime.plusHours(1),
            departure = referenceTime.plusHours(1).plusMinutes(2),
            scheduledArrival = referenceTime.plusHours(1),
            scheduledDeparture = referenceTime.plusHours(1).plusMinutes(2)
        )

        fun train(num: Int) = Train(
            num = num.toString(),
            originDate = stop1.departure,
            routeName = "Route $num",
            stops = listOf(stop1, stop2),
            provider = "Amtrak",
            velocity = 0.0,
            lastUpdated = referenceTime
        )
    }
}