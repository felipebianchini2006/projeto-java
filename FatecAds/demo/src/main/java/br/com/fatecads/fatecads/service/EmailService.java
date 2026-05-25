package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.entity.Usuario;

public interface EmailService {

    boolean enviarRecuperacaoSenha(Usuario usuario, String linkRecuperacao);
}
