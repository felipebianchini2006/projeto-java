package br.com.fatecads.fatecads.entity;

import jakarta.persistence.Entity;

@Entity
public class Aluno {
    private Integer id;
    private String nome;    
    private String email;
    private String telefone;
    private String endereco;
    private String cpf;
    private String ra;
    
}
