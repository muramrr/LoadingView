/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.08.20 20:53
 *
 * Licensed under the Apache License,
 * Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.mmdev.loadingviewlib

import android.content.res.Resources
import androidx.core.view.animation.PathInterpolatorCompat
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.max
import kotlin.math.min

/*
 * see [https://dribbble.com/shots/5095383-Loader-Animation]
 */

class LoadingView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
	
	private val ovalRectF = RectF()
	private val sweepPaint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.STROKE
		strokeCap = Paint.Cap.ROUND
	}
	private var sweepPaintShadowRadius = 0f
	
	private var minStrokeSize = 2.toPx()
	private var maxStrokeSize = 8.toPx()
	
	/** force stroke to be in bounds min 2dp and max 8dp
	 * also auto apply [sweepPaint] strokeWidth
	 * @see minStrokeSize
	 * @see maxStrokeSize
	 */
	private var strokeSize = 0
		private set(value) {
			field = when {
				value > maxStrokeSize -> maxStrokeSize
				value < minStrokeSize -> minStrokeSize
				else -> value
			}
			sweepPaint.strokeWidth = field.toFloat()
			sweepPaintShadowRadius = field * 1.25f
			sweepPaint.setShadowLayer(sweepPaintShadowRadius,0f,0f, sweepColor)
		}
	
	//starting angles of 3 sweeps
	private var sweepAngle1 = 5f
	private var sweepAngle2 = 5f
	private var sweepAngle3 = 5f
	
	//auto apply paint color while changing this
	//auto apply "glowing" shadow with same color
	private var sweepColor = Color.WHITE
		private set(value) {
			field = value
			sweepPaint.color = field
			sweepPaint.setShadowLayer(sweepPaintShadowRadius,0f,0f, field)
		}
	
	private val animatorSet = AnimatorSet()
	
	//rotating whole view
	private fun viewRotateAnimator() = ValueAnimator.ofFloat(0f, 360f).apply {
		duration = 1600
		interpolator = LinearInterpolator()
		repeatCount = ValueAnimator.INFINITE
		repeatMode = ValueAnimator.RESTART
		addUpdateListener { rotation = it.animatedValue as Float }
	}
	
	//animate 3 sweeps inside view
	private fun angleAnimator() = ValueAnimator.ofFloat(5f, 105f).apply {
		duration = 800
		// god given custom interpolator
		interpolator = PathInterpolatorCompat.create(1f, 0f, 0f, 1f)
		repeatCount = ValueAnimator.INFINITE
		repeatMode = ValueAnimator.REVERSE
		addUpdateListener {
			
			sweepAngle1 = it.animatedValue as Float
			sweepAngle2 = it.animatedValue as Float
			sweepAngle3 = it.animatedValue as Float
			invalidate()
			
		}
		
	}
	
	/** using for toggle animation
	 * true -> animatorSet.resume()
	 * false -> animatorSet.pause()
	 */
	private var isAnimating = true
		private set(value) {
			field = value
			if (field) animatorSet.resume()
			else animatorSet.pause()
		}
	
	// constructor init
	init {
		attrs?.let {
			val ta = context.obtainStyledAttributes(
					it,
					R.styleable.LoadingView,
					defStyleAttr,
					R.style.LoadingView
			)
			
			sweepColor = ta.getColor(R.styleable.LoadingView_loadStrokeColor, Color.WHITE)
			
			strokeSize = ta.getDimensionPixelSize(R.styleable.LoadingView_loadStrokeWidth, minStrokeSize)
			
			ta.recycle()
		}
		
	}
	
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		//force square view
		val width = MeasureSpec.getSize(widthMeasureSpec)
		val height = MeasureSpec.getSize(heightMeasureSpec)
		
		//check the lowest value to draw square
		val minSize = min(width, height).also {
			strokeSize = max(minStrokeSize, min(maxStrokeSize, it / 24))
		}
		
		//calculate bounds to draw without cutting off
		ovalRectF.set(paddingLeft.toFloat() + strokeSize,
		              paddingTop.toFloat() + strokeSize,
		              (minSize - paddingRight).toFloat() - strokeSize,
		              (minSize - paddingBottom).toFloat() - strokeSize)
		
		
		setMeasuredDimension(minSize, minSize)
		
		// auto start animation on pre-draw stage
		// no need to toggle
		animatorSet.cancel()
		animatorSet.playTogether(angleAnimator(), viewRotateAnimator())
		animatorSet.start()
	}
	
	override fun onDraw(canvas: Canvas) {
		canvas.drawArc(ovalRectF, 0f, sweepAngle1, false, sweepPaint)
		canvas.drawArc(ovalRectF, 120f, sweepAngle2, false, sweepPaint)
		canvas.drawArc(ovalRectF, 240f, sweepAngle3, false, sweepPaint)
		
	}
	
	//can be applied to clickListener in your activity/fragment
	fun toggleAnimation() {
		isAnimating = !isAnimating
	}
	
	private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
	
	private fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
}