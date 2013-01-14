package com.onextent.android.ui;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;

import com.onextent.augie.camera.NamedInt;

public class UiUtil {

    public final static List<NamedInt> COLOR_LIST;

    static {
        COLOR_LIST = new ArrayList<NamedInt>();
        COLOR_LIST.add(new ColorItem(Color.GREEN, "Green"));
        COLOR_LIST.add(new ColorItem(Color.GRAY, "Gray"));
        COLOR_LIST.add(new ColorItem(Color.RED, "Red"));
        COLOR_LIST.add(new ColorItem(Color.BLACK, "Black"));
        COLOR_LIST.add(new ColorItem(Color.WHITE, "White"));
        COLOR_LIST.add(new ColorItem(Color.BLUE, "Blue"));
        COLOR_LIST.add(new ColorItem(Color.CYAN, "Cyan"));
        COLOR_LIST.add(new ColorItem(Color.MAGENTA, "Magenta"));
        COLOR_LIST.add(new ColorItem(Color.YELLOW, "Yellow"));
    }

    public static class ColorItem implements NamedInt {

        private final int color;
        private final String name;
        ColorItem(int c, String n) {
            color = c;
            name = n;
        }
        @Override
        public String toString() {
            return name;
        }
        @Override
        public int toInt() {
            return color;
        }
    }
}
