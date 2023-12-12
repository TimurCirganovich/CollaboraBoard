package com.example.colaboraboard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.colaboraboard.R
import com.example.colaboraboard.models.Board

open class BoardItemsAdapter(
    private val conext: Context,
    private var list: ArrayList<Board>
    ): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(conext).inflate(
                R.layout.item_board,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            Glide
                .with(conext)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(holder.itemView.findViewById(R.id.iv_list_board_image))

            holder.itemView.findViewById<TextView>(R.id.tv_board_name).text = model.name
            holder.itemView.findViewById<TextView>(R.id.tv_board_created_by).text = "Created By: ${model.createdBy}"

            holder.itemView.setOnClickListener{
                if(onClickListener != null){
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    interface OnClickListener{
        fun onClick(position: Int, model: Board){

        }
    }

    private class MyViewHolder(view: View): RecyclerView.ViewHolder(view)

}