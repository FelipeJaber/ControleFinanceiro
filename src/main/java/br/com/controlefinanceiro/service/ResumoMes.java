package br.com.controlefinanceiro.service;

import java.math.BigDecimal;

/** Totais de um mês. "Previsto" inclui tudo; "realizado" apenas o que já foi pago/recebido. */
public record ResumoMes(
        BigDecimal entradasPrevistas,
        BigDecimal entradasRealizadas,
        BigDecimal saidasPrevistas,
        BigDecimal saidasRealizadas,
        int pendentesDeValor) {

    public BigDecimal saldoPrevisto() {
        return entradasPrevistas.subtract(saidasPrevistas);
    }

    public BigDecimal saldoRealizado() {
        return entradasRealizadas.subtract(saidasRealizadas);
    }
}
