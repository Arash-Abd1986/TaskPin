package com.jobtick.android.network.coroutines

import com.jobtick.android.models.event.EventRequest
import com.jobtick.android.network.model.request.BlockUserRequest
import com.jobtick.android.network.retrofit.ApiInterface

class ApiHelper(private val apiService: ApiInterface) {

    suspend fun reviews(id: Int, howIs: String) = apiService.reviews(id, howIs)
    suspend fun events(request: EventRequest) =
            apiService.event(request)

    suspend fun skills(query: String) = apiService.skills(query)
    suspend fun blockUser(query: BlockUserRequest) = apiService.blockUser(query)
    suspend fun budgetPlans() = apiService.budgetPlans()
    suspend fun draft() = apiService.draft()
    suspend fun getNearJobs(latitude: Float, longitude: Float, radius: Int, limit: Int, query: String) =
            apiService.getNearJobs(latitude, longitude, radius, limit, query)
}
