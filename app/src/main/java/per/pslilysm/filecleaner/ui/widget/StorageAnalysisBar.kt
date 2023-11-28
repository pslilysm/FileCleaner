package per.pslilysm.filecleaner.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import per.pslilysm.filecleaner.R


/**
 * A View for show storage analysis result
 *
 * @author caoxuedong
 * Created on 2023/11/27 15:54
 * @since 1.0
 */
class StorageAnalysisBar(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    var dataAndColorArray: Array<Pair<Float, Int>>? = null

    private val paint = Paint()
    private val rectF = RectF()
    private val srcInXfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    private val srcOverXfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
    private var radius = 0f

    private val backgroundColor: Int

    init {
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        setLayerType(LAYER_TYPE_HARDWARE, paint)
        backgroundColor = context.getColor(R.color.ffe4e4e4)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        radius = height / 2f
    }

    override fun onDraw(canvas: Canvas) {
        dataAndColorArray?.let { dataAndColorArray ->
            paint.xfermode = srcOverXfermode
            paint.color = Color.BLACK
            val width = width.toFloat()
            val height = height.toFloat()
            rectF.set(0f, 0f, width, height)
            canvas.saveLayer(rectF, paint)
            canvas.drawRoundRect(rectF, radius, radius, paint)
            paint.xfermode = srcInXfermode
            canvas.saveLayer(rectF, paint)

            // draw your owner thing
            paint.xfermode = srcOverXfermode
            canvas.drawColor(backgroundColor, PorterDuff.Mode.SRC_OVER)
            dataAndColorArray.forEachIndexed { index, pair ->
                paint.color = pair.second
                if (index == 0) {
                    rectF.set(0f, 0f, width * pair.first, height)
                } else {
                    val prevRight = rectF.right
                    rectF.set(prevRight, 0f, prevRight + width * pair.first, height)
                }
                canvas.drawRect(rectF, paint)
            }

            canvas.restore()
        }
    }

}