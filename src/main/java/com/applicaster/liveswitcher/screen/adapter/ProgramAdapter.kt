package com.applicaster.liveswitcher.screen.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.applicaster.liveswitcher.R
import com.applicaster.liveswitcher.model.ProgramModel
import com.applicaster.util.ui.Toaster
import kotlinx.android.synthetic.main.item_program.view.*

class ProgramAdapter(val items: List<ProgramModel.Program>, val context: Context?,
                     val listener: OnProgramClickListener) : RecyclerView.Adapter<ProgramViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramViewHolder {
        return ProgramViewHolder(LayoutInflater.from(context).inflate(R.layout.item_program, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ProgramViewHolder, position: Int) {
        holder.tvProgramName.text = items[position].name
        holder.itemView.setOnClickListener {
            Toaster.makeToast(context, "item clicked")
            listener.onProgramClicked("programId")
        }
    }

    interface OnProgramClickListener {
        fun onProgramClicked(programId: String)
    }


}

class ProgramViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var tvProgramName = view.tv_program_name
}
