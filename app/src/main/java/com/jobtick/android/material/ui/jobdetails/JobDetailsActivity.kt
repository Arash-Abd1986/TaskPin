package com.jobtick.android.material.ui.jobdetails

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.textview.MaterialTextView
import com.jobtick.android.R
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.models.TaskModel
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.viewmodel.EventsViewModel
import com.jobtick.android.viewmodel.JobDetailsViewModel
import com.jobtick.android.viewmodel.ViewModelFactory


class JobDetailsActivity : ActivityBase() {
    lateinit var navController: NavController
    lateinit var viewModel: JobDetailsViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var title: MaterialTextView
    private lateinit var linTitle: LinearLayout
    private lateinit var close: AppCompatImageView
    private lateinit var back: AppCompatImageView
    private lateinit var eventsViewModel: EventsViewModel
    private var strSlug: String = ""
    private var isFromSearch = false
    private var pushOfferID = 0
    private var pushQuestionID = 0
    private var isUserThePoster = false
    private var isUserTheTicker = false
    private var noActionAvailable = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_details)
        title = findViewById(R.id.title)
        close = findViewById(R.id.close)
        back = findViewById(R.id.back)
        back.setOnClickListener {
            navController.popBackStack()
        }
        close.setOnClickListener {
            navController.popBackStack()
        }
        linTitle = findViewById(R.id.linTitle)
        val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment?
        navController = navHostFragment!!.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            linTitle.visibility = View.VISIBLE
            close.visibility = View.VISIBLE
            when (destination.id) {
                R.id.postAJobSetTitleFragment -> {
                    title.text = "Title"
                }
            }
        }
        initVars()
        initVm()
    }

    private fun initVars() {
        sessionManager = SessionManager(this)
    }

    private fun initVm() {
        viewModel = ViewModelProvider(
                this,
                ViewModelFactory(ApiHelper(ApiClient.getClient()))
        ).get(JobDetailsViewModel::class.java)
        eventsViewModel = ViewModelProvider(
                this,
                ViewModelFactory(ApiHelper(ApiClient.getClientV2(sessionManager)))
        ).get(EventsViewModel::class.java)
        viewModel.getTaskModel(applicationContext, strSlug, sessionManager.tokenType, sessionManager.accessToken, sessionManager.userAccount.id)
        viewModel.geTaskModelResponse().observe(this) {
            setOwnerTask(taskModel = it)
        }
    }

    private fun setOwnerTask(taskModel: TaskModel) {
        if (taskModel.poster.id == sessionManager.userAccount.id) {
            isUserThePoster = true
            isUserTheTicker = false
        } else {
            if (taskModel.worker != null) {
                if (taskModel.worker.id == sessionManager.userAccount.id) {
                    isUserThePoster = false
                    isUserTheTicker = true
                    noActionAvailable = false
                    setTickerViewerMode()
                } else {
                    noActionAvailable = true
                    isUserTheTicker = false
                }
            } else {
                isUserThePoster = false
                isUserTheTicker = false
                noActionAvailable = false
                setTickerViewerMode()
            }
        }
    }
    private fun setTickerViewerMode(){
        val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment?
        val inflater = navHostFragment?.navController?.navInflater
        val graph = inflater?.inflate(R.navigation.job_details_graph)
        graph?.startDestination = R.id.jobDetailsTicketViewerFragment
        navHostFragment?.navController?.graph = graph!!
    }

    override fun getExtras() {
        super.getExtras()
        if (intent == null || intent.extras == null) {
            return
        }
        val bundle = intent.extras
        if (bundle!!.getString(ConstantKey.SLUG) != null) {
            strSlug = bundle.getString(ConstantKey.SLUG)!!
        }
        isFromSearch = bundle.getBoolean(ConstantKey.IS_FROM_SEARCH)
        if (bundle.getInt(ConstantKey.PUSH_OFFER_ID) != 0) {
            pushOfferID = bundle.getInt(ConstantKey.PUSH_OFFER_ID)
        }
        if (bundle.getInt(ConstantKey.PUSH_QUESTION_ID) != 0) {
            pushQuestionID = bundle.getInt(ConstantKey.PUSH_QUESTION_ID)
        }
    }

}