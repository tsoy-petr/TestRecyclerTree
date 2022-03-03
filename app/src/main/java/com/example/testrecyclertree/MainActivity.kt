package com.example.testrecyclertree

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testrecyclertree.bean.Dir
import com.example.testrecyclertree.bean.File
import com.example.testrecyclertree.lib.TreeNode
import com.example.testrecyclertree.lib.TreeViewAdapter
import com.example.testrecyclertree.viewbinder.DirectoryNodeBinder
import com.example.testrecyclertree.viewbinder.FileNodeBinder


class MainActivity : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var adapter: TreeViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView();
        initData();

    }

    private fun initView() {
        rv = findViewById(R.id.rv)
    }

    private fun initData() {

        val nodes = arrayListOf<TreeNode<Dir>>()
        val app = TreeNode(Dir("app"))
        nodes.add(app)

        app.addChild(
            TreeNode(Dir("manifests"))
                .addChild(TreeNode(File("AndroidManifest.xml")))
        )

        app.addChild(
            TreeNode(Dir("java")).addChild(
                TreeNode(Dir("tellh")).addChild(
                    TreeNode(Dir("com")).addChild(
                        TreeNode(Dir("recyclertreeview"))
                            .addChild(TreeNode(File("Dir")))
                            .addChild(TreeNode(File("DirectoryNodeBinder")))
                            .addChild(TreeNode(File("File")))
                            .addChild(TreeNode(File("FileNodeBinder")))
                            .addChild(TreeNode(File("TreeViewBinder")))
                    )
                )
            )
        )

        val res = TreeNode(Dir("res"))
        nodes.add(res)
        res.addChild(
            TreeNode(Dir("layout")).lock() // lock this TreeNode
                .addChild(TreeNode(File("activity_main.xml")))
                .addChild(TreeNode(File("item_dir.xml")))
                .addChild(TreeNode(File("item_file.xml")))
        )
        res.addChild(
            TreeNode(Dir("mipmap"))
                .addChild(TreeNode(File("ic_launcher.png")))
        )

        rv.layoutManager = LinearLayoutManager(this)

        adapter = TreeViewAdapter(
            nodes = nodes, listOf(
                FileNodeBinder(), DirectoryNodeBinder()
            )
        )

        adapter.setOnTreeNodeListener(object : TreeViewAdapter.OnTreeNodeListener {
            override fun onClick(node: TreeNode<*>, holder: RecyclerView.ViewHolder?): Boolean {
                if (!node.isLeaf) {
                    //Update and toggle the node.
                    onToggle(!node.isExpand, holder)
                    //                    if (!node.isExpand())
                    //                        adapter.collapseBrotherNode(node);
                }
                return false
            }

            override fun onToggle(isExpand: Boolean, holder: RecyclerView.ViewHolder?) {
                val dirViewHolder = holder as DirectoryNodeBinder.ViewHolder
                val ivArrow: ImageView = dirViewHolder.ivArrow
                val rotateDegree = if (isExpand) 90 else -90
                ivArrow.animate().rotationBy(rotateDegree.toFloat())
                    .start()
            }

        })

        rv.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            R.id.id_action_close_all -> adapter.collapseAll()
        }

        return super.onOptionsItemSelected(item)
    }
}