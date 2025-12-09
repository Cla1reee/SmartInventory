package com.smartinventory.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smartinventory.R
import com.smartinventory.adapter.TransactionAdapter
import com.smartinventory.viewmodel.InventoryViewModel

class HistoryActivity : AppCompatActivity() {

    private lateinit var viewModel: InventoryViewModel
    private lateinit var adapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val rv = findViewById<RecyclerView>(R.id.rvHistory)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = TransactionAdapter()
        rv.adapter = adapter

        viewModel = ViewModelProvider(this).get(InventoryViewModel::class.java)

        viewModel.loadTransactions()

        viewModel.transactionList.observe(this) { list ->
            adapter.updateData(list)
        }
    }
}