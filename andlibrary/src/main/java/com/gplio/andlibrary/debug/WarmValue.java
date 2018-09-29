package com.gplio.andlibrary.debug;

import com.gplio.andlibrary.files.TextFiles;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by goncalopalaio on 16/09/18.
 */
public class WarmValue {
    private static String TAG = "WarmValue";
    private static String FILENAME = "values.txt";

    private static final String SEPARATOR = ":";
    private static final String END = ";";

    private static final String TYPE_STRING = "string";
    private static final String TYPE_INT = "int";
    private static final String TYPE_FLOAT = "float";

    public static String getString(String key) {
        Element loaded = load(TYPE_STRING, key);

        if (loaded == null) return "";

        return loaded.rawValue;
    }

    public static int getInt(String key) {
        Element loaded = load(TYPE_INT, key);

        if (loaded == null) return 0;

        return Integer.valueOf(loaded.rawValue);
    }

    public static float getFloat(String key) {
        Element loaded = load(TYPE_FLOAT, key);

        if (loaded == null) return 0;

        return Float.valueOf(loaded.rawValue);
    }

    @Nullable
    private static Element load(String type, String key) {
        List<Element> list = loadValues();

        return searchValue(list, type, key);
    }

    private static Element searchValue(List<Element> list, String type, String varName) {
        for (Element element : list) {
            if (element.name.equals(varName) && element.type.equals(type)) {
                return element;
            }
        }

        return null;
    }

    private static List<Element> loadValues() {
        String s = TextFiles.readTextFromSdcard(FILENAME, "");

        if (TextUtils.isEmpty(s)) {
            return new ArrayList<>();
        }

        String[] split = s.split(END);

        List<Element> list = new LinkedList<>();
        for (String line : split) {
            line = line.trim();
            if (TextUtils.isEmpty(line)) {
                continue;
            }

            if (!line.contains(SEPARATOR)) {
                continue;
            }
            String[] lineValues = line.split(SEPARATOR);

            list.add(new Element(
                    lineValues[0],
                    lineValues[1],
                    lineValues[2]));
        }

        return list;
    }

    private static class Element {
        public String type;
        public String name;
        public String rawValue;

        public Element(String type, String name, String rawValue) {
            this.type = type;
            this.name = name;
            this.rawValue = rawValue;
        }
    }
}
