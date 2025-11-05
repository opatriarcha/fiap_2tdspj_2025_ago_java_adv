package com.example.procedures.repository;

import com.example.procedures.domain.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Procedure(procedureName = "INSERIR_PRODUTO")
    void inserirProduto(String p_nome, Double p_preco);
}
