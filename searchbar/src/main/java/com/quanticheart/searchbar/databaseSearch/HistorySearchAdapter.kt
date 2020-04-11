package com.quanticheart.searchbar.databaseSearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quanticheart.searchbar.R
import com.quanticheart.searchbar.databaseSearch.entity.SearchHistoryModel
import kotlinx.android.synthetic.main.m_cell_searchbar_history.view.*

class HistorySearchAdapter(
    private val recyclerView: RecyclerView,
    private val callbackSelect: (String) -> Unit,
    private val callbackDelete: (Int) -> Unit
) :
    RecyclerView.Adapter<HistorySearchAdapter.HistoryViewHolder>() {

    private val databaseList = ArrayList<SearchHistoryModel>()

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
        holder.bind(databaseList[position].historyText)
        holder.itemView.setOnClickListener {
            callbackSelect(databaseList[position].historyText)
        }
        holder.itemView.mDeleteHistory.setOnClickListener {
            callbackDelete(databaseList[position].id)
            databaseList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    fun addList(list: ArrayList<SearchHistoryModel>) {
        if (list.size > 0) {
            databaseList.addAll(list)
            notifyDataSetChanged()
        }
    }

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(label: String) {
            itemView.mLabelHistory.text = label

        }
    }
}