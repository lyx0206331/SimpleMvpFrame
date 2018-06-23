package com.adrian.simplemvpframe.views.wheel_view1;

/**
 * 一个数字的整数倍显示
 *
 * @author chenshiqiang
 */
public class MultipleWheelAdapter implements WheelAdapter {

    /**
     * 基数
     */
    private int baseNum = 1;
    /**
     * 最小倍数
     */
    private int minMultiple = 0;
    /**
     * 最大倍数
     */
    private int maxMultiple = 9;

    public MultipleWheelAdapter(int baseNum, int minMultiple, int maxMultiple) {
        super();
        this.baseNum = baseNum;
        this.minMultiple = minMultiple;
        this.maxMultiple = maxMultiple;
    }

    @Override
    public int getItemsCount() {
        // TODO Auto-generated method stub
        return maxMultiple - minMultiple + 1;
    }

    @Override
    public String getItem(int index) {
        // TODO Auto-generated method stub
        return "" + (minMultiple + index) * baseNum;
    }

    @Override
    public int getMaximumLength() {
        // TODO Auto-generated method stub
        return ("" + maxMultiple * baseNum).length();
    }

}
