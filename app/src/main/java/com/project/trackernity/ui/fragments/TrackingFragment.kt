package com.project.trackernity.ui.fragments

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.project.trackernity.R
import com.project.trackernity.adapter.TrackernityAdapter
import com.project.trackernity.adapter.TrackernitySecondAdapter
import com.project.trackernity.adapter.TrackernityThirdAdapter
import com.project.trackernity.data.model.TrackernityResponseSecondItem
import com.project.trackernity.databinding.FragmentTrackingBinding
import com.project.trackernity.other.Constants
import com.project.trackernity.other.Constants.DEFAULT_ZOOM
import com.project.trackernity.other.Constants.KEY_CAMERA_POSITION
import com.project.trackernity.other.Constants.KEY_LOCATION
import com.project.trackernity.other.Constants.LOGOUT_DIALOG_TAG
import com.project.trackernity.other.Constants.MAP_ZOOM
import com.project.trackernity.other.Constants.POLYLINE_WIDTH
import com.project.trackernity.other.Constants.START_INTERACTIVE_DIALOG_TAG
import com.project.trackernity.other.Constants.START_TRACKING_DIALOG_TAG
import com.project.trackernity.other.Constants.STOP_TRACKING_DIALOG_TAG
import com.project.trackernity.other.Constants.TOKEN_TRACKING_DIALOG
import com.project.trackernity.other.MapLifecycleObserver
import com.project.trackernity.other.TrackingUtility
import com.project.trackernity.repositories.Polylines
import com.project.trackernity.repositories.TrackingRepository
import com.project.trackernity.repositories.TrackingRepository.Companion.pathPoints
import com.project.trackernity.ui.fragments.ui.login.LoginResultToNavigate
import com.project.trackernity.ui.fragments.ui.login.LoginViewModel
import com.project.trackernity.util.AnimationUtils
import com.project.trackernity.util.MapUtils
import com.project.trackernity.viewmodels.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.android.synthetic.main.layout_bottom_sheet.*
import kotlinx.coroutines.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.util.*
import javax.annotation.meta.When
import javax.inject.Inject


@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking), EasyPermissions.PermissionCallbacks,
    GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLongClickListener {

    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!

    private val trackingViewModel: TrackingViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    private var mapView: MapView? = null
    private var map: GoogleMap? = null
    private lateinit var mapLifecycleObserver: MapLifecycleObserver

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private var currentOffsetBottomsheet = 0f;

    private val TAG: String = TrackingFragment::class.java.getSimpleName()

    @Inject
    lateinit var locationRequest: LocationRequest

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    //////Location//////////
    private var locationPermissionGranted = false
    private val defaultLocation = LatLng(3.597031, 98.678513)
    private var lastKnownLocation: Location? = null

    ////////////////////Maps Component Property///////////////
    private var originMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private var grayPolyline: Polyline? = null
    private var blackPolyline: Polyline? = null
    private var movingCabMarker: Marker? = null
    private var previousLatLng: LatLng? = null
    private var currentLatLng: LatLng? = null
    private var isTrackingEnd: Boolean = false

    /////////Request Param//////////////
    private var paramRequest: String? = "route-1"

    ////////////RecyclerView Adapter//////
    private lateinit var trackernityAdapter: TrackernityAdapter
    private lateinit var trackernitySecondAdapter: TrackernitySecondAdapter
    private lateinit var trackernityThirdAdapter: TrackernityThirdAdapter

    /////////////current general marker///////////////
    private var currentGeneralMarker: MutableList<Marker>? = null
    private var alproGeneralMarker: MutableList<Marker>? = null
    private lateinit var alproGeneralPolyline: Polyline
//    private var currentGeneralMarkerSecond:MutableList<Marker>? = null
//    private var currentGeneralMarkerThird:MutableList<Marker>? = null

    /////////////////dropdown array adapter/////////////
    private lateinit var remarkFromArrayAdapter: ArrayAdapter<String>
    private lateinit var remarkToArrayAdapter: ArrayAdapter<String>

    /////////////////to determine order query/////////////////
    private lateinit var remarkCheckArray: List<String?>

    ////////SafeArgs Variable//////////
//    private val args:TrackingFragmentArgs by navArgs<TrackingFragmentArgs>()

    //////onResult Fragment////////
    val resolutionForResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                isGpsActive = true
                Snackbar.make(
                    requireActivity().findViewById(R.id.rootView),
                    getString(R.string.gps_enabled),
                    Snackbar.LENGTH_LONG
                ).show()
            }
            if (result.resultCode == RESULT_CANCELED) {
                isGpsActive = false
                Snackbar.make(
                    requireActivity().findViewById(R.id.rootView),
                    getString(R.string.gps_not_enabled),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

    ///////////isGpsActive/////////////
    private var isGpsActive = false;

    ///////marker when long click maps////////
    private var longClickMarker: Marker? = null

    //////////chip variable/////////////
    private var chipTag: String = "CHIP_1"

    ///////////////////Apps Lifecycle Behavior///////////////////////
    ///////////////////Apps Lifecycle Behavior///////////////////////
    ///////////////////Apps Lifecycle Behavior///////////////////////

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        val navController = findNavController()
//
//        val currentBackStackEntry = navController.currentBackStackEntry!!
//        val savedStateHandle = currentBackStackEntry.savedStateHandle
//        savedStateHandle.getLiveData<Boolean>(LoginFragment.LOGIN_SUCCESSFUL)
//            .observe(currentBackStackEntry, { success ->
//                if (!success){
//                    val startDestination = navController.graph.startDestinationId
//                    val navOptions = NavOptions.Builder()
//                        .setPopUpTo(startDestination, true)
//                        .build()
//                    navController.navigate(startDestination, null, navOptions)
//                }
//            })
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentTrackingBinding.inflate(inflater, container, false)

        mapView = binding.mapView

        mapLifecycleObserver = MapLifecycleObserver(mapView, lifecycle)

        TrackingRepository.userDataFromSharedPreference.value = getUserDataSharedPreference()
        val userData = loginViewModel.userDataSharedPreferences.value!!
        mainViewModel.userDataRequest.value = MainViewModel.TrackernityRequestSecondUserdata(
            regional = userData.regional,
            witel = userData.witel,
            unit = userData.unit
        )

        Timber.d("Test userData: ${TrackingRepository.userDataFromSharedPreference.value!!.nama},${TrackingRepository.userDataFromSharedPreference.value!!.regional}, ${TrackingRepository.userDataFromSharedPreference.value!!.witel}, " +
                "${TrackingRepository.userDataFromSharedPreference.value!!.unit}, ${TrackingRepository.userDataFromSharedPreference.value!!.c_profile}, ${TrackingRepository.userDataFromSharedPreference.value!!.userId}")

        subscribeToObservers()


        binding.apply {
            lifecycleOwner = this@TrackingFragment
            contentIncluded.viewModel = trackingViewModel
            contentIncluded.viewModel2 = mainViewModel
            //viewModel = trackingViewModel

        }

//        mainViewModel.getDropdownItemsTracking()
        mainViewModel.getDropdownItemsTreg()
        mainViewModel.getDropdownItemsOthers()

        //////not used//////
//        TrackingService.viewModelForService = trackingViewModel

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        TrackingUtility.requestPermissions(this)

        val bottomSheet = binding.contentIncluded.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_SETTLING

        setupInitFragment()
//        getData()

        /////viewListener
        bottomSheetFunc(bottomSheetBehavior)
        dropDownItemFunc()
        searchBarFunc()
        searchInputBtnFunc()
        sendDataBtnFunc()
        sendDataPredictionBtnFunc()
        btnLogoutFunc()
        chipListenerFunc()
        refreshBtnFunc()
        refreshBtnFuncOther()

        if (savedInstanceState != null) {
            val stopTrackingDialog = parentFragmentManager.findFragmentByTag(
                STOP_TRACKING_DIALOG_TAG
            ) as CustomDialog?
            stopTrackingDialog?.setPositiveButtonListener {
                trackingViewModel.setStopCommandService(requireContext())
            }
        }

        mapView?.let { mapView ->
            mapView.onCreate(savedInstanceState)
            mapView.getMapAsync {
                map = it
                mapStyle()
                showtLocationOnMap(defaultLocation)
                mapsDefault()
                addAllPolylines()
                map!!.setInfoWindowAdapter(CustomInfoWindow(requireContext()))
                map!!.setOnInfoWindowClickListener(this)
                map!!.setOnMapLongClickListener(this)
                map!!.setOnCameraMoveStartedListener {
                    bottomSheetBehavior.setPeekHeight(250, true)
                }
            }
        }
    }

//    override fun onStart() {
//        super.onStart()
//        if (trackingViewModel.isTracking.value!! || trackingViewModel.currentTimeInMillis.value!! > 0L) {
//            motionLayout.transitionToEnd()
//        }
//    }

    override fun onSaveInstanceState(outState: Bundle) {
        map?.let { map ->
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        }
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        mapView?.onDestroy()
//    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    ///////////ViewModel Behavior///////////////////////
    ///////////ViewModel Behavior///////////////////////
    ///////////ViewModel Behavior///////////////////////

    ////////////////deprecated not using observe to viewmodel/////////////
//    private fun checkUserData(){
//        if(trackingViewModel.userData.value != null){
//            Toast.makeText(context,"Back to UI",Toast.LENGTH_SHORT).show()
//        }else{
//            val args:TrackingFragmentArgs by navArgs()
//            val userData = args.userData
//            trackingViewModel.userData.value = userData
//        }
//    }

    private fun setupRecyclerView() {
        binding.contentIncluded.recyclerView.apply {
            trackernityAdapter = TrackernityAdapter()
            adapter = trackernityAdapter
            layoutManager = LinearLayoutManager(requireContext())
//            ItemTouchHelper(SwipeToDeleteCallback()).attachToRecyclerView(this)
        }
    }

    private fun setupRecyclerViewSecond() {
        binding.contentIncluded.recyclerView.apply {
            trackernitySecondAdapter = TrackernitySecondAdapter()
            adapter = trackernitySecondAdapter
            layoutManager = LinearLayoutManager(requireContext())
//            ItemTouchHelper(SwipeToDeleteCallback()).attachToRecyclerView(this)
        }
    }

    private fun setupRecyclerViewThird() {
        binding.contentIncluded.recyclerView.apply {
            trackernityThirdAdapter = TrackernityThirdAdapter()
            adapter = trackernityThirdAdapter
            layoutManager = LinearLayoutManager(requireContext())
//            ItemTouchHelper(SwipeToDeleteCallback()).attachToRecyclerView(this)
        }
    }


    private fun setupInitFragment() {
        try {
            if (TrackingRepository.userData.value!!.flagging == "f-0101") {
                binding.sendDataBtn.isEnabled = true
                binding.sendDataPredictionBtn.isEnabled = true
            }
        } catch (ex: Exception) {
            binding.sendDataBtn.isEnabled = false
            binding.sendDataPredictionBtn.isEnabled = false
            Toast.makeText(requireContext(), "Something went wrong !", Toast.LENGTH_LONG).show()
        }
    }

    private fun getUserDataSharedPreference() : LoginResultToNavigate{
        val sharedPreference =  requireContext().getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        return LoginResultToNavigate(
            nama = sharedPreference.getString("nama","")!!,
            regional = sharedPreference.getString("regional","")!!,
            witel = sharedPreference.getString("witel","")!!,
            unit = sharedPreference.getString("unit","")!!,
            c_profile = sharedPreference.getString("c_profile","")!!,
            userId = sharedPreference.getString("userId","")!!
        )
    }

    private fun removeUserDataSharedPreference(){
        val sharedPreference =  requireContext().getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.clear()
    }

    private fun subscribeToObservers() {

        mainViewModel.apply {
            dropdownItemsTrackingResult.observe(viewLifecycleOwner) {
                handleStateDropdown(it)
            }
            dropdownItemsOthersResult.observe(viewLifecycleOwner) {
                handleStateDropdownOthers(it)
            }
        }

        loginViewModel.userData.observe(viewLifecycleOwner) {
            if (it == null) {
                findNavController().navigate(TrackingFragmentDirections.actionTrackingFragmentToLoginFragment())
            }
        }

        trackingViewModel.pathPoints.observe(viewLifecycleOwner) {
//            trackingViewModel.sendingData(it)
//            addLatestPolyline()
            showPath()
            updateCarLocation(it)
            moveCameraToUser()
        }
        trackingViewModel.isTracking.observe(viewLifecycleOwner) {
            btnCheckTracking(it)
        }

        mainViewModel.apply {
            viewstate.observe(viewLifecycleOwner, {
                handleState(it)
            })

            latLngTriggered.observe(viewLifecycleOwner, {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                showtLocationOnMap(it)
            })

            viewStateSecond.observe(viewLifecycleOwner, {
                sendLocationDataResponse(it)
                if (chipTag == "CHIP_3") {
                    if (currentGeneralMarker != null) {
                        removeGeneralMarker(currentGeneralMarker!!)
                    }
                    getDataThird()
                }
            })

        }

        trackingViewModel.errorTrackingMsg.observe(viewLifecycleOwner, {
            if (it != "") {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                trackingViewModel.setStopCommandService(requireContext())
            }
        })

    }

    ///////////////////Add on Behavior (Dialog, etc)/////////////////
    ///////////////////Add on Behavior (Dialog, etc)/////////////////
    ///////////////////Add on Behavior (Dialog, etc)/////////////////
    private fun handleStateDropdownOthers(items: DropdownItemsOthersResult?) {
        try {
            items?.let {
                toggleLoadingDropdownOthers(it.loading)
                showSuccessDropdownOthers(it.notesItems!!, it.descItems!!)
                it.error?.let { error -> showErrorDropdownOthers(error) }
            }
        } catch (ex: Exception) {
            Timber.d("error Handle: ${ex}")
        }
    }

    private fun toggleLoadingDropdownOthers(loadingState: Boolean) {
        binding.apply {
//            errorImage.visibility = View.GONE
            if (loadingState) {
                linearLayoutLoading.visibility = View.VISIBLE
                linearLayoutBtn.visibility = View.GONE
            } else {
                linearLayoutLoading.visibility = View.GONE
            }
        }
    }

    private fun showSuccessDropdownOthers(data: List<String?>, data2: List<String?>) {
        if (data.isNotEmpty() && data2.isNotEmpty()) {
            binding.linearLayoutBtn.visibility = View.VISIBLE
        }
    }

    private fun showErrorDropdownOthers(errorMsg: String) {
        if (errorMsg.isNotEmpty()) {
            binding.apply {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
                linearLayoutBtnRefresh.visibility = View.VISIBLE
                linearLayoutLoading.visibility = View.GONE
                linearLayoutBtn.visibility = View.GONE
            }
        }
    }

    private fun refreshBtnFuncOther() {
        binding.refreshBtn.setOnClickListener() {
            Timber.d("test click !!!")
            mainViewModel.getDropdownItemsOthers()
            binding.linearLayoutBtnRefresh.visibility = View.GONE
        }
    }


    private fun handleStateDropdown(items: DropdownItemsTrackingResult?) {
        try {
            items?.let {
                toggleLoadingDropdown(it.loading)
                setDropdownItemGeneral(it)
                it.error?.let { error -> showErrorDropdown(error) }
            }
        } catch (ex: Exception) {
            Timber.d("error Handle: ${ex}")
        }
    }

    private fun setDropdownItemSpecial(remarkFrom: List<String?>, remarkTo: List<String?>) {
        if (remarkFrom.isNotEmpty() && remarkTo.isNotEmpty()) {
            binding.contentIncluded.apply {
                lldropdown.visibility = View.VISIBLE
                lldropdown2.visibility = View.GONE
                lldropdown3.visibility = View.GONE
            }
            val remarks = remarkFrom.plus(remarkTo)
            remarkCheckArray = remarkFrom

            val tregList = mainViewModel.dropdownItemsTrackingResult.value!!.treg
            val witelList = mainViewModel.dropdownItemsTrackingResult.value!!.witel
//        Timber.d("what is inside : ${dropdownList}")

            val tregDropdownArrayAdapter =
                ArrayAdapter(requireContext(), R.layout.dropdown_item, tregList!!)
            binding.contentIncluded.tregDropdown.setAdapter(tregDropdownArrayAdapter)

            val witelDropdownArrayAdapter =
                ArrayAdapter(requireContext(), R.layout.dropdown_item, witelList!!)
            binding.contentIncluded.witelDropdown.setAdapter(witelDropdownArrayAdapter)

            remarkFromArrayAdapter =
                ArrayAdapter(requireContext(), R.layout.dropdown_item, remarks)
            binding.contentIncluded.headDropdown.setAdapter(remarkFromArrayAdapter)

            remarkToArrayAdapter =
                ArrayAdapter(requireContext(), R.layout.dropdown_item, remarks)
            binding.contentIncluded.tailDropdown.setAdapter(remarkToArrayAdapter)

        }
    }

    private fun setDropdownItemGeneral(dropdownItemsTrackingResult: DropdownItemsTrackingResult) {
        if (!dropdownItemsTrackingResult.loading) {
            binding.contentIncluded.apply {
                lldropdown.visibility = View.VISIBLE
                lldropdownOthers.visibility = View.VISIBLE
                lldropdownOthers2.visibility = View.VISIBLE
                searchInputBtn.visibility = View.VISIBLE
                lldropdown2.visibility = View.GONE
                lldropdown3.visibility = View.GONE
            }

            when (dropdownItemsTrackingResult.label) {
                "TREGS" -> {
                    val dropdownList = dropdownItemsTrackingResult.treg
                    val tregsDropdownArrayAdapter =
                        ArrayAdapter(requireContext(), R.layout.dropdown_item, dropdownList!!)
                    binding.contentIncluded.tregDropdown.setAdapter(tregsDropdownArrayAdapter)
                }
                "WITELS" -> {
                    val dropdownList = dropdownItemsTrackingResult.witel
                    val witelsDropdownArrayAdapter =
                        ArrayAdapter(requireContext(), R.layout.dropdown_item, dropdownList!!)
                    binding.contentIncluded.witelDropdown.setAdapter(witelsDropdownArrayAdapter)
                }
                "REMARKS" -> {
                    val dropdownList = dropdownItemsTrackingResult.all
                    val remarkFromArrayAdapter =
                        ArrayAdapter(requireContext(), R.layout.dropdown_item, dropdownList!!)
                    binding.contentIncluded.headDropdown.setAdapter(remarkFromArrayAdapter)

                    val remarkToArrayAdapter =
                        ArrayAdapter(requireContext(), R.layout.dropdown_item, dropdownList!!)
                    binding.contentIncluded.tailDropdown.setAdapter(remarkToArrayAdapter)
                }
                "ROUTES" -> {
                    val dropdownList = dropdownItemsTrackingResult.route
                    val routesDropdownArrayAdapter =
                        ArrayAdapter(requireContext(), R.layout.dropdown_item, dropdownList!!)
                    binding.contentIncluded.routeDropdown.setAdapter(routesDropdownArrayAdapter)
                }
            }

        }
    }

    private fun toggleLoadingDropdown(loadingState: Boolean) {
        binding.contentIncluded.apply {
//            errorImage.visibility = View.GONE
            if (loadingState) {
                lldropdown3.visibility = View.VISIBLE
                lldropdown.visibility = View.GONE
                lldropdown2.visibility = View.GONE
                lldropdownOthers.visibility = View.GONE
                lldropdownOthers2.visibility = View.GONE
                searchInputBtn.visibility = View.GONE
            } else {
                lldropdown3.visibility = View.GONE
            }
        }
    }

    private fun showErrorDropdown(errorMsg: String) {
        if (errorMsg.isNotEmpty()) {
            binding.contentIncluded.apply {
                errorTxt.text = errorMsg
                lldropdown2.visibility = View.VISIBLE
                lldropdown.visibility = View.GONE
                lldropdown3.visibility = View.GONE
                lldropdownOthers.visibility = View.GONE
                lldropdownOthers2.visibility = View.GONE
                searchInputBtn.visibility = View.GONE
            }
        }
    }

    private fun refreshBtnFunc() {
        binding.contentIncluded.refreshBtn.setOnClickListener() {
            Timber.d("test click !!!")
            mainViewModel.getDropdownItemsTreg()
        }
    }


    private fun sendLocationData() {
        mainViewModel.sendingDataSecond()
    }

    private fun sendLocationDataPrediction() {
        mainViewModel.sendingDataThird()
    }

    private fun sendLocationDataResponse(viewState: MainViewStateSecond?) {
        try {
            viewState?.let {
//                toggleLoadingForSendData(it.loading)
                if (it.data != null) {
                    val status = it.data.status
                    val code = it.data.code
                    if (code == "200")
                        Toast.makeText(requireContext(), "${status}", Toast.LENGTH_LONG).show()
                    else
                        Toast.makeText(
                            requireContext(),
                            "Failed something happened in the server !!!",
                            Toast.LENGTH_LONG
                        ).show()
                }
                if (it.error != null) {
                    val error = it.error
                    Toast.makeText(requireContext(), "Error : ${error}", Toast.LENGTH_LONG).show()
                }
            }
        } catch (ex: Exception) {
            Timber.d("Error Handle: ${ex}")
        }
    }


    private fun getData() {
        try {
            setupRecyclerView()
//            val headDropdown = binding.contentIncluded.headDropdown
//            val tailDropdown = binding.contentIncluded.tailDropdown
//            val tregDropdown = binding.contentIncluded.tregDropdown
//            val witelDropdown = binding.contentIncluded.witelDropdown
//            val routeDropdown = binding.contentIncluded.routeDropdown
//            if (mainViewModel.dropdownItemsTrackingResult.value!!.head!!.contains(headDropdown.text.toString())) {
//                paramRequest = "${headDropdown.text}-${tailDropdown.text}"
//            } else {
//                paramRequest = "${tailDropdown.text}-${headDropdown.text}"
//            }
//
//            val paramTreg = tregDropdown.text.toString()
//            val paramWitel = witelDropdown.text.toString()
//            val paramRemark = paramRequest
//            val paramRoute = routeDropdown.text.toString()

            val paramTreg = mainViewModel.tregAlpro.value!!
            val paramWitel = mainViewModel.witelAlpro.value!!
            val paramRemark = mainViewModel.remarkAlpro.value!!
            val paramRoute = mainViewModel.routeAlpro.value!!

            Timber.d("Test Param: ${paramTreg},${paramWitel},${paramRemark},${paramRoute}")

            mainViewModel.getData(
                paramTreg,
                paramWitel,
                paramRemark,
                paramRoute
            )
        } catch (ex: Exception) {
            Timber.d("Error: ${ex}")
            Toast.makeText(requireContext(), "Error: ${ex}", Toast.LENGTH_LONG)
        }
    }

    private fun getDataSecond() {
        try {
            setupRecyclerViewSecond()
            mainViewModel.getDataSecond(paramRequest.toString())
        } catch (ex: Exception) {
            Timber.d("Error: ${ex}")
            Toast.makeText(requireContext(), "Error: ${ex}", Toast.LENGTH_LONG)
        }
    }

    private fun getDataThird() {
        try {
            setupRecyclerViewThird()
            mainViewModel.getDataThird(paramRequest.toString())
        } catch (ex: Exception) {
            Timber.d("Error: ${ex}")
            Toast.makeText(requireContext(), "Error: ${ex}", Toast.LENGTH_LONG)
        }
    }

    private fun handleState(viewState: MainViewState?) {
        try {
            viewState?.let {
                toggleLoading(it.loading)
                it.data?.let { data -> showData(data) }
                it.error?.let { error -> showError(error) }
            }
        } catch (ex: Exception) {
            Timber.d("error Handle: ${ex}")
        }
    }


    private fun toggleLoading(loading: Boolean) {
        binding.contentIncluded.apply {
            errorImage.visibility = View.GONE
            if (loading) {
                loadingIndicator.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                loadingIndicator.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }

//        binding.contentIncluded.apply {
//            errorImage.visibility = View.GONE
//        }
//        if(loading)
//           binding.contentIncluded.apply {
//               loadingIndicator.visibility = View.VISIBLE
//               recyclerView.visibility = View.GONE
//           }
//        else
//            binding.contentIncluded.apply {
//                loadingIndicator.visibility = View.GONE
//                recyclerView.visibility = View.VISIBLE
//            }


    }

    private fun showData(data: MutableList<TrackernityResponseSecondItem>) {
        try {
            binding.contentIncluded.apply {
                errorImage.visibility = View.GONE
                if (chipTag == "CHIP_1") {
                    if (alproGeneralMarker != null) {
                        removeGeneralMarker(alproGeneralMarker!!)
                    }
                    if (::alproGeneralPolyline.isInitialized) {
                        removeGeneralPolylines(alproGeneralPolyline)
                    }
                    val bitmapDescriptor =
                        BitmapDescriptorFactory.fromBitmap(
                            MapUtils.getPointMarkerBitmap(
                                requireContext()
                            )
                        )
                    CoroutineScope(Dispatchers.IO).launch {
                        alproGeneralMarker = addGeneralMarker(data, bitmapDescriptor)
                        alproGeneralPolyline = addGeneralPolylines(alproGeneralMarker)
                        try {
                            if (mainViewModel.remarksHeadList.value!!.contains(
                                    headDropdown.text.toString()
                                )
                            ) {
                                trackernityAdapter.updateData(data, alproGeneralMarker!!)
                            } else {
                                trackernityAdapter.updateData(
                                    data.asReversed(),
                                    alproGeneralMarker!!
                                )
                            }
                        } catch (ex: Exception) {
                            Toast.makeText(
                                requireContext(),
                                "Something went wrong !",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }

                } else if (chipTag == "CHIP_2") {
                    if (currentGeneralMarker != null) {
                        removeGeneralMarker(currentGeneralMarker!!)
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        currentGeneralMarker = addGeneralMarkerSecond(
                            data,
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
                        )
                        trackernitySecondAdapter.updateData(data, currentGeneralMarker!!)
                    }


                } else if (chipTag == "CHIP_3") {
                    if (currentGeneralMarker != null) {
                        removeGeneralMarker(currentGeneralMarker!!)
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        currentGeneralMarker = addGeneralMarker(
                            data,
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                        )
                        trackernityThirdAdapter.updateData(data, currentGeneralMarker!!,mainViewModel)
                    }


                } else {
                    Toast.makeText(requireContext(), "Something went wrong !", Toast.LENGTH_LONG)
                        .show()
                }
                recyclerView.visibility = View.VISIBLE
            }
        }catch (ex: Exception){
            Toast.makeText(requireContext(), "Something went wrong !", Toast.LENGTH_LONG).show()
        }

        //////legacy code/////////////////
//        trackernityAdapter.updateData(data,addGeneralMarker(data))
//        binding.contentIncluded.apply {
//            errorImage.visibility = View.GONE
//            recyclerView.visibility = View.VISIBLE
//        }
    }


    private fun showError(error: Exception) {
        binding.contentIncluded.apply {
            errorImage.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
    }

    private fun btnCheckTracking(isTracking: Boolean) {
        binding.contentIncluded.btnService.setOnClickListener {
            if (!isTracking) {
                checkGps()
                if (isGpsActive) {
                    showStartTrackingDialog()
                    if (trackingViewModel.token.value != "") {
                        showTokenTrackingDialog()
                    }
                } else {
                    Toast.makeText(requireContext(), "GPS is Not Active !", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                showStopTrackingDialog()
            }
        }
    }

    private fun showOtherAlproParamDialog() {
        CustomInteractiveDialog3(mainViewModel).apply {
            setPositiveButtonListener {
                try {
                    getData()
                } catch (ex: Exception) {
                    Toast.makeText(requireContext(), "Error: ${ex}", Toast.LENGTH_SHORT).show()
                }
            }
        }.show(parentFragmentManager, START_INTERACTIVE_DIALOG_TAG)
    }

    private fun showSendLoactionDialog() {
        if (longClickMarker != null) {
            CustomInteractiveDialog(mainViewModel).apply {
                setPositiveButtonListener {
                    try {
                        sendLocationData()
                    } catch (ex: Exception) {
                        Toast.makeText(requireContext(), "Error: ${ex}", Toast.LENGTH_SHORT).show()
                    }
                }
            }.show(parentFragmentManager, START_INTERACTIVE_DIALOG_TAG)
        } else {
            Toast.makeText(requireContext(), "Choose marker location first !!!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun showSendLoactionPredictionDialog() {
        CustomInteractiveDialog2(mainViewModel).apply {
            setPositiveButtonListener {
                try {
                    sendLocationDataPrediction()
                } catch (ex: Exception) {
                    Toast.makeText(requireContext(), "Error: ${ex}", Toast.LENGTH_SHORT).show()
                }
            }
        }.show(parentFragmentManager, START_INTERACTIVE_DIALOG_TAG)
    }


    private fun showStartTrackingDialog() {
        CustomDialog().apply {
            setPositiveButtonListener {
                try {
                    if (trackingViewModel.token.value == "") {
                        generateTokenForTracking()
                    }
                    isTrackingEnd = false
                    trackingViewModel.setStartCommandService(requireContext())
//                    if (lastKnownLocation != null && movingCabMarker != null)
//                    movingCabMarker = addCarMarkerAndGet(LatLng(lastKnownLocation!!.latitude,lastKnownLocation!!.longitude))
                    Toast.makeText(
                        requireContext(),
                        "Location Tracking Started !",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (ex: Exception) {
                    Toast.makeText(requireContext(), "Error: ${ex}", Toast.LENGTH_SHORT).show()
                }
            }
        }.show(parentFragmentManager, START_TRACKING_DIALOG_TAG)
    }

    private fun generateTokenForTracking() {
        val uniqueID = UUID.randomUUID().toString()
        trackingViewModel.token.value = uniqueID
    }

    private fun showTokenTrackingDialog() {
        CustomDialog().apply {
            setTitleDialog("Resume The Tracking?")
            setMessageDialog("Do you want to resume your previous tracking ?")
            setNegativeButtonListener {
                generateTokenForTracking()
            }
        }.show(parentFragmentManager, TOKEN_TRACKING_DIALOG)
    }

    private fun showStopTrackingDialog() {
        CustomDialog().apply {
            setTitleDialog("Stop The Tracking?")
            setMessageDialog("Are you sure to stop the tracking?")
            setPositiveButtonListener {
                try {
                    isTrackingEnd = true
                    trackingViewModel.setStopCommandService(requireContext())
//                    if (movingCabMarker != null) deleteCarMarker(movingCabMarker!!)
                    Toast.makeText(
                        requireContext(),
                        "Location Tracking Stopped !",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (ex: Exception) {
                    Toast.makeText(requireContext(), "Error: ${ex}", Toast.LENGTH_SHORT).show()
                }
            }
        }.show(parentFragmentManager, STOP_TRACKING_DIALOG_TAG)
    }

    private fun bottomSheetFunc(bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>) {

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // handle onSlide
                if (currentOffsetBottomsheet < slideOffset) {
                    currentOffsetBottomsheet = slideOffset
                    Timber.d("slide: Up ${currentOffsetBottomsheet}")
                }
                if (currentOffsetBottomsheet > slideOffset) {
                    currentOffsetBottomsheet = slideOffset
                    Timber.d("slide: Down ${currentOffsetBottomsheet}")
                }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.contentIncluded.constraintLayout.apply {
                        visibility = View.VISIBLE
                        animate().translationY(0f)
                    }
                    binding.contentIncluded.coordinatorLayout.apply {
                        translationY = 60f.dp()
                    }
                    binding.myLocationBtn.apply {
                        visibility = View.VISIBLE
                        animate().alpha(1f)
                    }
                    binding.sendDataBtn.apply {
                        visibility = View.VISIBLE
                        animate().alpha(1f)
                    }
                    binding.sendDataPredictionBtn.apply {
                        visibility = View.VISIBLE
                        animate().alpha(1f)
                    }
                } else if (newState == BottomSheetBehavior.STATE_SETTLING) {
                    binding.contentIncluded.constraintLayout.apply {
                        animate().translationY(height.toFloat())
                        visibility = View.GONE
                    }
                    binding.contentIncluded.coordinatorLayout.apply {
                        translationY = 0f
                    }
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.myLocationBtn.apply {
                        animate().alpha(0f)
                        visibility = View.INVISIBLE
                    }
                    binding.sendDataBtn.apply {
                        animate().alpha(0f)
                        visibility = View.INVISIBLE
                    }
                    binding.sendDataPredictionBtn.apply {
                        animate().alpha(0f)
                        visibility = View.INVISIBLE
                    }

                } else if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.setPeekHeight(800, false)
                } else {
                    Timber.d("else: ${newState}")
                }
            }
        })
    }


    ///////////legacy component///////////////////
//    private fun searchBarFunc (){
//        binding.contentIncluded.searchBarInput.addTextChangedListener(object : TextWatcher{
//            override fun beforeTextChanged(txt: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                Timber.d("before: ${txt}")
//            }
//
//            override fun onTextChanged(txt: CharSequence?, p1: Int, p2: Int, p3: Int) {
////                mainViewModel.getData(txt.toString())
//                paramRequest = txt.toString()
//                Timber.d("On: ${txt} + ${paramRequest}")
//            }
//
//            override fun afterTextChanged(editable: Editable?) {
//                Timber.d("after: ${editable}")
//            }
//        })
//    }
    /////////dropdown field listener/////////////////////
    private fun dropDownItemFunc() {
        binding.contentIncluded.apply {
            val emptyList = emptyList<String>()
            val emptyDropdownArrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, emptyList)
            tregDropdown.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(typed: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (witelDropdown.text.isNotEmpty() || headDropdown.text.isNotEmpty()
                        || tailDropdown.text.isNotEmpty() || routeDropdown.text.isNotEmpty()
                    ) {
                        witelDropdown.text.clear()
                        witelDropdown.setAdapter(emptyDropdownArrayAdapter)
                        headDropdown.text.clear()
                        headDropdown.setAdapter(emptyDropdownArrayAdapter)
                        tailDropdown.text.clear()
                        tailDropdown.setAdapter(emptyDropdownArrayAdapter)
                        routeDropdown.text.clear()
                        routeDropdown.setAdapter(emptyDropdownArrayAdapter)
                    }
                    mainViewModel.tregAlpro.value = typed.toString()
                    mainViewModel.getDropdownItemsWitel(typed.toString())
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })

            witelDropdown.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(typed: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (headDropdown.text.isNotEmpty()
                        || tailDropdown.text.isNotEmpty() || routeDropdown.text.isNotEmpty()
                    ) {
                        headDropdown.text.clear()
                        headDropdown.setAdapter(emptyDropdownArrayAdapter)
                        tailDropdown.text.clear()
                        tailDropdown.setAdapter(emptyDropdownArrayAdapter)
                        routeDropdown.text.clear()
                        routeDropdown.setAdapter(emptyDropdownArrayAdapter)
                    }
                    mainViewModel.witelAlpro.value = typed.toString()
                    mainViewModel.getDropdownItemsRemarks(
                        mainViewModel.tregAlpro.value!!,
                        mainViewModel.witelAlpro.value!!
                    )
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })


            headDropdown.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(typed: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (routeDropdown.text.isNotEmpty()) {
                        routeDropdown.text.clear()
                        routeDropdown.setAdapter(emptyDropdownArrayAdapter)
                    }
                    mainViewModel.remarksFrom.value = typed.toString()
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })

            tailDropdown.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(typed: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (routeDropdown.text.isNotEmpty()) {
                        routeDropdown.text.clear()
                        routeDropdown.setAdapter(emptyDropdownArrayAdapter)
                    }
                    mainViewModel.remarksTo.value = typed.toString()
                    if(chipTag == "CHIP_1") {
                        try {

                            if (mainViewModel.remarksHeadList.value!!.contains(
                                    mainViewModel.remarksFrom.value
                                )
                            ) {
                                mainViewModel.remarkAlpro.value =
                                    "${mainViewModel.remarksFrom.value}-${mainViewModel.remarksTo.value}"
                            } else {
                                mainViewModel.remarkAlpro.value =
                                    "${mainViewModel.remarksTo.value}-${mainViewModel.remarksFrom.value}"
                            }
                        } catch (ex: Exception) {
                            mainViewModel.remarkAlpro.value =
                                "${mainViewModel.remarksTo.value}-${mainViewModel.remarksFrom.value}"
                        }
                        mainViewModel.getDropdownItemsRoutes(
                            mainViewModel.tregAlpro.value!!,
                            mainViewModel.witelAlpro.value!!,
                            mainViewModel.remarkAlpro.value!!
                        )
                    }
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })

            routeDropdown.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(typed: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    mainViewModel.routeAlpro.value = typed.toString()

                    ///////for custom dialog/////////
                    mainViewModel.remarks2.value = typed.toString()
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
        }
    }

    private fun searchBarFunc() {
        val headDropdown = binding.contentIncluded.headDropdown
        val tailDropdown = binding.contentIncluded.tailDropdown
        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(txt: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Timber.d("before: ${txt}")
            }

            override fun onTextChanged(txt: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Timber.d("On: ${txt} + ${paramRequest}")
            }

            override fun afterTextChanged(editable: Editable?) {
                Timber.d("after: ${editable}")
                paramRequest = "${headDropdown.text}-${tailDropdown.text}"
            }
        }
        headDropdown.addTextChangedListener(afterTextChangedListener)
        tailDropdown.addTextChangedListener(afterTextChangedListener)
    }

    private fun searchInputBtnFunc() {
        binding.contentIncluded.searchInputBtn.apply {
            setOnClickListener { view ->
                Timber.d("Test Click: ${view}")
                if (paramRequest != null) {
                    binding.contentIncluded.apply {
                        if (chipTag == "CHIP_1") {
                            getData()
//                            Toast.makeText(requireContext(),"ChipTag: ${chipTag}",Toast.LENGTH_LONG).show()
                        } else if (chipTag == "CHIP_2") {
                            getDataSecond()
                        } else if (chipTag == "CHIP_3") {
                            getDataThird()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Something went wrong !",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun myLocationBtnFunc() {
        binding.myLocationBtn.apply {
            setOnClickListener {
                checkGps()
                getDeviceLocation()
            }
        }
    }

    private fun sendDataBtnFunc() {
        binding.sendDataBtn.apply {
            setOnClickListener {
                showSendLoactionDialog()
            }
        }
    }

    private fun sendDataPredictionBtnFunc() {
        binding.sendDataPredictionBtn.apply {
            setOnClickListener {
                showSendLoactionPredictionDialog()
            }
        }
    }

    private fun chipListenerFunc() {
//        val selectedChips = chipGroup.children
//            .filter { (it as Chip).isChecked }
//            .map { (it as Chip).text.toString() }
        binding.contentIncluded.chipGroup.setOnCheckedChangeListener { _, checkedId ->
            binding.contentIncluded.apply {
                if (chip1.id == checkedId) {
                    chipTag = "CHIP_1"
                    binding.contentIncluded.lldropdownOthers.visibility = View.VISIBLE
                    binding.contentIncluded.lldropdownOthers2.visibility = View.VISIBLE
//                    Toast.makeText(requireContext(),"ChipTag: ${chipTag}",Toast.LENGTH_LONG).show()
                } else if (chip2.id == checkedId) {
                    chipTag = "CHIP_2"
                    binding.contentIncluded.lldropdownOthers.visibility = View.GONE
                    binding.contentIncluded.lldropdownOthers2.visibility = View.GONE
                } else {
                    chipTag = "CHIP_3"
                    binding.contentIncluded.lldropdownOthers.visibility = View.GONE
                    binding.contentIncluded.lldropdownOthers2.visibility = View.GONE
//                    Toast.makeText(requireContext(),"ChipTag: ${chipTag}",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

//    private fun showCancelTrackingDialog() {
//        CancelTrackingDialog().apply {
//            setPositiveButtonListener {
//                trackingViewModel.setCancelCommand(requireContext())
//                motionLayout.transitionToStart()
//            }
//        }.show(parentFragmentManager, CANCEL_TRACKING_DIALOG_TAG)
//    }

    //////////////////////Maps Method/Behavior////////////////////////////
    //////////////////////Maps Method/Behavior////////////////////////////
    //////////////////////Maps Method/Behavior////////////////////////////

    private fun mapsDefault() {
        map?.apply {
            uiSettings.apply {
                setAllGesturesEnabled(true)
                setIndoorEnabled(true)
                isCompassEnabled = true
                isIndoorLevelPickerEnabled = true
            }
            isTrafficEnabled = true
            isBuildingsEnabled = true
            isIndoorEnabled = true
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                TrackingUtility.requestPermissions(this@TrackingFragment)
                return
            } else {
                locationPermissionGranted = true
            }
//            updateLocationUI()
            checkGps()
            getDeviceLocation()
            myLocationBtnFunc()
//            setOnMyLocationButtonClickListener (GoogleMap.OnMyLocationButtonClickListener {
//                checkGps()
//                fusedLocationProviderClient.lastLocation
//                true
//            })

        }
    }

    private fun checkGps() {

        val builder = LocationSettingsRequest.Builder().apply {
            addLocationRequest(locationRequest)
            setAlwaysShow(true)
        }

        val locationSettingResponseTask = LocationServices.getSettingsClient(requireContext())
            .checkLocationSettings(builder.build())

        locationSettingResponseTask.addOnCompleteListener() {
            try {
                val response =
                    it.getResult(ApiException::class.java) /// to call the dialog for enabling the gps
//                Snackbar.make(
//                    requireActivity().findViewById(R.id.rootView),
//                    getString(R.string.gps_info),
//                    Snackbar.LENGTH_LONG
//                ).show()
//                getDeviceLocation()
                isGpsActive = true
            } catch (e: ApiException) {
                if (e.statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    val resolvableApiException = e as ResolvableApiException
                    try {
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(resolvableApiException.resolution).build()
                        resolutionForResult.launch(intentSenderRequest)

                    } catch (sendIntentException: IntentSender.SendIntentException) {
                        sendIntentException.printStackTrace()
                        isGpsActive = false
                    }
                }
                if (e.statusCode == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                    isGpsActive = false
                    Snackbar.make(
                        requireActivity().findViewById(R.id.rootView),
                        getString(R.string.gps_setting_info),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

        }

    }


    private fun mapStyle() {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success: Boolean? = map?.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.style_json
                )
            )
            if (!success!!) {
                Timber.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Timber.e(TAG, "Can't find style. Error: ", e)
        }
    }

    private fun moveCameraToUser() {
        if (pathPoints.value!!.isNotEmpty() && pathPoints.value!!.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(pathPoints.value!!.last().last(), MAP_ZOOM)
            )
        }
    }

//    private fun addLatestPolyline() {
//        if (pathPoints.value!!.isNotEmpty() && pathPoints.value!!.last().size > 1) {
//            val preLastLatLng = pathPoints.value!!.last()[pathPoints.value!!.last().size - 2]
//            val lastLatLng = pathPoints.value!!.last().last()
//            val polylineOptions = PolylineOptions()
//                    .color(POLYLINE_COLOR)
//                    .width(POLYLINE_WIDTH)
//                    .add(preLastLatLng)
//                    .add(lastLatLng)
//
//            map?.addPolyline(polylineOptions)
//        }
//    }

    private fun addAllPolylines() {
        pathPoints.value?.forEach {
            val polylineOptions = PolylineOptions()
                .color(Color.BLACK)
                .width(POLYLINE_WIDTH)
                .addAll(it)

            map?.addPolyline(polylineOptions)
        }
    }


//    private fun zoomToSeeWholeTrack() {
//        val bounds = LatLngBounds.Builder()
//        for (polyline in pathPoints.value!!) {
//            for (pos in polyline) {
//                bounds.include(pos)
//            }
//        }
//
//        val latLngBounds = try {
//            bounds.build()
//        } catch (e: IllegalStateException) {
//            Timber.e(e, "Cannot find any path points, associated with this run")
//            return
//        }
//
//        map?.moveCamera(
//                CameraUpdateFactory.newLatLngBounds(
//                        latLngBounds,
//                        mapView!!.width,
//                        mapView!!.height,
//                        (mapView!!.height * 0.05f).toInt()
//                )
//        )
//    }


    ////////////Map Components Behavior//////////////
    ////////////Map Components Behavior//////////////
    ////////////Map Components Behavior//////////////

//    private fun updateLocationUI() {
//        if (map == null) {
//            return
//        }
//        try {
//            if (locationPermissionGranted) {
//                map?.isMyLocationEnabled = true
//                map?.uiSettings?.isMyLocationButtonEnabled = true
//            } else {
//                map?.isMyLocationEnabled = false
//                map?.uiSettings?.isMyLocationButtonEnabled = false
//                lastKnownLocation = null
//                TrackingUtility.requestPermissions(this)
//            }
//        } catch (e: SecurityException) {
//            Timber.e("Exception: %s", e.message, e)
//        }
//    }

    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            val lastLatLng = LatLng(
                                lastKnownLocation!!.latitude,
                                lastKnownLocation!!.longitude
                            )
                            map?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    lastLatLng, DEFAULT_ZOOM.toFloat()
                                )
                            )
                            animateCamera(lastLatLng)
                            updateCarLocation(lastLatLng)
                        }
                    } else {
                        Timber.d(TAG, "Current location is null. Using defaults.")
                        Timber.e(TAG, "Exception: %s", task.exception)
                        map?.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                        )
                        animateCamera(defaultLocation)
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Timber.e("Exception: %s", e.message, e)
        }
    }
    // [END maps_current_place_get_device_location]


    private fun showtLocationOnMap(latLng: LatLng) {
        moveCamera(latLng)
        animateCamera(latLng)
    }

    /////////PolyLines Customize///////////////
    private fun showPath() {
        if (pathPoints.value!!.isNotEmpty() && pathPoints.value!!.last().size > 1) {
            val preLastLatLng = pathPoints.value!!.last()[pathPoints.value!!.last().size - 2]
            val lastLatLng = pathPoints.value!!.last().last()
            val originLastLng = pathPoints.value!!.last()[0]

//            val builder = LatLngBounds.Builder()
//            builder.apply {
//                include(preLastLatLng)
//                include(lastLatLng)
//            }
//            val bounds = builder.build()
//            map?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2))

            val polylineOptions = PolylineOptions()
            polylineOptions.color(Color.GRAY)
            polylineOptions.width(10f)
            grayPolyline = map?.addPolyline(polylineOptions)

            val blackPolylineOptions = PolylineOptions()
            blackPolylineOptions.color(Color.BLACK)
            blackPolylineOptions.width(10f)
            blackPolylineOptions.add(preLastLatLng)
            blackPolylineOptions.add(lastLatLng)
            blackPolyline = map?.addPolyline(blackPolylineOptions)

            originMarker = addOriginDestinationMarkerAndGet(originLastLng)
            originMarker?.setAnchor(0.5f, 0.5f)
            if (isTrackingEnd) {
                val destinationLastLng = pathPoints.value!!.last().last()
                destinationMarker =
                    addOriginDestinationMarkerAndGet(destinationLastLng)
                destinationMarker?.setAnchor(0.5f, 0.5f)
            }

//            val polylineAnimator = AnimationUtils.polylineAnimator()
//            polylineAnimator.addUpdateListener { valueAnimator ->
//                val percentValue = (valueAnimator.animatedValue as Int)
//                val index = (blackPolyline?.points!!.size) * (percentValue / 100.0f).toInt()
//                grayPolyline?.points = blackPolyline?.points!!.subList(0, index)
//            }
//            polylineAnimator.start()
        }
    }

    //////////marker customize////////
    private fun updateCarLocation(polylines: Polylines) {
        if (polylines.isNotEmpty()) {
            val latlng = polylines.last().last()
            if (movingCabMarker == null) {
                movingCabMarker = addCarMarkerAndGet(latlng)
            }
            if (previousLatLng == null) {
                currentLatLng = latlng
                previousLatLng = currentLatLng
                movingCabMarker?.position = currentLatLng as LatLng
                movingCabMarker?.setAnchor(0.5f, 0.5f)
                //animateCamera(currentLatLng!!)
            } else {
                previousLatLng = currentLatLng
                currentLatLng = latlng
                CoroutineScope(Dispatchers.Default).launch {
                    val valueAnimator = async { AnimationUtils.carAnimator() }
                    valueAnimator.await().addUpdateListener { va ->
                        if (currentLatLng != null && previousLatLng != null) {
                            val multiplier = va.animatedFraction
                            val nextLocation = LatLng(
                                multiplier * currentLatLng!!.latitude + (1 - multiplier) * previousLatLng!!.latitude,
                                multiplier * currentLatLng!!.longitude + (1 - multiplier) * previousLatLng!!.longitude
                            )
                            movingCabMarker?.position = nextLocation
                            val rotation = MapUtils.getRotation(previousLatLng!!, nextLocation)
                            if (!rotation.isNaN()) {
                                movingCabMarker?.rotation = rotation
                            }
                            movingCabMarker?.setAnchor(0.5f, 0.5f)
                            //animateCamera(nextLocation)
                        }
                    }
                    withContext(Dispatchers.Main) {
                        valueAnimator.await().start()
                        delay(Constants.UPDATE_INTERVAL_MARKER)
                    }
                    withContext(Dispatchers.Main) {
                        delay(Constants.UPDATE_INTERVAL_MARKER_ANIMATION)
                    }
                }
            }
        }
    }

    //////for getDeviceLocation()//////////////
    private fun updateCarLocation(latLng: LatLng) {
        if (movingCabMarker == null) {
            movingCabMarker = addCarMarkerAndGet(latLng)
        }
        if (previousLatLng == null) {
            currentLatLng = latLng
            previousLatLng = currentLatLng
            movingCabMarker?.position = currentLatLng as LatLng
            movingCabMarker?.setAnchor(0.5f, 0.5f)
            //animateCamera(currentLatLng!!)
        } else {
            previousLatLng = currentLatLng
            currentLatLng = latLng
            CoroutineScope(Dispatchers.Default).launch {
                val valueAnimator = async { AnimationUtils.carAnimator() }
                valueAnimator.await().addUpdateListener { va ->
                    if (currentLatLng != null && previousLatLng != null) {
                        val multiplier = va.animatedFraction
                        val nextLocation = LatLng(
                            multiplier * currentLatLng!!.latitude + (1 - multiplier) * previousLatLng!!.latitude,
                            multiplier * currentLatLng!!.longitude + (1 - multiplier) * previousLatLng!!.longitude
                        )
                        movingCabMarker?.position = nextLocation
                        val rotation = MapUtils.getRotation(previousLatLng!!, nextLocation)
                        if (!rotation.isNaN()) {
                            movingCabMarker?.rotation = rotation
                        }
                        movingCabMarker?.setAnchor(0.5f, 0.5f)
                        //animateCamera(nextLocation)
                    }
                }
                withContext(Dispatchers.Main) {
                    valueAnimator.await().start()
                    delay(Constants.UPDATE_INTERVAL_MARKER)
                }
                withContext(Dispatchers.Main) {
                    delay(Constants.UPDATE_INTERVAL_MARKER_ANIMATION)
                }
            }
        }
    }

    private fun moveCamera(latLng: LatLng) {
        map?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    private fun animateCamera(latLng: LatLng) {
        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(15.5f).build()
        map?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun addCarMarkerAndGet(latLng: LatLng): Marker? {
        val bitmapDescriptor =
            BitmapDescriptorFactory.fromBitmap(MapUtils.getCarBitmap(requireContext()))
        return map?.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
                .snippet("${loginViewModel.userData.value!!.userId}")
        )
    }

    private suspend fun addGeneralPolylines(markerList: MutableList<Marker>?): Polyline {
        val latLngList = withContext(Dispatchers.Main) { markerList!!.map { it.position } }
        val polylineOptions = PolylineOptions()
            .color(Color.BLACK)
            .width(POLYLINE_WIDTH)
            .addAll(latLngList)
        return withContext(Dispatchers.Main) { map?.addPolyline(polylineOptions)!! }
    }

    private fun removeGeneralPolylines(polyline: Polyline) {
        polyline.remove()
    }

    private suspend fun addGeneralMarker(
        viewStateData: MutableList<TrackernityResponseSecondItem>,
        markerIcon: BitmapDescriptor
    ): MutableList<Marker> {
        val markerList = mutableListOf<Marker>()
        val bitmapDescriptor = markerIcon
        lateinit var markerItem: Marker
        for (data in viewStateData) {
            val latLng = LatLng(data.lat!!, data.lgt!!)
            val descMarker = data.descriptions
            withContext(Dispatchers.Main) {
                when (chipTag) {
                    "CHIP_3" -> {
                        markerItem = map?.addMarker(
                            MarkerOptions().position(latLng).flat(true).snippet(
                                "Id: ${data.id}" +
                                        "\n" +
                                        "Lat: ${data.lat}" +
                                        "\n" +
                                        "Long: ${data.lgt}" +
                                        "\n" +
                                        "Perkiraan Jarak Gangguan: ${data.jarakGangguan} m"
                            )
                                .icon(bitmapDescriptor)
                        )!!
                    }
                    "CHIP_2" -> {
                        markerItem = map?.addMarker(
                            MarkerOptions().position(latLng).flat(true).snippet(
                                "Id: ${data.id}" +
                                        "\n" +
                                        "Lat: ${data.lat}" +
                                        "\n" +
                                        "Long: ${data.lgt}" +
                                        "\n" +
                                        "Route: ${data.remarks} -> ${data.remarks2}"
                            )
                                .icon(bitmapDescriptor)
                        )!!
                    }
                    else -> {
                        markerItem = map?.addMarker(
                            MarkerOptions().position(latLng).flat(true).snippet(
                                "Id: ${data.id}" +
                                        "\n" +
                                        "Lat: ${data.lat}" +
                                        "\n" +
                                        "Long: ${data.lgt}" +
                                        "\n" +
                                        "Route: ${data.remarks} -> ${data.descriptions}"
                            )
                                .icon(bitmapDescriptor)
                        )!!
                    }
                }
                markerList.add(markerItem)
            }
        }
        return markerList
    }

    private suspend fun addGeneralMarkerSecond(
        viewStateData: MutableList<TrackernityResponseSecondItem>,
        markerIcon: BitmapDescriptor,
        markerIcon2: BitmapDescriptor
    ): MutableList<Marker> {
        val markerList = mutableListOf<Marker>()
        lateinit var markerItem: Marker
        for (data in viewStateData) {
            val latLng = LatLng(data.lat!!, data.lgt!!)
            val descMarker = data.descriptions
            withContext(Dispatchers.Main) {
                if (chipTag == "CHIP_3") {
                    markerItem = map?.addMarker(
                        MarkerOptions().position(latLng).flat(true).snippet(
                            "Id: ${data.id}" +
                                    "\n" +
                                    "Lat: ${data.lat}" +
                                    "\n" +
                                    "Long: ${data.lgt}" +
                                    "\n" +
                                    "Perkiraan Jarak Gangguan: ${data.jarakGangguan} m"
                        )
                            .icon(markerIcon)
                    )!!
                } else {
                    if (data.flaggingGgn == 0) {
                        markerItem = map?.addMarker(
                            MarkerOptions().position(latLng).flat(true).snippet(
                                "Id: ${data.id}" +
                                        "\n" +
                                        "Lat: ${data.lat}" +
                                        "\n" +
                                        "Long: ${data.lgt}" +
                                        "\n" +
                                        "Route: ${data.remarks}-> ${data.remarks2}"
                            )
                                .icon(markerIcon)
                        )!!
                    } else {
                        markerItem = map?.addMarker(
                            MarkerOptions().position(latLng).flat(true).snippet(
                                "Id: ${data.id}" +
                                        "\n" +
                                        "Lat: ${data.lat}" +
                                        "\n" +
                                        "Long: ${data.lgt}" +
                                        "\n" +
                                        "Route: ${data.remarks}-> ${data.remarks2}"
                            )
                                .icon(markerIcon2)
                        )!!
                    }
                }
                markerList.add(markerItem)
            }
        }
        return markerList
    }


    private fun removeGeneralMarker(currentGeneralMarker: MutableList<Marker>) {
        for (marker in currentGeneralMarker) {
            marker.remove()
        }
    }

    private fun addMarkerLongClick(latLng: LatLng): Marker? {
        return map?.addMarker(
            MarkerOptions().position(latLng).flat(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )
    }

    ////////////////////implement of listener////////////////

    override fun onInfoWindowClick(marker: Marker) {
        trackingViewModel.remarks.value = marker.snippet
        mainViewModel.remarks.value = marker.snippet
        Toast.makeText(
            requireContext(),
            "Set '${trackingViewModel.remarks.value}' as remarks for tracking",
            Toast.LENGTH_SHORT
        ).show()
        Timber.d("Click Marker Listener ; Data: ${trackingViewModel.remarks.value} !!!")
    }

    override fun onMapLongClick(latLng: LatLng) {
        if (longClickMarker != null)
            longClickMarker!!.remove()
        longClickMarker = addMarkerLongClick(latLng)
        mainViewModel.latLng.value = latLng
    }

    //    private fun setMarkerFunc(){
//        map?.setOnInfoWindowClickListener {
//            trackingViewModel.remarks.postValue(it.snippet)
//            Toast.makeText(requireContext(),"Set ${it.snippet} as remarks for tracking", Toast.LENGTH_LONG)
//            Timber.d("Click Marker Listener !!!")
//        }
//    }

    private fun deleteCarMarker(marker: Marker) {
        marker.remove()
    }

    private fun addOriginDestinationMarkerAndGet(latLng: LatLng): Marker? {
        val bitmapDescriptor =
            BitmapDescriptorFactory.fromBitmap(MapUtils.getOriginDestinationMarkerBitmap())
        return map?.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }


    //////////////////////Permission//////////////////////////////
    //////////////////////Permission//////////////////////////////
    //////////////////////Permission//////////////////////////////

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Timber.d("Location Permission Granted")
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            TrackingUtility.requestPermissions(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    //////Logout/Exit Tracking Fragment Functions//////////////
    private fun btnLogoutFunc() {
        binding.contentIncluded.logoutBtn.apply {
            setOnClickListener { view ->
                Timber.d("Test Click LogoutBtn: ${view}")
                if (trackingViewModel.isTracking.value!!) {
                    Toast.makeText(
                        context,
                        "Please stop tracking before logout!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    CustomDialog().apply {
                        setTitleDialog("Logout?")
                        setMessageDialog("Are You Sure Want to Logout?")
                        setPositiveButtonListener {
                            try {
                                /////call logout function in here//////
//                                trackingViewModel.userData.value = null
//                                findNavController().navigate(TrackingFragmentDirections.actionTrackingFragmentToLoginFragment())
//                                Toast.makeText(requireContext(),"Logout!",Toast.LENGTH_SHORT).show()

                                removeUserDataSharedPreference()
                                loginViewModel.logout()

                            } catch (ex: Exception) {
                                Toast.makeText(requireContext(), "Error: ${ex}", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }.show(parentFragmentManager, LOGOUT_DIALOG_TAG)
                }
            }
        }
    }

    /////utilites functions///////
    private fun Float.dp(): Float {
        return this * resources.displayMetrics.density
    }

    //////bacpressed button func/////

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        activity?.onBackPressedDispatcher?.addCallback(this,object: OnBackPressedCallback(true){
//            override fun handleOnBackPressed() {
//                Toast.makeText(requireContext(),trackingViewModel.userData.value.toString(),Toast.LENGTH_LONG).show()
//            }
//        })
//    }

}