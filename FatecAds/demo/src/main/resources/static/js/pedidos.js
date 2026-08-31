(() => {
    const form = document.getElementById("pedido-form");
    if (!form) {
        return;
    }

    const alunoSelect = document.getElementById("alunoId");
    const produtoSelect = document.getElementById("produtoId");
    const quantidadeInput = document.getElementById("quantidade");
    const itensBody = document.getElementById("itens-pedido");
    const totalElement = document.getElementById("total-pedido");
    const feedback = document.getElementById("pedido-feedback");
    const salvarButton = document.getElementById("salvar-pedido");
    const itens = new Map();
    const moeda = new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" });

    document.getElementById("adicionar-item").addEventListener("click", adicionarItem);
    form.addEventListener("submit", salvarPedido);

    function adicionarItem() {
        const option = produtoSelect.selectedOptions[0];
        const produtoId = Number(produtoSelect.value);
        const quantidade = Number(quantidadeInput.value);

        if (!produtoId || !Number.isInteger(quantidade) || quantidade <= 0) {
            mostrarFeedback("Selecione um produto e informe uma quantidade valida.", true);
            return;
        }

        const existente = itens.get(produtoId);
        itens.set(produtoId, {
            produtoId,
            descricao: option.dataset.descricao,
            valor: Number(option.dataset.valor),
            quantidade: existente ? existente.quantidade + quantidade : quantidade
        });

        produtoSelect.value = "";
        quantidadeInput.value = "1";
        esconderFeedback();
        renderizarItens();
    }

    function removerItem(produtoId) {
        itens.delete(produtoId);
        renderizarItens();
    }

    function renderizarItens() {
        itensBody.replaceChildren();

        if (itens.size === 0) {
            const row = document.createElement("tr");
            const cell = document.createElement("td");
            cell.colSpan = 5;
            cell.className = "empty-state";
            cell.textContent = "Adicione pelo menos um produto.";
            row.appendChild(cell);
            itensBody.appendChild(row);
            totalElement.textContent = moeda.format(0);
            return;
        }

        let total = 0;
        itens.forEach((item) => {
            const subtotal = item.valor * item.quantidade;
            total += subtotal;

            const row = document.createElement("tr");
            adicionarCelula(row, item.descricao);
            const quantidadeCell = document.createElement("td");
            const quantidade = document.createElement("input");
            quantidade.type = "number";
            quantidade.min = "1";
            quantidade.step = "1";
            quantidade.value = String(item.quantidade);
            quantidade.className = "quantidade-pedido";
            quantidade.setAttribute("aria-label", `Quantidade de ${item.descricao}`);
            quantidadeCell.appendChild(quantidade);
            row.appendChild(quantidadeCell);
            adicionarCelula(row, moeda.format(item.valor));
            const subtotalCell = adicionarCelula(row, moeda.format(subtotal));

            quantidade.addEventListener("input", () => {
                atualizarQuantidade(item, quantidade, subtotalCell);
            });

            const actionCell = document.createElement("td");
            const button = document.createElement("button");
            button.type = "button";
            button.className = "btn btn-danger";
            button.textContent = "Remover";
            button.addEventListener("click", () => removerItem(item.produtoId));
            actionCell.appendChild(button);
            row.appendChild(actionCell);
            itensBody.appendChild(row);
        });

        totalElement.textContent = moeda.format(total);
    }

    function adicionarCelula(row, value) {
        const cell = document.createElement("td");
        cell.textContent = value;
        row.appendChild(cell);
        return cell;
    }

    function atualizarQuantidade(item, input, subtotalCell) {
        const quantidade = Number(input.value);
        const quantidadeValida = Number.isInteger(quantidade) && quantidade > 0;

        item.quantidade = quantidadeValida ? quantidade : 0;
        input.setAttribute("aria-invalid", String(!quantidadeValida));
        subtotalCell.textContent = moeda.format(item.valor * item.quantidade);
        atualizarTotal();
    }

    function atualizarTotal() {
        const total = Array.from(itens.values()).reduce(
            (soma, item) => soma + item.valor * item.quantidade,
            0
        );
        totalElement.textContent = moeda.format(total);
    }

    async function salvarPedido(event) {
        event.preventDefault();
        const alunoId = Number(alunoSelect.value);

        const quantidadeInvalida = Array.from(itens.values()).some(
            (item) => !Number.isInteger(item.quantidade) || item.quantidade <= 0
        );

        if (!alunoId || itens.size === 0 || quantidadeInvalida) {
            mostrarFeedback("Selecione um aluno, adicione produtos e informe quantidades validas.", true);
            return;
        }

        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
        const request = {
            alunoId,
            itens: Array.from(itens.values()).map((item) => ({
                produtoId: item.produtoId,
                quantidade: item.quantidade
            }))
        };

        salvarButton.disabled = true;
        esconderFeedback();

        try {
            const response = await fetch("/pedidos", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify(request)
            });
            const body = await response.json();

            if (!response.ok) {
                throw new Error(body.mensagem || "Nao foi possivel salvar o pedido.");
            }

            itens.clear();
            form.reset();
            renderizarItens();
            mostrarFeedback(`Pedido #${body.idPedido} salvo com total de ${moeda.format(body.total)}.`, false);
        } catch (error) {
            mostrarFeedback(error.message, true);
        } finally {
            salvarButton.disabled = false;
        }
    }

    function mostrarFeedback(mensagem, erro) {
        feedback.textContent = mensagem;
        feedback.className = erro ? "alert alert-error" : "alert alert-success";
        feedback.hidden = false;
    }

    function esconderFeedback() {
        feedback.hidden = true;
        feedback.textContent = "";
    }
})();
