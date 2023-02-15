package org.turkiye.offlinechat.data.local

import androidx.room.TypeConverter
import java.util.*

/**
 * Created by ferhat.ozcelik on 12-02-2023.
 */

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date {
        return value?.let { Date(it) } ?: Date(System.currentTimeMillis())
    }

    @TypeConverter
    fun toTimestamp(value: Date?): Long {
        return value?.let { value.time } ?: System.currentTimeMillis()
    }
}