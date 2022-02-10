package com.awab.calculator.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.awab.calculator.R

class CustomSelectableTextView(context: Context, attr: AttributeSet) : LinearLayout(context, attr) {

    init {
        initView(attr)
    }

    private fun initView(attr: AttributeSet) {
        gravity = Gravity.CENTER
        // adding the custom attributes
        attr.let {
            val attributes = context.obtainStyledAttributes(it, R.styleable.CustomSelectableTextView)
            try {
                // the back ground of the button
                val backGroundColor = attributes.getDrawable(R.styleable.CustomSelectableTextView_button_background)
                background = backGroundColor
                val tv = TextView(context).apply {
                    // adding the attributes
                    gravity = Gravity.CENTER
                    this.text = attributes.getString(R.styleable.CustomSelectableTextView_button_text)
                    textSize = attributes.getDimension(R.styleable.CustomSelectableTextView_button_text_size,10F)
                    val p = attributes.getDimension(R.styleable.CustomSelectableTextView_button_text_padding,0F).toInt()
                    setPadding(p,p,p,p)
                    // adding the ripple background
                    val outValue = TypedValue()
                    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
                    setBackgroundResource(outValue.resourceId)
                    isClickable = true

                    // id to adjust the layout params
                    id = R.id.buttonText
                }
                addView(tv)
                // this must happen after adding the view
                this.findViewById<TextView>(R.id.buttonText).apply {
                    layoutParams.width = LayoutParams.MATCH_PARENT
                    layoutParams.height = LayoutParams.MATCH_PARENT
                    // calling the parent on click listener
                    setOnClickListener {
                        this@CustomSelectableTextView.callOnClick()
                    }
                }
            } finally {
                attributes.recycle()
            }
        }
    }
}