package br.com.fiap.javaadv.lazyGrader.service;

import br.com.fiap.javaadv.lazyGrader.model.GradeResult;
import br.com.fiap.javaadv.lazyGrader.model.Group;
import br.com.fiap.javaadv.lazyGrader.tool.ProjectAnalysisTools;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GraderService {

    private final ProjectAnalysisTools projectAnalysisTools;
    private final ObjectMapper objectMapper;
    private final ChatClient.Builder chatClientBuilder;

    // ===================================================
    // ENUNCIADO DO PROJETO (edite conforme sua disciplina)
    // ===================================================
    private static final String PROJECT_STATEMENT = """
        ## ENUNCIADO DO PROJETO

        Desenvolver um sistema de gerenciamento de biblioteca utilizando os conceitos de:
        - Programação Orientada a Objetos (herança, polimorfismo, encapsulamento)
        - Persistência de dados (banco de dados relacional ou arquivo)
        - Interface com o usuário (console ou REST API)
        - Tratamento de exceções
        - Padrões de projeto (ao menos um padrão GoF)

        ### Para projetos Java/Maven:
        - Usar Spring Boot (versão mínima 3.x) ou Java puro com Maven
        - Implementar operações CRUD para livros, usuários e empréstimos
        - Incluir pelo menos 5 classes de domínio
        - Testes unitários com JUnit 5 (cobertura mínima de 60%)

        ### Para projetos Python:
        - Usar Python 3.10+
        - Implementar operações CRUD
        - Usar bibliotecas adequadas (SQLAlchemy, FastAPI, Flask ou similar)
        - Testes com pytest (cobertura mínima de 60%)

        ### Requisitos comuns:
        - README.md com instruções de execução
        - Código comentado
        - Tratamento de erros adequado
        """;

    // ===================================================
    // RUBRICA DE AVALIAÇÃO
    // ===================================================
    private static final String RUBRIC = """
        ## RUBRICA DE AVALIAÇÃO (total: 10 pontos)

        ### 1. Funcionalidade (0-3 pontos)
        - 3.0: Todas as funcionalidades requeridas implementadas e funcionando
        - 2.0: A maioria das funcionalidades implementadas com pequenos problemas
        - 1.0: Funcionalidades parcialmente implementadas
        - 0.0: Projeto não executa ou não atende ao enunciado

        ### 2. Qualidade do Código (0-2 pontos)
        - 2.0: Código limpo, bem estruturado, sem duplicação, nomes significativos
        - 1.0: Código razoável com alguns problemas de qualidade
        - 0.0: Código desorganizado, duplicado ou difícil de entender

        ### 3. Arquitetura e Design (0-2 pontos)
        - 2.0: Boa separação de responsabilidades, uso de padrões de projeto, OOP adequado
        - 1.0: Arquitetura básica com alguns problemas de design
        - 0.0: Sem arquitetura clara, código procedural ou monolítico

        ### 4. Documentação (0-2 pontos)
        - 2.0: README completo, código comentado, JavaDoc/docstrings adequados
        - 1.0: Documentação parcial
        - 0.0: Sem documentação

        ### 5. Testes (0-1 ponto)
        - 1.0: Testes unitários com boa cobertura (>60%)
        - 0.5: Testes presentes mas com cobertura insuficiente
        - 0.0: Sem testes
        """;

    public GradeResult gradeProject(Group group) {
        log.info("Iniciando avaliação do grupo {} em: {}", group.getGroupNumber(), group.getLocalRepoPath());

        try {
            ChatClient chatClient = chatClientBuilder
                    .defaultTools(projectAnalysisTools)
                    .build();

            String systemPrompt = buildSystemPrompt();
            String userPrompt = buildUserPrompt(group);

            String response = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .content();

            return parseGradeResult(group.getGroupNumber(), response);

        } catch (Exception e) {
            log.error("Erro ao avaliar grupo {}: {}", group.getGroupNumber(), e.getMessage(), e);
            return GradeResult.builder()
                    .groupNumber(group.getGroupNumber())
                    .evaluated(false)
                    .grade(0.0)
                    .errorMessage("Erro durante avaliação: " + e.getMessage())
                    .feedback("Não foi possível avaliar automaticamente: " + e.getMessage())
                    .build();
        }
    }

    private GradeResult parseGradeResult(int groupNumber, String response) {
    }

    private String buildUserPrompt(Group group) {
    }

    private String buildSystemPrompt() {

    }


}
