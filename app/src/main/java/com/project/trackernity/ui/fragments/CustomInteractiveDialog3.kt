package com.project.trackernity.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.trackernity.R
import com.project.trackernity.databinding.CustomDialogInteractive3Binding
import com.project.trackernity.viewmodels.MainViewModel

class CustomInteractiveDialog3(mainViewModel: MainViewModel): DialogFragment() {
    private var _binding: CustomDialogInteractive3Binding? = null
    private val binding get() = _binding!!
    private val viewModel = mainViewModel

    private var positiveButtonListener: (() -> Unit)? = null
    private var negativeButtonListener: (() -> Unit)? = null


    fun setPositiveButtonListener(listener: () -> Unit) {
        positiveButtonListener = listener
    }

    fun setNegativeButtonListener(listener: () -> Unit) {
        negativeButtonListener = listener
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = CustomDialogInteractive3Binding.inflate(LayoutInflater.from(context))
        binding.viewModel = viewModel
        setDropdown()

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setCancelable(false)
            .setPositiveButton("Search") { _,_ ->
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

//    @SuppressLint("ServiceCast")
//    fun View.hideKeyboard() {
//        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.hideSoftInputFromWindow(windowToken, 0)
//    }

    private fun setDropdown(){

//        val headList = viewModel.dropdownItemsTrackingResult.value!!.head
//        val tailList = viewModel.dropdownItemsTrackingResult.value!!.tail
        val tregList = viewModel.dropdownItemsTrackingResult.value!!.treg
        val witelList = viewModel.dropdownItemsTrackingResult.value!!.witel
//        Timber.d("what is inside : ${dropdownList}")

        val tregDropdownArrayAdapter =
            ArrayAdapter(requireContext(), R.layout.dropdown_item, tregList!!)
        binding.tregDropdown.setAdapter(tregDropdownArrayAdapter)

        val witelDropdownArrayAdapter =
            ArrayAdapter(requireContext(), R.layout.dropdown_item, witelList!!)
        binding.witelDropdown.setAdapter(witelDropdownArrayAdapter)
    }
}