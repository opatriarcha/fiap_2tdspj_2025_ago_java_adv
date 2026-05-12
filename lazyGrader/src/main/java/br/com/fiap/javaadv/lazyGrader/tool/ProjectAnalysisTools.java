package br.com.fiap.javaadv.lazyGrader.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ProjectAnalysisTools {

    @Tool(description = "Lê o conteudo de um arquivo no repositorio do aluno. Use para inspecionar o codigo fonte, configuracoes e documentacao")
    public String readFile(  @ToolParam(description= "Caminho absoluto do arquivo a ser lido") String filePath){
        try{
            Path path = Path.of(filePath);
            if( !Files.exists(path))
                return "ERRO: Arquivo não Encontrado";
            if( !Files.isDirectory(path))
                return "ERRO: O Caminho aponta para um diretorio, e deveria ser uma arquivo Excel.";

            long size = Files.size(path);
            if( size > 100_000)
                return "AVISO: Arquivo muito mas muiot GRANDE( " + size + " bytes). Mostrando primeiras 200 linhas: \n" +
                    Files.lines(path).limit(200).collect(Collectors.joining("\n"));
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lista arquivos e diretórios em um caminho.
     */
    @Tool(description = "Lista arquivos e diretórios dentro de um caminho. Use para explorar a estrutura do projeto.")
    public String listDirectory(
            @ToolParam(description = "Caminho absoluto do diretório a ser listado") String dirPath) {
        try {
            Path dir = Path.of(dirPath);
            if (!Files.exists(dir)) return "ERRO: Diretório não encontrado: " + dirPath;
            if (!Files.isDirectory(dir)) return "ERRO: Caminho não é um diretório.";

            StringBuilder sb = new StringBuilder("Conteúdo de: ").append(dirPath).append("\n");
            Files.list(dir)
                    .sorted()
                    .forEach(p -> {
                        String type = Files.isDirectory(p) ? "[DIR] " : "      ";
                        sb.append(type).append(p.getFileName()).append("\n");
                    });
            return sb.toString();
        } catch (IOException e) {
            return "ERRO ao listar diretório: " + e.getMessage();
        }
    }

    /**
     * Verifica existência de arquivo ou diretório.
     */
    @Tool(description = "Verifica se um arquivo ou diretório existe no repositório.")
    public String fileExists(
            @ToolParam(description = "Caminho absoluto a verificar") String path) {
        boolean exists = Files.exists(Path.of(path));
        boolean isDir = exists && Files.isDirectory(Path.of(path));
        return exists
                ? "EXISTS: " + path + (isDir ? " (diretório)" : " (arquivo)")
                : "NOT_FOUND: " + path;
    }

    /**
     * Busca texto em arquivos do repositório (grep simples).
     */
    @Tool(description = "Busca um texto em todos os arquivos do repositório. Retorna os arquivos e linhas que contêm o texto.")
    public String searchInFiles(
            @ToolParam(description = "Caminho raiz do repositório onde buscar") String repoPath,
            @ToolParam(description = "Texto a ser buscado (case-insensitive)") String searchText) {
        try {
            StringBuilder results = new StringBuilder();
            String lowerSearch = searchText.toLowerCase();

            Files.walk(Path.of(repoPath))
                    .filter(Files::isRegularFile)
                    .filter(p -> isSourceFile(p.toString()))
                    .forEach(p -> {
                        try {
                            long lineNum = 0;
                            for (String line : Files.readAllLines(p)) {
                                lineNum++;
                                if (line.toLowerCase().contains(lowerSearch)) {
                                    results.append(p).append(":").append(lineNum)
                                            .append(": ").append(line.trim()).append("\n");
                                }
                            }
                        } catch (IOException ignored) {}
                    });

            String result = results.toString();
            return result.isEmpty() ? "Nenhuma ocorrência encontrada de: " + searchText : result;
        } catch (IOException e) {
            return "ERRO ao buscar: " + e.getMessage();
        }
    }

    /**
     * Conta linhas de código por extensão.
     */
    @Tool(description = "Conta as linhas de código do projeto por tipo de arquivo. Útil para avaliar o tamanho e esforço do projeto.")
    public String countLinesOfCode(
            @ToolParam(description = "Caminho raiz do repositório") String repoPath) {
        try {
            java.util.Map<String, long[]> counts = new java.util.TreeMap<>();

            Files.walk(Path.of(repoPath))
                    .filter(Files::isRegularFile)
                    .filter(p -> isSourceFile(p.toString()))
                    .filter(p -> !p.toString().contains("/.git") && !p.toString().contains("/target/"))
                    .forEach(p -> {
                        String ext = getExtension(p.getFileName().toString());
                        try {
                            long lines = Files.lines(p).count();
                            counts.computeIfAbsent(ext, k -> new long[]{0, 0});
                            counts.get(ext)[0]++;   // arquivos
                            counts.get(ext)[1] += lines; // linhas
                        } catch (IOException ignored) {}
                    });

            StringBuilder sb = new StringBuilder("Linhas de código por tipo:\n");
            long totalLines = 0;
            for (var entry : counts.entrySet()) {
                sb.append(String.format("  .%-10s %3d arquivo(s)  %5d linhas\n",
                        entry.getKey(), entry.getValue()[0], entry.getValue()[1]));
                totalLines += entry.getValue()[1];
            }
            sb.append(String.format("  TOTAL: %d linhas\n", totalLines));
            return sb.toString();
        } catch (IOException e) {
            return "ERRO ao contar linhas: " + e.getMessage();
        }
    }

    private boolean isSourceFile(String path) {
        return path.endsWith(".java") || path.endsWith(".py") || path.endsWith(".xml")
                || path.endsWith(".yml") || path.endsWith(".yaml") || path.endsWith(".properties")
                || path.endsWith(".sql") || path.endsWith(".md") || path.endsWith(".txt")
                || path.endsWith(".json") || path.endsWith(".html") || path.endsWith(".js");
    }

    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot + 1) : "sem extensão";
    }
}

