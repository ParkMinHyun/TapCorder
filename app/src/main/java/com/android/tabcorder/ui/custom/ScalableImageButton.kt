package com.android.tabcorder.ui.custom

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation
import androidx.appcompat.widget.AppCompatImageView
import com.android.tabcorder.R
import com.android.tabcorder.util.ExtensionUtil.TAG

private const val FAST_DURATION_MILLI = 60
private const val TOUCH_EXTRA_AREA_RATIO = 0.2

private const val SCALE_DOWN_RATIO = 0.9f
private const val SCALE_ORIGINAL_RATIO = 1f
private const val SCALE_UP_REVERSE_RATIO = 1.1f

private const val PIVOT_CENTER = 0.5f

/**
 * [ScalableImageButton] custom view.
 */
@SuppressLint("CustomViewStyleable")
class ScalableImageButton(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageView(context, attrs) {

    private var reverseAnimation = false
    private var pressAnimationIsDone = true

    private var onClickListener: OnClickListener? = null
    private var isLongClick = false

    private val longClickCallback = Runnable {
        if (isLongClick) {
            performLongClick()
            isLongClick = false
        }
    }

    init {
        with(context.obtainStyledAttributes(attrs, R.styleable.ScalableImageButton)) {
            reverseAnimation = this.getBoolean(R.styleable.ScalableImageButton_reverseAnimation, false)
            this.recycle()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return true
        }

        return when (event.action) {
            MotionEvent.ACTION_DOWN -> onTouchActionDown()
            MotionEvent.ACTION_UP -> onTouchActionUp(event)
            MotionEvent.ACTION_MOVE -> onTouchActionMove(event)
            else -> super.onTouchEvent(event)
        }
    }

    private fun onTouchActionDown(): Boolean {
        if (isPressed) {
            return false
        }

        isPressed = true
        onPressedDown()
        isLongClick = true

        removeCallbacks(longClickCallback)
        postDelayed(longClickCallback, 1000)
        return true
    }


    private fun onTouchActionUp(event: MotionEvent): Boolean {
        isLongClick = false
        if (isPressed) {
            onPressCanceled(10)
        }
        isPressed = false

        if (containsTouchInsideView(this, event, true)) {
            onClickListener?.onClick(this)
        }
        return true
    }


    private fun onTouchActionMove(event: MotionEvent): Boolean {
        if (containsTouchInsideView(this, event, false)) {
            if (isPressed) {
                return false
            }

            isPressed = true
            onPressedDown()
        } else {
            if (!isPressed) {
                return false
            }
            isPressed = false
            onPressCanceled(FAST_DURATION_MILLI)
        }
        return false
    }


    private fun onPressCanceled(durationMilli: Int) {
        if (reverseAnimation) {
            if (pressAnimationIsDone) {
                viewUpScaleUp(this, durationMilli)
            } else {
                postDelayed(
                    { viewUpScaleUp(this@ScalableImageButton, durationMilli) },
                    FAST_DURATION_MILLI.toLong()
                )
            }
        } else {
            if (pressAnimationIsDone) {
                viewDownScaleUp(this, durationMilli)
            } else {
                postDelayed(
                    { viewDownScaleUp(this@ScalableImageButton, durationMilli) },
                    FAST_DURATION_MILLI.toLong()
                )
            }
        }
    }


    private fun onPressedDown() {
        pressAnimationIsDone = false
        if (reverseAnimation) {
            viewUpScaleDown(this, FAST_DURATION_MILLI)
        } else {
            viewDownScaleDown(this, FAST_DURATION_MILLI)
        }
        postDelayed({ pressAnimationIsDone = true }, FAST_DURATION_MILLI.toLong())
    }


    /**
     * [ScalableView]'s setOnClickListener
     */
    override fun setOnClickListener(onClickListener: OnClickListener?) {
        this.onClickListener = onClickListener
        super.setOnClickListener(onClickListener)
    }

    private fun containsTouchInsideView(
        view: View,
        event: MotionEvent,
        addExtraSpace: Boolean
    ): Boolean {
        if (!view.isShown) {
            return false
        }

        val rect = Rect()
        view.getLocalVisibleRect(rect)

        if (addExtraSpace) {
            val horizontalExtra = ((rect.right - rect.left) * TOUCH_EXTRA_AREA_RATIO).toInt()
            val verticalExtra = ((rect.bottom - rect.top) * TOUCH_EXTRA_AREA_RATIO).toInt()
            rect[rect.left - horizontalExtra, rect.top - verticalExtra,
                    rect.right + horizontalExtra] = rect.bottom + verticalExtra
        }

        return rect.contains(event.x.toInt(), event.y.toInt())
    }


    companion object {
        @Suppress("SameParameterValue")

        private fun viewDownScaleDown(view: View?, durationMilli: Int) {
            try {
                val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", SCALE_DOWN_RATIO)
                val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", SCALE_DOWN_RATIO)

                AnimatorSet().apply {
                    duration = durationMilli.toLong()
                    playTogether(scaleDownX, scaleDownY)
                    start()
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }

        private fun viewDownScaleUp(view: View?, durationMilli: Int) {
            try {
                val scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", SCALE_ORIGINAL_RATIO)
                val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", SCALE_ORIGINAL_RATIO)

                AnimatorSet().apply {
                    duration = durationMilli.toLong()
                    playTogether(scaleUpX, scaleUpY)
                    start()
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }

        @Suppress("SameParameterValue")
        private fun viewUpScaleDown(view: View, durationMilli: Int) {
            try {
                val scaleDownAni = AnimationUtils.loadAnimation(view.context, R.anim.up_scale_down)
                scaleDownAni.duration = durationMilli.toLong()
                scaleDownAni.fillAfter = true
                view.startAnimation(scaleDownAni)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }

        private fun viewUpScaleUp(view: View, durationMilli: Int) {
            try {
                val scaleUpAnimation: Animation = ScaleAnimation(
                    SCALE_UP_REVERSE_RATIO, 1f,
                    SCALE_UP_REVERSE_RATIO, 1f,
                    Animation.RELATIVE_TO_SELF, PIVOT_CENTER,
                    Animation.RELATIVE_TO_SELF, PIVOT_CENTER
                )

                scaleUpAnimation.interpolator = DecelerateInterpolator()
                scaleUpAnimation.duration = durationMilli.toLong()
                view.startAnimation(scaleUpAnimation)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }
}