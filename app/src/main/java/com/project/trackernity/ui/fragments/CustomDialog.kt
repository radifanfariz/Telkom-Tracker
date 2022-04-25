package com.project.trackernity.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.trackernity.R

class CustomDialog: DialogFragment() {
    private var title: String = "Start The Tracking?"
    private var message:String = "Are you sure to Start the tracking?"
    private var positiveButtonListener: (() -> Unit)? = null
    private var negativeButtonListener: (() -> Unit)? = null

    fun setPositiveButtonListener(listener: () -> Unit) {
        positiveButtonListener = listener
    }

    fun setNegativeButtonListener(listener: () -> Unit) {
        negativeButtonListener = listener
    }

    fun setTitleDialog(title:String){
        this.title = title
    }

    fun setMessageDialog(message:String){
        this.message = message
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(title)
            .setMessage(message)
            .setIcon(R.drawable.ic_baseline_info_24)
            .setPositiveButton("Yes") { _, _ -> positiveButtonListener?.let { it() } }
            .setNegativeButton("No") { dialog, _ ->
                negativeButtonListener?.let { it() }
                dialog.cancel()
            }
            .create()
}