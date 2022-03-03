package com.example.testrecyclertree.viewbinder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testrecyclertree.R
import com.example.testrecyclertree.bean.Dir
import com.example.testrecyclertree.lib.TreeNode
import com.example.testrecyclertree.lib.TreeViewBinder


class DirectoryNodeBinder : TreeViewBinder<DirectoryNodeBinder.ViewHolder>() {

    override fun provideViewHolder(itemView: View): ViewHolder {
        return ViewHolder(itemView)
    }

    override fun bindView(holder: TreeViewBinder.ViewHolder, position: Int, node: TreeNode<*>) {
        val dirNode = node.content as Dir
        val rotateDegree = if (node.isExpand) 90 else 0

        (holder as ViewHolder).apply {
            ivArrow.rotation = rotateDegree.toFloat()
            tvName.text = dirNode.dirName
            ivArrow.rotation = 0F
            ivArrow.setImageResource(R.drawable.ic_keyboard_arrow_right_black_18dp)

            if (node.isLeaf) ivArrow.visibility = View.INVISIBLE
            else ivArrow.visibility = View.VISIBLE
        }

    }

    override val layoutId = R.layout.item_dir

    class ViewHolder(rootView: View) : TreeViewBinder.ViewHolder(rootView) {

        var ivArrow: ImageView = rootView.findViewById(R.id.iv_arrow) as ImageView
        var tvName: TextView = rootView.findViewById(R.id.tv_name)

    }
}