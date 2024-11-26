package org.linphone.activities.assistant.api

import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

object AppConstantes {

    const val BASE_URL = "https://tu_api_o_servicio.com/api/tuservicio"
}
interface WebService {
    @GET("/endpoint/listar/directorio/")
    suspend fun getUsers(): Response<UserResponse>

    @GET("/endpoint/listar/usuario/buscado/{ci}")
    suspend fun getUserCi(@Path("ci") ci: String): Response<User>

    @PUT("/endpoint/listar/usuario/{ci}")
    suspend fun updateStatus(
        @Path("ci") ci: String,
        @Body usuario: User
    ): Response<User>

    @GET("/endpoint/listar/usuario/sistemas")
    suspend fun getUsersSystem(): Response<UserSistemas>
}

object RetrofitClient {
    val webService: WebService by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstantes.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build().create(WebService::class.java)
    }
}
