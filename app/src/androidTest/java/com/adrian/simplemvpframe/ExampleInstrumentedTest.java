package com.adrian.simplemvpframe;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.adrian.simplemvpframe.utils.SortUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.adrian.simplemvpframe", appContext.getPackageName());
    }

    @Test
    public void testSort() {
        int[] expected = new int[]{3, 8, 10, 12, 33, 34, 35, 64};
        int[] array = new int[]{3, 8, 10, 34, 12, 33, 35, 64};
        assertArrayEquals(expected, SortUtils.INSTANCE.insertionSort(array));
    }
}
