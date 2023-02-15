package org.turkiye.offlinechat.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import org.turkiye.offlinechat.data.entity.User

/**
 * Created by ferhat.ozcelik on 12-02-2023.
 */

@Dao
interface UsersDao {

    @Query("SELECT * FROM user_table ORDER BY userWordCreateAt DESC")
    fun getDevice(): LiveData<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deviceInfo: User)

    @Update
    suspend fun update(deviceInfo: User)

    @Delete
    suspend fun delete(deviceInfo: User)


    @Query("DELETE FROM user_table")
    suspend fun deleteAll()

}