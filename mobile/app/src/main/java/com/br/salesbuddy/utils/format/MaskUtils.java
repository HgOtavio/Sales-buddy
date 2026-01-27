package com.br.salesbuddy.utils.format;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class MaskUtils {


    public static TextWatcher moneyMask(final EditText editText) {
        return new TextWatcher() {
            private final WeakReference<EditText> editTextWeakReference = new WeakReference<>(editText);
            private boolean isUpdating = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                EditText editText = editTextWeakReference.get();
                if (editText == null) return;

                // Previne loop infinito
                if (isUpdating) return;

                String s = editable.toString();
                if (s.isEmpty()) return;

                isUpdating = true; // Trava

                try {
                    //  Remove tudo que não é número
                    String cleanString = s.replaceAll("[^0-9]", "");

                    if (cleanString.isEmpty()) {
                        editText.setText("");
                        isUpdating = false;
                        return;
                    }

                    // Transforma em BigDecimal e divide por 100 para criar os centavos
                    BigDecimal parsed = new BigDecimal(cleanString).divide(new BigDecimal(100));

                    // Formata para Brasil (R$ 1.230,50)
                    String formatted = NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(parsed);

                    editText.setText(formatted);
                    editText.setSelection(formatted.length()); // Move cursor pro final

                } catch (Exception e) {

                }

                isUpdating = false;
            }
        };
    }


    public static TextWatcher cpfMask(final EditText editText) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString().replaceAll("[^0-9]", "");
                String mask = "###.###.###-##";
                String mascara = "";

                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }

                int i = 0;
                for (char m : mask.toCharArray()) {
                    if (m != '#' && str.length() > old.length()) {
                        mascara += m;
                        continue;
                    }
                    try {
                        mascara += str.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }

                isUpdating = true;
                editText.setText(mascara);
                editText.setSelection(mascara.length());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

    public static String unmaskMoney(String value) {
        if (value == null || value.isEmpty()) return "0";

        return value.replaceAll("[^0-9,]", "").replace(",", ".");
    }
}