package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.dto.ProdutoForm;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.exception.ImagemProdutoInvalidaException;
import br.com.fatecads.fatecads.exception.ProdutoEmUsoException;
import br.com.fatecads.fatecads.exception.ProdutoNaoEncontradoException;
import br.com.fatecads.fatecads.repository.ItemPedidoRepository;
import br.com.fatecads.fatecads.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ProdutoService {

    private static final long TAMANHO_MAXIMO_IMAGEM = 3 * 1024 * 1024;
    private static final Set<String> TIPOS_DE_IMAGEM_PERMITIDOS = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

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

    @Transactional
    public Produto salvar(ProdutoForm form, MultipartFile imagemArquivo) {
        Produto produto = form.getIdProduto() == null
                ? new Produto()
                : buscarObrigatorio(form.getIdProduto());

        produto.setDescricao(form.getDescricao().trim());
        produto.setValor(form.getValor());
        produto.setUnidade(form.getUnidade().trim());
        produto.setMarca(form.getMarca().trim());
        salvarImagemSeInformada(produto, imagemArquivo);
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

    private void salvarImagemSeInformada(Produto produto, MultipartFile imagemArquivo) {
        if (imagemArquivo == null || imagemArquivo.isEmpty()) {
            return;
        }

        String tipo = imagemArquivo.getContentType() == null
                ? ""
                : imagemArquivo.getContentType().toLowerCase(Locale.ROOT);

        if (!TIPOS_DE_IMAGEM_PERMITIDOS.contains(tipo)) {
            throw new ImagemProdutoInvalidaException("Envie uma imagem JPEG, PNG ou WebP");
        }
        if (imagemArquivo.getSize() > TAMANHO_MAXIMO_IMAGEM) {
            throw new ImagemProdutoInvalidaException("A imagem deve ter no maximo 3 MB");
        }

        try {
            produto.setImagem(imagemArquivo.getBytes());
            produto.setImagemTipo(tipo);
        } catch (IOException exception) {
            throw new ImagemProdutoInvalidaException("Nao foi possivel salvar a imagem", exception);
        }
    }
}
