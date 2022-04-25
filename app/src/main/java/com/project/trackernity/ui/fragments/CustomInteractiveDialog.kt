package com.project.trackernity.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.trackernity.R
import com.project.trackernity.databinding.CustomDialogInteractiveBinding
import com.project.trackernity.repositories.TrackingRepository
import com.project.trackernity.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class CustomInteractiveDialog(mainViewModel:MainViewModel):DialogFragment() {

    private var _binding:CustomDialogInteractiveBinding? = null
    private val binding get() = _binding!!
    private val viewModel = mainViewModel

    private var positiveButtonListener: (() -> Unit)? = null
    private var negativeButtonListener: (() -> Unit)? = null

    private val userIdText = TrackingRepository.userData.value!!.userId!!

    fun setPositiveButtonListener(listener: () -> Unit) {
        positiveButtonListener = listener
    }

    fun setNegativeButtonListener(listener: () -> Unit) {
        negativeButtonListener = listener
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = CustomDialogInteractiveBinding.inflate(LayoutInflater.from(context))

        binding.viewModel = viewModel
        binding.userIdEdTxt.hint = userIdText
        setDropdown()


            return MaterialAlertDialogBuilder(requireContext())
                .setView(binding.root)
                .setCancelable(true)
                .setPositiveButton("Send") { _,_ ->
                    positiveButtonListener?.let { it() }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    negativeButtonListener?.let { it() }
                    dialog.cancel()
                }
                .create()
            }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setDropdown(){

//        val headList = viewModel.dropdownItemsTrackingResult.value!!.head
//        val tailList = viewModel.dropdownItemsTrackingResult.value!!.tail
        var allList = listOf<String?>()
        if (viewModel.remarksAllList.value != null) {
            allList = viewModel.remarksAllList.value!!
        }

        var remarks2List = listOf<String?>()
        if (viewModel.remarks2List.value != null){
            remarks2List = viewModel.remarks2List.value!!
        }

        val notesList = viewModel.dropdownItemsOthersResult.value!!.notesItems

        val descList = viewModel.dropdownItemsOthersResult.value!!.descItems

        val remarks2ArrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, remarks2List!!)
        binding.remarks2Dropdown.setAdapter(remarks2ArrayAdapter)

        val notesArrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, notesList!!)
        binding.notesEdTxt.setAdapter(notesArrayAdapter)

        val descArrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, descList!!)
        binding.descEdTxt.setAdapter(descArrayAdapter)

        val remarkFromArrayAdapter =
            ArrayAdapter(requireContext(), R.layout.dropdown_item, allList)
        binding.remarksFromDropdown.setAdapter(remarkFromArrayAdapter)

        val remarkToArrayAdapter =
            ArrayAdapter(requireContext(), R.layout.dropdown_item, allList)
        binding.remarksToDropdown.setAdapter(remarkToArrayAdapter)
    }

}