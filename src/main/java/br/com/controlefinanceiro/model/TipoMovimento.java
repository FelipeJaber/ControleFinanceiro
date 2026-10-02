package br.com.controlefinanceiro.model;

public enum TipoMovimento {
    ENTRADA("Entrada"),
    SAIDA("Saída");

    private final String rotulo;

    TipoMovimento(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
