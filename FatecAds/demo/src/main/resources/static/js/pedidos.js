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
            adicionarCelula(row, String(item.quantidade));
            adicionarCelula(row, moeda.format(item.valor));
            adicionarCelula(row, moeda.format(subtotal));

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
    }

    async function salvarPedido(event) {
        event.preventDefault();
        const alunoId = Number(alunoSelect.value);

        if (!alunoId || itens.size === 0) {
            mostrarFeedback("Selecione um aluno e adicione pelo menos um produto.", true);
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
