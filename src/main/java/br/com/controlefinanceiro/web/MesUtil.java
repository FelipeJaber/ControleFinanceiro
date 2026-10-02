package br.com.controlefinanceiro.web;

import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

final class MesUtil {
    static final Locale PT_BR = Locale.of("pt", "BR");

    private MesUtil() {}

    static YearMonth ou(YearMonth mes) {
        return mes != null ? mes : YearMonth.now();
    }

    static String rotulo(YearMonth mes) {
        String nome = mes.getMonth().getDisplayName(TextStyle.FULL, PT_BR);
        return Character.toUpperCase(nome.charAt(0)) + nome.substring(1) + " de " + mes.getYear();
    }
}
