package br.com.controlefinanceiro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

/** Valor em aberto de períodos anteriores, quitado aos poucos por pagamentos. */
@Entity
public class Divida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe a descrição")
    private String descricao;

    private String credor;

    @NotNull(message = "Informe o valor")
    @Positive(message = "O valor deve ser maior que zero")
    @Column(precision = 15, scale = 2)
    private BigDecimal valorOriginal;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataOrigem;

    private String observacao;

    @OneToMany(mappedBy = "divida", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("data DESC, id DESC")
    private List<PagamentoDivida> pagamentos = new ArrayList<>();

    public BigDecimal getTotalPago() {
        return pagamentos.stream().map(PagamentoDivida::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getSaldo() {
        return valorOriginal.subtract(getTotalPago());
    }

    public boolean isQuitada() {
        return getSaldo().signum() <= 0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getCredor() { return credor; }
    public void setCredor(String credor) { this.credor = credor; }
    public BigDecimal getValorOriginal() { return valorOriginal; }
    public void setValorOriginal(BigDecimal valorOriginal) { this.valorOriginal = valorOriginal; }
    public LocalDate getDataOrigem() { return dataOrigem; }
    public void setDataOrigem(LocalDate dataOrigem) { this.dataOrigem = dataOrigem; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public List<PagamentoDivida> getPagamentos() { return pagamentos; }
    public void setPagamentos(List<PagamentoDivida> pagamentos) { this.pagamentos = pagamentos; }
}
