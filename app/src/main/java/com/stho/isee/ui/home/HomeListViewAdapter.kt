package com.stho.isee.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.stho.isee.databinding.FragmentHomeListEntryBinding

class HomeListViewAdapter(
    private val activity: FragmentActivity,
    private val onClickItem: (HomeListEntry) -> Unit,
) : RecyclerView.Adapter<HomeListViewAdapter.ViewHolder>() {

    private var list: List<HomeListEntry> = listOf()

    override fun getItemCount(): Int =
        list.size

    private fun getItem(position: Int): HomeListEntry =
        list[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val binding = FragmentHomeListEntryBinding.inflate(inflater, parent, false)
        return ViewHolder(context, binding);
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val entry = getItem(position)
        viewHolder.binding.category.text = entry.category
        viewHolder.binding.size.text = entry.size.toString()
        viewHolder.itemView.setOnClickListener { open(entry); }
    }

    class ViewHolder(context: Context, val binding: FragmentHomeListEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        // private val gestureDetector = GestureDetector(context, GestureDetector.SimpleOnGestureListener())
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(newList: List<HomeListEntry>) {
        list = newList
        notifyDataSetChanged()
    }

    private fun open(entry: HomeListEntry) {
        onClickItem(entry)
    }
}