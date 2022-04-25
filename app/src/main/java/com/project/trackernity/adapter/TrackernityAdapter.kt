package com.project.trackernity.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.Marker
import com.project.trackernity.data.model.TrackernityResponseSecondItem
import com.project.trackernity.databinding.ItemTrackingBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackernityAdapter:RecyclerView.Adapter<TrackernityAdapter.ViewHolder>() {

    private val trackernityData = mutableListOf<TrackernityResponseSecondItem>()
    private val markerData = mutableListOf<Marker>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(trackernityData[position],markerData[position])
    }

    override fun getItemCount(): Int = trackernityData.size


    @SuppressLint("NotifyDataSetChanged")
    suspend fun updateData(newData:MutableList<TrackernityResponseSecondItem>, newMarkerData: MutableList<Marker>){
        trackernityData.clear()
        trackernityData.addAll(newData)
        markerData.clear()
        markerData.addAll(newMarkerData)
        withContext(Dispatchers.Main) {
            notifyDataSetChanged()
        }
    }

    class ViewHolder(val binding: ItemTrackingBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item1:TrackernityResponseSecondItem,item2:Marker){
            binding.itemTrackernity = item1
            binding.marker = item2
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup):ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemTrackingBinding.inflate(layoutInflater,parent,false)
                return ViewHolder(binding)
            }
        }

    }
}