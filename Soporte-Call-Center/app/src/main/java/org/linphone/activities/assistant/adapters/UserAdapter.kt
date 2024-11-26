package org.linphone.activities.assistant.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.text.TextPaint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.linphone.R
import org.linphone.activities.assistant.api.User

class UserAdapter(
    var context: Context,
    var listausuarios: ArrayList<User>,
    private val listener: RegistroClickListener

) : RecyclerView.Adapter<UserAdapter.UsuarioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(
            R.layout.staff_contact_book,
            parent,
            false
        )
        return UsuarioViewHolder(vista)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = listausuarios.get(position)
        val userName = listOfNotNull(usuario.username, usuario.first_name, usuario.last_name)
            .filter { it.isNotBlank() } // Filtrar valores que son cadenas vacías o espacios en blanco
            .joinToString(" ")

        holder.tvUsername.text = userName
        holder.tvPhonenumber.text = usuario.phone_number
        holder.tvRole.text = usuario.job_type

        holder.btnLlamar.setOnClickListener {
            Log.d("UserAdapter", "Botón de llamar clickeado")
            listener.onCall(usuario.phone_number, userName)
        }

        loadInitialsImage(holder.tvImage, usuario.username)
    }

    override fun getItemCount(): Int {
        return listausuarios.size
    }

    inner class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvImage = itemView.findViewById<ImageView>(R.id.tvImage)
        val tvUsername = itemView.findViewById<TextView>(R.id.tvUsename)
        val tvPhonenumber = itemView.findViewById<TextView>(R.id.tvPhoneNumber)
        val tvRole = itemView.findViewById<TextView>(R.id.tvRoleDescription)
        val btnLlamar = itemView.findViewById<Button>(R.id.btnLlamar)
    }

    private fun loadInitialsImage(imageView: ImageView, username: String?) {
        val initial = username?.firstOrNull()?.uppercaseChar().toString() // toUpperCase()
        val drawable = createInitialsDrawable(initial, imageView.context)
        imageView.setImageDrawable(drawable)
    }

    private fun createInitialsDrawable(initial: String, context: Context): BitmapDrawable {
        val size = context.resources.getDimensionPixelSize(R.dimen.contact_avatar_size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Fondo circular
        val paint = Paint().apply {
            color = Color.parseColor("#CCCCCC") // Color de fondo
            isAntiAlias = true
        }
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

        // Texto
        val textPaint = TextPaint().apply {
            color = Color.WHITE // Color del texto
            textSize = context.resources.getDimension(R.dimen.contact_avatar_text_size)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val textBounds = Rect()
        textPaint.getTextBounds(initial, 0, initial.length, textBounds)
        val x = size / 2f
        val y = size / 2f - (textPaint.descent() + textPaint.ascent()) / 2
        canvas.drawText(initial, x, y, textPaint)

        return BitmapDrawable(context.resources, bitmap)
    }

    interface RegistroClickListener {
        fun onCall(phoneNumber: String, userName: String)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateFilterUser(listausuarios: ArrayList<User>) {
        this.listausuarios = listausuarios
        notifyDataSetChanged()
    }
}
