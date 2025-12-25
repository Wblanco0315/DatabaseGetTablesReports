package dev.wilsonblanco.reportgenerator.batch.read;

import dev.wilsonblanco.reportgenerator.services.DataBaseConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.ColumnMapRowMapper;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DynamicJdbcReader {
    private final DataBaseConnectionService connectionService;

    @Bean
    @StepScope
    public JdbcCursorItemReader<Map<String, Object>> databaseReader(
            @Value("#{jobParameters['connectionUuid']}") String connectionUuid,
            @Value("#{jobParameters['sqlQuery']}") String sqlQuery
    ) throws Exception {

        // 1. Obtenemos el DataSource REAL usando tu servicio
        DataSource dataSource = connectionService.getDataSource(connectionUuid);

        // 2. Retornamos el Reader configurado
        return new JdbcCursorItemReaderBuilder<Map<String, Object>>()
                .name("dynamicReader")
                .dataSource(dataSource)
                .sql(sqlQuery)
                .rowMapper(new ColumnMapRowMapper()) // Convierte cada fila en un Map<Columna, Valor>
                .verifyCursorPosition(false) // Optimización para evitar errores en algunos drivers
                .build();
    }
}
