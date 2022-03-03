package com.example.testrecyclertree.bean

import com.example.testrecyclertree.R
import com.example.testrecyclertree.lib.LayoutItemType

class File(var fileName: String) : LayoutItemType {
    override val layoutId: Int
        get() = R.layout.item_file

}