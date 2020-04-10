package com.quanticheart.searchbar.databaseSearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quanticheart.searchbar.R
import kotlinx.android.synthetic.main.m_cell_searchbar_history.view.*

class HistorySearchAdapter(private val recyclerView: RecyclerView) :
    RecyclerView.Adapter<HistorySearchAdapter.HistoryViewHolder>() {

    private val databaseList = ArrayList<String>()

    init {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(recyclerView.context, RecyclerView.VERTICAL, false)
            adapter = this@HistorySearchAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder =
        HistoryViewHolder(
            LayoutInflater.from(recyclerView.context).inflate(
                R.layout.m_cell_searchbar_history,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = databaseList.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(databaseList[position])
    }

    fun addList(list: ArrayList<String>) {
        if (list.size > 0) {
            databaseList.addAll(list)
            notifyDataSetChanged()
        }
    }

    fun addNewHistory(searchText: String) {
        if (searchText.isNotEmpty()) {
            databaseList.add(searchText)
            notifyDataSetChanged()
        }
    }

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(label: String) {
            itemView.mLabelHistory.text = label
        }
    }
}