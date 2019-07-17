package com.applicaster.liveswitcher.screen.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.applicaster.atom.model.APAtomEntry
import com.applicaster.liveswitcher.R
import com.applicaster.liveswitcher.model.ChannelModel
import com.applicaster.liveswitcher.utils.Constants
import com.applicaster.liveswitcher.utils.Constants.CONF_REMINDER_ASSET
import com.applicaster.liveswitcher.utils.Constants.EXTENSION_APPLICASTER_CHANNEL_ID
import com.applicaster.liveswitcher.utils.Constants.EXTENSION_END_TIME
import com.applicaster.liveswitcher.utils.Constants.EXTENSION_START_TIME
import com.applicaster.liveswitcher.utils.Constants.PREFERENCE_ITEM_SELECTED_POSITION
import com.applicaster.liveswitcher.utils.LiveSwitcherUtil
import com.applicaster.util.AlarmManagerUtil
import com.applicaster.util.PreferenceUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ViewTarget
import kotlinx.android.synthetic.main.item_program.view.*
import com.bumptech.glide.request.RequestOptions


class ProgramAdapter(private val items: List<APAtomEntry>, private val channels: List<ChannelModel.Channel>?,
                     private val context: Context?, private val listener: OnProgramClickListener,
                     private val isLive: Boolean) : RecyclerView.Adapter<ProgramViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramViewHolder {
        return ProgramViewHolder(LayoutInflater.from(context).inflate(R.layout.item_program,
                parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ProgramViewHolder, position: Int) {
        holder.tvProgramName.text = items[position].title
        // get formatted date-time
        holder.tvTime.text = LiveSwitcherUtil.getTimeField(
                items[position].extensions?.get(EXTENSION_START_TIME).toString(),
                items[position].extensions?.get(EXTENSION_END_TIME).toString())

        // implementation pending for live tag
//        if (items[position].extensions?.get(EXTENSION_IS_LIVE).toString().toBoolean()) {
//            holder.tvLiveEvent.visibility = View.VISIBLE
//        } else {
//            holder.tvLiveEvent.visibility = View.GONE
//        }

        context?.let {
            val drawable = if (AlarmManagerUtil.isAlarmSet(context, items[position].id)) {
                R.drawable.set_reminder_asset
            } else {
                R.drawable.remove_reminder_asset
            }

            val requestOptions = RequestOptions()
            // set alarm icon depending on if the reminder is set or not
            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions.placeholder(R.drawable.program_card_placeholder))
                    .load(drawable)
                    .into(holder.ivAlert)

            // load image of the program
            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions.placeholder(R.drawable.program_card_placeholder))
                    .load(items[position].mediaGroups[0].mediaItems[0].src)
                    .into(holder.ivImage)

            // load logo of the channel
            Glide.with(context).asDrawable().load(LiveSwitcherUtil.getChannelIconUrl(channels,
                    items[position].extensions?.get(EXTENSION_APPLICASTER_CHANNEL_ID).toString()))
                    .into(holder.ivChannel)
        }

        // if it's live, don't show the alert icon
        if (isLive) {
            holder.ivAlert.visibility = View.GONE
            holder.itemView.setOnClickListener {
                listener.onProgramClicked(items[position])
                holder.tvIsWatching.visibility = View.VISIBLE
                PreferenceUtil.getInstance().setIntPref(PREFERENCE_ITEM_SELECTED_POSITION, position)
                notifyDataSetChanged()
            }
        } else {
            holder.ivAlert.visibility = View.VISIBLE
            holder.itemView.setOnClickListener {
                context?.let {
                    toggleReminder(it, position, holder)
                }
            }
        }

        if (isLive && (PreferenceUtil.getInstance()
                        .getIntPref(PREFERENCE_ITEM_SELECTED_POSITION, 0) == position)) {
            holder.tvIsWatching.visibility = View.VISIBLE
        } else {
            // avoid recycling issues
            holder.tvIsWatching.visibility = View.GONE
        }

        holder.ivAlert.setOnClickListener {
            context?.let {
                toggleReminder(it, position, holder)
            }
        }
    }

    private fun toggleReminder(context: Context, position: Int, holder: ProgramViewHolder):
            ViewTarget<ImageView, Drawable> {
        return if (AlarmManagerUtil.isAlarmSet(context, items[position].id)) {
            listener.removeReminder(items[position])
            Glide.with(context)
                    .load(R.drawable.remove_reminder_asset)
                    .into(holder.ivAlert)
        } else {
            listener.addReminder(items[position])
            Glide.with(context)
                    .load(R.drawable.set_reminder_asset)
                    .into(holder.ivAlert)
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
    var tvLiveEvent = view.tv_live_event

    init {
        tvProgramName.setTextColor(LiveSwitcherUtil.parseColor(
                LiveSwitcherUtil.getParam(Constants.CONF_PROGRAM_TITLE_TEXT_COLOR)))
        tvProgramName.textSize = LiveSwitcherUtil.getFloat(
                LiveSwitcherUtil.getParam(Constants.CONF_PROGRAM_TITLE_FONTSIZE))

        tvTime.setTextColor(LiveSwitcherUtil.parseColor(
                LiveSwitcherUtil.getParam(Constants.CONF_PROGRAM_SCHEDULE_TEXT_COLOR)))
        tvTime.textSize = LiveSwitcherUtil.getFloat(
                LiveSwitcherUtil.getParam(Constants.CONF_PROGRAM_SCHEDULE_FONTSIZE))

        tvIsWatching.text = LiveSwitcherUtil.getParam(Constants.CONF_WATCHING_TAG_TEXT)
        tvIsWatching.setTextColor(LiveSwitcherUtil.parseColor(
                LiveSwitcherUtil.getParam(Constants.CONF_WATCHING_TAG_TEXT_COLOR)))
        tvIsWatching.textSize = LiveSwitcherUtil.getFloat(
                LiveSwitcherUtil.getParam(Constants.CONF_WATCHING_TAG_TEXT_FONTSIZE))
        tvIsWatching.setBackgroundColor(LiveSwitcherUtil.parseColor(
                LiveSwitcherUtil.getParam(Constants.CONF_WATCHING_TAG_TEXT_BACKGROUND_COLOR)))

        tvLiveEvent.text = LiveSwitcherUtil.getParam(Constants.CONF_LIVE_EVENT_TAG_TEXT)
        tvLiveEvent.setTextColor(LiveSwitcherUtil.parseColor(
                LiveSwitcherUtil.getParam(Constants.CONF_LIVE_EVENT_TAG_TEXT_COLOR)))
        tvLiveEvent.textSize = LiveSwitcherUtil.getFloat(
                LiveSwitcherUtil.getParam(Constants.CONF_WATCHING_TAG_TEXT_FONTSIZE))
        tvLiveEvent.setBackgroundColor(LiveSwitcherUtil.parseColor(
                LiveSwitcherUtil.getParam(Constants.CONF_LIVE_EVENT_TAG_TEXT_BACKGROUND_COLOR)))
    }
}
