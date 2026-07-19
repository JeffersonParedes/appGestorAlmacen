package com.gestoralmacen.app.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public final class ReportUtils {

    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("es", "PE"));
    private static final NumberFormat QUANTITY_FORMAT = NumberFormat.getNumberInstance(new Locale("es", "PE"));

    private ReportUtils() {
    }

    public static String formatCurrency(BigDecimal amount) {
        return amount != null ? CURRENCY_FORMAT.format(amount) : "S/ 0.00";
    }

    public static String formatCurrency(Double amount) {
        return amount != null ? CURRENCY_FORMAT.format(amount) : "S/ 0.00";
    }

    public static String formatQuantity(BigDecimal quantity) {
        return quantity != null ? QUANTITY_FORMAT.format(quantity) : "0";
    }
}
