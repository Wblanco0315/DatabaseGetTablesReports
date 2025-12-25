package dev.wilsonblanco.reportgenerator.services;

import dev.wilsonblanco.reportgenerator.dto.responses.GlobalResponse;
import dev.wilsonblanco.reportgenerator.models.ConnectionCredentialsEntity;
import dev.wilsonblanco.reportgenerator.repositories.ConnectionCredentialsRepository;
import dev.wilsonblanco.reportgenerator.utils.EncryptorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class DataBaseConnectionService {

    private final ConnectionCredentialsRepository repository;

    public DataSource getDataSource(String connectionUuid) throws Exception {
        // 1. Buscar credenciales en H2
        ConnectionCredentialsEntity entity = repository.getByUuid(connectionUuid)
                .orElseThrow(() -> new RuntimeException("No se encontró la conexión con ID: " + connectionUuid));

        // 2. Desencriptar la contraseña real
        String realPassword = EncryptorUtils.decrypt(entity.getPassword());

        // 3. Construir la URL JDBC adecuada
        String url = buildJdbcUrl(entity);

        System.out.println("Conectando a la base de datos con URL: " + url);

        // 4. Crear el DataSource al vuelo
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(resolveDriverClass(entity.getDbType()));
        dataSource.setUrl(url);
        dataSource.setUsername(entity.getUsername());
        dataSource.setPassword(realPassword);

        // Propiedades extra para evitar bloqueos
        Properties props = new Properties();
        // Timeout de conexión (vital si la IP no existe para no congelar la app)
        props.setProperty("loginTimeout", "10");
        dataSource.setConnectionProperties(props);

        return dataSource;
    }

    public ResponseEntity<List<String>> listTables(String connectionUuid) throws Exception {

        DataSource dataSource = getDataSource(connectionUuid);

        try (Connection connection = dataSource.getConnection()) {

            List<String> tables = new ArrayList<>();

            ResultSet rs = connection.getMetaData().getTables(null, null, "%", new String[]{"TABLE"});

            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }

            return ResponseEntity.ok(tables);
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar las tablas: " + e.getMessage(), e);
        }

    }

    private String buildJdbcUrl(ConnectionCredentialsEntity entity) {
        if ("postgres".equalsIgnoreCase(entity.getDbType())) {
            // jdbc:postgresql://localhost:5432/mi_base
            return String.format("jdbc:postgresql://%s:%s/%s",
                    entity.getHost(), entity.getPort(), entity.getDbName());
        } else if ("sqlserver".equalsIgnoreCase(entity.getDbType())) {
            // jdbc:sqlserver://localhost:1433;databaseName=mi_base;encrypt=true;trustServerCertificate=true;
            return String.format("jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=true;trustServerCertificate=true;",
                    entity.getHost(), entity.getPort(), entity.getDbName());
        }
        throw new IllegalArgumentException("Tipo de base de datos no soportado: " + entity.getDbType());
    }

    private String resolveDriverClass(String dbType) {
        if ("postgres".equalsIgnoreCase(dbType)) return "org.postgresql.Driver";
        if ("sqlserver".equalsIgnoreCase(dbType)) return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        return "";
    }

    public ResponseEntity<GlobalResponse> getTableColumns(String connectionUuid, String tableName) throws Exception {
        DataSource dataSource = getDataSource(connectionUuid);

        try (Connection connection = dataSource.getConnection()) {

            List<String> columns = new ArrayList<>();

            ResultSet rs = connection.getMetaData().getColumns(null, null, tableName, null);

            while (rs.next()) {
                columns.add(rs.getString("COLUMN_NAME"));
            }

            return ResponseEntity.ok(
                    GlobalResponse.success("Columnas obtenidas", columns)
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar las columnas: " + e.getMessage(), e);
        }
    }
}
