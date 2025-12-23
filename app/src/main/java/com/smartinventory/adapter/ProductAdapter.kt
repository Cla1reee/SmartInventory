package com.smartinventory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartinventory.R
import com.smartinventory.model.Product
import com.smartinventory.utils.toRupiah // Import Utility Anda

class ProductAdapter(
    private var productList: List<Product> = listOf(),
    // Callback ini akan dipanggil saat kasir mengklik barang untuk masuk keranjang
    private val onClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

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

        holder.tvName.text = product.productName
        holder.tvStock.text = "Stok: ${product.stock}"

        // PERBAIKAN: Gunakan Utils, jangan format manual lagi!
        // Pastikan product.price tipe datanya Int/Long/Double sesuai model Anda
        holder.tvPrice.text = product.price.toDouble().toRupiah()

        // Logika Klik: Kirim data produk ke Activity/ViewModel
        holder.itemView.setOnClickListener {
            onClick(product)
        }
    }

    override fun getItemCount() = productList.size

    fun updateData(newList: List<Product>) {
        productList = newList
        notifyDataSetChanged()
    }
}