package org.linphone.activities.assistant.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.linphone.LinphoneApplication
import org.linphone.R
import org.linphone.activities.assistant.adapters.UserAdapter
import org.linphone.activities.assistant.adapters.UserSystemAdapter
import org.linphone.activities.assistant.api.RetrofitClient
import org.linphone.activities.assistant.api.User
import org.linphone.core.RegistrationState
import org.linphone.databinding.ContactUserDirectoryFragmentBinding

@Suppress("DEPRECATION")
class GenericAccountListContactFragment : Fragment() {

    private lateinit var binding: ContactUserDirectoryFragmentBinding
    private var listaUsuarios = arrayListOf<User>()
    lateinit var adatador: UserAdapter
    private var listaSistemas = arrayListOf<User>()
    lateinit var adaptadorSystem: UserSystemAdapter
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ContactUserDirectoryFragmentBinding.inflate(inflater, container, false)
        createAccountIfNotExists()
        setupRecyclerView()
        setupSystemRecyclerView()
        setupSearchFilter()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    private fun getUsersList() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call = RetrofitClient.webService.getUsers()
                if (call.isSuccessful) {
                    listaUsuarios = call.body()!!.listaUsuarios
                    withContext(Dispatchers.Main) {
                        if (isAdded) {
                            setupRecyclerView()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        if (isAdded) {
                            Toast.makeText(
                                requireContext(),
                                "Error al obtener registros",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        Toast.makeText(
                            requireContext(),
                            "Error de conexión",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        context?.let { ctx -> // Usar context de manera segura
            adatador = UserAdapter(
                ctx,
                listaUsuarios,
                object : UserAdapter.RegistroClickListener {
                    override fun onCall(phoneNumber: String, userName: String) {
                        val core =
                            LinphoneApplication.coreContext.core // Obtener la instancia de Core
                        if (phoneNumber.isNotEmpty()) {
                            val sipAddress = phoneNumber
                            val addressToCall =
                                core.interpretUrl(sipAddress)?.apply { displayName = userName }
                            addressToCall?.let {
                                core.invite(it.asString())
                                Log.d("UserAdapter", "Llamando")
                            } ?: run {
                            }
                        } else {
                            if (isAdded) {
                                Toast.makeText(
                                    requireContext(),
                                    "Número de teléfono no válido",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            )
            binding.rvUsuarios.adapter = adatador
            binding.rvUsuarios.layoutManager = LinearLayoutManager(ctx)

            binding.tvWelcomeMessage.visibility = View.VISIBLE
            binding.rvUsuarios.visibility = View.GONE
        }
    }

    // listado de administrativos de sistemas
    private fun getUsersListSystem() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call = RetrofitClient.webService.getUsersSystem()
                if (call.isSuccessful) {
                    listaSistemas = call.body()!!.listaSistemas
                    withContext(Dispatchers.Main) {
                        if (isAdded) {
                            setupSystemRecyclerView()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        if (isAdded) {
                            Toast.makeText(
                                requireContext(),
                                "No se encontraron registros de sistemas",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        Toast.makeText(
                            requireContext(),
                            "Error de conexión: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun setupSystemRecyclerView() {
        context?.let { ctx -> // Usar context de manera segura
            adaptadorSystem = UserSystemAdapter(
                ctx,
                listaSistemas,
                object : UserSystemAdapter.RegistroClickListener {
                    override fun onCall(phoneNumber: String, userName: String) {
                        val core =
                            LinphoneApplication.coreContext.core // Obtener la instancia de Core
                        if (phoneNumber.isNotEmpty()) {
                            val sipAddress = phoneNumber
                            val addressToCall =
                                core.interpretUrl(sipAddress)?.apply { displayName = userName }
                            addressToCall?.let {
                                core.invite(it.asString())
                                Log.d("UserAdapter", "Llamando")
                            } ?: run {
                            }
                        } else {
                            if (isAdded) {
                                Toast.makeText(
                                    requireContext(),
                                    "Número de teléfono no válido",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            )
            binding.rvSistemas.adapter = adaptadorSystem
            binding.rvSistemas.layoutManager = LinearLayoutManager(ctx)
        }
    }

    private fun createAccountIfNotExists() {
        val core = LinphoneApplication.coreContext.core
        if (core.accountList.isNotEmpty()) {
            val hasFailedAccount = core.accountList.any { account ->
                account.state == RegistrationState.Failed
            }
            getUsersList()
            getUsersListSystem()
            Log.d("users", "se mostró los registros")
        } else {
            showFalloDialogUser()
            Log.d("users", "debe registrar")
        }
    }

    private fun showFalloDialogUser() {
        if (isAdded) { // Verificar que el Fragment siga adjunto
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("Error al obtener registros de los Contactos")
                .setMessage("Existe problemas con su registro")
                .setCancelable(false)
                .setPositiveButton("Aceptar") { dialog, _ ->
                    dialog.dismiss()
                    findNavController().navigate(
                        R.id.action_genericAccountListContactFragment_to_dialerFragment
                    )
                    Log.d("user", "se traslado correctamente a dialer fragment")
                }
                .create()
            dialog.show()
        }
    }

    private fun setupSearchFilter() {
        binding.etFilter.addTextChangedListener { userFilter ->
            searchJob?.cancel()
            searchJob = CoroutineScope(Dispatchers.Main).launch {
                delay(300)
                val filterText = userFilter.toString().trim().lowercase()
                // Verificar si el campo de filtro está vacío
                if (filterText.isEmpty()) {
                    // Mostrar mensaje de bienvenida y ocultar el RecyclerView
                    binding.tvWelcomeMessage.visibility = View.VISIBLE
                    binding.rvUsuarios.visibility = View.GONE
                } else {
                    // Realizar el filtrado en segundo plano
                    val userFilterName = withContext(Dispatchers.Default) {
                        listaUsuarios.filter { users ->
                            val fullName = listOfNotNull(
                                users.username?.lowercase(),
                                users.first_name?.lowercase(),
                                users.last_name?.lowercase()
                            ).joinToString(" ")

                            fullName.contains(filterText.lowercase()) || users.phone_number?.lowercase()
                                ?.contains(
                                    filterText
                                ) == true
                        }.take(4).toMutableList()
                    }

                    if (isAdded) {
                        // Si la lista filtrada está vacía, mostrar el mensaje de bienvenida
                        if (userFilterName.isEmpty()) {
                            binding.tvWelcomeMessage.visibility = View.VISIBLE
                            binding.rvUsuarios.visibility = View.GONE
                        } else {
                            // Si hay resultados, mostrar el RecyclerView y ocultar el mensaje
                            binding.tvWelcomeMessage.visibility = View.GONE
                            binding.rvUsuarios.visibility = View.VISIBLE
                            adatador.updateFilterUser(userFilterName as ArrayList<User>)
                        }
                    }
                }
            }
        }
    }
}
