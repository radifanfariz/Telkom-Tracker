package com.project.trackernity.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.textfield.TextInputEditText
import com.project.trackernity.R
import com.project.trackernity.data.model.TrackernityResponseSecondItem
import com.project.trackernity.repositories.DefaultMainRepository
import com.project.trackernity.repositories.TrackingRepository
import com.project.trackernity.viewmodels.MainViewModel
import timber.log.Timber

@BindingAdapter("btnToggleText")
fun setBtnToggleText(button:Button, isTracking:Boolean){
    button.apply {
        if (isTracking) {
            text = context.getString(R.string.stop)
        }
        else {
            text = context.getString(R.string.start)
        }
    }

}

@BindingAdapter("setUserId")
fun TextView.setUserId(trackernityItem:TrackernityResponseSecondItem){
    text = trackernityItem.user_id
}

@BindingAdapter("setRemarks")
fun TextView.setRemarks(trackernityItem: TrackernityResponseSecondItem){
    text = trackernityItem.remarks
}

@BindingAdapter("setRemarks2")
fun TextView.setRemarks2(trackernityItem: TrackernityResponseSecondItem){
    text = trackernityItem.remarks2
}

@BindingAdapter("setRemarks2Special")
fun TextView.setRemarks2Special(trackernityItem: TrackernityResponseSecondItem){
    text = trackernityItem.descriptions
}

@BindingAdapter("setIdData")
fun TextView.setIdData(trackernityItem: TrackernityResponseSecondItem){
    text = trackernityItem.id.toString()
}

@BindingAdapter("setDescriptions")
fun TextView.setDescriptions(trackernityItem: TrackernityResponseSecondItem){
    text = trackernityItem.descriptions
}

@BindingAdapter("setLat")
fun TextView.setLat(trackernityItem: TrackernityResponseSecondItem){
    text = trackernityItem.lat.toString()
}
@BindingAdapter("setLng")
fun TextView.setLng(trackernityItem: TrackernityResponseSecondItem){
    text = trackernityItem.lgt.toString()
}

@BindingAdapter("setNotes")
fun TextView.setNotes(trackernityItem: TrackernityResponseSecondItem){
    text = trackernityItem.notes.toString()
}

@BindingAdapter("setJarakGangguan")
fun TextView.setJarakGangguan(trackernityItem: TrackernityResponseSecondItem){
    text = trackernityItem.jarakGangguan.toString()
}

@BindingAdapter(value = ["bind:setCardViewListener","bind:marker"], requireAll = false)
fun CardView.setCardViewListener(trackernityItem: TrackernityResponseSecondItem,marker: Marker){
    setOnClickListener(View.OnClickListener {
        val latLng = LatLng(trackernityItem.lat!!, trackernityItem.lgt!!)
        DefaultMainRepository.latLngTriggered.postValue(latLng)
        marker.showInfoWindow()
    })
}

@BindingAdapter(value = ["bind:setCardViewListenerSpecial","bind:marker","bind:setViewModelField"], requireAll = false)
fun CardView.setCardViewListenerSpecial(trackernityItem: TrackernityResponseSecondItem,marker: Marker,viewModel: MainViewModel){
    setOnClickListener(View.OnClickListener {
        viewModel.idPerkiraan.postValue(trackernityItem.id_perkiraan)
        val latLng = LatLng(trackernityItem.lat!!, trackernityItem.lgt!!)
        DefaultMainRepository.latLngTriggered.postValue(latLng)
        marker.showInfoWindow()
    })
}

/////////////////////custom interactive dialog//////////////////

@BindingAdapter("getTregForAlpro")
fun AutoCompleteTextView.getTregForAlpro(mainViewModel:MainViewModel){
    setText(mainViewModel.tregAlpro.value)
    addTextChangedListener(object : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(typed: CharSequence?, p1: Int, p2: Int, p3: Int) {
            mainViewModel.tregAlpro.value = typed.toString()

        }

        override fun afterTextChanged(p0: Editable?) {

        }
    })
}

@BindingAdapter("getWitelForAlpro")
fun AutoCompleteTextView.getWitelForAlpro(mainViewModel:MainViewModel){
    setText(mainViewModel.witelAlpro.value)
    addTextChangedListener(object : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(typed: CharSequence?, p1: Int, p2: Int, p3: Int) {
            mainViewModel.witelAlpro.value = typed.toString()
        }

        override fun afterTextChanged(p0: Editable?) {

        }
    })
}

@BindingAdapter("getUserId")
fun TextInputEditText.getUserId(mainViewModel:MainViewModel){
    mainViewModel.userId.value = TrackingRepository.userData.value!!.userId
    setText(mainViewModel.userId.value.toString())
}

@BindingAdapter("setIdPerkiraan")
fun TextInputEditText.setIdPerkiraan(mainViewModel: MainViewModel){
    setText(mainViewModel.idPerkiraan.value.toString())
}

@BindingAdapter("getRemarksFrom")
fun AutoCompleteTextView.getRemarksFrom(mainViewModel:MainViewModel){
    setText(mainViewModel.remarksFrom.value,false)
    addTextChangedListener(object : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(typed: CharSequence?, p1: Int, p2: Int, p3: Int) {
            mainViewModel.remarksFrom.value = typed.toString()
        }

        override fun afterTextChanged(p0: Editable?) {

        }
    })
}

@BindingAdapter("getRemarksTo")
fun AutoCompleteTextView.getRemarksTo(mainViewModel:MainViewModel){
    setText(mainViewModel.remarksTo.value,false)
    addTextChangedListener(object : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(typed: CharSequence?, p1: Int, p2: Int, p3: Int) {
            mainViewModel.remarksTo.value = typed.toString()
        }

        override fun afterTextChanged(p0: Editable?) {

        }
    })
}

@BindingAdapter("getRemarks2")
fun AutoCompleteTextView.getRemarks2(mainViewModel:MainViewModel){
    setText(mainViewModel.remarks2.value,false)
    addTextChangedListener(object : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(typed: CharSequence?, p1: Int, p2: Int, p3: Int) {
            mainViewModel.remarks2.value = typed.toString()
        }

        override fun afterTextChanged(p0: Editable?) {

        }
    })
}

@BindingAdapter("getDesc")
fun AutoCompleteTextView.getDesc(mainViewModel:MainViewModel){
    setText(mainViewModel.descriptions.value,false)
    addTextChangedListener(object : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(typed: CharSequence?, p1: Int, p2: Int, p3: Int) {
            mainViewModel.descriptions.value = typed.toString()
        }

        override fun afterTextChanged(p0: Editable?) {

        }
    })
}

@BindingAdapter("getNotes")
fun AutoCompleteTextView.getNotes(mainViewModel:MainViewModel){
    setText(mainViewModel.notes.value,false)
    addTextChangedListener(object : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(typed: CharSequence?, p1: Int, p2: Int, p3: Int) {
            mainViewModel.notes.value = typed.toString()
        }

        override fun afterTextChanged(p0: Editable?) {

        }
    })
}

@BindingAdapter("getJarakGangguan")
fun TextInputEditText.getJarakGangguan(mainViewModel:MainViewModel){
    setText(mainViewModel.jarakGangguan.value)
    addTextChangedListener(object : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(typed: CharSequence?, p1: Int, p2: Int, p3: Int) {
            mainViewModel.jarakGangguan.value = typed.toString()
        }

        override fun afterTextChanged(p0: Editable?) {

        }
    })

    ////////////for dropdown////////////////////////
    ///////////////underdevelop, still not working//////////////////
//    @BindingAdapter("chooseTreg")
//    fun AutoCompleteTextView.chooseTreg(mainViewModel:MainViewModel){
//        addTextChangedListener(object : TextWatcher{
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(typed: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                mainViewModel.tregAlpro.value = typed.toString()
//                mainViewModel.getDropdownItemsWitel(typed.toString())
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//
//            }
//        })
//    }
//
//    @BindingAdapter("chooseWitel")
//    fun AutoCompleteTextView.chooseWitel(mainViewModel:MainViewModel){
//        addTextChangedListener(object : TextWatcher{
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(typed: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                mainViewModel.witelAlpro.value = typed.toString()
//                mainViewModel.getDropdownItemsRemarks(mainViewModel.tregAlpro.value!!,typed.toString())
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//
//            }
//        })
//    }
//
//    @BindingAdapter("chooseRemarkFrom")
//    fun AutoCompleteTextView.chooseRemarkFrom(mainViewModel:MainViewModel){
//        addTextChangedListener(object : TextWatcher{
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(typed: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                mainViewModel.remarksFrom.value = typed.toString()
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//
//            }
//        })
//    }
//
//    @BindingAdapter("chooseRemarkTo")
//    fun AutoCompleteTextView.chooseRemarkTo(mainViewModel:MainViewModel){
//        addTextChangedListener(object : TextWatcher{
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(typed: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                mainViewModel.remarksTo.value = typed.toString()
//                if (mainViewModel.dropdownItemsTrackingResult.value!!.head!!.contains(mainViewModel.remarksFrom.value)) {
//                    mainViewModel.remarkAlpro.value = "${mainViewModel.remarksFrom.value}-${mainViewModel.remarksTo.value}"
//                } else {
//                    mainViewModel.remarkAlpro.value = "${mainViewModel.remarksTo.value}-${mainViewModel.remarksFrom.value}"
//                }
//                mainViewModel.getDropdownItemsRoutes(
//                    mainViewModel.tregAlpro.value!!,
//                    mainViewModel.witelAlpro.value!!,
//                    mainViewModel.remarkAlpro.value!!
//                )
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//
//            }
//        })
//    }
//
//    @BindingAdapter("chooseRoute")
//    fun AutoCompleteTextView.chooseRoute(mainViewModel:MainViewModel){
//        addTextChangedListener(object : TextWatcher{
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(typed: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                mainViewModel.routeAlpro.value = typed.toString()
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//
//            }
//        })
//    }
}