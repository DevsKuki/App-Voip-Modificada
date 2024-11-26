package org.linphone.activities.assistant.api

import com.google.gson.annotations.SerializedName

class UserResponse(
    @SerializedName("listaUsuarios") var listaUsuarios: ArrayList<User>
)

class UserSistemas(
    @SerializedName("listaSistemas") var listaSistemas: ArrayList<User>
)
