package com.example.testrecyclertree.bean

import com.example.testrecyclertree.R
import com.example.testrecyclertree.lib.LayoutItemType


class Dir(var dirName: String) : LayoutItemType {
    override val layoutId: Int
        get() = R.layout.item_dir

}