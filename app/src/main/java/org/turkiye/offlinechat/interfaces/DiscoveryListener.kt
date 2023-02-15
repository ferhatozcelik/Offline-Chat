package org.turkiye.offlinechat.interfaces

import org.turkiye.offlinechat.data.entity.User

/**
 * Created by ferhat.ozcelik on 12-02-2023.
 */

interface DiscoveryListener {
    fun serviceDiscovered(device: User)
}