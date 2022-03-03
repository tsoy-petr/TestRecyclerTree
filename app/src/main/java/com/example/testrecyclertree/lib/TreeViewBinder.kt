package com.example.testrecyclertree.lib

import android.view.View
import androidx.annotation.IdRes

import androidx.recyclerview.widget.RecyclerView


abstract class TreeViewBinder<VH : TreeViewBinder.ViewHolder> : LayoutItemType {

    abstract fun provideViewHolder(itemView: View): VH

    abstract fun bindView(holder: ViewHolder, position: Int, node: TreeNode<*>)

    open class ViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
         fun <T : View?> findViewById(@IdRes id: Int): T {
            return itemView.findViewById<View>(id) as T
        }
    }
}