package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.dto.ProdutoForm;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.exception.ProdutoEmUsoException;
import br.com.fatecads.fatecads.exception.ProdutoNaoEncontradoException;
import br.com.fatecads.fatecads.repository.ItemPedidoRepository;
import br.com.fatecads.fatecads.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    public ProdutoService(ProdutoRepository produtoRepository, ItemPedidoRepository itemPedidoRepository) {
        this.produtoRepository = produtoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public Produto buscarObrigatorio(Integer idProduto) {
        return produtoRepository.findById(idProduto)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(idProduto));
    }

    public ProdutoForm buscarFormulario(Integer idProduto) {
        return ProdutoForm.from(buscarObrigatorio(idProduto));
    }

    @Transactional
    public Produto salvar(ProdutoForm form) {
        Produto produto = form.getIdProduto() == null
                ? new Produto()
                : buscarObrigatorio(form.getIdProduto());

        produto.setDescricao(form.getDescricao().trim());
        produto.setValor(form.getValor());
        produto.setUnidade(form.getUnidade().trim());
        produto.setMarca(form.getMarca().trim());
        return produtoRepository.save(produto);
    }

    @Transactional
    public void excluirPorId(Integer idProduto) {
        buscarObrigatorio(idProduto);
        if (itemPedidoRepository.existsByProdutoIdProduto(idProduto)) {
            throw new ProdutoEmUsoException(idProduto);
        }
        produtoRepository.deleteById(idProduto);
    }
}
