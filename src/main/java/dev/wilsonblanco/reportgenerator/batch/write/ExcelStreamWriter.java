package dev.wilsonblanco.reportgenerator.batch.write;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@Component
@StepScope
public class ExcelStreamWriter implements ItemWriter<Map<String, Object>>, StepExecutionListener {

    @Value("#{jobParameters['outputFilePath']}")
    private String outputFilePath;

    private SXSSFWorkbook workbook;
    private Sheet sheet;
    private int currentRowIndex = 0;
    private boolean headersCreated = false;

    // --- 1. Al iniciar el paso (Crear archivo) ---
    @Override
    public void beforeStep(StepExecution stepExecution) {
        // Mantiene solo 100 filas en RAM, el resto a disco temporal
        workbook = new SXSSFWorkbook(100);
        sheet = workbook.createSheet("Datos Exportados");
    }

    // --- 2. Escribir por lotes ---
    @Override
    public void write(Chunk<? extends Map<String, Object>> chunk) throws Exception {
        for (Map<String, Object> rowData : chunk) {

            // A. Crear Cabeceras (Solo con la primera fila del primer lote)
            if (!headersCreated) {
                createHeaders(rowData);
                headersCreated = true;
            }

            // B. Crear fila de datos
            Row row = sheet.createRow(currentRowIndex++);
            int cellIndex = 0;
            for (Object value : rowData.values()) {
                Cell cell = row.createCell(cellIndex++);
                if (value != null) {
                    cell.setCellValue(value.toString());
                }
            }
        }
    }

    // --- 3. Al finalizar (Guardar y Cerrar) ---
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        try (FileOutputStream out = new FileOutputStream(outputFilePath)) {
            workbook.write(out);
            workbook.dispose(); // Borra los archivos temporales de SXSSF
            return ExitStatus.COMPLETED;
        } catch (IOException e) {
            e.printStackTrace();
            return ExitStatus.FAILED;
        }
    }


    private void createHeaders(Map<String, Object> sampleRow) {
        Row headerRow = sheet.createRow(currentRowIndex++);
        int cellIndex = 0;
        for (String colName : sampleRow.keySet()) {
            Cell cell = headerRow.createCell(cellIndex++);
            cell.setCellValue(colName);
            // Podrías poner negritas aquí
        }
    }

}
