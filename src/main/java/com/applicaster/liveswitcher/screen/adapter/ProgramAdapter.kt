package com.applicaster.liveswitcher.screen.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.applicaster.atom.model.APAtomEntry
import com.applicaster.liveswitcher.R
import com.applicaster.liveswitcher.model.ProgramModel
import com.applicaster.util.ui.Toaster
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_program.view.*

class ProgramAdapter(val items: List<APAtomEntry>, val context: Context?,
                     val listener: OnProgramClickListener, val isLive: Boolean) : RecyclerView.Adapter<ProgramViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramViewHolder {
        return ProgramViewHolder(LayoutInflater.from(context).inflate(R.layout.item_program, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ProgramViewHolder, position: Int) {
        holder.tvProgramName.text = items[position].title
        holder.tvStartTime.text = items[position].extensions?.get("start_time").toString()
        context?.let {
            Glide.with(context).asDrawable().load(items[position].mediaGroups[0].mediaItems[0].src)
                    .into(holder.ivImage)
        }

        if (isLive) {
            holder.ivAlert.visibility = View.GONE
            holder.itemView.setOnClickListener {
                listener.onProgramClicked(items[position])
            }
        } else {
            holder.ivAlert.visibility = View.VISIBLE
        }
    }

    interface OnProgramClickListener {
        fun onProgramClicked(atomEntry: APAtomEntry)
    }
}

class ProgramViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var tvProgramName = view.tv_program_name
    var ivImage = view.iv_image
    var ivAlert = view.iv_alert
    var tvStartTime = view.tv_start_time
}
