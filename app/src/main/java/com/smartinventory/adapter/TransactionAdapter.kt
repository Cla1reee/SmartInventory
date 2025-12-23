package com.smartinventory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartinventory.R
import com.smartinventory.model.Transaction
import com.smartinventory.utils.toRupiah // Gunakan Utility Anda
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
        val transaction = list[position]

        // PERBAIKAN 1: Menangani Nama Barang (Multi-Item)
        // Karena 'items' adalah List, kita gabungkan namanya.
        // Contoh Output: "Indomie Goreng, Teh Kotak, Kopi"
        val gabunganNamaBarang = transaction.items.joinToString(", ") { it.productName }
        holder.tvName.text = if (gabunganNamaBarang.isNotEmpty()) gabunganNamaBarang else "Item dihapus"

        // PERBAIKAN 2: Menghitung Total Qty
        // Menjumlahkan qty dari semua item di dalam list
        val totalQty = transaction.items.sumOf { it.qty }
        holder.tvQty.text = "$totalQty items"

        // PERBAIKAN 3: Format Rupiah (Pakai Utils)
        holder.tvTotal.text = transaction.totalAmount.toRupiah()

        // PERBAIKAN 4: Format Tanggal
        val dateFormat = SimpleDateFormat("dd MMM HH:mm", Locale("id", "ID"))
        if (transaction.transactionDate != null) {
            holder.tvDate.text = dateFormat.format(transaction.transactionDate!!)
        } else {
            holder.tvDate.text = "Memproses..."
        }
    }

    override fun getItemCount() = list.size

    fun updateData(newList: List<Transaction>) {
        list = newList
        notifyDataSetChanged()
    }
}