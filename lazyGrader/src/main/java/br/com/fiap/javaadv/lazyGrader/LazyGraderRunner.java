package br.com.fiap.javaadv.lazyGrader;


import br.com.fiap.javaadv.lazyGrader.model.GradeResult;
import br.com.fiap.javaadv.lazyGrader.service.OrchestratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Runner de linha de comando.
 *
 * Uso:
 *   java -jar project-grader.jar --excel=grupos.xlsx --output=resultado.xlsx --grupos=1,2,3
 *
 * Ou via variável de ambiente:
 *   OPENAI_API_KEY=sk-... java -jar project-grader.jar --excel=grupos.xlsx
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LazyGraderRunner implements ApplicationRunner {

    private final OrchestratorService orchestratorService;

    @Value("${grader.excel-file}")
    private String defaultExcelFile;

    @Value("${grader.output-file}")
    private String defaultOutputFile;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Só executa CLI se argumento --excel for passado ou arquivo padrão existe
        boolean hasExcelArg = args.containsOption("excel");
        boolean hasRunArg = args.containsOption("run");

        if (!hasExcelArg && !hasRunArg) {
            log.info("Project Grader iniciado em modo servidor (REST API disponível em :8080/api/grader)");
            log.info("Para executar via CLI: --excel=caminho/grupos.xlsx [--output=resultado.xlsx] [--grupos=1,2,3]");
            return;
        }

        String excelPath = hasExcelArg
                ? args.getOptionValues("excel").get(0)
                : defaultExcelFile;

        String outputPath = args.containsOption("output")
                ? args.getOptionValues("output").get(0)
                : defaultOutputFile;

        List<Integer> grupos = new ArrayList<>();
        if (args.containsOption("grupos")) {
            String gruposStr = args.getOptionValues("grupos").get(0);
            for (String g : gruposStr.split(",")) {
                try { grupos.add(Integer.parseInt(g.trim())); }
                catch (NumberFormatException ignored) {}
            }
        }

        log.info("=== PROJECT GRADER - AVALIADOR AUTOMÁTICO ===");
        log.info("Excel de entrada : {}", excelPath);
        log.info("Excel de saída   : {}", outputPath);
        log.info("Grupos filtrados : {}", grupos.isEmpty() ? "todos" : grupos);

        List<GradeResult> results = orchestratorService.runEvaluation(excelPath, outputPath, grupos);

        log.info("\n✅ Avaliação concluída! {} grupos avaliados.", results.size());
        log.info("Resultados salvos em: {}", outputPath);
    }
}
