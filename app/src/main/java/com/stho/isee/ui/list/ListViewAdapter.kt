package com.stho.isee.ui.list

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.stho.isee.core.Entry
import com.stho.isee.databinding.FragmentHomeListEntryBinding
import com.stho.isee.databinding.FragmentListEntryBinding
import com.stho.isee.utilities.toDateTimeString

class ListViewAdapter(
    private val activity: FragmentActivity,
    private val onClickItem: (Entry) -> Unit,
) : RecyclerView.Adapter<ListViewAdapter.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private var list: List<Entry> = listOf()

    override fun getItemCount(): Int =
        list.size

    private fun getItem(position: Int): Entry =
        list[position]

    override fun getItemId(position: Int): Long =
        getItem(position).id.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val binding = FragmentListEntryBinding.inflate(inflater, parent, false)
        return ViewHolder(context, binding);
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val entry = getItem(position)
        viewHolder.binding.title.text = entry.title
        viewHolder.binding.created.text = entry.created.toDateTimeString()
        viewHolder.itemView.setOnClickListener { open(entry); }
    }

    class ViewHolder(context: Context, val binding: FragmentListEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        // private val gestureDetector = GestureDetector(context, GestureDetector.SimpleOnGestureListener())
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(newList: List<Entry>) {
        list = newList
        notifyDataSetChanged()
    }

    private fun open(entry: Entry) {
        onClickItem(entry)
    }
}

