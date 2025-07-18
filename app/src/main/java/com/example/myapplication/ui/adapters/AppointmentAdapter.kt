package com.example.myapplication.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.Appointment

class AppointmentAdapter : ListAdapter<Appointment, AppointmentAdapter.AppointmentViewHolder>(DiffCallback()) {

    class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val monthText: TextView = view.findViewById(R.id.monthText)
        private val dayText: TextView = view.findViewById(R.id.dayText)
        private val typeText: TextView = view.findViewById(R.id.typeText)
        private val doctorText: TextView = view.findViewById(R.id.doctorText)

        fun bind(appointment: Appointment) {
            monthText.text = appointment.date.split("/")[0] // Extract month
            dayText.text = appointment.date.split("/")[1] // Extract day
            typeText.text = appointment.type
            doctorText.text = appointment.doctorName
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Appointment>() {
        override fun areItemsTheSame(oldItem: Appointment, newItem: Appointment) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Appointment, newItem: Appointment) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}