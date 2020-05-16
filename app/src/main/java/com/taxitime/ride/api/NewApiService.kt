package com.taxitime.ride.api

import retrofit2.Response
import retrofit2.http.*

@JvmSuppressWildcards
interface NewApiService {

    @GET("api/v4/new-offering/{slug}")
    suspend fun fetchNfoDetails(@Path("slug") slug: String): Response<NfoResponse>

    @POST("api/v3/new-offering/{slug}/generate-lead/")
    suspend fun generateLead(@Path("slug") slug: String, @Body generateLeadRequest: GenerateLeadRequest): Response<BaseResponse>

    @GET("api/v3/new-offering/{slug}/similar-funds?page=1&limit=3")
    suspend fun fetchSimilarFunds(@Path("slug") slug: String): Response<NfoSimilarFundResponse>

    @GET("api/v3/new-offering/{slug}")
    suspend fun fetchIpoDetails(@Path("slug") slug: String): Response<IpoResponse>

    @GET("api/v3/new-offering/")
    suspend fun getNewOfferings(@QueryMap queryMap: Map<String, Any>): Response<NewOfferingsResponse>
}