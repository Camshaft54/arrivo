package me.cameronshaw.arrivo.ui.previewdata

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import me.cameronshaw.arrivo.ui.screens.scheduledetail.ScheduleDetailScreenContent

@Composable
@Preview(showBackground = true)
fun ScheduleDetailScreenContentPreview() {
    MaterialTheme {
        ScheduleDetailScreenContent(
            train = SampleData.train(546),
        )
    }
}