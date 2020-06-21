package com.orbital.snus.modules.Forum.MainPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.orbital.snus.R
import com.orbital.snus.dashboard.Today.TodayEventAdapter
import com.orbital.snus.data.UserEvent
import kotlinx.android.synthetic.main.module_forum_recycler_enrolled_individual.view.*

class MainPageAdapter(val moduleList: List<String>) :
    RecyclerView.Adapter<MainPageAdapter.ModuleViewHolder>() {

    class ModuleViewHolder(val textView: View) : RecyclerView.ViewHolder(textView)

    override fun getItemCount() = moduleList.size


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainPageAdapter.ModuleViewHolder {
        // create a new view
        // EventDailyView shows the layout of each view
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_daily_view, parent, false)
        // set the view's size, margins, paddings and layout parameters

        return MainPageAdapter.ModuleViewHolder(
            textView
        )
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        val module = moduleList[position]

        holder.textView.module_enrolled_individual_name.text = module

        holder.textView.setOnClickListener(onClickListener(module))
    }

    private fun onClickListener(moduleName: String): View.OnClickListener? {
        return View.OnClickListener {
            val bundle = Bundle()
            bundle.putString("module", moduleName)
            it.findNavController().navigate(R.id.action_mainPageFragment_to_individualModuleFragment, bundle)
        }
    }
}