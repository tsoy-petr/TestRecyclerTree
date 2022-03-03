package com.example.testrecyclertree.lib


class TreeNode<T : LayoutItemType>(var content: T) : Cloneable {

    private var parent: TreeNode<*>? = null
    private var childList: MutableList<TreeNode<*>> = mutableListOf()

    var isExpand = false
        private set

    var isLocked = false
        private set

    //the tree high
    var height = UNDEFINE
        get() {
            if (isRoot) field = 0 else if (field == UNDEFINE) field = parent!!.height + 1
            return field
        }
        private set

    val isRoot: Boolean
        get() = parent == null

    val isLeaf: Boolean
        get() = childList.isEmpty()

    fun getChildList(): List<TreeNode<*>> {
        return childList
    }

    init {
        childList = ArrayList()
    }

    fun setChildList(childList: List<TreeNode<T>>) {
        this.childList.clear()
        for (treeNode in childList) {
            addChild(treeNode)
        }
    }

    fun addChild(node: TreeNode<*>): TreeNode<*> {
        childList.add(node)
        node.parent = this
        return this
    }

    fun toggle(): Boolean {
        isExpand = !isExpand
        return isExpand
    }

    fun collapse() {
        if (isExpand) {
            isExpand = false
        }
    }

    fun collapseAll() {
        if (childList.isEmpty()) {
            return
        }
        for (child in childList) {
            child.collapseAll()
        }
    }

    fun expand() {
        if (!isExpand) {
            isExpand = true
        }
    }

    fun expandAll() {
        expand()
        if (childList.isEmpty()) {
            return
        }
        for (child in childList) {
            child.expandAll()
        }
    }

    fun setParent(parent: TreeNode<T>) {
        this.parent = parent
    }

    fun getParent(): TreeNode<*>? {
        return parent
    }

    fun lock(): TreeNode<T> {
        isLocked = true
        return this
    }

    fun unlock(): TreeNode<T> {
        isLocked = false
        return this
    }

    override fun toString(): String {
        return "TreeNode{" +
                "content=" + content +
                ", parent=" + (if (parent == null) "null" else parent!!.content.toString()) +
                ", childList=" + (childList.toString()) +
                ", isExpand=" + isExpand +
                '}'
    }

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): TreeNode<T> {
        val clone = TreeNode(content)
        clone.isExpand = isExpand
        return clone
    }

    companion object {
        private const val UNDEFINE = -1
    }


}