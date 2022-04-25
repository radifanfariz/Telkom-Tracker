package com.project.trackernity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.Marker
import com.project.trackernity.data.model.TrackernityResponseSecondItem
import com.project.trackernity.databinding.ItemTracking3Binding
import com.project.trackernity.viewmodels.MainViewModel

class TrackernityThirdAdapter:RecyclerView.Adapter<TrackernityThirdAdapter.ViewHolder>() {

    private val trackernityData = mutableListOf<TrackernityResponseSecondItem>()
    private val markerData = mutableListOf<Marker>()
    private lateinit var mainViewModel: MainViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(trackernityData[position],markerData[position],mainViewModel)
    }

    override fun getItemCount(): Int = trackernityData.size


    fun updateData(newData:MutableList<TrackernityResponseSecondItem>, newMarkerData: MutableList<Marker>,viewModel: MainViewModel){
        trackernityData.clear()
        trackernityData.addAll(newData)
        markerData.clear()
        markerData.addAll(newMarkerData)
        mainViewModel = viewModel
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemTracking3Binding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item1:TrackernityResponseSecondItem,item2:Marker, item3: MainViewModel){
            binding.itemTrackernity = item1
            binding.marker = item2
            binding.viewModel = item3
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup):ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemTracking3Binding.inflate(layoutInflater,parent,false)
                return ViewHolder(binding)
            }
        }

    }
}