package com.project.trackernity.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.project.trackernity.data.model.TrackernityResponseSecond
import com.project.trackernity.data.model.TrackernityResponseSecondItem

@Dao
interface TrackingDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracking(vararg tracking:TrackernityResponseSecondItem)

    @Delete
    suspend fun deleteTracking(tracking: TrackernityResponseSecondItem)

    @Query("SELECT * FROM tracking_table WHERE remarks LIKE :remarksParam ")
    fun getAllTrackingsFilteredByRemarks(remarksParam:String):MutableList<TrackernityResponseSecondItem>

    @Query("SELECT * FROM tracking_table WHERE :param IN (user_id,date_time,continuity,remarks) ")
    fun getAllTrackingsByParam(param:String):MutableList<TrackernityResponseSecondItem>
}