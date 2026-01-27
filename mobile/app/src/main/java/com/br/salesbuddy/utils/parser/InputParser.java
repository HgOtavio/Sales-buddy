package com.br.salesbuddy.utils.parser;

public class InputParser {

    public static double parseMoney(String formattedValue) {
        if (formattedValue == null || formattedValue.isEmpty()) return 0.0;

        try {

            String clean = formattedValue
                    .replace("R$", "")
                    .replace(".", "")
                    .replace(" ", "")
                    .replace(",", ".")
                    .trim();

            return Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}