package com.stuf.itinder.components.chipgroup

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat

import com.stuf.itinder.R
import androidx.core.content.withStyledAttributes

/**
 * Custom view for chip group.
 *
 * Always draws chips in horizontal-flow (like flex-row)
 *
 * Our own alternative to radio-input (but with multi select option)
 */
class ChipGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(
    context,
    attrs,
    defStyleAttr
) {

    /**
     * Calls when reached limit of selected chips
     */
    var onSelectionLimitReached : ((limit: Int) -> Unit)? = null

    /**
     * calls after list of selected chips changed
     *
     * > Note: Call ONLY after changing list of selected chips
     */
    var onSelectionChange: ((selectedIds: List<String>) -> Unit)? = null

    /**
     * calls after chip has been clicked
     *
     * > Note: if user tries to click on chip when reached max selected amount, it won't call
     */
    var onTagClick: ((tagId: String, isSelectedAfterClick: Boolean) -> Unit)? = null

    enum class SelectionMode {
        SINGLE,
        MULTI
    }

    private val textPaint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var selectionMode: SelectionMode = SelectionMode.MULTI
    private var maxSelected: Int = Int.MAX_VALUE

    private var chipTextColor: Int = 0
    private var chipSelectedTextColor: Int = 0
    private var chipTextSizePx: Float = 0f
    private var chipFontFamilyResId: Int = R.font.inter_bold
    private var chipFontWeight: Int = 400

    private var chipBackgroundColor: Int = 0
    private var chipSelectedBackgroundColor: Int = 0
    private var chipCornerRadiusPx: Float = 0f

    private var chipHorizontalGapPx: Int = 0
    private var chipVerticalGapPx: Int = 0
    private var chipHorizontalPaddingPx: Int = 0
    private var chipVerticalPaddingPx: Int = 0

    private val chips: MutableList<Chip> = mutableListOf()

    private val previewChips: Map<String, String> = mapOf(
        "1" to "python",
        "2" to "Django",
        "3" to "REST",
        "4" to "Swift",
        "5" to "Obj-C",
        "6" to "React JS",
        "7" to "Kotlin",
        "8" to "Git",
        "9" to "Unity",
        "10" to ".NET",
        "11" to "SQL",
        "12" to "Clean Architecture",
        "13" to "UML"
    )

    private data class Chip(
        val id: String,
        val text: String,
        var isSelected: Boolean = false,
        var displayText: String = text,
        val bounds: RectF = RectF(),
    )

    /**
     * sets list of chips
     *
     * > Note: updating chips will clear selections
     *
     * @param chips - representation of chips (key - id, value - text)
     */
    fun setChips(chips: Map<String, String>) {
        this.chips.clear()
        chips.forEach { (id, text) ->
            this.chips.add(
                Chip(
                    id = id,
                    text = text,
                    isSelected = false
                )
            )
        }
        onChipsUpdated()
        requestLayout()
        invalidate()
    }

    /**
     * Returns ids of all selected chips.
     */
    fun getSelectedIds(): List<String> =
        chips.filter { it.isSelected }.map { it.id }

    /**
     * Clears selection for all chips.
     */
    fun clearSelection() {
        var changed = false
        chips.forEach {
            if (it.isSelected) {
                it.isSelected = false
                changed = true
            }
        }
        if (changed) {
            notifySelectionChanged()
            invalidate()
        }
    }

    /**
     * Sets selection for the given chip ids.
     * In SINGLE mode only the first id will remain selected.
     * In MULTI mode respects maxSelected limit.
     */
    fun setSelectedIds(ids: Collection<String>) {
        if (ids.isEmpty()) {
            clearSelection()
            return
        }

        when (selectionMode) {
            SelectionMode.SINGLE -> {
                val targetId = ids.first()
                var changed = false
                chips.forEach { chip ->
                    val shouldBeSelected = chip.id == targetId
                    if (chip.isSelected != shouldBeSelected) {
                        chip.isSelected = shouldBeSelected
                        changed = true
                    }
                }
                if (changed) {
                    notifySelectionChanged()
                    invalidate()
                }
            }

            SelectionMode.MULTI -> {
                var remaining = maxSelected
                var changed = false
                chips.forEach { chip ->
                    val shouldBeSelected = chip.id in ids && remaining > 0
                    if (chip.isSelected != shouldBeSelected) {
                        chip.isSelected = shouldBeSelected
                        changed = true
                    }
                    if (shouldBeSelected) {
                        remaining--
                    }
                }
                if (changed) {
                    notifySelectionChanged()
                    invalidate()
                }
            }
        }
    }

    init {
        context.withStyledAttributes(
            attrs,
            R.styleable.ChipGroup,
            defStyleAttr,
            0
        ) {

            val modeInt = getInt(
                R.styleable.ChipGroup_chipSelectionMode,
                1
            )
            selectionMode = if (modeInt == 0) SelectionMode.SINGLE else SelectionMode.MULTI

            maxSelected = getInt(
                R.styleable.ChipGroup_chipMaxSelectedCount,
                Int.MAX_VALUE
            )

            val defaultTextColor = 0xFF000000.toInt()
            chipTextColor = getColor(
                R.styleable.ChipGroup_chipTextColor,
                defaultTextColor
            )
            chipSelectedTextColor = getColor(
                R.styleable.ChipGroup_chipSelectedTextColor,
                chipTextColor
            )

            chipTextSizePx = getDimension(
                R.styleable.ChipGroup_chipTextSize,
                sp(14f)
            )

            chipFontFamilyResId = getResourceId(
                R.styleable.ChipGroup_chipFontFamily,
                0
            )

            chipFontWeight = getInt(
                R.styleable.ChipGroup_chipFontWeight,
                400
            )

            val defaultBg = 0xFFE0E0E0.toInt()
            chipBackgroundColor = getColor(
                R.styleable.ChipGroup_chipBackground,
                defaultBg
            )
            chipSelectedBackgroundColor = getColor(
                R.styleable.ChipGroup_chipSelectedBackground,
                chipBackgroundColor
            )

            chipCornerRadiusPx = getDimension(
                R.styleable.ChipGroup_chipCornerRadius,
                dp(16f).toFloat()
            )

            chipHorizontalGapPx = getDimensionPixelSize(
                R.styleable.ChipGroup_chipHorizontalGap,
                dp(8f)
            )
            chipVerticalGapPx = getDimensionPixelSize(
                R.styleable.ChipGroup_chipVerticalGap,
                dp(4f)
            )
            chipHorizontalPaddingPx = getDimensionPixelSize(
                R.styleable.ChipGroup_chipHorizontalPadding,
                dp(12f)
            )
            chipVerticalPaddingPx = getDimensionPixelSize(
                R.styleable.ChipGroup_chipVerticalPadding,
                dp(6f)
            )

        }

        textPaint.color = chipTextColor
        textPaint.textSize = chipTextSizePx

        if (chipFontFamilyResId != R.font.inter_bold) {
            val typeface = ResourcesCompat.getFont(context, chipFontFamilyResId)
            if (typeface != null) {
                textPaint.typeface = typeface
            }
        }
    }

    private fun dp(value: Float): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun sp(value: Float): Float =
        value * resources.displayMetrics.scaledDensity

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        if (isInEditMode && chips.isEmpty()) {
            setChips(previewChips)
        }

        if (chips.isEmpty()) {
            val minHeight = (chipVerticalPaddingPx * 2 + textPaint.textSize).toInt()
            val resolvedWidth = when (widthMode) {
                MeasureSpec.EXACTLY -> widthSize
                MeasureSpec.AT_MOST -> widthSize
                else -> 0
            }
            setMeasuredDimension(resolvedWidth, minHeight)
            return
        }

        val availableWidth = when (widthMode) {
            MeasureSpec.EXACTLY, MeasureSpec.AT_MOST -> widthSize
            else -> Int.MAX_VALUE
        }

        var x = 0f
        var y = 0f
        var lineHeight = 0f
        var totalHeight = 0f

        val fontMetrics = textPaint.fontMetrics
        val textHeight = fontMetrics.bottom - fontMetrics.top
        val chipHeight = textHeight + chipVerticalPaddingPx * 2

        chips.forEachIndexed { index, chip ->
            val (chipWidth, _) = measureChipWidthForLayout(
                originalText = chip.text,
                availableWidth = availableWidth
            )

            if (x + chipWidth > availableWidth && x > 0f) {
                x = 0f
                y += lineHeight + chipVerticalGapPx
                lineHeight = 0f
            }

            if (chipHeight > lineHeight) {
                lineHeight = chipHeight
            }

            x += chipWidth + chipHorizontalGapPx

            if (index == chips.lastIndex) {
                totalHeight = y + lineHeight
            }
        }

        val resolvedWidth = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> widthSize
            else -> x.toInt()
        }

        setMeasuredDimension(resolvedWidth, totalHeight.toInt())
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onChipsUpdated()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val fontMetrics = textPaint.fontMetrics
        val textBaselineShift = (fontMetrics.ascent + fontMetrics.descent) / 2f

        chips.forEach { chip ->
            if (chip.bounds.isEmpty) return@forEach

            val bgColor = if (chip.isSelected) {
                chipSelectedBackgroundColor
            } else {
                chipBackgroundColor
            }
            val textColor = if (chip.isSelected) {
                chipSelectedTextColor
            } else {
                chipTextColor
            }

            backgroundPaint.color = bgColor

            canvas.drawRoundRect(
                chip.bounds,
                chipCornerRadiusPx,
                chipCornerRadiusPx,
                backgroundPaint
            )

            textPaint.color = textColor
            val textX = chip.bounds.left + chipHorizontalPaddingPx
            val textY = chip.bounds.centerY() - textBaselineShift
            canvas.drawText(chip.displayText, textX, textY, textPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }

            MotionEvent.ACTION_UP -> {
                val x = event.x
                val y = event.y

                val target = chips.firstOrNull { chip ->
                    !chip.bounds.isEmpty && chip.bounds.contains(x, y)
                }

                if (target != null) {
                    onChipClicked(target)
                    performClick()
                    return true
                }
            }
        }

        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun onChipClicked(chip: Chip) {

        if (selectionMode == SelectionMode.SINGLE) {
            var changed = false

            chips.forEach {
                val shouldBeSelected = it.id == chip.id
                if (it.isSelected != shouldBeSelected) {
                    it.isSelected = shouldBeSelected
                    changed = true
                }
            }

            if (changed) {
                onTagClick?.invoke(chip.id, true)
                notifySelectionChanged()
                invalidate()
            }
            return
        }

        if (chip.isSelected) {
            chip.isSelected = false
            onTagClick?.invoke(chip.id, false)
            notifySelectionChanged()
            invalidate()
        } else {
            val selectedBefore = chips.count { it.isSelected }

            if (selectedBefore >= maxSelected) {
                return
            }

            chip.isSelected = true
            onTagClick?.invoke(chip.id, true)
            notifySelectionChanged()
            invalidate()

            val selectedAfter = selectedBefore + 1
            if (selectedAfter == maxSelected) {
                onSelectionLimitReached?.invoke(maxSelected)
            }
        }
    }

    private fun notifySelectionChanged() {
        val selectedIds = chips.filter { it.isSelected }.map { it.id }
        onSelectionChange?.invoke(selectedIds)
    }

    private fun measureChipWidthForLayout(
        originalText: String,
        availableWidth: Int,
    ): Pair<Float, String> {
        if (availableWidth <= 0) {
            val width = textPaint.measureText(originalText) + chipHorizontalPaddingPx * 2
            return width to originalText
        }

        val maxChipWidth = availableWidth.toFloat()
        val maxTextWidth = maxChipWidth - chipHorizontalPaddingPx * 2

        if (maxTextWidth <= 0f) {
            return (chipHorizontalPaddingPx * 2).toFloat() to originalText
        }

        var displayText = originalText
        var textWidth = textPaint.measureText(displayText)

        if (textWidth > maxTextWidth) {
            displayText = TextUtils.ellipsize(
                originalText,
                textPaint,
                maxTextWidth,
                TextUtils.TruncateAt.END
            ).toString()
            textWidth = textPaint.measureText(displayText)
        }

        val chipWidth = textWidth + chipHorizontalPaddingPx * 2
        return chipWidth to displayText
    }

    private fun onChipsUpdated() {
        if (chips.isEmpty() || width == 0) return

        var x = 0f
        var y = 0f
        var lineHeight = 0f

        val fontMetrics = textPaint.fontMetrics
        val textHeight = fontMetrics.bottom - fontMetrics.top
        val chipHeight = textHeight + chipVerticalPaddingPx * 2

        val availableWidth = width

        chips.forEach { chip ->
            val (chipWidth, displayText) = measureChipWidthForLayout(
                originalText = chip.text,
                availableWidth = availableWidth
            )
            chip.displayText = displayText

            if (x + chipWidth > availableWidth && x > 0f) {
                x = 0f
                y += lineHeight + chipVerticalGapPx
                lineHeight = 0f
            }

            val left = x
            val top = y
            val right = left + chipWidth
            val bottom = top + chipHeight
            chip.bounds.set(left, top, right, bottom)

            if (chipHeight > lineHeight) {
                lineHeight = chipHeight
            }

            x += chipWidth + chipHorizontalGapPx
        }

        invalidate()
    }

}