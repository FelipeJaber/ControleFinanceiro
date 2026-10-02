package br.com.controlefinanceiro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/** Modelo de conta/entrada que se repete todo mês (fixa ou variável). */
@Entity
public class ModeloMensal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe a descrição")
    private String descricao;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoMovimento tipo = TipoMovimento.SAIDA;

    /** FIXO (valor conhecido) ou VARIAVEL (valor informado a cada mês, ex.: luz). */
    @NotNull
    @Enumerated(EnumType.STRING)
    private Natureza natureza = Natureza.FIXO;

    /** Valor padrão (fixo) ou estimativa (variável). Opcional para variáveis. */
    @PositiveOrZero(message = "O valor não pode ser negativo")
    @Column(precision = 15, scale = 2)
    private BigDecimal valor;

    @NotNull
    @Min(value = 1, message = "Dia entre 1 e 31")
    @Max(value = 31, message = "Dia entre 1 e 31")
    private Integer diaVencimento = 1;

    private boolean ativo = true;

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
    public Integer getDiaVencimento() { return diaVencimento; }
    public void setDiaVencimento(Integer diaVencimento) { this.diaVencimento = diaVencimento; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
