package com.example.testrecyclertree.lib

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


class TreeViewAdapter(
    nodes: List<TreeNode<*>> = listOf(),
    private val viewBinders: List<TreeViewBinder<out TreeViewBinder.ViewHolder>>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //private val viewBinders: List<TreeViewBinder<RecyclerView.ViewHolder>>
    private val displayNodes: MutableList<TreeNode<*>> = mutableListOf()
    private var padding = 30
    private var onTreeNodeListener: OnTreeNodeListener? = null
    private var toCollapseChild = false

    init {
//        nodes.let { findDisplayNodes(it) }
        findDisplayNodes(nodes)
    }

    private fun findDisplayNodes(nodes: List<TreeNode<*>>) {
        for (node in nodes) {
            displayNodes.add(node)
            if (!node.isLeaf && node.isExpand)
                findDisplayNodes(node.getChildList())
        }
    }

    override fun getItemViewType(position: Int): Int {
        return displayNodes[position].content.layoutId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(viewType, parent, false)
        if (viewBinders.size == 1) return viewBinders[0].provideViewHolder(v)
        for (viewBinder in viewBinders) {
            if (viewBinder.layoutId == viewType) {
                return viewBinder.provideViewHolder(v)
            }
        }
        return viewBinders[0].provideViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
        if (!payloads.isEmpty()) {
            val b = payloads[0] as Bundle
            for (key in b.keySet()) {
                when (key) {
                    KEY_IS_EXPAND -> if (onTreeNodeListener != null) onTreeNodeListener!!.onToggle(
                        b.getBoolean(
                            key
                        ), holder
                    )
                }
            }
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            holder.itemView.setPaddingRelative(displayNodes!![position].height * padding, 3, 3, 3)
        } else {
            holder.itemView.setPadding(displayNodes!![position].height * padding, 3, 3, 3)
        }
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val selectedNode = displayNodes[holder.layoutPosition]
                // Prevent multi-click during the short interval.
                try {
                    val lastClickTime = holder.itemView.tag as Long
                    if (System.currentTimeMillis() - lastClickTime < 500) return
                } catch (e: Exception) {
                    holder.itemView.tag = System.currentTimeMillis()
                }
                holder.itemView.tag = System.currentTimeMillis()
                if (onTreeNodeListener != null && onTreeNodeListener!!.onClick(
                        selectedNode,
                        holder
                    )
                ) return
                if (selectedNode.isLeaf) return
                // This TreeNode was locked to click.
                if (selectedNode.isLocked) return
                val isExpand = selectedNode.isExpand
                val positionStart = displayNodes.indexOf(selectedNode) + 1
                if (!isExpand) {
                    notifyItemRangeInserted(
                        positionStart,
                        addChildNodes(selectedNode, positionStart)
                    )
                } else {
                    notifyItemRangeRemoved(positionStart, removeChildNodes(selectedNode, true))
                }
            }
        })
        for (viewBinder in viewBinders) {
            if (displayNodes[position].content.layoutId == viewBinder.layoutId) {
                viewBinder.bindView(
                    holder as TreeViewBinder.ViewHolder, position,
                    displayNodes[position]
                )
            }
        }
    }

    private fun addChildNodes(pNode: TreeNode<*>, startIndex: Int): Int {
        val childList = pNode.getChildList()
        var addChildCount = 0
        for (treeNode in childList) {
            displayNodes.add(startIndex + addChildCount++, treeNode)
            if (treeNode.isExpand) {
                addChildCount += addChildNodes(treeNode, startIndex + addChildCount)
            }
        }
        if (!pNode.isExpand) pNode.toggle()
        return addChildCount
    }

    private fun removeChildNodes(pNode: TreeNode<*>, shouldToggle: Boolean = true): Int {
        if (pNode.isLeaf) return 0
        val childList = pNode.getChildList()
        var removeChildCount = childList.size
        displayNodes.removeAll(childList)
        for (child in childList) {
            if (child.isExpand) {
                if (toCollapseChild) child.toggle()
                removeChildCount += removeChildNodes(child, false)
            }
        }
        if (shouldToggle) pNode.toggle()
        return removeChildCount
    }

    override fun getItemCount(): Int {
        return displayNodes.size
    }

    fun setPadding(padding: Int) {
        this.padding = padding
    }

    fun ifCollapseChildWhileCollapseParent(toCollapseChild: Boolean) {
        this.toCollapseChild = toCollapseChild
    }

    fun setOnTreeNodeListener(onTreeNodeListener: OnTreeNodeListener?) {
        this.onTreeNodeListener = onTreeNodeListener
    }

    interface OnTreeNodeListener {
        /**
         * called when TreeNodes were clicked.
         * @return weather consume the click event.
         */
        fun onClick(node: TreeNode<*>, holder: RecyclerView.ViewHolder?): Boolean

        /**
         * called when TreeNodes were toggle.
         * @param isExpand the status of TreeNodes after being toggled.
         */
        fun onToggle(isExpand: Boolean, holder: RecyclerView.ViewHolder?)
    }

    fun refresh(treeNodes: List<TreeNode<*>>) {
        displayNodes.clear()
        findDisplayNodes(treeNodes)
        notifyDataSetChanged()
    }

    val displayNodesIterator: Iterator<TreeNode<*>>
        get() = displayNodes.iterator()

    private fun notifyDiff(temp: List<TreeNode<*>>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return temp.size
            }

            override fun getNewListSize(): Int {
                return displayNodes.size
            }

            // judge if the same items
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return this@TreeViewAdapter.areItemsTheSame(
                    temp[oldItemPosition],
                    displayNodes[newItemPosition]
                )
            }

            // if they are the same items, whether the contents has bean changed.
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return this@TreeViewAdapter.areContentsTheSame(
                    temp[oldItemPosition],
                    displayNodes[newItemPosition]
                )
            }

            override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                return this@TreeViewAdapter.getChangePayload(
                    temp[oldItemPosition],
                    displayNodes[newItemPosition]
                )
            }
        })
        diffResult.dispatchUpdatesTo(this)
    }

    private fun getChangePayload(oldNode: TreeNode<*>, newNode: TreeNode<*>): Any? {
        val diffBundle = Bundle()
        if (newNode.isExpand != oldNode.isExpand) {
            diffBundle.putBoolean(KEY_IS_EXPAND, newNode.isExpand)
        }
        return if (diffBundle.size() == 0) null else diffBundle
    }

    // For DiffUtil, if they are the same items, whether the contents has bean changed.
    private fun areContentsTheSame(oldNode: TreeNode<*>, newNode: TreeNode<*>): Boolean {
        return oldNode.content == newNode.content && oldNode.isExpand == newNode.isExpand
    }

    // judge if the same item for DiffUtil
    private fun areItemsTheSame(oldNode: TreeNode<*>, newNode: TreeNode<*>): Boolean {
        return oldNode.content == newNode.content
    }

    /**
     * collapse all root nodes.
     */
    fun collapseAll() {
        // Back up the nodes are displaying.
        val temp = backupDisplayNodes()
        //find all root nodes.
        val roots: MutableList<TreeNode<*>> = ArrayList()
        for (displayNode in displayNodes) {
            if (displayNode.isRoot) roots.add(displayNode)
        }
        //Close all root nodes.
        for (root in roots) {
            if (root.isExpand) removeChildNodes(root)
        }
        notifyDiff(temp)
    }

    private fun backupDisplayNodes(): List<TreeNode<*>> {
        val temp: MutableList<TreeNode<*>> = ArrayList()
        for (displayNode in displayNodes) {
            try {
                temp.add(displayNode.clone())
            } catch (e: CloneNotSupportedException) {
                temp.add(displayNode)
            }
        }
        return temp
    }

    fun collapseNode(pNode: TreeNode<*>) {
        val temp = backupDisplayNodes()
        removeChildNodes(pNode)
        notifyDiff(temp)
    }

    fun collapseBrotherNode(pNode: TreeNode<*>) {
        val temp = backupDisplayNodes()
        if (pNode.isRoot) {
            val roots: MutableList<TreeNode<*>> = ArrayList()
            for (displayNode in displayNodes) {
                if (displayNode.isRoot) roots.add(displayNode)
            }
            //Close all root nodes.
            for (root in roots) {
                if (root.isExpand && root != pNode) removeChildNodes(root)
            }
        } else {
            val parent = pNode.getParent() ?: return
            val childList = parent.getChildList()
            for (node in childList) {
                if (node == pNode || !node.isExpand) continue
                removeChildNodes(node)
            }
        }
        notifyDiff(temp)
    }

    companion object {
        private const val KEY_IS_EXPAND = "IS_EXPAND"
    }


}