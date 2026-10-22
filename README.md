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
