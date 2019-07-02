package com.applicaster.liveswitcher.screen.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.applicaster.atom.model.APAtomEntry
import com.applicaster.liveswitcher.R
import com.applicaster.liveswitcher.model.ChannelModel
import com.applicaster.liveswitcher.utils.Constants.EXTENSION_APPLICASTER_CHANNEL_ID
import com.applicaster.liveswitcher.utils.Constants.EXTENSION_END_TIME
import com.applicaster.liveswitcher.utils.Constants.EXTENSION_START_TIME
import com.applicaster.liveswitcher.utils.LiveSwitcherUtil
import com.applicaster.util.AlarmManagerUtil
import com.applicaster.util.PreferenceUtil
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_program.view.*

class ProgramAdapter(val items: List<APAtomEntry>, val channels: List<ChannelModel.Channel>?,
                     val context: Context?, val listener: OnProgramClickListener,
                     val isLive: Boolean) : RecyclerView.Adapter<ProgramViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramViewHolder {
        return ProgramViewHolder(LayoutInflater.from(context).inflate(R.layout.item_program, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ProgramViewHolder, position: Int) {
        holder.tvProgramName.text = items[position].title
        holder.tvTime.text = LiveSwitcherUtil.getTimeField(
                items[position].extensions?.get(EXTENSION_START_TIME).toString(),
                items[position].extensions?.get(EXTENSION_END_TIME).toString())

        context?.let {
            if (AlarmManagerUtil.isAlarmSet(context, items[position].id)) {
                holder.ivAlert.setImageResource(R.drawable.alert_set)
            } else {
                holder.ivAlert.setImageResource(R.drawable.alert)
            }

            Glide.with(context).asDrawable().load(items[position].mediaGroups[0].mediaItems[0].src)
                    .into(holder.ivImage)
            Glide.with(context).asDrawable().load(LiveSwitcherUtil.getChannelIconUrl(channels,
                    items[position].extensions?.get(EXTENSION_APPLICASTER_CHANNEL_ID).toString()))
                    .into(holder.ivChannel)
        }

        if (isLive) {
            holder.ivAlert.visibility = View.GONE
            holder.itemView.setOnClickListener {
                listener.onProgramClicked(items[position])
                holder.tvIsWatching.visibility = View.VISIBLE
                PreferenceUtil.getInstance().setIntPref("item_selected_position", position)
                notifyDataSetChanged()
            }
        } else {
            holder.ivAlert.visibility = View.VISIBLE
        }

        if (isLive && (PreferenceUtil.getInstance()
                        .getIntPref("item_selected_position", -1) == position)) {
            holder.tvIsWatching.visibility = View.VISIBLE
        } else {
            // avoid recycling issues
            holder.tvIsWatching.visibility = View.GONE
        }

        holder.ivAlert.setOnClickListener {
            context?.let {
                if (AlarmManagerUtil.isAlarmSet(it, items[position].id)) {
                    listener.removeReminder(items[position])
                    holder.ivAlert.setImageResource(R.drawable.alert)
                } else {
                    listener.addReminder(items[position])
                    holder.ivAlert.setImageResource(R.drawable.alert_set)
                }
            }
        }
    }

    interface OnProgramClickListener {
        fun onProgramClicked(atomEntry: APAtomEntry)
        fun addReminder(atomEntry: APAtomEntry)
        fun removeReminder(atomEntry: APAtomEntry)
    }
}

class ProgramViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var tvProgramName = view.tv_program_name
    var ivImage = view.iv_image
    var ivAlert = view.iv_alert
    var tvTime = view.tv_time
    var tvIsWatching = view.tv_is_watching
    var ivChannel = view.iv_channel
}
