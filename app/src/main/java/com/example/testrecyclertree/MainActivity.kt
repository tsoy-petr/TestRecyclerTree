package com.example.testrecyclertree

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.testrecyclertree.bean.Dir
import com.example.testrecyclertree.bean.File
import com.example.testrecyclertree.lib.TreeNode
import com.example.testrecyclertree.lib.TreeViewAdapter
import com.example.testrecyclertree.viewbinder.DirectoryNodeBinder
import com.example.testrecyclertree.viewbinder.FileNodeBinder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.security.GeneralSecurityException


class MainActivity : AppCompatActivity() {

    private val inInterpolator by lazy { FastOutSlowInInterpolator() }
    private var isAnimatingOut = false

    private lateinit var rv: RecyclerView
    private lateinit var adapter: TreeViewAdapter
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab = findViewById(R.id.fab)

        initView()
        initData()

    }

//    override fun onAtta() {
//        super.onAttachedToWindow()
//
//        val sharesPrefsFile = "api_pass.txt"
//
//        try {
//            val keyGenParametersSpec = MasterKeys.AES256_GCM_SPEC
//            val mainKeyAlias = MasterKeys.getOrCreate(keyGenParametersSpec)
//            val sharedPreferences = EncryptedSharedPreferences.create(
//                sharesPrefsFile,
//                mainKeyAlias,
//                this,
//                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//            )
//            with(sharedPreferences.edit()){
//               putString("api_pass", "608840")
//                apply()
//            }
//
//        } catch (e: GeneralSecurityException){
//
//        }
//    }

    private fun initView() {
        rv = findViewById(R.id.rv)
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && fab.visibility === View.VISIBLE) {
                    animateOut(fab)
                } else if (dy < 0 && fab.visibility !== View.VISIBLE) {
                    animateIn(fab)
                }
            }
        })
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
        when (item.itemId) {
            R.id.id_action_close_all -> adapter.collapseAll()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun animateOut(button: FloatingActionButton) {
        ViewCompat.animate(button).translationY((button.height + getMarginBottom(button)).toFloat())
            .setInterpolator(inInterpolator).withLayer()
            .setListener(object : ViewPropertyAnimatorListener {
                override fun onAnimationStart(view: View) {
                    isAnimatingOut = true
                }
                override fun onAnimationCancel(view: View) {
                    isAnimatingOut = false
                }
                override fun onAnimationEnd(view: View) {
                    isAnimatingOut = false
                    view.visibility = View.INVISIBLE
                }
            }).start()
    }

    private fun animateIn(button: FloatingActionButton) {
        button.visibility = View.VISIBLE
        ViewCompat.animate(button).translationY(0f)
            .setInterpolator(inInterpolator).withLayer()
            .setListener(null)
            .start()
    }

    private fun getMarginBottom(v: View): Int {
        var marginBottom = 0
        val layoutParams = v.layoutParams
        if (layoutParams is ViewGroup.MarginLayoutParams) marginBottom = layoutParams.bottomMargin
        return marginBottom
    }

}