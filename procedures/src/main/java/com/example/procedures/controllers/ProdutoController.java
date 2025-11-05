package com.example.procedures.controllers;

import com.example.procedures.domain.Produto;
import com.example.procedures.service.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;


    @GetMapping
    public String inserirProduto(@RequestParam String name, @RequestParam Double preco) {
        Produto produto = new Produto();
        produto.setNome(name);
        produto.setPreco(preco);
        this.produtoService.inserirProdutosProcedure(List.of(produto));
        return "Produto inserido com sucesso!";
    }

    @GetMapping("/inserirLote")
    public String inserirProdutos() {
        List<Produto> produtos = new LinkedList<>();
       for( int i=0; i <= 10000; i++){
           produtos.add(new Produto(null, "FUCK PRODUTO" + i, (double) (i * 100)));
       }
        this.produtoService.inserirProdutosProcedure(produtos);
        return "Produtos inserido com sucesso!";
    }

    @GetMapping("/inserirLoteSimpleJdbc")
    public String inserirProdutosSimpleJDBC() {
        List<Produto> produtos = new LinkedList<>();
        for( int i=0; i <= 100; i++){
            produtos.add(new Produto(null, "FUCK PRODUTO" + i, (double) (i * 100)));
        }
        this.produtoService.inserirComSimpleJDBC(produtos);
        return "Produtos inserido via simple JDBC com sucesso!";
    }

    @GetMapping("/EntityManager")
    public String inserirProdutosEntityManager() {
        List<Produto> produtos = new LinkedList<>();
        for( int i=0; i <= 10000; i++){
            this.produtoService.inserirComEntityManager("FUCK PRODUTO" + i, (double) (i * 100));
        }

        return "Produtos inserido com sucesso!";
    }
}
;