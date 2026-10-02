package br.com.controlefinanceiro.model;

public enum Natureza {
    FIXO("Fixo mensal"),
    VARIAVEL("Variável mensal"),
    ESPORADICO("Esporádico");

    private final String rotulo;

    Natureza(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
