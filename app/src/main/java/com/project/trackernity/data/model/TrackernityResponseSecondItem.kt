package com.project.trackernity.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "tracking_table")
data class TrackernityResponseSecondItem(
    var continuity: String?,
    @SerializedName("perkiraan_jarak_gangguan")
    var jarakGangguan: Double?,
    @SerializedName("id_perkiraan")
    var id_perkiraan: Int?,
    @SerializedName("remarks2")
    var remarks2: String?,
    @SerializedName("flagging_ggn_selesai")
    var flaggingGgn: Int?,
    var notes: String?,
    var date_time: String?,
    var descriptions: String?,
    var lat: Double?,
    var lgt: Double?,
    @ColumnInfo(name = "remarks") var remarks: String?,
    var user_id: String?
){
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null
    data class TrackernityAllDataResponse(
        var trackernityData: MutableList<TrackernityResponseSecondItem>?
    )
}