package br.com.controlefinanceiro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

/** Uma entrada ou saída em uma data específica, pertencente a um mês (competência). */
@Entity
public class Lancamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe a descrição")
    private String descricao;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoMovimento tipo = TipoMovimento.SAIDA;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Natureza natureza = Natureza.ESPORADICO;

    /** Nulo enquanto uma conta variável ainda não teve o valor informado. */
    @PositiveOrZero(message = "O valor não pode ser negativo")
    @Column(precision = 15, scale = 2)
    private BigDecimal valor;

    @NotNull(message = "Informe a data")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate data = LocalDate.now();

    /** Primeiro dia do mês ao qual o lançamento pertence. */
    private LocalDate competencia;

    /** Pago (saída) ou recebido (entrada). */
    private boolean efetivado;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataEfetivacao;

    private String observacao;

    @ManyToOne(fetch = FetchType.LAZY)
    private ModeloMensal modelo;

    @PrePersist
    @PreUpdate
    void ajustarCompetencia() {
        if (data != null) {
            competencia = data.withDayOfMonth(1);
        }
    }

    public boolean isPendenteDeValor() {
        return valor == null;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public TipoMovimento getTipo() { return tipo; }
    public void setTipo(TipoMovimento tipo) { this.tipo = tipo; }
    public Natureza getNatureza() { return natureza; }
    public void setNatureza(Natureza natureza) { this.natureza = natureza; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public LocalDate getCompetencia() { return competencia; }
    public void setCompetencia(LocalDate competencia) { this.competencia = competencia; }
    public boolean isEfetivado() { return efetivado; }
    public void setEfetivado(boolean efetivado) { this.efetivado = efetivado; }
    public LocalDate getDataEfetivacao() { return dataEfetivacao; }
    public void setDataEfetivacao(LocalDate dataEfetivacao) { this.dataEfetivacao = dataEfetivacao; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public ModeloMensal getModelo() { return modelo; }
    public void setModelo(ModeloMensal modelo) { this.modelo = modelo; }
}
