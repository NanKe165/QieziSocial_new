package com.eggplant.qiezisocial.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.utils.TypefaceHelper
import com.mabeijianxi.jianxiexpression.widget.ExpressionEditText
import java.util.*

/**
 * Created by Administrator on 2020/4/23.
 */

class QzEdittext : ExpressionEditText {
    private val FONT_MIAOWU: String = "miaowu"
    //    private val FONT_SHAOER: String = "shaoer"
    private val FONT_CHANGGUI: String = "zaozi"
    private lateinit var font_miaowuti: Typeface
    private lateinit var font_shoerti: Typeface
    private lateinit var font_changguiti: Typeface

    private var currentFont = ""
    private var lastFont = ""

    constructor(context: Context) : super(context) {
        initFontFormat("")
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.QzTextView)
        val fontFormat = typedArray.getString(R.styleable.QzTextView_font_format)
        typedArray.recycle()

        if (!TextUtils.isEmpty(fontFormat)) {
            initFontFormat(fontFormat)
        } else {
            initFontFormat("PINGFANGSCMEDIUM")
        }
    }


    @SuppressLint("NewApi")
    private fun initFontFormat(fontFormat: String) {
        lastFont=currentFont
        currentFont = fontFormat
        font_miaowuti = TypefaceHelper.get(context, "font/zf_miaowuti.ttf")
        font_shoerti = TypefaceHelper.get(context, "font/zf_shaoerjianti.ttf")
        font_changguiti = TypefaceHelper.get(context, "font/kk_changguiti.ttf")

        var typeface: Typeface =
                when (fontFormat) {
                    FONT_CHANGGUI -> font_changguiti
                    FONT_MIAOWU -> font_miaowuti
//                    FONT_SHAOER -> font_shoerti
                    else -> {
                        when (typeface) {
                            font_changguiti, font_miaowuti, Typeface.DEFAULT ->
                                TypefaceHelper.get(context, "font/PINGFANG_SC_MEDIUM.TTF")
                            Typeface.DEFAULT_BOLD ->
                                TypefaceHelper.get(context, "font/PINGFANG_BOLD.TTF")
                            else ->
                                typeface
                        }
                    }
                }
        setTypeface(typeface)
        includeFontPadding = false
        if (currentFont == FONT_CHANGGUI) {
            letterSpacing = 0.2f
            setLineSpacing(0f, 1.3f)

        } else {
            letterSpacing = 0.0f
            setLineSpacing(0f, 1.0f)

        }

    }

    override fun setTextSize(size: Float) {
        var sz = size
        if (currentFont == FONT_CHANGGUI) {
            sz-=3
        } else if (lastFont == FONT_CHANGGUI) {
            sz+=3
        }
        super.setTextSize(sz)
    }

    fun setFontFormat(fontFormat: String) {
        initFontFormat(fontFormat)
    }

    fun getFontFormat(): String {
        return currentFont
    }

    fun nextFontFormat() {
        var nextFont = when (currentFont) {
            FONT_MIAOWU -> FONT_CHANGGUI
//            FONT_SHAOER -> FONT_CHANGGUI
            FONT_CHANGGUI -> ""
            else -> {
                FONT_MIAOWU
            }
        }
        initFontFormat(nextFont)
    }
    fun  randomFontFormat(){
        val nextInt = Random().nextInt(3)
        var font=  when(nextInt){
            0-> FONT_MIAOWU
            1->FONT_CHANGGUI
            else-> ""
        }
        initFontFormat(font)
    }
}
