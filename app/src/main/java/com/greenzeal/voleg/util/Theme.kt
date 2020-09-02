package com.greenzeal.voleg.util

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.StateSet
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.sqrt

object Theme {
    const val ACTION_BAR_WHITE_SELECTOR_COLOR = 0x40ffffff

    private val maskPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    fun createSelectorDrawable(color: Int): Drawable {
        return createSelectorDrawable(color, 1)
    }

    private fun createSelectorDrawable(color: Int, maskType: Int): Drawable {
        var drawable: Drawable
        return if (Build.VERSION.SDK_INT >= 21) {
            var maskDrawable: Drawable? = null
            if (maskType == 1 || maskType == 3 || maskType == 4 || maskType == 5) {
                maskPaint.color = -0x1
                maskDrawable = object : Drawable() {
                    override fun draw(canvas: Canvas) {
                        val bounds = bounds
                        val rad: Int
                        rad = when (maskType) {
                            1 -> {
                                20.dp
                            }
                            3 -> {
                                max(bounds.width(), bounds.height()) / 2
                            }
                            else -> {
                                ceil(sqrt((bounds.left - bounds.centerX()) * (bounds.left - bounds.centerX()) + (bounds.top - bounds.centerY()) * (bounds.top - bounds.centerY()).toDouble()))
                                    .toInt()
                            }
                        }
                        canvas.drawCircle(
                            bounds.centerX().toFloat(),
                            bounds.centerY().toFloat(),
                            rad.toFloat(),
                            maskPaint
                        )
                    }

                    override fun setAlpha(alpha: Int) {}
                    override fun setColorFilter(colorFilter: ColorFilter?) {}
                    override fun getOpacity(): Int {
                        return PixelFormat.UNKNOWN
                    }
                }
            } else if (maskType == 2) {
                maskDrawable = ColorDrawable(-0x1)
            }
            val colorStateList =
                ColorStateList(arrayOf(StateSet.WILD_CARD), intArrayOf(color))
            val rippleDrawable = RippleDrawable(colorStateList, null, maskDrawable)
            if (Build.VERSION.SDK_INT >= 23) {
                if (maskType == 1) {
                    rippleDrawable.radius = 20.dp
                } else if (maskType == 5) {
                    rippleDrawable.radius = RippleDrawable.RADIUS_AUTO
                }
            }
            rippleDrawable
        } else {
            val stateListDrawable =
                StateListDrawable()
            stateListDrawable.addState(
                intArrayOf(android.R.attr.state_pressed),
                ColorDrawable(color)
            )
            stateListDrawable.addState(
                intArrayOf(android.R.attr.state_selected),
                ColorDrawable(color)
            )
            stateListDrawable.addState(StateSet.WILD_CARD, ColorDrawable(0x00000000))
            stateListDrawable
        }
    }

    val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()
}