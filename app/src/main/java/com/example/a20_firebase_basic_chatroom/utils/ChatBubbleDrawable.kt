package com.example.a20_firebase_basic_chatroom.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import kotlin.io.path.Path
import kotlin.io.path.moveTo

/*
class ChatBubbleDrawable : Drawable() {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE  // Bubble color
        style = Paint.Style.FILL
        setShadowLayer(2f, 0f, 1f, Color.parseColor("#20000000"))  // Subtle shadow
    }

    private val path = android.graphics.Path()

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        val width = bounds.width().toFloat()
        val height = bounds.height().toFloat()

        // Clear the path
        path.reset()

        // Starting point (top-left after the tail)
        path.moveTo(16f, 0f)

        // Draw the tail
        path.lineTo(0f, 10f)
        path.lineTo(16f, 20f)

        // Draw the main bubble
        path.lineTo(16f, 0f)  // Top left corner
        path.lineTo(width - 16f, 0f)  // Top edge
        path.quadTo(width, 0f, width, 16f)  // Top right corner
        path.lineTo(width, height - 16f)  // Right edge
        path.quadTo(width, height, width - 16f, height)  // Bottom right corner
        path.lineTo(16f, height)  // Bottom edge
        path.quadTo(0f, height, 0f, height - 16f)  // Bottom left corner
        path.lineTo(0f, 20f)  // Left edge

        path.close()

        // Draw the bubble
        canvas.drawPath(path, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}*/
class ChatBubbleDrawable : Drawable() {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFFFFF")  // WhatsApp bubble color
        style = Paint.Style.FILL
        setShadowLayer(2f, 0f, 1f, Color.parseColor("#20000000"))
    }

    private val path = android.graphics.Path()

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        val width = bounds.width().toFloat()
        val height = bounds.height().toFloat()

        path.reset()

        val tailWidth = 20f
        val tailHeight = 15f
        val cornerRadius = 18f

        // Start from the point after tail
        path.moveTo(tailWidth, 0f)

        // Draw tail using bezier curve for smooth shape
        path.lineTo(3f, 0f)
        path.cubicTo(
            3f, 0f,      // First control point
            0f, 0f,      // Second control point
            0f, 3f       // End point
        )
        path.cubicTo(
            0f, 5f,      // First control point
            1f, tailHeight - 3f,  // Second control point
            4f, tailHeight        // End point
        )
        path.cubicTo(
            7f, tailHeight + 2f,  // First control point
            tailWidth, tailHeight - 2f,  // Second control point
            tailWidth, tailHeight + 2f   // End point
        )

        // Draw main bubble with rounded corners
        // Top edge
        path.lineTo(width - cornerRadius, 0f)
        path.quadTo(width, 0f, width, cornerRadius)

        // Right edge
        path.lineTo(width, height - cornerRadius)
        path.quadTo(width, height, width - cornerRadius, height)

        // Bottom edge
        path.lineTo(cornerRadius, height)
        path.quadTo(0f, height, 0f, height - cornerRadius)

        // Left edge
        path.lineTo(0f, tailHeight + cornerRadius)

        path.close()

        canvas.drawPath(path, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}

