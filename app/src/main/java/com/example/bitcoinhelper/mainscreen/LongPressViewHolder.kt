package com.example.bitcoinhelper.mainscreen

import android.content.res.Resources
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.bitcoinhelper.R

class LongPressViewHolder : RecyclerView.ViewHolder {
    private var mView: View

    constructor(itemView: View, oldView: View) : super(itemView) {
        mView = itemView
        val animation = AnimationUtils.loadAnimation(oldView.context, R.anim.fadeout)
        oldView.startAnimation(animation)

        val width = Resources.getSystem().displayMetrics.widthPixels / 2
        var layoutParams = mView.findViewById<RelativeLayout>(R.id.delete).layoutParams
        layoutParams.width = width
        layoutParams = mView.findViewById<RelativeLayout>(R.id.modify).layoutParams
        layoutParams.width = width

        val animation2 = AnimationUtils.loadAnimation(oldView.context, R.anim.fadein)
        itemView.startAnimation(animation2)
    }
}