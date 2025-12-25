package dev.wilsonblanco.reportgenerator.batch;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;

@Component("csvStreamWriter") // Nombre del Bean importante para diferenciarlo
@StepScope
public class CsvStreamWriter implements ItemWriter<Map<String, Object>>, StepExecutionListener {

    @Value("#{jobParameters['outputFilePath']}")
    private String outputFilePath;

    private BufferedWriter writer;
    private boolean headersCreated = false;
    private final String DELIMITER = ";"; // Puedes cambiar a "," si prefieres

    @Override
    public void beforeStep(StepExecution stepExecution) {
        try {
            FileOutputStream fos = new FileOutputStream(outputFilePath);
            fos.write(0xef);
            fos.write(0xbb);
            fos.write(0xbf);

            writer = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Error creando archivo CSV", e);
        }
    }

    @Override
    public void write(Chunk<? extends Map<String, Object>> chunk) throws Exception {
        for (Map<String, Object> rowData : chunk) {

            // 1. Escribir Cabeceras (solo la primera vez)
            if (!headersCreated) {
                writeLine(rowData.keySet());
                headersCreated = true;
            }

            // 2. Escribir Valores
            writeLine(rowData.values());
        }
        writer.flush(); // Asegurar que se escriba en disco
    }

    private void writeLine(Iterable<?> values) throws IOException {
        StringJoiner joiner = new StringJoiner(DELIMITER);
        for (Object value : values) {
            String str = value != null ? value.toString() : "";
            // Escapar comillas si el texto ya trae comillas o delimitadores
            if (str.contains(DELIMITER) || str.contains("\"") || str.contains("\n")) {
                str = "\"" + str.replace("\"", "\"\"") + "\"";
            }
            joiner.add(str);
        }
        writer.write(joiner.toString());
        writer.newLine();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        try {
            if (writer != null) writer.close();
            return ExitStatus.COMPLETED;
        } catch (IOException e) {
            return ExitStatus.FAILED;
        }
    }
}