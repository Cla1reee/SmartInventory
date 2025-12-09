package com.smartinventory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartinventory.R
import com.smartinventory.model.Transaction
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(private var list: List<Transaction> = listOf())
    : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvTransName)
        val tvDate: TextView = view.findViewById(R.id.tvTransDate)
        val tvQty: TextView = view.findViewById(R.id.tvTransQty)
        val tvTotal: TextView = view.findViewById(R.id.tvTransTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.tvName.text = item.namaBarang
        holder.tvQty.text = "${item.jumlahBeli} pcs"

        // Format Rupiah
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        holder.tvTotal.text = formatRupiah.format(item.totalHarga)

        // Format Tanggal
        val dateFormat = SimpleDateFormat("dd MMM HH:mm", Locale("id", "ID"))
        holder.tvDate.text = dateFormat.format(item.tanggal)
    }

    override fun getItemCount() = list.size

    fun updateData(newList: List<Transaction>) {
        list = newList
        notifyDataSetChanged()
    }
}