package com.project.trackernity.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.project.trackernity.data.model.TrackernityResponseSecondItem

@Database(
    entities = [TrackernityResponseSecondItem::class],
    version = 1
)
abstract class TrackingDatabase:RoomDatabase() {
    abstract fun getTrackingDao():TrackingDAO
}