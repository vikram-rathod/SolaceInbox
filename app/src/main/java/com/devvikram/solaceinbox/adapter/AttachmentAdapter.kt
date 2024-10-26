package com.devvikram.solaceinbox.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devvikram.solaceinbox.R
import com.devvikram.solaceinbox.model.Attachment
import java.io.InputStream
import kotlin.math.ln
import kotlin.math.pow

class AttachmentAdapter(
    private val context: Context,
    private val attachments: List<Attachment>,
    private val onRemoveClick: (Attachment) -> Unit
) : RecyclerView.Adapter<AttachmentAdapter.AttachmentViewHolder>() {

    inner class AttachmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val attachmentName: TextView = view.findViewById(R.id.attachment_name)
        val removeButton: ImageView = view.findViewById(R.id.cancel_icon)
        val imagePreview: ImageView? = view.findViewById(R.id.image_preview)
        val documentIcon: ImageView? = view.findViewById(R.id.document_icon)
        val otherIcon: ImageView? = view.findViewById(R.id.other_icon)
        val fileSize: TextView = view.findViewById(R.id.file_size)
    }

    override fun getItemViewType(position: Int): Int {
        val attachment = attachments[position]
        return when {
            attachment.fileType.startsWith("image/") -> 1
            attachment.fileType.startsWith("application/") -> 2
            else -> 3
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentViewHolder {
        val layoutId = when (viewType) {
            1 -> R.layout.item_attachment_image
            2 -> R.layout.item_attachment_document
            else -> R.layout.item_attachment_other
        }
        val view = LayoutInflater.from(context).inflate(layoutId, parent, false)
        return AttachmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int) {
        val attachment = attachments[position]
        holder.attachmentName.text = attachment.fileName
        holder.fileSize.text = formatFileSize(attachment.fileSize)

        holder.removeButton.setOnClickListener {
            onRemoveClick(attachment)
        }

        // Set visibility based on file type
        holder.imagePreview?.visibility = if (holder.itemViewType == 1) View.VISIBLE else View.GONE
        holder.documentIcon?.visibility = if (holder.itemViewType == 2) View.VISIBLE else View.GONE
        holder.otherIcon?.visibility = if (holder.itemViewType == 3) View.VISIBLE else View.GONE

        // Load image preview for image attachments
        if (holder.itemViewType == 1) {
            val inputStream: InputStream? = context.contentResolver.openInputStream(Uri.parse(attachment.filePath))
            val bitmap = BitmapFactory.decodeStream(inputStream)
            holder.imagePreview?.setImageBitmap(bitmap)
        }
    }
    @SuppressLint("DefaultLocale")
    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"

        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val magnitude = (ln(bytes.toDouble()) / ln(1024.0)).toInt()

        // Calculate the size in the appropriate unit
        val value = bytes / 1024.0.pow(magnitude.toDouble())

        // Format the string to show one decimal place
        return String.format("%.1f %s", value, units[magnitude])
    }


    override fun getItemCount(): Int = attachments.size
}
