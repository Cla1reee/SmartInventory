package com.smartinventory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartinventory.R
import com.smartinventory.model.TransactionItem
import com.smartinventory.utils.toRupiah

class CartAdapter(
    private val cartItems: ArrayList<TransactionItem>,
    private val onCartChanged: () -> Unit // Callback agar Activity tahu total harga berubah
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvQty: TextView = itemView.findViewById(R.id.tvQty)
        val tvSubtotal: TextView = itemView.findViewById(R.id.tvSubtotal)
        val btnPlus: Button = itemView.findViewById(R.id.btnPlus)
        val btnMinus: Button = itemView.findViewById(R.id.btnMinus)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart_row, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]

        // 1. Set Teks Dasar
        holder.tvName.text = item.productName

        // Gunakan format Rupiah (Ganti logika ini jika Anda belum punya CurrencyUtils)
        holder.tvPrice.text = item.price.toRupiah()
        holder.tvSubtotal.text = item.subtotal.toRupiah()

        holder.tvQty.text = item.qty.toString()

        // 2. Logika Tombol Plus (+)
        holder.btnPlus.setOnClickListener {
            // Cek logic stok di Activity nanti, di sini naikkan saja dulu
            item.qty++
            item.subtotal = item.price * item.qty
            notifyItemChanged(position) // Refresh baris ini saja
            onCartChanged() // Hitung ulang Total Belanja di Activity
        }

        // 3. Logika Tombol Minus (-)
        holder.btnMinus.setOnClickListener {
            if (item.qty > 1) {
                item.qty--
                item.subtotal = item.price * item.qty
                notifyItemChanged(position)
                onCartChanged()
            }
        }

        // 4. Logika Tombol Hapus (X)
        holder.btnDelete.setOnClickListener {
            cartItems.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, cartItems.size)
            onCartChanged()
        }
    }

    override fun getItemCount(): Int = cartItems.size
}