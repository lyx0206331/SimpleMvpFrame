package com.adrian.wheelviewlib.adapter

/**
 * author:RanQing
 * date:2018/7/10 0010
 * description:
 **/
interface WheelAdapter<T> {

    /**
     * get item count
     * @return the count of wheel items
     */
    fun getItemCount(): Int

    /**
     * get a wheel item by index
     * @param index the item index
     * @return the wheel item text or null
     */
    fun getItem(index: Int): T?

    /**
     * get maximum item length.It is used to determine the wheel width.
     * If -1 is returned there will be used the default wheel width.
     * @param o the item object
     * @return the maximum item length or -1
     */
    fun indexOf(o: T): Int
}