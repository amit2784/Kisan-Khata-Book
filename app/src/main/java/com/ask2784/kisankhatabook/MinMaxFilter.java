package com.ask2784.kisankhatabook;
import android.text.InputFilter;
import android.text.Spanned;

public class MinMaxFilter implements InputFilter {
    private final int IntMin;
    private final int IntMax;

    public MinMaxFilter(int minVal, int maxVal) {
        this.IntMin = minVal;
        this.IntMax = maxVal;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(IntMin, IntMax, input))
                return null;
        } catch (NumberFormatException e) {
            e.printStackTrace();

        }
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
