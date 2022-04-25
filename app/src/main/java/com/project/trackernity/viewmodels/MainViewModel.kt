package com.project.trackernity.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.gson.annotations.SerializedName
import com.project.trackernity.data.model.TrackernityRequestSecond
import com.project.trackernity.data.model.TrackernityRequestThird
import com.project.trackernity.repositories.*
import com.project.trackernity.ui.fragments.data.Result
import com.project.trackernity.ui.fragments.ui.signup.DropdownItemsAuthResult
import com.project.trackernity.util.DispatcherProvider
import com.project.trackernity.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor (
    private val repository: ListRepository,
    private val repositorySecond: ListSecondRepository,
    private val repositoryThird: ListThirdRepository,
    private val defaultRepository: DefaultMainRepository,
    private val dropdownItemsRepository: DropdownItemsRepository,
    private val dispatchers: DispatcherProvider
):ViewModel(){
    private val _mViewState = MutableLiveData<MainViewState>()
    val viewstate: LiveData<MainViewState>
        get() = _mViewState
    val _latLngTriggered = DefaultMainRepository.latLngTriggered
    val latLngTriggered:LiveData<LatLng>
        get() = _latLngTriggered
//    val _polylineTriggered = DefaultMainRepository.polyLineTriggered
//    val polylineTriggered:LiveData<Polyline>
//        get() = _polylineTriggered
    val userId = MutableLiveData("")
    //////////// should be mutable in repository class, this class(viewmodel) just for observed////////////////////
    val tregAlpro = MutableLiveData("")
    val witelAlpro = MutableLiveData("")
    val remarkAlpro = MutableLiveData("")
    val routeAlpro = MutableLiveData("")
    val latLng = MutableLiveData<LatLng>()
    val remarksFrom = MutableLiveData("")
    val remarksTo = MutableLiveData("")
    val remarks = MutableLiveData("")
    val remarks2 = MutableLiveData("")
    val remarks2List = MutableLiveData<List<String>>()
    val remarksHeadList = MutableLiveData<List<String>>()
    val remarksAllList = MutableLiveData<List<String>>()
    val descriptions = MutableLiveData("")
    val notes = MutableLiveData("")
    val jarakGangguan = MutableLiveData("")
    val idPerkiraan = MutableLiveData(0)
    val userDataRequest = MutableLiveData<TrackernityRequestSecondUserdata>()

    data class TrackernityRequestSecondUserdata(
        @SerializedName("regional")
        val regional: String?,
        @SerializedName("witel")
        val witel: String?,
        @SerializedName("unit")
        val unit: String?,
    )
//    val trackernityRequestSecond = MutableLiveData<TrackernityRequestSecond?>()

    private val _mViewStateSecond =  MutableLiveData<MainViewStateSecond>()

    val viewStateSecond: LiveData<MainViewStateSecond>
        get() = _mViewStateSecond

    private val _dropdownItemsTrackingResult = MutableLiveData<DropdownItemsTrackingResult>()
    val dropdownItemsTrackingResult: LiveData<DropdownItemsTrackingResult>
        get() = _dropdownItemsTrackingResult

    private val _dropdownItemsOthersResult = MutableLiveData<DropdownItemsOthersResult>()
    val dropdownItemsOthersResult: LiveData<DropdownItemsOthersResult>
        get() = _dropdownItemsOthersResult

    private val _orderQuery = DropdownItemsRepository.orderQuery

    /////if no interaction to request or request value is fixed not dynamic///////
//    init {
//        //sendData()
//    }

//    sealed class ListViewState {
//        class Success(val result: MutableList<TrackernityResponseSecondItem>,val successtext: String): ListViewState()
//        class Failure(val errorText: String): ListViewState()
//        object Loading : ListViewState()
//        object Empty : ListViewState()
//    }

    fun getDropdownItemsTracking(){
        _dropdownItemsTrackingResult.value = DropdownItemsTrackingResult(loading = true)
        viewModelScope.launch(dispatchers.io) {
            val result = dropdownItemsRepository.getDropdownItemsTracking()
            Timber.d("Result API: ${result}")

            if (result is Result.Success) {
                val tregList = result.data.dropdownItem!!.treg!!
                val witelList = result.data.dropdownItem.witel!!
                val headList = result.data.dropdownItem.remarks!!.head!!
                val tailList = result.data.dropdownItem.remarks.tail!!
                val allList = result.data.dropdownItem.remarks.all!!
                _dropdownItemsTrackingResult.postValue(
                    DropdownItemsTrackingResult(
                        loading = false,
                        error = null,
                        treg = tregList,
                        witel = witelList,
                        head = headList,
                        tail = tailList,
                        all = allList,
                    )
                )
            }else if(result is Result.Error){
                val erroMsg = result.exception.message
                _dropdownItemsTrackingResult.postValue(DropdownItemsTrackingResult(loading = false,error = erroMsg))
            } else {
                _dropdownItemsTrackingResult.postValue(DropdownItemsTrackingResult(loading = false,error = "Something is Wrong!"))
            }
        }
        Timber.d("dropdown item tracking result API: ${_dropdownItemsTrackingResult.value}")
    }

    fun getDropdownItemsTreg(){
        _dropdownItemsTrackingResult.value = DropdownItemsTrackingResult(loading = true)
        viewModelScope.launch(dispatchers.io) {
            val result = dropdownItemsRepository.getDropdownItemsTregs()
            Timber.d("Result API: ${result}")

            if (result is Result.Success) {
                val tregList = result.data.dropdownItems
                _dropdownItemsTrackingResult.postValue(
                    DropdownItemsTrackingResult(
                        loading = false,
                        error = null,
                        label = "TREGS",
                        treg = tregList,
                    )
                )
            }else if(result is Result.Error){
                val erroMsg = result.exception.message
                _dropdownItemsTrackingResult.postValue(DropdownItemsTrackingResult(loading = false,error = erroMsg))
            } else {
                _dropdownItemsTrackingResult.postValue(DropdownItemsTrackingResult(loading = false,error = "Something is Wrong!"))
            }
        }
        Timber.d("dropdown item tracking result API: ${_dropdownItemsTrackingResult.value}")
    }

    fun getDropdownItemsWitel(treg: String){
        _dropdownItemsTrackingResult.value = DropdownItemsTrackingResult(loading = true)
        viewModelScope.launch(dispatchers.io) {
            val result = dropdownItemsRepository.getDropdownItemsWitels(treg)
            Timber.d("Result API: ${result}")

            if (result is Result.Success) {
                val witelList = result.data.dropdownItems
                _dropdownItemsTrackingResult.postValue(
                    DropdownItemsTrackingResult(
                        loading = false,
                        error = null,
                        label = "WITELS",
                        witel = witelList,
                    )
                )
            }else if(result is Result.Error){
                val erroMsg = result.exception.message
                _dropdownItemsTrackingResult.postValue(DropdownItemsTrackingResult(loading = false,error = erroMsg))
            } else {
                _dropdownItemsTrackingResult.postValue(DropdownItemsTrackingResult(loading = false,error = "Something is Wrong!"))
            }
        }
        Timber.d("dropdown item tracking result API: ${_dropdownItemsTrackingResult.value}")
    }

    fun getDropdownItemsRemarks(treg: String, witel: String){
        _dropdownItemsTrackingResult.value = DropdownItemsTrackingResult(loading = true)
        viewModelScope.launch(dispatchers.io) {
            val result = dropdownItemsRepository.getDropdownItemsRemarks(treg,witel)
            Timber.d("Result API: ${result}")

            if (result is Result.Success) {
                val headList = result.data.dropdownItems.head!!
                val tailList = result.data.dropdownItems.tail!!
                val allList = result.data.dropdownItems.all!!
                remarksHeadList.postValue(headList)
                remarksAllList.postValue(allList)
                _dropdownItemsTrackingResult.postValue(
                    DropdownItemsTrackingResult(
                        loading = false,
                        error = null,
                        label = "REMARKS",
                        head = headList,
                        tail = tailList,
                        all = allList,
                    )
                )
            }else if(result is Result.Error){
                val erroMsg = result.exception.message
                _dropdownItemsTrackingResult.postValue(DropdownItemsTrackingResult(loading = false,error = erroMsg))
            } else {
                _dropdownItemsTrackingResult.postValue(DropdownItemsTrackingResult(loading = false,error = "Something is Wrong!"))
            }
        }
        Timber.d("dropdown item tracking result API: ${_dropdownItemsTrackingResult.value}")
    }

    fun getDropdownItemsRoutes(treg: String,witel: String,remarks: String){
        _dropdownItemsTrackingResult.value = DropdownItemsTrackingResult(loading = true)
        viewModelScope.launch(dispatchers.io) {
            val result = dropdownItemsRepository.getDropdownItemsRoutes(treg,witel,remarks)
            Timber.d("Result API: ${result}")

            if (result is Result.Success) {
                val routeList = result.data.dropdownItems
                remarks2List.postValue(routeList)
                _dropdownItemsTrackingResult.postValue(
                    DropdownItemsTrackingResult(
                        loading = false,
                        error = null,
                        label = "ROUTES",
                        route = routeList,
                    )
                )
            }else if(result is Result.Error){
                val erroMsg = result.exception.message
                _dropdownItemsTrackingResult.postValue(DropdownItemsTrackingResult(loading = false,error = erroMsg))
            } else {
                _dropdownItemsTrackingResult.postValue(DropdownItemsTrackingResult(loading = false,error = "Something is Wrong!"))
            }
        }
        Timber.d("dropdown item tracking result API: ${_dropdownItemsTrackingResult.value}")
    }


    fun getDropdownItemsOthers(){
        _dropdownItemsOthersResult.value = DropdownItemsOthersResult(loading = true)
        viewModelScope.launch(dispatchers.io) {
            val result = dropdownItemsRepository.getDropdownItemsOthers()
            Timber.d("Result API: ${result}")

            if (result is Result.Success) {
                val notesList = result.data.dropdownItemsOthersItems.notes
                val descList = result.data.dropdownItemsOthersItems.descriptions
                _dropdownItemsOthersResult.postValue(
                    DropdownItemsOthersResult(
                        loading = false,
                        error = null,
                        notesItems = notesList,
                        descItems = descList
                    )
                )
            }else if(result is Result.Error){
                val erroMsg = result.exception.message
                _dropdownItemsOthersResult.postValue(DropdownItemsOthersResult(loading = false,error = erroMsg))
            } else {
                _dropdownItemsOthersResult.postValue(DropdownItemsOthersResult(loading = false,error = "Something is Wrong!"))
            }
        }
        Timber.d("dropdown item others result API: ${_dropdownItemsOthersResult.value}")
    }

    fun getData(param:String) {
        _mViewState.value = MainViewState(loading = true)
        viewModelScope.launch(dispatchers.io) {
            try {
                val data = repository.getData(param)
                Timber.d("Data ViewModel: ${data}")
                _mViewState.postValue(_mViewState.value?.copy(loading = false,
                    error = null,
                    data = data))
            } catch (ex: Exception) {
                Timber.d("Error ViewModel: ${ex}")
                _mViewState.postValue(_mViewState.value?.copy(loading = false,
                    error = ex,
                    data = null))
            }
        }
    }

    fun getData(param:String,param2: String,param3: String,param4: String) {
        _mViewState.value = MainViewState(loading = true)
        viewModelScope.launch(dispatchers.io) {
            try {
                val data = repository.getData(param,param2,param3,param4)
                Timber.d("Data ViewModel: ${data}")
                _mViewState.postValue(_mViewState.value?.copy(loading = false,
                    error = null,
                    data = data))
            } catch (ex: Exception) {
                Timber.d("Error ViewModel: ${ex}")
                _mViewState.postValue(_mViewState.value?.copy(loading = false,
                    error = ex,
                    data = null))
            }
        }
    }

    fun getDataSecond(param:String) {
        _mViewState.value = MainViewState(loading = true)
        viewModelScope.launch(dispatchers.io) {
            try {
                val data = repositorySecond.getDataSecond(param)
                Timber.d("Data ViewModel: ${data}")
                _mViewState.postValue(_mViewState.value?.copy(loading = false,
                    error = null,
                    data = data))
            } catch (ex: Exception) {
                Timber.d("Error ViewModel: ${ex}")
                _mViewState.postValue(_mViewState.value?.copy(loading = false,
                    error = ex,
                    data = null))
            }
        }
    }

    fun getDataThird(param:String) {
        _mViewState.value = MainViewState(loading = true)
        viewModelScope.launch(dispatchers.io) {
            try {
                val data = repositoryThird.getDataThird(param)
                Timber.d("Data ViewModel: ${data}")
                _mViewState.postValue(_mViewState.value?.copy(loading = false,
                    error = null,
                    data = data))
            } catch (ex: Exception) {
                Timber.d("Error ViewModel: ${ex}")
                _mViewState.postValue(_mViewState.value?.copy(loading = false,
                    error = ex,
                    data = null))
            }
        }
    }

    fun sendingDataSecond(){

        remarks.value = "${remarksFrom.value}-${remarksTo.value}"

        val trackernityRequestData = TrackernityRequestSecond(
            regional = userDataRequest.value!!.regional,
            witel = userDataRequest.value!!.witel,
            unit = userDataRequest.value!!.unit,
            userId = userId.value,
            lat = latLng.value!!.latitude,
            lgt = latLng.value!!.longitude,
            remarks = remarks.value,
            remarks2 = remarks2.value,
            continuity = "",
            descriptions = descriptions.value,
            notes = notes.value,
            idPerkiraan = idPerkiraan.value
        )

        _mViewStateSecond.value = MainViewStateSecond(loading = true)
            viewModelScope.launch(dispatchers.io) {
                try {
                    when (val sendResponse = defaultRepository.sendDataSecond(trackernityRequestData)) {
                        is Resource.Error -> {
                            _mViewStateSecond.postValue(_mViewStateSecond.value?.copy(loading = false,error = sendResponse.message,data = null))
                            Timber.d("Error Sending Data!!!")
                        }
                        is Resource.Success -> {
                            Timber.d("Success Sending Data!!!")
                            val status = sendResponse.data!!.status
                            Timber.d("Status : ${status}")
                            _mViewStateSecond.postValue(_mViewStateSecond.value?.copy(loading = false,error = null,data = sendResponse.data))
                        }
                    }
                } catch (ex: java.lang.Exception) {
                    Timber.d("Error Sending Data: ${ex}")
                    _mViewStateSecond.postValue(_mViewStateSecond.value?.copy(loading = false,error = ex.toString(),data = null))
                }
            }
    }

    fun sendingDataThird(){

        if(remarksHeadList.value!!.contains(remarksFrom.value)){
            _orderQuery.value = "ASC"
            remarks.value = "${remarksFrom.value}-${remarksTo.value}"
        }else{
            _orderQuery.value = "DESC"
            remarks.value = "${remarksTo.value}-${remarksFrom.value}"
        }

        val realRemarks = "${remarksFrom.value}-${remarksTo.value}"

        val trackernityRequestData = TrackernityRequestThird(
            regional = userDataRequest.value!!.regional,
            witel = userDataRequest.value!!.witel,
            unit = userDataRequest.value!!.unit,
            userId = userId.value,
            remarks = remarks.value,
            remarks2 = remarks2.value,
            descriptions = descriptions.value,
            notes = notes.value,
            perkiraanJarakGangguan = jarakGangguan.value,
            orderQuery = _orderQuery.value,
            realRemarks = realRemarks
        )

        _mViewStateSecond.value = MainViewStateSecond(loading = true)
        viewModelScope.launch(dispatchers.io) {
            try {
                when (val sendResponse = defaultRepository.sendDataThird(trackernityRequestData)) {
                    is Resource.Error -> {
                        _mViewStateSecond.postValue(_mViewStateSecond.value?.copy(loading = false,error = sendResponse.message,data = null))
                        Timber.d("Error Sending Data!!!")
                    }
                    is Resource.Success -> {
                        Timber.d("Success Sending Data!!!")
                        val status = sendResponse.data!!.status
                        Timber.d("Status : ${status}")
                        _mViewStateSecond.postValue(_mViewStateSecond.value?.copy(loading = false,error = null,data = sendResponse.data))
                    }
                }
            } catch (ex: java.lang.Exception) {
                Timber.d("Error Sending Data: ${ex}")
                _mViewStateSecond.postValue(_mViewStateSecond.value?.copy(loading = false,error = ex.toString(),data = null))
            }
        }
    }

}