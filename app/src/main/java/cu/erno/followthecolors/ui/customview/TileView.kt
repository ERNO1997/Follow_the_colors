package cu.erno.followthecolors.ui.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import cu.erno.followthecolors.R

class TileView : androidx.appcompat.widget.AppCompatImageView {

    private var srcON: Drawable? = null
    private var srcOFF: Drawable? = null

    var isON = false
        private set

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context, attrs, defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.TileView, defStyle, 0
        )

        if (a.hasValue(R.styleable.TileView_srcON)) {
            srcON = a.getDrawable(R.styleable.TileView_srcON)
            srcON?.callback = this
        }

        if (a.hasValue(R.styleable.TileView_srcOFF)) {
            srcOFF = a.getDrawable(R.styleable.TileView_srcOFF)
            srcOFF?.callback = this
        }

        if (a.hasValue(R.styleable.TileView_isON)) {
            isON = a.getBoolean(R.styleable.TileView_isON, false)
        }

        a.recycle()
        setImageDrawable(if (isON) srcON else srcOFF)
    }

    fun turnON() {
        setImageDrawable(srcON)
        isON = true
    }

    fun turnOFF() {
        setImageDrawable(srcOFF)
        isON = false
    }

    fun toggle() {
        setImageDrawable(if (isON) srcOFF else srcON)
        isON = !isON
    }
}