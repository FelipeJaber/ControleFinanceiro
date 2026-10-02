package br.com.controlefinanceiro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class PagamentoDivida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Divida divida;

    @NotNull(message = "Informe a data")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate data = LocalDate.now();

    @NotNull(message = "Informe o valor")
    @Positive(message = "O valor deve ser maior que zero")
    @Column(precision = 15, scale = 2)
    private BigDecimal valor;

    private String observacao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Divida getDivida() { return divida; }
    public void setDivida(Divida divida) { this.divida = divida; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
