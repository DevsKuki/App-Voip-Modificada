package org.linphone.activities.assistant.api

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import org.linphone.R
import org.linphone.activities.main.viewmodels.DialogViewModel
import org.linphone.databinding.AssistenteGenericAccountDialogUsersBinding
import org.linphone.databinding.VoipDialogBinding

class DialogUser {
    companion object {
        fun VerificationUser(context: Context, onOk: (String) -> Unit, onCancel: () -> Unit): Dialog {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            /* tener en cuenta siempre que el dialogbinding lo crea a partir del nombre del documento ejemplo userdialog entonces es userdialogbinding*/
            val binding: AssistenteGenericAccountDialogUsersBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.assistente_generic_account_dialog_users,
                null,
                false
            )

            dialog.setContentView(binding.root)

            val d: Drawable = ColorDrawable(
                ContextCompat.getColor(dialog.context, R.color.black_color)
            )
            d.alpha = 200
            dialog.window
                ?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
            dialog.window?.setBackgroundDrawable(d)

            binding.btnAceptarFecha.setOnClickListener {
                val feha_nac = binding.etFechaNacimiento.text.toString()
                onOk(feha_nac)
                dialog.dismiss()
            }

            binding.btnCancelarFecha.setOnClickListener {
                onCancel()
                dialog.dismiss()
            }

            return dialog
        }

        fun CallDialog(context: Context, viewModel: DialogViewModel): Dialog {
            val dialog = Dialog(context, R.style.AppTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            val binding: VoipDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.voip_dialog,
                null,
                false
            )
            binding.viewModel = viewModel
            dialog.setContentView(binding.root)

            val d: Drawable = ColorDrawable(
                ContextCompat.getColor(dialog.context, R.color.voip_dark_gray)
            )
            d.alpha = 166
            dialog.window
                ?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
            dialog.window?.setBackgroundDrawable(d)
            return dialog
        }

        fun TransferCallDialog(context: Context, viewModel: DialogViewModel): Dialog {
            val dialog = Dialog(context, R.style.AppTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            val binding: VoipDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.voip_dialog,
                null,
                false
            )
            binding.viewModel = viewModel
            dialog.setContentView(binding.root)

            val d: Drawable = ColorDrawable(
                ContextCompat.getColor(dialog.context, R.color.voip_dark_gray)
            )
            d.alpha = 166
            dialog.window
                ?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
            dialog.window?.setBackgroundDrawable(d)
            return dialog
        }

        fun SearchUserDialog(context: Context, viewModel: DialogViewModel): Dialog {
            val dialog = Dialog(context, R.style.AppTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            val binding: VoipDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.voip_dialog,
                null,
                false
            )
            binding.viewModel = viewModel
            dialog.setContentView(binding.root)

            val d: Drawable = ColorDrawable(
                ContextCompat.getColor(dialog.context, R.color.voip_dark_gray)
            )
            d.alpha = 166
            dialog.window
                ?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
            dialog.window?.setBackgroundDrawable(d)
            return dialog
        }

        fun RegistrationUserDialog(context: Context, viewModel: DialogViewModel): Dialog {
            val dialog = Dialog(context, R.style.AppTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            val binding: VoipDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.voip_dialog,
                null,
                false
            )
            binding.viewModel = viewModel
            dialog.setContentView(binding.root)

            val d: Drawable = ColorDrawable(
                ContextCompat.getColor(dialog.context, R.color.voip_dark_gray)
            )
            d.alpha = 166
            dialog.window
                ?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
            dialog.window?.setBackgroundDrawable(d)
            return dialog
        }

        fun NotificationDialog(context: Context, viewModel: DialogViewModel): Dialog {
            val dialog = Dialog(context, R.style.AppTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            val binding: VoipDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.voip_dialog,
                null,
                false
            )
            binding.viewModel = viewModel
            dialog.setContentView(binding.root)

            val d: Drawable = ColorDrawable(
                ContextCompat.getColor(dialog.context, R.color.voip_dark_gray)
            )
            d.alpha = 166
            dialog.window
                ?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
            dialog.window?.setBackgroundDrawable(d)
            return dialog
        }
    }
}
