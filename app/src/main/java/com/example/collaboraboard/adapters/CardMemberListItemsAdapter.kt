package com.example.collaboraboard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.collaboraboard.R
import com.example.collaboraboard.models.SelectedMembers
import de.hdodenhof.circleimageview.CircleImageView

open class CardMemberListItemsAdapter(
    private val context: Context,
    private val list: ArrayList<SelectedMembers>,
    private val assignedMembers: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_card_selected_members,
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

        if (holder is MyViewHolder){
            if (position == list.size -1 && assignedMembers){
                holder.itemView.findViewById<CircleImageView>(R.id.iv_add_member).visibility = View.VISIBLE
                holder.itemView.findViewById<CircleImageView>(R.id.iv_selected_member_image).visibility = View.GONE
            }else{
                holder.itemView.findViewById<CircleImageView>(R.id.iv_add_member).visibility = View.GONE
                holder.itemView.findViewById<CircleImageView>(R.id.iv_selected_member_image).visibility = View.VISIBLE

                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(holder.itemView.findViewById<CircleImageView>(R.id.iv_selected_member_image))
            }

            holder.itemView.setOnClickListener {
                if (onClickListener != null){
                    onClickListener!!.onClick()
                }
            }
        }
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)

    interface OnClickListener{
        fun onClick()
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }
}