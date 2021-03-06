package io.legado.app.ui.book.read.page.delegate

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.GradientDrawable
import io.legado.app.ui.book.read.page.PageView

class CoverPageDelegate(pageView: PageView) : HorizontalPageDelegate(pageView) {

    private val shadowDrawableR: GradientDrawable
    private val bitmapMatrix = Matrix()

    init {
        val shadowColors = intArrayOf(0x66111111, 0x00000000)
        shadowDrawableR = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, shadowColors
        )
        shadowDrawableR.gradientType = GradientDrawable.LINEAR_GRADIENT
    }

    override fun onScrollStart() {
        val distanceX: Float
        when (direction) {
            Direction.NEXT -> distanceX =
                if (isCancel) {
                    var dis = viewWidth - startX + touchX
                    if (dis > viewWidth) {
                        dis = viewWidth.toFloat()
                    }
                    viewWidth - dis
                } else {
                    -(touchX + (viewWidth - startX))
                }
            else -> distanceX =
                if (isCancel) {
                    -(touchX - startX)
                } else {
                    viewWidth - (touchX - startX)
                }
        }

        startScroll(touchX.toInt(), 0, distanceX.toInt(), 0)
    }

    override fun onScrollStop() {
        curPage?.x = 0.toFloat()
        if (!isCancel) {
            pageView.fillPage(direction)
        }
    }

    override fun onDraw(canvas: Canvas) {
        val offsetX = touchX - startX

        if ((direction == Direction.NEXT && offsetX > 0)
            || (direction == Direction.PREV && offsetX < 0)
        ) return

        val distanceX = if (offsetX > 0) offsetX - viewWidth else offsetX + viewWidth
        bitmap?.let {
            if (direction == Direction.PREV) {
                bitmapMatrix.setTranslate(distanceX, 0.toFloat())
                canvas.drawBitmap(it, bitmapMatrix, null)
            } else if (direction == Direction.NEXT) {
                curPage?.translationX = offsetX
            }
            addShadow(distanceX.toInt(), canvas)
        }
    }

    private fun addShadow(left: Int, canvas: Canvas) {
        if (left < 0) {
            shadowDrawableR.setBounds(left + viewWidth, 0, left + viewWidth + 30, viewHeight)
            shadowDrawableR.draw(canvas)
        } else if (left > 0) {
            shadowDrawableR.setBounds(left, 0, left + 30, viewHeight)
            shadowDrawableR.draw(canvas)
        }
    }
}
