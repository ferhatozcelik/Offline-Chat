package org.turkiye.offlinechat.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.parcelize.Parcelize
import org.turkiye.offlinechat.data.local.Converters
import java.util.*

/**
 * Created by ferhat.ozcelik on 12-02-2023.
 */

@Parcelize
@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    var userId: Int? = null,
    val userName: String?,
    val userHost: String?,
    @TypeConverters(Converters::class) val userWordCreateAt: Long = System.currentTimeMillis()
) : Parcelable