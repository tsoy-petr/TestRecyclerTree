package com.example.testrecyclertree.viewbinder


import android.view.View
import android.widget.TextView
import com.example.testrecyclertree.R
import com.example.testrecyclertree.bean.File
import com.example.testrecyclertree.lib.TreeNode
import com.example.testrecyclertree.lib.TreeViewBinder


class FileNodeBinder : TreeViewBinder<FileNodeBinder.ViewHolder>() {

    override fun provideViewHolder(itemView: View): ViewHolder {
        return ViewHolder(itemView)
    }

    override fun bindView(holder: TreeViewBinder.ViewHolder, position: Int, node: TreeNode<*>) {
        val fileNode = node.content as File
        (holder as ViewHolder).tvName.text = fileNode.fileName
    }

    override val layoutId: Int
        get() = R.layout.item_file

    inner class ViewHolder(rootView: View) : TreeViewBinder.ViewHolder(rootView) {
        var tvName: TextView = rootView.findViewById(R.id.tv_name)
    }

}