package com.jobtick.android.network.coroutines

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.jobtick.android.network.coroutines.ServiceType.NEAR_JOBS
import com.jobtick.android.network.coroutines.ServiceType.REVIEWS
import com.jobtick.android.network.model.Response
import com.jobtick.android.network.model.request.NearJobsRequest
import com.jobtick.android.network.model.request.ReviewsRequest
import kotlinx.coroutines.Dispatchers

fun <T> getData(service: ServiceType, mainRepository: MainRepository, input: T): LiveData<Resource<Response>> = liveData(Dispatchers.IO) {
    emit(Resource.loading(data = null))
    try {
        val response: Response? = when (service) {
            REVIEWS -> mainRepository.reviews((input as ReviewsRequest).id, (input as ReviewsRequest).howIs)
            else -> null
        }
        if (response!!.success)
            emit(Resource.success(response))
        else
            emit(Resource.error(null, response.message))

    } catch (exception: Exception) {
        emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
    }

}

suspend fun <T, G> getData(
    service: ServiceType,
    mainRepository: MainRepository,
    input: T,
    liveData: MutableLiveData<Resource<G>>
) {
    liveData.postValue(Resource.loading(data = null))
    try {

        val response: Any? = when (service) {
            NEAR_JOBS -> mainRepository.getNearJobs((input as NearJobsRequest).latitude,(input as NearJobsRequest).longitude
                ,(input as NearJobsRequest).radius,(input as NearJobsRequest).limit)
            else -> null
        }
        liveData.postValue(Resource.success(response as G))

    } catch (exception: Exception) {
        liveData.postValue(
            Resource.error(
                data = null,
                message = exception.message ?: "Fetch data error, please try again later"
            )
        )
    }

}