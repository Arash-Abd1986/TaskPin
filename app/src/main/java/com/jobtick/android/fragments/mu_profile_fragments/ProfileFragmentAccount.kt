package com.jobtick.android.fragments.mu_profile_fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Explode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.databinding.FragmentProfileAccountBinding
import com.jobtick.android.fragments.LogOutBottomSheet
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.SetToolbar
import com.jobtick.android.utils.setSpanStyledTwoLineText


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragmentAccount : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var activity: DashboardActivity
    private lateinit var sessionManager: SessionManager
    private var _binding: FragmentProfileAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = (requireActivity() as DashboardActivity)
        sessionManager = SessionManager(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SetToolbar(activity, "Account", "", R.id.navigation_profile, binding.header, view)


        if(sessionManager.userAccount?.email?.toString().equals(null)) {
            binding.txtEmailValue.text = "Status"
            binding.edittextForEmail.visibility = View.VISIBLE
            binding.emailStatus.text = "Not Verified"
            binding.txtChangeEmail.text = "Verify Email Address"
            binding.emailStatus.setTextColor(activity.getColor(R.color.primary_error))
        }
        else {
            binding.txtEmailValue.text = sessionManager.userAccount.email
            binding.edittextForEmail.visibility = View.GONE
            binding.emailStatus.text = "Verified"
            binding.txtChangeEmail.text = "Change Email Address"
            binding.emailStatus.setTextColor(activity.getColor(R.color.feedback))
        }
        if(sessionManager.userAccount?.mobile?.toString().equals(null)) {
            Log.d("emailaccount", sessionManager.userAccount?.mobile?.toString()+"")

            binding.textMobileValue.text = "Status"
            binding.edittextForMobile.visibility = View.VISIBLE
            binding.txtNumberStatus.text = "Not Verified"
            binding.textChangeNumber.text = "Verify Mobile Number"
            binding.txtNumberStatus.setTextColor(activity.getColor(R.color.primary_error))
        }
        else {
            binding.textMobileValue.text = sessionManager.userAccount.mobile
            binding.edittextForMobile.visibility = View.GONE
            binding.txtNumberStatus.text = "Verified"
            binding.textChangeNumber.text = "Change Mobile Number"
            binding.txtNumberStatus.setTextColor(activity.getColor(R.color.feedback))
        }

        binding.nameParent.setOnClickListener {
            val bundle = bundleOf("firstName" to sessionManager.userAccount.fname,
                "lastName" to sessionManager.userAccount.lname)
            view.findNavController().navigate(R.id.action_navigation_profile_account_to_navigation_profile_name, bundle)
        }


        binding.aboutParent.setOnClickListener{
            view.findNavController().navigate(R.id.action_navigation_profile_account_to_navigation_profile_about)
        }
        binding.header.back.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_account_to_navigation_profile)
        }

        binding.deleteAccountParent.setOnClickListener{
            view.findNavController().navigate(R.id.action_navigation_profile_account_to_navigation_profile_delete_account)
        }
        binding.logoutParent.setOnClickListener {
            showDialog()
        }

        binding.textChangeNumber.setOnClickListener {
            binding.numberTextInput.error = null
            if(binding.txtNumberStatus.text == "Verified") {
                val bundle = bundleOf("number" to "1")
                view.findNavController().navigate(
                    R.id.action_navigation_profile_account_to_navigation_profile_change_email_first_page, bundle)
            } else {
                if (validate(listOf(binding.number))) {
                    val bundle = bundleOf("number" to sessionManager.userAccount.mobile)
                    view.findNavController().navigate(
                        R.id.action_navigation_profile_account_to_navigation_profile_change_email_second_page,
                        bundle
                    )
                } else {
                    binding.numberTextInput.error = "Please Enter Your Number"
                }

            }

        }
        binding.txtChangeEmail.setOnClickListener {
            binding.emailTextInput.error = null
            if(binding.emailStatus.text == "Verified") {
                val bundle = bundleOf("email" to "1")
                view.findNavController().navigate(
                    R.id.action_navigation_profile_account_to_navigation_profile_change_email_first_page, bundle)
            } else {
                if (validate(listOf(binding.email))) {
                    val bundle = bundleOf("email" to binding.email.text)
                    view.findNavController().navigate(
                        R.id.action_navigation_profile_account_to_navigation_profile_change_email_second_page,
                        bundle
                    )
                } else {
                    binding.emailTextInput.error = "Please Enter Your Email Address"
                }

            }

        }

        //        val callback: OnBackPressedCallback =
//            object : OnBackPressedCallback(true /* enabled by default */) {
//                override fun handleOnBackPressed() {
//                    when (view.id)
//                    {
//                        R.layout.fragment_profile_account ->
//                            view.findNavController().navigate(R.id.action_navigation_profile_account_to_navigation_profile)
//
//                    }
//                }
//            }
//        requireActivity().onBackPressedDispatcher.addCallback(activity, callback)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragmentAccount().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun validate(view: List<TextInputEditText>): Boolean {
        for (t in view)
            if(t.text?.trim()!!.isEmpty())
                return false
        return true
    }

    fun showDialog() {
        val cancel: MaterialButton?
        val delete: MaterialButton?
        val title: TextView?
        val mainTitle: TextView?
        val dialog = Dialog(activity, R.style.AnimatedDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialog.setContentView(R.layout.dialog_discard_changes_new)

        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.window!!.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        cancel = dialog.findViewById(R.id.cancel)
        delete = dialog.findViewById(R.id.discard)
        title = dialog.findViewById(R.id.title)
        mainTitle = dialog.findViewById(R.id.mainTitle)
        mainTitle.text = "Come back soon!"
        delete.text = "Logout"
        cancel.text = "Cancel"
        title.setText(activity.getString(R.string.profile_logout))

        cancel.setOnClickListener {
            dialog.cancel()
        }

        delete.setOnClickListener {
            dialog.cancel()
            sessionManager.userAccount = null
            sessionManager.login = false
            sessionManager.tokenType = null
            sessionManager.accessToken = null
            val intent = Intent(context, DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }


        dialog.show()

    }



}