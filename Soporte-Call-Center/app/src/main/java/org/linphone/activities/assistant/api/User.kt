package org.linphone.activities.assistant.api

import com.google.gson.annotations.SerializedName

class User(
    @SerializedName("ci") var ci: String,
    @SerializedName("phone_number") var phone_number: String,
    @SerializedName("secret_number") var secret_number: String,
    @SerializedName("username") var username: String,
    @SerializedName("first_name") var first_name: String,
    @SerializedName("last_name") var last_name: String,
    @SerializedName("job_type") var job_type: String,
    @SerializedName("status_number") var status_number: String

)
