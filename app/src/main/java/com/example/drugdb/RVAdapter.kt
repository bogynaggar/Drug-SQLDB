package com.example.drugdb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RVAdapter(private val drugArrayList: MutableList<Drug>):
    RecyclerView.Adapter<RVAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)  {

        val drugID: TextView = itemView.findViewById(R.id.TV_id)
        val drugName: TextView = itemView.findViewById(R.id.TV_name)
        val drugAI: TextView = itemView.findViewById(R.id.TV_AI)
        val drugCategory: TextView = itemView.findViewById(R.id.TV_category)
        val drugPrice: TextView = itemView.findViewById(R.id.TV_price)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return drugArrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val drug :Drug = drugArrayList[position]
        holder.drugID.text = drug.id.toString()
        holder.drugName.text = drug.name
        holder.drugAI.text = drug.activeI
        holder.drugCategory.text = drug.category
        holder.drugPrice.text = drug.price.toString()

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(position, drug)

        }
    }


    // Set the click listener for the adapter
    fun setOnClickListener(onClickListener: RVAdapter.OnClickListener) {
        this.onClickListener = onClickListener
    }




    // Interface for the click listener
    interface OnClickListener : View.OnClickListener {
        fun onClick(position: Int, model: Drug)
        override fun onClick(v: View?) {
        }
    }

}