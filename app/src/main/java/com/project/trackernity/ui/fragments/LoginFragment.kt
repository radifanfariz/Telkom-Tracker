package com.project.trackernity.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import com.project.trackernity.R
import com.project.trackernity.databinding.FragmentLoginBinding
import com.project.trackernity.ui.fragments.ui.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class LoginFragment : Fragment() {

    companion object{
        const val LOGIN_SUCCESSFUL:String = "LOGIN_SUCCESSFUL"
    }

    private lateinit var savedStateHandle: SavedStateHandle

    private val loginViewModel: LoginViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    ////////////////////trial date time/////////////////////////////////////////////////////////////
    var endDateTime: String? = "2022-07-20 07:05:00"

    // Replace JVM's default time zone, ZoneId.systemDefault() with applicable time
    // zone e.g. ZoneId.of("America/New_York")
    var dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("u-M-d H:m:s", Locale.ENGLISH)
        .withZone(ZoneId.systemDefault())

    var zdt: ZonedDateTime = ZonedDateTime.parse(endDateTime, dtf)

    var instantEnd: Instant = zdt.toInstant()
    //////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /////Manual viewModel decalared with factory//////
//        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
//            .get(LoginViewModel::class.java)

//        savedStateHandle = findNavController().previousBackStackEntry!!.savedStateHandle
//        savedStateHandle.set(LOGIN_SUCCESSFUL, false)

        if (loginViewModel.userData.value != null){
            findNavController().navigate(R.id.action_loginFragment_to_trackingFragment)
        }

        val usernameEditText = binding.username
        val passwordEditText = binding.password
        val loginButton = binding.login
        val loadingProgressBar = binding.loading
        val signUpTextView = binding.signUp

        loginViewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                loginButton.isEnabled = loginFormState.isDataValid
                loginFormState.usernameError?.let {
                    usernameEditText.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    passwordEditText.error = getString(it)
                }
            })

        loginViewModel.loginResult.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                loginResult.error?.let {
                    showLoginFailed(it)
                }
                loginResult.success?.let {
//                    updateUiWithUser(it)
//                    savedStateHandle.set(LOGIN_SUCCESSFUL,true)
//                    navigateUserData(it)
                    saveUserDataSharedPreference()
                    findNavController().navigate(R.id.action_loginFragment_to_trackingFragment)
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
                loginViewModel.loginDataChanged(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
        }
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                var now = Instant.now()
                if(now.isAfter(instantEnd)){
                    Toast.makeText(requireContext(), "App has expired...!!!", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(requireContext(), "App is valid until ${endDateTime}", Toast.LENGTH_LONG).show()
                    loadingProgressBar.visibility = View.VISIBLE
                    loginViewModel.login(
                        usernameEditText.text.toString(),
                        passwordEditText.text.toString()
                    )
                }
            }
            false
        }

        loginButton.setOnClickListener {
            var now = Instant.now()
            if(now.isAfter(instantEnd)){
                Toast.makeText(requireContext(), "App is expired...!!!", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(requireContext(), "App is valid until ${endDateTime}", Toast.LENGTH_LONG).show()
                loadingProgressBar.visibility = View.VISIBLE
                loginViewModel.login(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
        }

        signUpTextView.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

//    private fun navigateUserData(model: LoggedInUserView){
//        val data = model.userData.data!!.last().user!!
//        val token = model.userData.data!!.last().token
//        val dataToNavigate = LoginResultToNavigate(
//            regional=data.regional!!,
//            witel = data.witel!!,
//            userId = data.userid!!,
//            token = token!!
//        )
////        loginViewModel.userData.value?.copy(
////            regional=data.regional,
////            witel = data.witel,
////            userId = data.userid,
////            token = token
////        )
//        val action = LoginFragmentDirections.actionLoginFragmentToTrackingFragment(dataToNavigate)
//        findNavController().navigate(action)
//    }

//    private fun updateUiWithUser(model: LoggedInUserView) {
//        val welcome = getString(R.string.welcome) + model.displayName
//        val appContext = context?.applicationContext ?: return
//        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
//    }

    private fun showLoginFailed(errorString: String) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun saveUserDataSharedPreference(){
        val sharedPreference =  requireContext().getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("nama",loginViewModel.userData.value!!.nama)
        editor.putString("regional",loginViewModel.userData.value!!.regional)
        editor.putString("witel",loginViewModel.userData.value!!.witel)
        editor.putString("unit",loginViewModel.userData.value!!.unit)
        editor.putString("c_profile",loginViewModel.userData.value!!.c_profile)
        editor.putString("userId",loginViewModel.userData.value!!.userId)
        editor.commit()
    }
}

//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import com.project.trackernity.R
//import com.project.trackernity.databinding.FragmentLoginBinding
//import com.project.trackernity.databinding.FragmentTrackingBinding
//import dagger.hilt.android.AndroidEntryPoint
//
//
//@AndroidEntryPoint
//class LoginFragment : Fragment(R.layout.fragment_login) {
//    private lateinit var binding: FragmentLoginBinding
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentLoginBinding.inflate(inflater,container,false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding = FragmentLoginBinding.bind(view)
//    }
//
//}