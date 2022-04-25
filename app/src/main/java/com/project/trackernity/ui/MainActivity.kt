package com.project.trackernity.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.project.trackernity.databinding.ActivityMainBinding
import com.project.trackernity.util.MapUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

//    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        navController = findNavController(R.id.fragmentContainerView)





        //////////////////Bottom Sheet Behaviour in Activity Implementation/////////////////
        //////////////////Bottom Sheet Behaviour in Activity Implementation/////////////////
        //////////////////Bottom Sheet Behaviour in Activity Implementation/////////////////

//        val bottomSheetBinding = FragmentCustomBottomSheetDialogBinding.inflate(LayoutInflater.from(this))
//        val bottomSheetFragment = bottomSheetBinding.root.custom_buttom_sheet_dialog

//        val bottomSheet = binding.root.bottomSheet
//        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

//        val bottomsheetFragment = CustomBottomSheetDialogFragment()
//        bottomsheetFragment.show(supportFragmentManager,CustomBottomSheetDialogFragment.TAG)


//        bottomSheetBehavior.addBottomSheetCallback(object :
//            BottomSheetBehavior.BottomSheetCallback() {
//
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                // handle onSlide
//            }
//
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                when (newState) {
//                    BottomSheetBehavior.STATE_COLLAPSED -> Toast.makeText(this@MainActivity, "STATE_COLLAPSED", Toast.LENGTH_SHORT).show()
//                    BottomSheetBehavior.STATE_EXPANDED -> Toast.makeText(this@MainActivity, "STATE_EXPANDED", Toast.LENGTH_SHORT).show()
//                    BottomSheetBehavior.STATE_DRAGGING -> Toast.makeText(this@MainActivity, "STATE_DRAGGING", Toast.LENGTH_SHORT).show()
//                    BottomSheetBehavior.STATE_SETTLING -> Toast.makeText(this@MainActivity, "STATE_SETTLING", Toast.LENGTH_SHORT).show()
//                    BottomSheetBehavior.STATE_HIDDEN -> Toast.makeText(this@MainActivity, "STATE_HIDDEN", Toast.LENGTH_SHORT).show()
//                    else -> Toast.makeText(this@MainActivity, "OTHER_STATE", Toast.LENGTH_SHORT).show()
//                }
//            }
//        })
    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        navigateToTrackingFragmentIfNeeded(intent)
//    }

//    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?) {
//        when (intent?.action) {
//            ACTION_SHOW_TRACKING_FRAGMENT -> {
//                navController.navigate(R.id.action_global_trackingFragment)
//            }
//            ACTION_FINISH_RUN -> {
//                val action = TrackingFragmentDirections.actionGlobalTrackingFragment(true)
//                navController.navigate(action)
//
//            }
//        }
//    }
}