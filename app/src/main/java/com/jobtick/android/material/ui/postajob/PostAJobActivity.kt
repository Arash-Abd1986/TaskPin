package com.jobtick.android.material.ui.postajob

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.activities.CompleteMessageActivity
import com.jobtick.android.models.AttachmentModel
import com.jobtick.android.models.DueTimeModel
import com.jobtick.android.models.TaskModel
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.coroutines.Status
import com.jobtick.android.network.model.response.ReviewItem
import com.jobtick.android.network.model.response.draft.DraftResponse
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.*
import com.jobtick.android.viewmodel.PostAJobViewModel
import com.jobtick.android.viewmodel.ViewModelFactory
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.ArrayList


class PostAJobActivity : ActivityBase() {
    lateinit var navController: NavController
    lateinit var viewModel: PostAJobViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var title: MaterialTextView
    private lateinit var linTitle: LinearLayout
    private lateinit var taskModel: TaskModel
    private var isDraftWorkDone = false
    private var isEditTask = false
    private lateinit var draftResponse: DraftResponse


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_ajob)
        initVM()
        val bundle = intent.extras

        taskModel = TaskModel()
        if (bundle != null && bundle.containsKey(ConstantKey.TASK)) {
            taskModel = bundle.getParcelable(ConstantKey.TASK)!!
        }
        if (bundle != null && bundle.containsKey(ConstantKey.CATEGORY_ID)) {
            taskModel.category_id = bundle.getInt(ConstantKey.CATEGORY_ID, 1)
        }
        title = findViewById(R.id.title)
        linTitle = findViewById(R.id.linTitle)
        val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment?
        navController = navHostFragment!!.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            linTitle.visibility = View.VISIBLE
            when (destination.id) {
                R.id.postAJobSetTitleFragment -> {
                    title.text = "Title"
                }
                R.id.postAJobAddLocationFragment -> {
                    title.text = "Location"
                }
                R.id.getLocationFragment -> {
                    linTitle.visibility = View.GONE
                }
                R.id.postAJobDateTimeFragment -> {
                    title.text = "Date & Time"
                }
                R.id.postAJobBudgetFragment -> {
                    title.text = "Budget"
                }
                R.id.postAJobDateFragment -> {
                    title.text = "Date"
                }
                R.id.postAJobTimeFragment -> {
                    title.text = "Time"
                }
                R.id.detailsFragment -> {
                    title.text = "Details"
                }
                R.id.postAJobAttachmentFragment -> {
                    title.text = "Attachments"
                }
            }
        }
    }

    fun setData() {
     viewModel.setData(draftResponse)
    }

    fun postJob() {
        lifecycleScope.launch {
            viewModel.state.collectLatest {
                taskModel.budget = it.budget.toInt()
                taskModel.location = it.location?.place_name_en
                taskModel.attachments = it.attachments.map {
                    AttachmentModel(it.id, it.name, it.fileName, it.mime, it.url, it.thumbUrl, it.modalUrl, it.createdAt, it.drawable, it.type)
                } as ArrayList<AttachmentModel>?
                taskModel.description = ""
                it.date?.let {
                    taskModel.dueDate = (it.month).toString() + it.month + it.year
                }
                taskModel.apply {
                    dueTime = (DueTimeModel((it.time == PostAJobViewModel.PostAJobTime.ANY_TIME), (it.time == PostAJobViewModel.PostAJobTime.EVENING),
                            (it.time == PostAJobViewModel.PostAJobTime.MORNING), (it.time == PostAJobViewModel.PostAJobTime.AFTERNOON)))
                }
                taskModel.title = it.title

                taskModel.paymentType = "fixed"
                if (it.isRemote) {
                    taskModel.taskType = "remote"
                } else {
                    taskModel.taskType = "physical"
                    taskModel.position.longitude = it.location!!.geometry!!.coordinates?.get(0)
                    taskModel.position.latitude = it.location!!.geometry!!.coordinates?.get(1)
                }
                uploadDataToServer(false)
            }
        }
    }

    private fun uploadDataToServer(draft: Boolean) {
        val queryParameter: String
        val METHOD: Int
        if (taskModel.slug != null) {
            queryParameter = "/" + taskModel.slug
            METHOD = Request.Method.PATCH
        } else {
            queryParameter = "/create"
            METHOD = Request.Method.POST
        }
        showProgressDialog()
        val stringRequest: StringRequest =
                object : StringRequest(METHOD, Constant.URL_TASKS + queryParameter,
                        Response.Listener { response: String? ->
                            Timber.e(response)
                            hideProgressDialog()
                            try {
                                val jsonObject = JSONObject(response!!)
                                Timber.e(jsonObject.toString())
                                if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                                    if (jsonObject.getBoolean("success")) {
                                        var intent: Intent
                                        val bundle: Bundle
                                        if (draft) {
                                            intent = Intent()
                                            bundle = Bundle()
                                            bundle.putBoolean(ConstantKey.UPDATE_TASK, true)
                                            intent.putExtras(bundle)
                                            setResult(ConstantKey.RESULTCODE_UPDATE_TASK, intent)
                                            isDraftWorkDone = true
                                            //                                    Toasty.info(TaskCreateActivity.this,"Draft saved", Toast.LENGTH_LONG).show();
                                            Toast.makeText(
                                                    this@PostAJobActivity,
                                                    "Draft saved",
                                                    Toast.LENGTH_LONG
                                            ).show()
                                            onBackPressed()
                                            return@Listener
                                        } else {
                                            intent = Intent()
                                            bundle = Bundle()
                                            bundle.putBoolean(ConstantKey.CATEGORY, true)
                                            intent.putExtras(bundle)
                                            setResult(ConstantKey.RESULTCODE_CATEGORY, intent)
                                        }
                                        if (draft) {
                                            isDraftWorkDone = true
                                            onBackPressed()
                                            return@Listener
                                        }
                                        FireBaseEvent.getInstance(applicationContext)
                                                .sendEvent(
                                                        FireBaseEvent.Event.POST_A_JOB,
                                                        FireBaseEvent.EventType.API_RESPOND_SUCCESS,
                                                        FireBaseEvent.EventValue.POST_A_JOB_SUBMIT
                                                )
                                        if (isEditTask) {
                                            intent = Intent(this, CompleteMessageActivity::class.java)
                                            val bundle1 = Bundle()
                                            bundle1.putString(
                                                    ConstantKey.COMPLETES_MESSAGE_TITLE,
                                                    "Job Edited Successfully"
                                            )
                                            bundle1.putInt(
                                                    ConstantKey.COMPLETES_MESSAGE_FROM,
                                                    ConstantKey.RESULTCODE_CREATE_TASK
                                            )
                                            bundle1.putString(ConstantKey.SLUG, taskModel.slug)
                                            intent.putExtras(bundle1)
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            var taskSlug: String? = null
                                            try {
                                                if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                                    val data = jsonObject.getJSONObject("data")
                                                    taskSlug = data.getString("slug")
                                                    Log.d("taskSlug", taskSlug)
                                                } else taskSlug = null
                                            } catch (e: Exception) {
                                                taskSlug = null
                                            }
                                            intent = Intent(
                                                    this@PostAJobActivity,
                                                    CompleteMessageActivity::class.java
                                            )
                                            val bundle2 = Bundle()
                                            bundle2.putInt(
                                                    ConstantKey.COMPLETES_MESSAGE_FROM,
                                                    ConstantKey.RESULTCODE_CREATE_TASK
                                            )
                                            bundle2.putString(ConstantKey.SLUG, taskSlug)
                                            intent.putExtras(bundle2)
                                            startActivity(intent)
                                            finish()
                                        }
                                    } else {
                                        showToast("Something went Wrong", this@PostAJobActivity)
                                    }
                                }
                            } catch (e: JSONException) {
                                Timber.e(e.toString())
                                e.printStackTrace()
                            }
                        },
                        Response.ErrorListener { error: VolleyError ->
                            val networkResponse = error.networkResponse
                            if (networkResponse?.data != null) {
                                val jsonError = String(networkResponse.data)
                                if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                                    unauthorizedUser()
                                    hideProgressDialog()
                                    return@ErrorListener
                                }
                                try {
                                    val jsonObject = JSONObject(jsonError)
                                    val jsonObjectError = jsonObject.getJSONObject("error")
                                    if (jsonObjectError.has("message")) {
                                        showToast(jsonObjectError.getString("message"), this)
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            } else {
                                showToast("Something Went Wrong", this@PostAJobActivity)
                            }
                            println(error.toString())
                            hideProgressDialog()
                        }) {
                    override fun getHeaders(): Map<String, String> {
                        val map1: MutableMap<String, String> = HashMap()
                        map1["authorization"] =
                                sessionManager.tokenType + " " + sessionManager.accessToken
                        map1["Accept"] = "application/json"
                        map1["X-Requested-With"] = "XMLHttpRequest"
                        map1["Version"] = BuildConfig.VERSION_CODE.toString()
                        return map1
                    }

                    override fun getParams(): Map<String, String> {
                        val map1: MutableMap<String, String> = HashMap()
                        map1["category_id"] = Integer.toString(taskModel.category_id)
                        map1["title"] = taskModel.title
                        if (taskModel.description != null) map1["description"] = taskModel.description
                        if (taskModel.location != null && taskModel.location != "") map1["location"] =
                                taskModel.location
                        if (taskModel.position != null) {
                            map1["latitude"] = taskModel.position.latitude.toString()
                            map1["longitude"] = taskModel.position.longitude.toString()
                        }
                        if (taskModel.taskType != null) map1["task_type"] = taskModel.taskType
                        if (taskModel.paymentType != null) map1["payment_type"] = taskModel.paymentType
                        if (taskModel.paymentType != null) {
                            if (taskModel.paymentType.equals("fixed", ignoreCase = true)) {
                                if (taskModel.budget >= 5) map1["budget"] = taskModel.budget.toString()
                            } else {
                                if (taskModel.totalHours * taskModel.hourlyRate >= 5) {
                                    map1["budget"] =
                                            (taskModel.hourlyRate * taskModel.totalHours).toString()
                                    map1["total_hours"] = taskModel.totalHours.toString()
                                    map1["hourly_rate"] = taskModel.hourlyRate.toString()
                                }
                            }
                        }
                        if (taskModel.dueDate != null) map1["due_date"] =
                                Tools.getApplicationFromatToServerFormat(taskModel.dueDate)
                        if (taskModel.attachments != null && taskModel.attachments.size != 0) {
                            var i = 0
                            while (taskModel.attachments.size > i) {
                                if (taskModel.attachments[i].id != null) {
                                    map1["attachments[$i]"] = taskModel.attachments[i].id.toString()
                                }
                                i++
                            }
                        }
                        if (taskModel.musthave != null && taskModel.musthave.size != 0) {
                            var i = 0
                            while (taskModel.musthave.size > i) {
                                map1["musthave[$i]"] = taskModel.musthave[i]
                                i++
                            }
                        }
                        if (taskModel.dueTime != null) {
                            var count = 0
                            if (taskModel.dueTime.morning) {
                                map1["due_time[$count]"] = "morning"
                                count += 1
                            }
                            if (taskModel.dueTime.afternoon) {
                                map1["due_time[$count]"] = "afternoon"
                                count += 1
                            }
                            if (taskModel.dueTime.evening) {
                                map1["due_time[$count]"] = "evening"
                                count += 1
                            }
                            if (taskModel.dueTime.anytime) {
                                map1["due_time[$count]"] = "anytime"
                            }
                        }
                        if (draft) {
                            map1["draft"] = "1"
                        }
                        println(map1.size)
                        println(map1.toString())
                        pushEvent(
                                EventTitles.N_API_CREATE_TASK.key, bundleOf(
                                "usr_name" to sessionManager.userAccount.name,
                                "usr_id" to sessionManager.userAccount.id,
                                "email" to sessionManager.userAccount.email,
                                "phone_number" to sessionManager.userAccount.mobile,
                                "category_id" to taskModel.category_id.toString(),
                                "title" to eventCleaner(taskModel.title),
                                "budget" to taskModel.budget.toString(),
                                "location" to if (taskModel.location != null && taskModel.location != "") taskModel.location else "",
                                "description" to eventCleaner(taskModel.description)
                        )
                        )
                        return map1
                    }
                }
        stringRequest.retryPolicy = DefaultRetryPolicy(
                0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(this@PostAJobActivity)
        requestQueue.add(stringRequest)
    }

    override fun onBackPressed() {

        super.onBackPressed()
    }

    private fun initVM() {
        sessionManager = SessionManager(applicationContext)

        viewModel = ViewModelProvider(
                this,
                ViewModelFactory(ApiHelper(ApiClient.getClientV2(sessionManager)))
        ).get(PostAJobViewModel::class.java)
        viewModel.getDraft()
        viewModel.draftResponse.observe(this) {
            it?.let { it ->
                when (it.status) {
                    Status.SUCCESS -> {
                        draftResponse = it.data!!
                        hideProgressDialog()
                    }
                    Status.ERROR -> {
                        hideProgressDialog()
                    }
                    Status.LOADING -> {
                        showProgressDialog()
                    }
                }
            }
        }
    }
}