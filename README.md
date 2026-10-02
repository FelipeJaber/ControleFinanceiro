# Controle Financeiro

Gestão financeira pessoal local (Spring Boot + Thymeleaf + H2 em arquivo). Sem autenticação; escuta só em `127.0.0.1`.

## Executar

```
mvn spring-boot:run
```

Abra http://localhost:8081. Requer Java 21 e Maven. Os dados ficam em `./data/financeiro.mv.db` (ignorado pelo git; faça backup copiando o arquivo).

## Conceitos

| Conceito | Como usar |
|---|---|
| **Fixas mensais** (aluguel, salário) | Cadastre em *Contas mensais* com valor e dia. |
| **Variáveis mensais** (luz, água) | Cadastre em *Contas mensais* como Variável. Ao gerar o mês nascem sem valor; informe o valor na tela do mês quando a conta chegar. |
| **Gerar mês** | Botão na tela do mês: cria os lançamentos dos modelos ativos que ainda não existem (idempotente; dia 31 vira o último dia do mês). |
| **Esporádicos** | Botões *+ Entrada* / *+ Saída* na tela do mês. |
| **Dívidas** | Valor em aberto de períodos anteriores; registre pagamentos parciais. Cada pagamento entra como saída no mês em que foi feito. |

Cada lançamento pode ser marcado como pago/recebido. O resumo mostra o **previsto** (tudo) e o **realizado** (só o já pago/recebido). Contas variáveis sem valor não entram nos totais e aparecem como pendência.

## Testes

```
mvn test
```
