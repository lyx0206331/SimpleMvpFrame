
package com.adrian.simplemvpframe.views.wheel_view1;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Scroller;


import com.adrian.simplemvpframe.R;
import com.adrian.simplemvpframe.utils.PxUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * 滚轮组件
 */
public class WheelView extends View {

    /**
     * 滚动时间
     */
    private int mScrollingDuration = 400;

    /**
     * 滚动最小移动距离
     */
    private int mMinDeltaForScrolling = 1;

    /**
     * value 文字颜色
     */
    private int mValueTextColor = 0xff585A57;

    /**
     * label 文字颜色
     */
    private int mLabelTextColor = 0xff585A57;

    /**
     * 每个item上下、左右文字间距
     */
    private int mItemPaddingVertical = (int) PxUtil.dip2px(10), mItemPaddingHorizontal = (int) PxUtil.dip2px(20);
    /**
     * value和label之间的文字间距
     */
    private int mValueLabelPadding = (int) PxUtil.dip2px(20);

    /**
     * 文字大小
     */
    public int mTextSize = (int) PxUtil.dip2px(16);

    /**
     * 顶部和底部阴影颜色
     */
    private int[] mShadowsColors = new int[]{0xccffffff, 0x00ffffff};
    /**
     * 阴影图片
     */
    private GradientDrawable mTopShadowDrawable, mBottomShadowDrawable;

    /**
     * 可见item数量
     */
    private int mVisibleItems = 3;

    // Label
    private String label;

    // Wheel Values
    private WheelAdapter adapter = null;

    //当前item序号
    private int currentItem = 0;
    // Item height
    private int itemHeight = 0;

    // Text paints
    private TextPaint itemsPaint;
    private TextPaint valuePaint;
    private Paint paint;

    // Layouts
    private StaticLayout itemsLayout;  //所有文字
    private StaticLayout labelLayout;   //lable
    private StaticLayout valueLayout;  //curValue

    // Scrolling
    private boolean isScrollingPerformed;
    private int scrollingOffset;

    // Scrolling animation
    private GestureDetector gestureDetector;
    private Scroller scroller;
    private int lastScrollY;

    // Cyclic
    boolean isCyclic = false;

    // Listeners
    private List<OnWheelChangedListener> changingListeners = new LinkedList<OnWheelChangedListener>();
    private List<OnWheelScrollListener> scrollingListeners = new LinkedList<OnWheelScrollListener>();

    private int valueWidth = 0;
    private int labelWidth = 0;

    private int gapLineColor = 0xcccecece;

    public WheelView(Context context) {
        super(context);
        initData(context);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WheelView);
        mValueTextColor = ta.getColor(R.styleable.WheelView_valueTextColor, mValueTextColor);
        mLabelTextColor = ta.getColor(R.styleable.WheelView_labelTextColor, mLabelTextColor);
        mItemPaddingVertical = ta.getDimensionPixelSize(R.styleable.WheelView_itemPaddingVertical, (int) PxUtil.dip2px(getContext(), 10));
        mItemPaddingHorizontal = ta.getDimensionPixelSize(R.styleable.WheelView_itemPaddingHorizontal, (int) PxUtil.dip2px(getContext(), 30));
        mValueLabelPadding = ta.getDimensionPixelSize(R.styleable.WheelView_valueLabelPadding, (int) PxUtil.dip2px(getContext(), 30));
        mTextSize = ta.getDimensionPixelSize(R.styleable.WheelView_itemTextSize, (int) PxUtil.dip2px(getContext(), 20));
        mVisibleItems = ta.getInt(R.styleable.WheelView_visibleItems, mVisibleItems);
        label = ta.getString(R.styleable.WheelView_label);
        isCyclic = ta.getBoolean(R.styleable.WheelView_isCyclic, isCyclic);
        mShadowsColors = new int[]{ta.getColor(R.styleable.WheelView_shadowColor1, 0x66ffffff),
                ta.getColor(R.styleable.WheelView_shadowColor2, 0x00ffffff)};
        ta.recycle();

        initData(context);
    }

    private void initData(Context context) {
        gestureDetector = new GestureDetector(context, gestureListener);
        gestureDetector.setIsLongpressEnabled(false);
        scroller = new Scroller(context);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(PxUtil.dip2px(getContext(), 1));

        if (itemsPaint == null) {
            itemsPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            itemsPaint.setTextSize(mTextSize);
        }

        if (valuePaint == null) {
            valuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
            valuePaint.setTextSize(mTextSize);
        }

        if (mTopShadowDrawable == null) {
            mTopShadowDrawable
                    = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, mShadowsColors);
        }

        if (mBottomShadowDrawable == null) {
            mBottomShadowDrawable
                    = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, mShadowsColors);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //宽度
        //mItemPaddingHorizontal + valueWidth + mValueLabelPadding + lableWidth + mItemPaddingHorizontal
        int width = calculateLayoutWidth(widthSize, widthMode);

        //高度
        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = getDesiredHeight(itemsLayout);

            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }

        setMeasuredDimension(width, height);
    }


    private int calculateLayoutWidth(int widthSize, int mode) {
        int width = widthSize;

        valueWidth = 0;
        int maxLength = getMaxTextLength();
        if (maxLength > 0) {
            float textWidth = (float) Math.ceil(Layout.getDesiredWidth("0", itemsPaint));
            valueWidth = (int) (maxLength * textWidth);
        } else {
            valueWidth = 0;
        }

        labelWidth = 0;
        if (label != null && label.length() > 0) {
            labelWidth = (int) Math.ceil(Layout.getDesiredWidth(label, valuePaint));
        }

        if (mode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            //mItemPaddingHorizontal + valueWidth + mValueLabelPadding + lableWidth + mItemPaddingHorizontal
            width = valueWidth + labelWidth + 2 * mItemPaddingHorizontal;
            if (labelWidth > 0) {
                width += mValueLabelPadding;
            }

            // Check against our minimum width
            width = Math.max(width, getSuggestedMinimumWidth());

            if (mode == MeasureSpec.AT_MOST && widthSize < width) {
                width = widthSize;
            }
        }

        if (valueWidth > 0) {
            createLayouts(valueWidth, labelWidth);
        }

        return width;
    }

    private int getDesiredHeight(Layout layout) {
        if (layout == null) {
            return 0;
        }

        int desired = getItemHeight() * mVisibleItems;
        // Check against our minimum height
        desired = Math.max(desired, getSuggestedMinimumHeight());
        return desired;
    }

    /**
     * Returns height of wheel item
     *
     * @return the item height
     */
    private int getItemHeight() {
        if (itemHeight != 0) {
            return itemHeight;
        } else if (itemsLayout != null && itemsLayout.getLineCount() > 2) {
            itemHeight = itemsLayout.getLineTop(1) - itemsLayout.getLineTop(0);
            return itemHeight;
        }
        return getHeight() / mVisibleItems;
    }

    /**
     * Creates layouts
     *
     * @param widthValue width of values layout
     * @param widthLabel width of label layout
     */
    private void createLayouts(int widthValue, int widthLabel) {
        if (itemsLayout == null || itemsLayout.getWidth() > widthValue) {
            itemsLayout = new StaticLayout(buildText(isScrollingPerformed),
                    itemsPaint,
                    widthValue,
                    Layout.Alignment.ALIGN_CENTER,
                    1,
                    mItemPaddingVertical * 2,
                    false);
        } else {
            itemsLayout.increaseWidthTo(widthValue);
        }

        if (!isScrollingPerformed
                && (valueLayout == null || valueLayout.getWidth() > widthValue)) {
            String text = getAdapter() != null ? getAdapter().getItem(currentItem) : null;
            valueLayout = new StaticLayout(text != null ? text : "",
                    valuePaint,
                    widthValue,
                    Layout.Alignment.ALIGN_CENTER,
                    1,
                    mItemPaddingVertical * 2,
                    false);
        } else if (isScrollingPerformed) {
            valueLayout = null;
        } else {
            valueLayout.increaseWidthTo(widthValue);
        }

        if (widthLabel > 0) {
            if (labelLayout == null || labelLayout.getWidth() > widthLabel) {
                labelLayout = new StaticLayout(label,
                        valuePaint,
                        widthLabel,
                        Layout.Alignment.ALIGN_CENTER,
                        1,
                        mItemPaddingVertical * 2,
                        false);
            } else {
                labelLayout.increaseWidthTo(widthLabel);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (itemsLayout == null) {
            if (valueWidth == 0) {
                calculateLayoutWidth(getWidth(), MeasureSpec.EXACTLY);
            } else {
                createLayouts(valueWidth, labelWidth);
            }
        }

        int itemHeight = getItemHeight();

        //2.文字，要居中显示，需要向下偏移mItemPaddingVertical
        if (valueWidth > 0) {
            drawItems(canvas);
            drawValue(canvas);
        }

        //中间item的范围
        int sh = (getMeasuredHeight() - itemHeight) / 2;
        Rect centerItembounds = new Rect(0, sh, getMeasuredWidth(), getMeasuredHeight() - sh);

        //3.阴影
        mTopShadowDrawable.setBounds(0, 0, getMeasuredWidth(), centerItembounds.top);
        mTopShadowDrawable.draw(canvas);
        mBottomShadowDrawable.setBounds(0, centerItembounds.bottom, getMeasuredWidth(), getMeasuredHeight());
        mBottomShadowDrawable.draw(canvas);

        //中间分隔线
        paint.setColor(gapLineColor);
        canvas.drawLine(0, centerItembounds.top, getMeasuredWidth(), centerItembounds.top, paint);
        canvas.drawLine(0, centerItembounds.bottom, getMeasuredWidth(), centerItembounds.bottom, paint);
    }

    /**
     * Draws items
     */
    private void drawItems(Canvas canvas) {
        canvas.save();

        int px = (getMeasuredWidth() - valueWidth - (labelWidth > 0 ? (labelWidth + mValueLabelPadding) : 0)) / 2;

        int top = itemsLayout.getLineTop(1);
        canvas.translate(px, -top + scrollingOffset + mItemPaddingVertical);

        itemsPaint.setColor(mValueTextColor);
        itemsPaint.drawableState = getDrawableState();
        itemsLayout.draw(canvas);

        canvas.restore();
    }

    /**
     * Draws value and label layout
     */
    private void drawValue(Canvas canvas) {
        valuePaint.setColor(mValueTextColor);
        valuePaint.drawableState = getDrawableState();

        Rect bounds = new Rect();
        itemsLayout.getLineBounds(mVisibleItems / 2, bounds);

        int px = (getMeasuredWidth() - valueWidth - (labelWidth > 0 ? (labelWidth + mValueLabelPadding) : 0)) / 2;

        // draw current value
        if (valueLayout != null) {
            canvas.save();
            canvas.translate(px,
                    bounds.top + scrollingOffset + mItemPaddingVertical);
            valueLayout.draw(canvas);
            canvas.restore();
        }

        // draw label
        if (labelLayout != null) {
            canvas.save();
            canvas.translate(px + itemsLayout.getWidth() + mValueLabelPadding,
                    bounds.top + mItemPaddingVertical);
            labelLayout.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        WheelAdapter adapter = getAdapter();
        if (adapter == null) {
            return true;
        }

        if (!gestureDetector.onTouchEvent(event)
                && event.getAction() == MotionEvent.ACTION_UP) {
            justify();
        }
        return true;
    }


    /**
     * Justifies wheel
     */
    private void justify() {
        if (adapter == null) {
            return;
        }

        lastScrollY = 0;
        int offset = scrollingOffset;
        int itemHeight = getItemHeight();
        boolean needToIncrease = offset > 0 ? currentItem < adapter
                .getItemsCount() : currentItem > 0;
        if ((isCyclic || needToIncrease)
                && Math.abs((float) offset) > (float) itemHeight / 2) {
            if (offset < 0)
                offset += itemHeight + mMinDeltaForScrolling;
            else
                offset -= itemHeight + mMinDeltaForScrolling;
        }
        if (Math.abs(offset) > mMinDeltaForScrolling) {
            scroller.startScroll(0, 0, 0, offset, mMinDeltaForScrolling);
            setNextMessage(MESSAGE_JUSTIFY);
        } else {
            finishScrolling();
        }
    }


    /**
     * Gets wheel adapter
     *
     * @return the adapter
     */
    public WheelAdapter getAdapter() {
        return adapter;
    }

    /**
     * Sets wheel adapter
     *
     * @param adapter the new wheel adapter
     */
    public void setAdapter(WheelAdapter adapter) {
        this.adapter = adapter;
        clearLayouts();
        requestLayout();
    }

    /**
     * Set the the specified scrolling interpolator
     *
     * @param interpolator the interpolator
     */
    public void setInterpolator(Interpolator interpolator) {
        scroller.forceFinished(true);
        scroller = new Scroller(getContext(), interpolator);
    }

    /**
     * Gets count of visible items
     *
     * @return the count of visible items
     */
    public int getVisibleItems() {
        return mVisibleItems;
    }

    /**
     * Gets label
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets label
     *
     * @param newLabel the label to set
     */
    public void setLabel(String newLabel) {
        int length = label == null ? 0 : label.length();
        if (label == null || !label.equals(newLabel)) {
            label = newLabel;

            clearLayouts();
            int newLength = label == null ? 0 : label.length();
            if (newLength != length) {
                requestLayout();
            } else {
                invalidate();
            }
        }
    }

    /**
     * Adds wheel changing listener
     *
     * @param listener the listener
     */
    public void addChangingListener(OnWheelChangedListener listener) {
        changingListeners.add(listener);
    }

    /**
     * Removes wheel changing listener
     *
     * @param listener the listener
     */
    public void removeChangingListener(OnWheelChangedListener listener) {
        changingListeners.remove(listener);
    }

    /**
     * Notifies changing listeners
     *
     * @param oldValue the old wheel value
     * @param newValue the new wheel value
     */
    protected void notifyChangingListeners(int oldValue, int newValue) {
        for (OnWheelChangedListener listener : changingListeners) {
            listener.onChanged(this, oldValue, newValue);
        }
    }

    /**
     * Adds wheel scrolling listener
     *
     * @param listener the listener
     */
    public void addScrollingListener(OnWheelScrollListener listener) {
        scrollingListeners.add(listener);
    }

    /**
     * Removes wheel scrolling listener
     *
     * @param listener the listener
     */
    public void removeScrollingListener(OnWheelScrollListener listener) {
        scrollingListeners.remove(listener);
    }

    /**
     * Notifies listeners about starting scrolling
     */
    protected void notifyScrollingListenersAboutStart() {
        for (OnWheelScrollListener listener : scrollingListeners) {
            listener.onScrollingStarted(this);
        }
    }

    /**
     * Notifies listeners about ending scrolling
     */
    protected void notifyScrollingListenersAboutEnd() {
        for (OnWheelScrollListener listener : scrollingListeners) {
            listener.onScrollingFinished(this);
        }
    }

    /**
     * Gets current value
     *
     * @return the current value
     */
    public int getCurrentItem() {
        return currentItem;
    }

    /**
     * 获得当前选中的字符
     */
    public String getCurrentItemString() {
        return getTextItem(currentItem);
    }

    /**
     * Sets the current item. Does nothing when index is wrong.
     *
     * @param index    the item index
     * @param animated the animation flag
     */
    public void setCurrentItem(int index, boolean animated) {
        if (adapter == null || adapter.getItemsCount() == 0) {
            return; // throw?
        }
        if (index < 0 || index >= adapter.getItemsCount()) {
            if (isCyclic) {
                while (index < 0) {
                    index += adapter.getItemsCount();
                }
                index %= adapter.getItemsCount();
            } else {
                return; // throw?
            }
        }
        if (index != currentItem) {
            if (animated) {
                scroll(index - currentItem, mScrollingDuration);
            } else {
                clearLayouts();

                int old = currentItem;
                currentItem = index;

                notifyChangingListeners(old, currentItem);

                invalidate();
            }
        }
    }

    /**
     * Sets the current item w/o animation. Does nothing when index is wrong.
     *
     * @param index the item index
     */
    public void setCurrentItem(int index) {
        setCurrentItem(index, false);
    }

    /**
     * Tests if wheel is cyclic. That means before the 1st item there is shown
     * the last one
     *
     * @return true if wheel is cyclic
     */
    public boolean isCyclic() {
        return isCyclic;
    }

    /**
     * Set wheel cyclic flag
     *
     * @param isCyclic the flag to set
     */
    public void setCyclic(boolean isCyclic) {
        this.isCyclic = isCyclic;

        clearLayouts();
        invalidate();
    }

    /**
     * Invalidates layouts
     */
    private void clearLayouts() {
        itemsLayout = null;
        valueLayout = null;
        labelLayout = null;
        valueWidth = 0;
        labelWidth = 0;
        scrollingOffset = 0;
    }

    /**
     * Returns text item by index
     *
     * @param index the item index
     * @return the item or null
     */
    private String getTextItem(int index) {
        if (adapter == null || adapter.getItemsCount() == 0) {
            return null;
        }
        int count = adapter.getItemsCount();
        if ((index < 0 || index >= count) && !isCyclic) {
            return null;
        } else {
            while (index < 0) {
                index = count + index;
            }
        }

        index %= count;
        return adapter.getItem(index);
    }

    /**
     * Builds text depending on current value
     *
     * @param useCurrentValue
     * @return the text
     */
    private String buildText(boolean useCurrentValue) {
        StringBuilder itemsText = new StringBuilder();
        int addItems = mVisibleItems / 2 + 1;

        for (int i = currentItem - addItems; i <= currentItem + addItems; i++) {
            if (useCurrentValue || i != currentItem) {
                String text = getTextItem(i);
                if (text != null) {
                    itemsText.append(text);
                }
            }
            if (i < currentItem + addItems) {
                itemsText.append("\n");
            }
        }

        return itemsText.toString();
    }

    /**
     * 返回显示字符的最大文字长度
     */
    private int getMaxTextLength() {
        WheelAdapter adapter = getAdapter();
        if (adapter == null) {
            return 0;
        }

        int adapterLength = adapter.getMaximumLength();
        if (adapterLength > 0) {
            return adapterLength;
        }

        String maxText = null;
        int addItems = mVisibleItems / 2;
        for (int i = Math.max(currentItem - addItems, 0);
             i < Math.min(currentItem + mVisibleItems, adapter.getItemsCount());
             i++) {
            String text = adapter.getItem(i);
            if (text != null
                    && (maxText == null || maxText.length() < text.length())) {
                maxText = text;
            }
        }

        return maxText != null ? maxText.length() : 0;
    }


    /**
     * Scrolls the wheel
     *
     * @param delta the scrolling value
     */
    private void doScroll(int delta) {
        scrollingOffset += delta;

        int count = scrollingOffset / getItemHeight();
        int pos = currentItem - count;
        if (isCyclic && adapter.getItemsCount() > 0) {
            // fix position by rotating
            while (pos < 0) {
                pos += adapter.getItemsCount();
            }
            pos %= adapter.getItemsCount();
        } else if (isScrollingPerformed) {
            //
            if (pos < 0) {
                count = currentItem;
                pos = 0;
            } else if (pos >= adapter.getItemsCount()) {
                count = currentItem - adapter.getItemsCount() + 1;
                pos = adapter.getItemsCount() - 1;
            }
        } else {
            // fix position
            pos = Math.max(pos, 0);
            pos = Math.min(pos, adapter.getItemsCount() - 1);
        }

        int offset = scrollingOffset;
        if (pos != currentItem) {
            setCurrentItem(pos, false);
        } else {
            invalidate();
        }

        // update offset
        scrollingOffset = offset - count * getItemHeight();
        if (scrollingOffset > getMeasuredHeight()) {
            scrollingOffset = scrollingOffset % getMeasuredHeight() + getMeasuredHeight();
        }
    }

    // gesture listener
    private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
        public boolean onDown(MotionEvent e) {
            if (isScrollingPerformed) {
                scroller.forceFinished(true);
                clearMessages();
                return true;
            }
            return false;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            startScrolling();
            doScroll((int) -distanceY);
            return true;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            lastScrollY = currentItem * getItemHeight() + scrollingOffset;
            int maxY = isCyclic ? 0x7FFFFFFF : adapter.getItemsCount()
                    * getItemHeight();
            int minY = isCyclic ? -maxY : 0;
            scroller.fling(0, lastScrollY, 0, (int) -velocityY / 2, 0, 0, minY,
                    maxY);
            setNextMessage(MESSAGE_SCROLL);
            return true;
        }
    };

    // Messages
    private static final int MESSAGE_SCROLL = 0;
    private static final int MESSAGE_JUSTIFY = 1;

    /**
     * Set next message to queue. Clears queue before.
     *
     * @param message the message to set
     */
    private void setNextMessage(int message) {
        clearMessages();
        animationHandler.sendEmptyMessage(message);
    }

    /**
     * Clears messages from queue
     */
    private void clearMessages() {
        animationHandler.removeMessages(MESSAGE_SCROLL);
        animationHandler.removeMessages(MESSAGE_JUSTIFY);
    }

    // animation handler
    private Handler animationHandler = new Handler() {
        public void handleMessage(Message msg) {
            scroller.computeScrollOffset();
            int currY = scroller.getCurrY();
            int delta = lastScrollY - currY;
            lastScrollY = currY;
            if (delta != 0) {
                doScroll(delta);
            }

            // scrolling is not finished when it comes to final Y
            // so, finish it manually
            if (Math.abs(currY - scroller.getFinalY()) < mMinDeltaForScrolling) {
                currY = scroller.getFinalY();
                scroller.forceFinished(true);
            }
            if (!scroller.isFinished()) {
                animationHandler.sendEmptyMessage(msg.what);
            } else if (msg.what == MESSAGE_SCROLL) {
                justify();
            } else {
                finishScrolling();
            }
        }
    };


    /**
     * Starts scrolling
     */
    private void startScrolling() {
        if (!isScrollingPerformed) {
            isScrollingPerformed = true;
            notifyScrollingListenersAboutStart();
        }
    }

    /**
     * Finishes scrolling
     */
    void finishScrolling() {
        if (isScrollingPerformed) {
            notifyScrollingListenersAboutEnd();
            isScrollingPerformed = false;
        }
        clearLayouts();
        invalidate();
    }

    /**
     * Scroll the wheel
     *
     * @param time scrolling duration
     */
    public void scroll(int itemsToScroll, int time) {
        scroller.forceFinished(true);

        lastScrollY = scrollingOffset;
        int offset = itemsToScroll * getItemHeight();

        scroller.startScroll(0, lastScrollY, 0, offset - lastScrollY, time);
        setNextMessage(MESSAGE_SCROLL);

        startScrolling();
    }


    //=========================== 添加 ==============================

    public void setTextSize(int textsize) {
        this.mTextSize = textsize;

        if (itemsPaint == null) {
            itemsPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
                    | Paint.FAKE_BOLD_TEXT_FLAG);
            itemsPaint.setTextSize(mTextSize);
        }

        if (valuePaint == null) {
            valuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
                    | Paint.FAKE_BOLD_TEXT_FLAG | Paint.DITHER_FLAG);
            valuePaint.setTextSize(mTextSize);
        }

        clearLayouts();
        requestLayout();
    }

    /**
     * Sets count of visible items
     * 必须奇数
     */
    public void setVisibleItems(int count) {
        if (count % 2 == 0) {
            count++;
        }
        mVisibleItems = count;

        clearLayouts();
        requestLayout();
    }
}
