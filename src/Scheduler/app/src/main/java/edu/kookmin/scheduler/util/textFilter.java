package edu.kookmin.scheduler.util;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Pattern;

public class textFilter{
    // 영문만 허용 (숫자 포함)
    public static InputFilter filterAlphaNum = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");

            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };
}
