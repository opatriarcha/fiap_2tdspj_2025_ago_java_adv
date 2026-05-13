package br.com.fiap.javaadv.lazyGrader.service;

import br.com.fiap.javaadv.lazyGrader.model.GradeResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrchestratorService {

    private final ExcelReaderService excelReaderService;
    private final ExcelWriterService excelWriterService;
    private final RepositoryService repositoryService;
    private final GraderService gradingService;

    public List<GradeResult> runEvaluation(String excelPath, String outputPath, List<Integer> grupos) {
        return List.of( new GradeResult()); // TROCASAPORRA AMANHA
    }
}
