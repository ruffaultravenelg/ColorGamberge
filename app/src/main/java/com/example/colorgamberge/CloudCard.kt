package com.example.colorgamberge

import android.view.View
import androidx.cardview.widget.CardView
import android.graphics.Color
import android.widget.TextView
import android.content.res.ColorStateList

class CloudCard {

    // Elements
    private val _content: CardView
    private val _contentText: TextView
    private val _label: CardView
    private val _labelText: TextView

    // Constructor
    constructor(elm: View){
        _content = elm.findViewById(R.id.content)
        _contentText = elm.findViewById(R.id.content_text)
        _label = elm.findViewById(R.id.label)
        _labelText = elm.findViewById(R.id.label_text)
    }

    // Content background color
    var contentBackgroundColor: Int
        get() {
            return _content.backgroundTintList?.defaultColor ?: Color.TRANSPARENT
        }
        set(value) {
            _content.backgroundTintList = ColorStateList.valueOf(value)
        }

    // Content text
    var contentText: String
        get(){
            return _contentText.text.toString()
        }
        set(value) {
            _contentText.text = value
        }

    // Label background color
    var labelBackgroundColor: Int
        get() {
            return _label.backgroundTintList?.defaultColor ?: Color.TRANSPARENT
        }
        set(value) {
            _label.backgroundTintList = ColorStateList.valueOf(value)
        }

    // Label text
    var labelText: String
        get(){
            return _labelText.text.toString()
        }
        set(value) {
            _labelText.text = value
        }

}