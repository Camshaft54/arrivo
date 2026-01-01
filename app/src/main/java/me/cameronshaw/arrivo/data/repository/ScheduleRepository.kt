package me.cameronshaw.arrivo.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.zip
import me.cameronshaw.arrivo.data.model.ScheduleDatum
import me.cameronshaw.arrivo.data.model.Station
import me.cameronshaw.arrivo.data.model.Train
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

interface ScheduleRepository {
    /**
     * Fetches latest data from remote server for all trains and stations.
     */
    suspend fun refreshSchedule()

    fun getScheduleData(): Flow<List<ScheduleDatum>>
}

@Singleton
class ScheduleRepositoryImpl @Inject constructor(
    private val trainRepository: TrainRepository, private val stationRepository: StationRepository
) : ScheduleRepository {

    override suspend fun refreshSchedule() {
        trainRepository.refreshAllTrains()
        stationRepository.refreshAllStations()
    }

    override fun getScheduleData(): Flow<List<ScheduleDatum>> = trainRepository.getTrains()
        .zip(stationRepository.getStations()) { trains: List<Train>, stations: List<Station> ->
            val stationCodes = stations.map { station -> station.code }
            trains.map { train ->
                val stops = train.stops.filter {
                    it.code in stationCodes
                }
                when (stops.size) {
                    0 -> {
                        ScheduleDatum(
                            train = train, departureStop = null, arrivalStop = null
                        )
                    }

                    1 -> {
                        ScheduleDatum(
                            train = train, departureStop = stops[0], arrivalStop = stops[0]
                        )
                    }

                    else -> {
                        val relevantStops = pickTwoRelevantStops(stops)
                        ScheduleDatum(
                            train = train,
                            departureStop = relevantStops[0],
                            arrivalStop = relevantStops[1]
                        )
                    }
                }
            }
        }

    /**
     * Given a list of stops the user is tracking that is larger than two, this method chooses the
     * two stops the user is most likely interested in.
     * If only 0 or 1 stops remain, the last two stops are returned. Otherwise, the first two stops
     * that have not been made yet are returned.
     */
    private fun pickTwoRelevantStops(
        stops: List<Train.Stop>, now: OffsetDateTime = OffsetDateTime.now()
    ): List<Train.Stop> = stops.sortedBy {
        it.arrival ?: it.scheduledArrival
    }.let {
        val remainingStops = it.filter { stop -> stop.arrival?.isAfter(now) ?: false }
        when (remainingStops.size) {
            0 -> {
                it.takeLast(2)
            }

            1 -> {
                it.takeLast(2)
            }

            else -> {
                remainingStops.take(2)
            }
        }
    }
}