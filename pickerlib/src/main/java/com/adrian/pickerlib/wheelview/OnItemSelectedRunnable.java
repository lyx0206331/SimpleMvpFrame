package com.adrian.pickerlib.wheelview;

final class OnItemSelectedRunnable implements Runnable {
    final WheelView loopView;

    OnItemSelectedRunnable(WheelView loopview) {
        loopView = loopview;
    }

    @Override
    public final void run() {
        if (loopView != null && loopView.onItemSelectedListener != null) {
            loopView.onItemSelectedListener.onItemSelected(loopView.getCurrentItem());
        }
    }
}
