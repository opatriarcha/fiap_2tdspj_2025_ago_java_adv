package com.example.procedures.service;

import com.example.procedures.domain.Produto;
import com.example.procedures.repository.ProdutoRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import javax.sql.DataSource;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    private final DataSource dataSource;
    private SimpleJdbcCall simpleJdbcCall;

    @PersistenceContext
    private final EntityManager em;

    private final JdbcTemplate jdbcTemplate;

    public void inserirComJdbcTemplate(String nome, Double preco) {
        String call = "{call inserir_produto(?, ?)}";
        jdbcTemplate.update(call, nome, preco);
    }



    public void inserirComEntityManager(String nome, Double preco) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("inserir_produto");

        query.registerStoredProcedureParameter("p_nome", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_preco", Double.class, ParameterMode.IN);

        query.setParameter("p_nome", nome);
        query.setParameter("p_preco", preco);

        query.execute();
    }


    public void inserirProdutosProcedure(List<Produto> produtos){
        for(Produto produto: produtos){
            this.produtoRepository.inserirProduto(produto.getNome(), produto.getPreco());
        }
    }

    @PostConstruct
    void init(){
        this.simpleJdbcCall = new SimpleJdbcCall(this.dataSource)
                .withProcedureName("INSERIR_PRODUTO");
    }

    public void inserirComSimpleJDBC(List<Produto> produtos){
        for(Produto produto: produtos){
            this.simpleJdbcCall.execute(new MapSqlParameterSource()
                    .addValue("p_nome", produto.getNome())
                    .addValue("p_preco", produto.getPreco())
            );
        }
    }



}
