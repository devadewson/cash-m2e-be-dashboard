package com.m2e.dashboard.util;

import com.itextpdf.kernel.geom.PageSize;

import java.util.List;
import java.util.function.Function;

public class PdfUtility {
    public static <T> float calculateTotalTableWidth(List<T> rows, List<Function<T, String>> columnGetters) {
        float[] columnWidths = new float[columnGetters.size()];

        for (T row : rows) {
            for (int i = 0; i < columnGetters.size(); i++) {
                String cellValue = columnGetters.get(i).apply(row);
                columnWidths[i] = Math.max(columnWidths[i], calculateTextWidth(cellValue));
            }
        }

        float totalWidth = 0;
        for (float width : columnWidths) {
            totalWidth += width;
        }

        return totalWidth;
    }

    public static <T> float calculateTotalTableWidth(List<T> rows, List<String> columns, List<Function<T, String>> columnGetters) {
        float[] columnWidths = new float[columnGetters.size()];

        if(rows.isEmpty()){
            for (int i = 0; i < columnGetters.size(); i++) {
                String placeholderHeader = columns.get(i);
                columnWidths[i] = Math.max(columnWidths[i], calculateTextWidth(placeholderHeader));
            }
        } else {
            for (T row : rows) {
                for (int i = 0; i < columnGetters.size(); i++) {
                    String cellValue = columnGetters.get(i).apply(row);
                    columnWidths[i] = Math.max(columnWidths[i], calculateTextWidth(cellValue));
                }
            }
        }

        float totalWidth = 0;
        for (float width : columnWidths) {
            totalWidth += width;
        }

        return totalWidth;
    }

    public static <T> float calculateTotalHeadersTableWidth(List<String> columns, List<Function<T, String>> columnGetters) {
        float[] columnWidths = new float[columnGetters.size()];
        for (int i = 0; i < columnGetters.size(); i++) {
            String placeholderHeader = columns.get(i);
            columnWidths[i] = Math.max(columnWidths[i], calculateTextWidth(placeholderHeader));
        }

        float totalWidth = 0;
        for (float width : columnWidths) {
            totalWidth += width;
        }

        return totalWidth;
    }

    public static <T> float[] calculateColumnWidths(List<T> rows, List<String> columns, List<Function<T, String>> columnGetters, float pageWidth) {
        float[] columnWidths = new float[columnGetters.size()];
        float totalWidth = 0;

        if(rows.isEmpty()){
            for (int i = 0; i < columnGetters.size(); i++) {
                String placeholderHeader = columns.get(i);
                columnWidths[i] = Math.max(columnWidths[i], calculateTextWidth(placeholderHeader));
            }
        } else {
            for (T row : rows) {
                for (int i = 0; i < columnGetters.size(); i++) {
                    String cellValue = columnGetters.get(i).apply(row);
                    columnWidths[i] = Math.max(columnWidths[i], calculateTextWidth(cellValue));
                }
            }
        }

        for (float width : columnWidths) {
            totalWidth += width;
        }

        if (totalWidth == 0) {
            totalWidth = 1;
        }

        for (int i = 0; i < columnWidths.length; i++) {
            columnWidths[i] = (columnWidths[i] / totalWidth) * pageWidth;
        }

        return columnWidths;
    }

    public static <T> float[] calculateHeadersColumnWidths(List<String> columns, List<Function<T, String>> columnGetters, float pageWidth) {
        float[] columnWidths = new float[columnGetters.size()];
        float totalWidth = 0;


        for (int i = 0; i < columnGetters.size(); i++) {
            String placeholderHeader = columns.get(i);
            columnWidths[i] = Math.max(columnWidths[i], calculateTextWidth(placeholderHeader));
        }


        for (float width : columnWidths) {
            totalWidth += width;
        }

        if (totalWidth == 0) {
            totalWidth = 1;
        }

        for (int i = 0; i < columnWidths.length; i++) {
            columnWidths[i] = (columnWidths[i] / totalWidth) * pageWidth;
        }

        return columnWidths;
    }

    public static <T> float[] calculateColumnWidths(List<T> rows, List<Function<T, String>> columnGetters, float pageWidth) {
        float[] columnWidths = new float[columnGetters.size()];
        float totalWidth = 0;

        for (T row : rows) {
            for (int i = 0; i < columnGetters.size(); i++) {
                String cellValue = columnGetters.get(i).apply(row);
                columnWidths[i] = Math.max(columnWidths[i], calculateTextWidth(cellValue));
            }
        }

        for (float width : columnWidths) {
            totalWidth += width;
        }

        if (totalWidth == 0) {
            totalWidth = 1;
        }

        for (int i = 0; i < columnWidths.length; i++) {
            columnWidths[i] = (columnWidths[i] / totalWidth) * pageWidth;
        }

        return columnWidths;
    }


    private static float calculateTextWidth(String text) {
        if (text == null) {
            return 0;
        }
        int fontSize = 12;
        float averageCharacterWidth = fontSize * 0.6f;
        return text.length() * averageCharacterWidth;
    }

    public static PageSize getOptimalPageSize(float totalTableWidth) {
        PageSize defaultPageSize = PageSize.A4;
        if (totalTableWidth > defaultPageSize.getWidth()) {
            if (totalTableWidth <= PageSize.A4.rotate().getWidth()) {
                return PageSize.A4.rotate();
            } else if (totalTableWidth <= PageSize.A3.rotate().getWidth()) {
                return PageSize.A3.rotate();
            } else if (totalTableWidth <= PageSize.A2.rotate().getWidth()) {
                return PageSize.A2.rotate();
            } else if (totalTableWidth <= PageSize.A1.rotate().getWidth()) {
                return PageSize.A1.rotate();
            } else {
                return PageSize.A0.rotate();
            }
        }
        return defaultPageSize;
    }
}
