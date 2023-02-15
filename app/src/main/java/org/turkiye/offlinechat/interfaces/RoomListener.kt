package org.turkiye.offlinechat.interfaces

import org.turkiye.offlinechat.data.entity.User

/**
 * Created by ferhat.ozcelik on 12-02-2023.
 */

interface RoomListener {
    fun onCreate(device: User)
}