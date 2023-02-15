package org.turkiye.offlinechat.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.turkiye.offlinechat.data.entity.User
import org.turkiye.offlinechat.databinding.ItemUserInfoBinding
import org.turkiye.offlinechat.interfaces.ItemClickListener

/**
 * Created by ferhat.ozcelik on 12-02-2023.
 */

class UserListAdapter(
    var datalist: List<User>, var itemClickListener: ItemClickListener
) : RecyclerView.Adapter<UserListAdapter.UserHolder>() {

    inner class UserHolder(val binding: ItemUserInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(user: User) {
            binding.apply {
                userName.text = user.userName
                userHost.text = user.userHost

                itemView.setOnClickListener {
                    itemClickListener.customItemListener(user)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val binding =
            ItemUserInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserHolder(binding)
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        holder.bind(datalist[position])
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

}
