package com.br.salesbuddy.utils.format;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtils {

    public static String formatCurrency(double value) {
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(value);
    }

    public static String formatCPF(String cpf) {
        if (cpf == null || cpf.isEmpty()) return "-";
        // Remove tudo que não é número
        String cleaned = cpf.replaceAll("[^0-9]", "");

        if (cleaned.length() == 11) {
            return cleaned.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        }
        return cpf; // Retorna original se não tiver 11 dígitos
    }
}