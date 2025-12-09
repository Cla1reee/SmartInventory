package com.smartinventory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartinventory.R
import com.smartinventory.model.Product
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private var productList: List<Product> = listOf(),
    private val onClick: (Product) -> Unit // Untuk persiapan fitur Edit/Hapus nanti
    ) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    // Class pemegang tampilan
    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvItemName)
        val tvPrice: TextView = view.findViewById(R.id.tvItemPrice)
        val tvStock: TextView = view.findViewById(R.id.tvItemStock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.tvName.text = product.namaBarang
        holder.tvStock.text = "Stok: ${product.stok}"

        // Format Rupiah
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        holder.tvPrice.text = formatRupiah.format(product.harga)

        // Klik Item
        holder.itemView.setOnClickListener {
            onClick(product)
        }
    }

    override fun getItemCount() = productList.size

    // Fungsi untuk update data dari Activity
    fun updateData(newList: List<Product>) {
        productList = newList
        notifyDataSetChanged()
    }
}