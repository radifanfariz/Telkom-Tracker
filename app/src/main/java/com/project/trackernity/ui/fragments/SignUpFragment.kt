package com.project.trackernity.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.project.trackernity.R
import com.project.trackernity.data.model.SignUpRequest
import com.project.trackernity.databinding.FragmentSignUpBinding
import com.project.trackernity.ui.fragments.ui.signup.DropdownItemsAuthResult
import com.project.trackernity.ui.fragments.ui.signup.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private val signUpViewModel: SignUpViewModel by viewModels()
    private var _binding: FragmentSignUpBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var arrayAdapterRegional:ArrayAdapter<String>
    private lateinit var arrayAdapterWitel:ArrayAdapter<String>
    private lateinit var arrayAdapterUnit:ArrayAdapter<String>

    private lateinit var codeDropdownList:List<String?>

//    private val testArray = arrayOf("Belgium","France","Italy","Germany","Spain")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        signUpViewModel.getDropdownItemsAuth()
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /////Manual viewModel decalared with factory//////
//        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
//            .get(LoginViewModel::class.java)

//        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_item,testArray)



        val usernameEditText = binding.username
        val passwordEditText = binding.password
        val passwordRepeatEditText = binding.passwordRepeat
        val nameEditText = binding.name
        val regionalDropdown = binding.regional
        val witelDropdown = binding.witel
        val unitDropdown = binding.unit
        val signUpButton = binding.signUp
        val loadingProgressBar = binding.loading

        refreshBtnFunc()

        signUpViewModel.dropdownItemsAuthResult.observe(viewLifecycleOwner, Observer {
                dropdownItemsResult ->
            if(dropdownItemsResult == null){
                return@Observer
            }
            handleState(dropdownItemsResult)
        })
//        signUpViewModel.dropdownItemsAuthResult.observe(viewLifecycleOwner, Observer {
//                dropdownItemsResult ->
//            if(dropdownItemsResult == null){
//                return@Observer
//            }
//            handleState(dropdownItemsResult)
////            arrayAdapterRegional = ArrayAdapter(requireContext(),R.layout.dropdown_item,dropdownItemsResult.regional!!)
////            arrayAdapterWitel = ArrayAdapter(requireContext(),R.layout.dropdown_item,dropdownItemsResult.witel!!)
////            arrayAdapterUnit = ArrayAdapter(requireContext(),R.layout.dropdown_item,dropdownItemsResult.unit!!)
////            regionalDropdown.setAdapter(arrayAdapterRegional)
////            witelDropdown.setAdapter(arrayAdapterWitel)
////            unitDropdown.setAdapter(arrayAdapterUnit)
//        })

        signUpViewModel.signUpFormState.observe(viewLifecycleOwner,
            Observer { signUpFormState ->
                if (signUpFormState == null) {
                    return@Observer
                }
                signUpButton.isEnabled = signUpFormState.isDataValid
                signUpFormState.usernameError?.let {
                    usernameEditText.error = getString(it)
                }
                signUpFormState.passwordError?.let {
                    passwordEditText.error = getString(it)
                }
                signUpFormState.passwordRepeatError?.let {
                    passwordRepeatEditText.error = getString(it)
                }
                signUpFormState.nameError?.let {
                    nameEditText.error = getString(it)
                }
                signUpFormState.regionalError?.let {
                    regionalDropdown.error = getString(it)
                }
                signUpFormState.witelError?.let {
                    witelDropdown.error = getString(it)
                }
                signUpFormState.unitError?.let {
                    unitDropdown.error = getString(it)
                }
            })

        signUpViewModel.signUpResult.observe(viewLifecycleOwner,
            Observer { signUpResult ->
                signUpResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                signUpResult.error?.let {
                    showSignUpFailed(it)
                }
                signUpResult.success?.let {
//                    updateUiWithUser(it)
                    signUpSuccessMechanism(it)
                }
            })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                signUpViewModel.signUpDataChanged(
                    username = usernameEditText.text.toString(),
                    password = passwordEditText.text.toString(),
                    passwordRepeat = passwordRepeatEditText.text.toString(),
                    name = nameEditText.text.toString(),
                    regional = regionalDropdown.text.toString(),
                    witel = witelDropdown.text.toString(),
                    unit = unitDropdown.text.toString()
                )

                dropdownRemoveError()
            }
        }
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        passwordRepeatEditText.addTextChangedListener(afterTextChangedListener)
        nameEditText.addTextChangedListener(afterTextChangedListener)
        regionalDropdown.addTextChangedListener(afterTextChangedListener)
        witelDropdown.addTextChangedListener(afterTextChangedListener)
        unitDropdown.addTextChangedListener(afterTextChangedListener)
//        witelEditText.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                val requestItem = SignUpRequest(userId = usernameEditText.text.toString(),
//                    pass = passwordEditText.text.toString(),
//                    passRepeat = passwordRepeatEditText.text.toString(),
//                    nama = nameEditText.text.toString(),
//                    regional = regionalEditText.text.toString(),
//                    witel = witelEditText.text.toString())
//                signUpViewModel.signUp(requestItem)
//            }
//            false
//        }

        signUpButton.setOnClickListener {
            val regionalPos = arrayAdapterRegional.getPosition(regionalDropdown.text.toString())
            val witelPos = arrayAdapterWitel.getPosition(witelDropdown.text.toString())
            val unitPos = arrayAdapterUnit.getPosition(unitDropdown.text.toString())
            var cProfileParam = ""
            try {
                cProfileParam =
                    "p-${codeDropdownList[regionalPos] + codeDropdownList[witelPos] + codeDropdownList[unitPos]}"
            }catch (ex: Exception){
                cProfileParam = "p-null"
//                Toast.makeText(requireContext(), "Error to generate C_Profile !!! \n Please contact admin !!!", Toast.LENGTH_LONG).show()
            }
            loadingProgressBar.visibility = View.VISIBLE
            val requestItem = SignUpRequest(userId = usernameEditText.text.toString(),
                pass = passwordEditText.text.toString(),
                passRepeat = passwordRepeatEditText.text.toString(),
                nama = nameEditText.text.toString(),
                regional = regionalDropdown.text.toString(),
                witel = witelDropdown.text.toString(),
                unit = unitDropdown.text.toString(),
                c_profile = cProfileParam
            )
            signUpViewModel.signUp(requestItem)
        }
    }

//    private fun updateUiWithUser(model: LoggedInUserView) {
//        val welcome = getString(R.string.welcome) + model.displayName
//        val appContext = context?.applicationContext ?: return
//        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
//    }

    private fun handleState(items: DropdownItemsAuthResult?) {
        try {
            items?.let {
                toggleLoading(it.loading)
                setDropdownItem(it.code!!,it.regional!!,it.witel!!,it.unit!!)
                it.error?.let { error -> showError(error) }
            }
        }catch (ex:Exception){
            Timber.d("error Handle: ${ex}")
        }
    }

    private fun setDropdownItem(code:List<String?>,regional:List<String?>,witel:List<String?>,unit:List<String?>){
        if (code.isNotEmpty()){
            codeDropdownList = code
        }
        if(regional.isNotEmpty() && witel.isNotEmpty() && unit.isNotEmpty()) {
            binding.apply {
                scrollview.visibility = View.VISIBLE
                signUp.visibility = View.VISIBLE
                linearlayout2.visibility = View.GONE
            }
            arrayAdapterRegional =
                ArrayAdapter(requireContext(), R.layout.dropdown_item, regional)
            binding.regional.setAdapter(arrayAdapterRegional)
            arrayAdapterWitel =
                ArrayAdapter(requireContext(), R.layout.dropdown_item, witel)
            binding.witel.setAdapter(arrayAdapterWitel)
            arrayAdapterUnit =
                ArrayAdapter(requireContext(), R.layout.dropdown_item, unit)
            binding.unit.setAdapter(arrayAdapterUnit)
        }
    }

    private fun toggleLoading(loadingState: Boolean) {
        binding.apply {
//            errorImage.visibility = View.GONE
            if (loadingState) {
                loading.visibility = View.VISIBLE
                scrollview.visibility = View.GONE
                signUp.visibility = View.GONE
            } else {
                loading.visibility = View.GONE
            }
        }
    }
        private fun showError(errorMsg: String) {
            if(errorMsg.isNotEmpty()) {
                binding.apply {
                    errorTxt.text = errorMsg
                    linearlayout2.visibility = View.VISIBLE
                    scrollview.visibility = View.GONE
                    signUp.visibility = View.GONE
                }
            }
        }

    private fun refreshBtnFunc(){
        binding.refresh.setOnClickListener(){
            Timber.d("test click !!!")
            signUpViewModel.getDropdownItemsAuth()
        }
    }

    private fun signUpSuccessMechanism(successString: String){
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, successString, Toast.LENGTH_LONG).show()
        findNavController().popBackStack()
    }

    private fun showSignUpFailed(errorString: String) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    private fun dropdownRemoveError(){
        val regionalDropdown = binding.regional
        val witelDropdown = binding.witel
        val unitDropdown = binding.unit

        if(regionalDropdown.text.isNotEmpty()){
            regionalDropdown.error = null
        }

        if(witelDropdown.text.isNotEmpty()){
            witelDropdown.error = null
        }

        if(unitDropdown.text.isNotEmpty()){
            unitDropdown.error = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}