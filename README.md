# Fatec ADS

## Perfis de acesso

| Perfil       | Permissoes |
|--------------|------------|
| `ROLE_ADMIN` | Acesso total (alunos, cursos, professores, disciplinas, produtos, pedidos, H2 console). |
| `ROLE_USER`  | Ver produtos, usar o carrinho e ver o proprio historico (`/produtos`, `/carrinho`, `/pedidos`). |

- O cadastro publico (`/usuarios/novo`) sempre cria contas `ROLE_USER`.
- Carrinho: adicione itens direto na lista de produtos. O carrinho fica salvo no banco por usuario
  (sobrevive a logout) e o cabecalho mostra a quantidade de itens.
- Finalizar compra transforma o carrinho em um pedido no nome do usuario logado (precos congelados) e esvazia o carrinho.
  Vincular um aluno e opcional e so o admin pode.
- Historico (`/pedidos`): o usuario ve as proprias compras; o admin ve os pedidos de todos.
- Um admin e criado automaticamente na inicializacao. Credenciais padrao `admin` / `admin123`,
  configuraveis por `ADMIN_LOGIN`, `ADMIN_EMAIL` e `ADMIN_PASSWORD`.

## Tema 1 — Teste unitário do cálculo de desconto

**Autor:** Felipe Bianchini · **Disciplina:** Integração e Entrega Contínua (DevOps)

Um teste unitário executa uma parte do código de forma isolada e compara o resultado com o valor esperado. Aqui ele protege a regra que define quanto o cliente paga por um produto e detecta regressões de percentual e arredondamento.

O administrador pode cadastrar um desconto opcional de 0% a 100%, com até duas casas decimais. `Produto.getValorFinal()` calcula `valor × (1 − percentual / 100)` com `BigDecimal` e arredonda o preço unitário para dois decimais com `HALF_UP`. Um campo em branco, inclusive em produtos cadastrados antes da alteração, equivale a 0%. O catálogo e o carrinho usam o preço final, e o pedido grava o preço unitário vigente na compra.

### Como reproduzir

Com JDK 17, a partir da raiz do repositório:

```sh
cd FatecAds/demo
./mvnw -Pjunit5-desconto test
./mvnw clean verify
```

O perfil `junit5-desconto` usa JUnit Jupiter 5.14.4 nas duas classes unitárias da atividade. A execução padrão mantém o JUnit 6 do Spring Boot 4 para os testes de integração.

- `ProdutoDescontoTest` cria produtos reais em memória. `@ParameterizedTest` e `@CsvSource` executam a mesma asserção para cada linha, sem iniciar o Spring ou conectar ao banco.
- `PedidoServiceTest` usa Mockito para simular as dependências e capturar o pedido enviado ao repositório. No cenário com 50% de desconto, três unidades de R$ 19,99 resultam em três preços unitários de R$ 10,00 e um total de R$ 30,00. Os valores do pedido persistem mesmo quando o percentual do produto muda.

### Evidências de 9 de novembro de 2026

| Preço original | Desconto | Preço esperado | Preço obtido |
| --- | --- | --- | --- |
| R$ 100,00 | Em branco | R$ 100,00 | R$ 100,00 |
| R$ 100,00 | 0% | R$ 100,00 | R$ 100,00 |
| R$ 100,00 | 10% | R$ 90,00 | R$ 90,00 |
| R$ 100,00 | 25% | R$ 75,00 | R$ 75,00 |
| R$ 100,00 | 100% | R$ 0,00 | R$ 0,00 |
| R$ 19,90 | 12,5% | R$ 17,41 | R$ 17,41 |
| R$ 19,99 | 50% | R$ 10,00 | R$ 10,00 |

O perfil JUnit 5 passou com **12 testes**, incluindo as sete linhas da tabela. A verificação completa passou com **42 testes**. Em uma cópia isolada, trocar `HALF_UP` por `DOWN` causou a falha esperada: o cenário de R$ 19,99 a 50% esperava R$ 10,00 e recebeu R$ 9,99.

Para a demonstração no navegador, inicie a aplicação local com `./mvnw spring-boot:run`. Cadastre um produto de R$ 100,00 com 25% de desconto e adicione duas unidades ao carrinho. Na execução observada, o carrinho exibiu R$ 75,00 por unidade e R$ 150,00 de subtotal. Após finalizar a compra e retirar o desconto do produto, o catálogo voltou a R$ 100,00 e o histórico preservou o preço da compra. O servidor também rejeitou uma tentativa de cadastro com 101%.

**Referências:** [JUnit 5](https://docs.junit.org/5.14.4/writing-tests/parameterized-classes-and-tests.html), [Mockito](https://site.mockito.org/) e [Java 17: BigDecimal](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/math/BigDecimal.html).
