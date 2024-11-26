/*
 * Copyright (c) 2010-2020 Belledonne Communications SARL.
 *
 * This file is part of linphone-android

 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.linphone.activities.assistant.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.linphone.LinphoneApplication.Companion.coreContext
import org.linphone.LinphoneApplication.Companion.corePreferences
import org.linphone.R
import org.linphone.activities.GenericFragment
import org.linphone.activities.assistant.AssistantActivity
import org.linphone.activities.assistant.api.DialogUser
import org.linphone.activities.assistant.api.RetrofitClient
import org.linphone.activities.assistant.api.User
import org.linphone.activities.assistant.viewmodels.GenericLoginViewModel
import org.linphone.activities.assistant.viewmodels.GenericLoginViewModelFactory
import org.linphone.activities.assistant.viewmodels.SharedAssistantViewModel
import org.linphone.activities.main.viewmodels.DialogViewModel
import org.linphone.activities.navigateToEchoCancellerCalibration
import org.linphone.core.RegistrationState
import org.linphone.databinding.AssistantGenericAccountLoginFragmentBinding
import org.linphone.utils.DialogUtils

class GenericAccountLoginFragment : GenericFragment<AssistantGenericAccountLoginFragmentBinding>() {
    private lateinit var sharedAssistantViewModel: SharedAssistantViewModel
    private lateinit var viewModel: GenericLoginViewModel

    private var usuario = User("", "", "", "", "", "", "", "")

    private var CiComparar: String? = null
    private var storedBirthDate: String? = null
    private var usuarioObtenido: User? = null

    override fun getLayoutId(): Int = R.layout.assistant_generic_account_login_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        sharedAssistantViewModel = requireActivity().run {
            ViewModelProvider(this)[SharedAssistantViewModel::class.java]
        }

        viewModel = ViewModelProvider(
            this,
            GenericLoginViewModelFactory(sharedAssistantViewModel.getAccountCreator(true))
        )[GenericLoginViewModel::class.java]
        binding.viewModel = viewModel

        // cargar datos usuarios
        binding.btnMostrar.setOnClickListener {
            createAccountIfNotExists()
        }
        binding.btnLogin.setOnClickListener {
            LoginVerification()
        }

        viewModel.leaveAssistantEvent.observe(
            viewLifecycleOwner
        ) {
            it.consume {
                val isLinphoneAccount =
                    viewModel.domain.value.orEmpty() == corePreferences.defaultDomain
                coreContext.newAccountConfigured(isLinphoneAccount)

                if (coreContext.core.isEchoCancellerCalibrationRequired) {
                    navigateToEchoCancellerCalibration()
                } else {
                    requireActivity().runOnUiThread {
                        showWelcomeDialog()
                    }
                }
            }
        }

        viewModel.invalidCredentialsEvent.observe(
            viewLifecycleOwner
        ) {
            it.consume {
                val dialogViewModel =
                    DialogViewModel(getString(R.string.assistant_error_invalid_credentials))
                val dialog: Dialog = DialogUtils.getDialog(requireContext(), dialogViewModel)

                dialogViewModel.showCancelButton {
                    viewModel.removeInvalidProxyConfig()
                    UpdateStateToDisable()
                    clearUserInputFields()
                    dialog.dismiss()
                }

                dialogViewModel.showDeleteButton(
                    {
                        viewModel.removeInvalidProxyConfig()
                        UpdateStateToDisable()
                        clearUserInputFields()
                        dialog.dismiss()
                    },
                    getString(R.string.assistant_continue_even_if_credentials_invalid)
                )

                dialog.show()
            }
        }

        viewModel.onErrorEvent.observe(
            viewLifecycleOwner
        ) {
            it.consume { message ->
                (requireActivity() as AssistantActivity).showSnackBar(message)
            }
        }
    }

    fun RegistrationUsers() {
        val userCi = viewModel.areInputsEnabled.value.toString()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call = RetrofitClient.webService.getUserCi(userCi)
                if (call.isSuccessful) {
                    val usuario = call.body()
                    usuarioObtenido = usuario
                    if (usuario != null) {
                        // aumentar el campo ci en la tabla buscar
                        CiComparar = userCi
                        withContext(Dispatchers.Main) {
                            if (usuario.status_number == "enable") {
                                showToast("Usuario no disponible")
                                clearUserInputFields()
                            } else {
                                viewModel.username.value = usuario.phone_number
                                viewModel.password.value = usuario.secret_number
                                viewModel.domain.value = getString(R.string.conectDominio)
                                viewModel.displayName.value =
                                    "${usuario.username ?: ""} ${usuario.first_name ?: ""} ${usuario.last_name ?: ""}"
                                viewModel.roleDescriptionDisplay.value = usuario.job_type
                            }
                        }
                    } else {
                        Log.e("API Call", "Error: Usuario nulo")
                        withContext(Dispatchers.Main) {
                            // showToast("Error al encontrar al usuario")
                            showUserNotExist(
                                title = "El Usuario no existe en los registros",
                                message = "Por favor si presento problemas con su registro contactese"
                            )
                        }
                    }
                } else {
                    Log.e(
                        "API Call",
                        "Error al obtener usuario: ${call.code()} ${call.message()}"
                    )
                    withContext(Dispatchers.Main) {
                        // showToast("Error al obtener usuario")
                        showUserNotExist(
                            title = "El Usuario no se pudo cargar correctamente . Intentalo mas tarde",
                            message = "Por favor si presento problemas con su registro "
                        )
                        clearUserInputFields()
                    }
                }
            } catch (e: Exception) {
                Log.e("API Call", "Error al obtener usuario", e)
                withContext(Dispatchers.Main) {
                    // showToast("Error al obtener al usuario desde el inicio")
                    showUserNotExist(
                        title = "El servicio de contactos no esta disponible . Intentalo mas tarde",
                        message = "Por favor si presento problemas con la lista de contactos"
                    )
                    clearUserInputFields()
                }
            }
        }
    }

    /*funcion de actualizar estado*/
    private fun UpdateState(ci: String) {
        if (CiComparar != ci) {
            showToast("Datos no coincide con tu registro")
            return
        }
        usuario.status_number = "enable"
        storedBirthDate = ci
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("UpdateState", "Iniciando actualización de estado para CI: ${usuario.ci}")
                val call = RetrofitClient.webService.updateStatus(ci, usuario)
                if (call.isSuccessful) {
                    val usuarioActualizado = call.body()!!
                    withContext(Dispatchers.Main) {
                        showToast(
                            "Registro de usuario correctamente: ${usuario.ci}"
                        )
                        viewModel.createAccountAndAuthInfo()
                    }
                } else {
                    Log.e(
                        "UpdateState",
                        "Error al actualizar el estado: ${call.code()} ${call.message()}"
                    )
                    withContext(Dispatchers.Main) {
                        showToast("Error al registrarse datos no encontrados")
                    }
                }
            } catch (e: Exception) {
                Log.e("UpdateState", "Error al actualizar el estado: $e")
            }
        }
    }

    private fun UpdateStateToDisable() {
        if (storedBirthDate == null) {
            showToast("No se puede desactivar, carnet no disponible.")
            return
        }

        val birthDateString = storedBirthDate.toString()
        usuario.status_number = "disable"
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(
                    "UpdateStateToDisable",
                    "Iniciando actualización de estado a disponible para CI: ${usuario.ci}"
                )
                val call = RetrofitClient.webService.updateStatus(birthDateString, usuario)
                if (call.isSuccessful) {
                    val usuarioActualizado = call.body()!!
                    withContext(Dispatchers.Main) {
                        showToast("Usuario disponible para su registro ")
                    }
                } else {
                    Log.e(
                        "UpdateStateToDisable",
                        "Error al actualizar el estado: ${call.code()} ${call.message()}"
                    )
                    withContext(Dispatchers.Main) {
                        showToast("Error al actualizar el estado")
                    }
                }
            } catch (e: Exception) {
                Log.e("UpdateStateToDisable", "Error al actualizar el estado a habilitado: $e")
            }
        }
    }

    private fun LoginVerification() {
        // Procede con el registro de la cuenta SIP genérica
        val dialog = DialogUser.VerificationUser(
            requireContext(),
            onOk = { ci ->
                UpdateState(ci)
            },
            onCancel = {
            }
        )
        dialog.show()
    }

    /*limpiar campos los campos*/
    private fun clearUserInputFields() {
        viewModel.username.value = ""
        viewModel.password.value = ""
        viewModel.domain.value = ""
        viewModel.displayName.value = ""
        viewModel.roleDescriptionDisplay.value = ""
        viewModel.areInputsEnabled.value = ""
    }

    // Función para mostrar mensajes de Toast
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    /*analizarlo*/
    fun createAccountIfNotExists() {
        val core = coreContext.core
        if (core.accountList.isNotEmpty()) {
            val hasFailedAccount = core.accountList.any { account ->
                account.state == RegistrationState.Failed
            }

            if (hasFailedAccount) {
                // Manejar el caso de cuenta en estado de fallo
                showFalloDialog()
            } else {
                showFalloDialog()
            }
        } else {
            RegistrationUsers()
        }
    }

    private fun showWelcomeDialog() {
        // Usa los datos almacenados en usuarioObtenido
        val usuario = usuarioObtenido
        if (usuario != null) {
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle(
                    "Bienvenido, ${usuario.username ?: ""} ${usuario.first_name ?: ""} ${usuario.last_name ?: ""}"
                ) // Personaliza el título
                .setMessage(
                    "Tu cuenta ha sido configurada correctamente.\n\n" +
                        "Nombre: ${usuario.username ?: ""} ${usuario.first_name ?: ""} ${usuario.last_name ?: ""}\n" +
                        "Cargo: ${usuario.job_type}\n" + "Numero Asigando: ${usuario.phone_number}\n"

                )
                .setCancelable(false)
                .setPositiveButton("Aceptar") { dialog, _ ->
                    dialog.dismiss()
                    requireActivity().finish()
                }
                .create()
            dialog.show()
        } else {
            showToast("Datos del usuario no disponibles.")
        }
    }

    private fun showFalloDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Ya existe un NUMERO REGISTRADO EN LA APLICACION")
            .setMessage("Por favor si presento problemas con su registro contactese con IT")
            .setCancelable(false)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
                requireActivity().finish()
            }
            .create()
        dialog.show()
    }

    private fun showUserNotExist(title: String, message: String) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }
}
