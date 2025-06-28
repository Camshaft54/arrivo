package me.cameronshaw.amtraker.data.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.toDbString() = this.format(DateTimeFormatter.ISO_INSTANT)

fun String.toLocalDateTime() = LocalDateTime.parse(this, DateTimeFormatter.ISO_INSTANT)