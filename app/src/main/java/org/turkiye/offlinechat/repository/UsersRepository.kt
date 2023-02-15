package org.turkiye.offlinechat.repository

import org.turkiye.offlinechat.data.entity.User
import org.turkiye.offlinechat.data.local.UsersDao
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by ferhat.ozcelik on 12-02-2023.
 */

@Singleton
class UsersRepository @Inject constructor(
    private val deviceDao: UsersDao
) {

    fun getListDevice() = deviceDao.getDevice()

    suspend fun deleteListDevice() = deviceDao.deleteAll()

    suspend fun deviceInsert(device: User) = deviceDao.insert(device)

}