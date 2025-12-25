package dev.wilsonblanco.reportgenerator.services;

import dev.wilsonblanco.reportgenerator.ReportgeneratorApplication;
import dev.wilsonblanco.reportgenerator.dto.ConnectionCredentialsDto;
import dev.wilsonblanco.reportgenerator.dto.responses.GlobalResponse;
import dev.wilsonblanco.reportgenerator.exceptions.DbConnectionException;
import dev.wilsonblanco.reportgenerator.models.ConnectionCredentialsEntity;
import dev.wilsonblanco.reportgenerator.repositories.ConnectionCredentialsRepository;
import dev.wilsonblanco.reportgenerator.utils.EncryptorUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConnectionCredentialsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportgeneratorApplication.class);

    @Autowired
    private final ConnectionCredentialsRepository repo;

    @Transactional
    public ResponseEntity<GlobalResponse> createConnection(ConnectionCredentialsDto request) {

        try {

            LOGGER.info("Creating new DB connection with alias: {}", request.alias());

            ConnectionCredentialsEntity connectionEntity = ConnectionCredentialsEntity.builder().alias(request.alias()).dbType(request.dbType()).host(request.host()).port(request.port()).dbName(request.dbName()).username(request.username()).password(EncryptorUtils.encrypt(request.password())).build();

            repo.save(connectionEntity);
            LOGGER.info("Database connection with alias {} successfully has been created", request.alias());
        } catch (Exception e) {
            LOGGER.error("Error creating DB connection with alias: {}", request.alias(), e);
            throw new DbConnectionException("Failed to save database connection credentials");
        }

        return ResponseEntity.ok(GlobalResponse.success("Database connection created successfully"));
    }

    public ResponseEntity<GlobalResponse> getConnection(String connectionUuid) {

        try {
            LOGGER.info("Retrieving DB connection with uuid: {}", connectionUuid);
            Optional<ConnectionCredentialsEntity> conection = repo.getByUuid(connectionUuid);

            if (conection.isEmpty()) {
                LOGGER.error("DB connection with uuid: {} not found", connectionUuid);
                throw new DbConnectionException("Database connection not found");
            }

            ConnectionCredentialsEntity connectionEntity = conection.get();
            LOGGER.info("Database connection with uuid {} successfully retrieved", connectionUuid);

            ConnectionCredentialsDto connectionDto = ConnectionCredentialsDto.builder().uuid(connectionEntity.getPublicId().toString()).alias(connectionEntity.getAlias()).dbType(connectionEntity.getDbType()).host(connectionEntity.getHost()).port(connectionEntity.getPort()).dbName(connectionEntity.getDbName()).username(connectionEntity.getUsername()).password(EncryptorUtils.decrypt(connectionEntity.getPassword())).build();

            return ResponseEntity.ok(GlobalResponse.success("Database connection retrieved successfully", connectionDto));

        } catch (Exception e) {
            LOGGER.error("Error retrieving DB connection with uuid: {}", connectionUuid, e);
            throw new DbConnectionException("Failed to retrieve database connection credentials");
        }
    }

    public ResponseEntity<GlobalResponse> getConnectionList() {
        try {
            LOGGER.info("Retrieving list of DB connections");

            var connections = repo.findAll().stream().map(connectionEntity -> {
                try {
                    return ConnectionCredentialsDto.builder().uuid(connectionEntity.getPublicId().toString()).alias(connectionEntity.getAlias()).dbType(connectionEntity.getDbType()).host(connectionEntity.getHost()).port(connectionEntity.getPort()).dbName(connectionEntity.getDbName()).username(connectionEntity.getUsername()).password(EncryptorUtils.decrypt(connectionEntity.getPassword())).build();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }).toList();

            LOGGER.info("Successfully retrieved list of DB connections");
            return ResponseEntity.ok(GlobalResponse.success("Database connections retrieved successfully", connections));
        } catch (Exception e) {
            LOGGER.error("Error retrieving list of DB connections", e);
            throw new DbConnectionException("Failed to retrieve database connections");
        }
    }

    public ResponseEntity<GlobalResponse> deleteConnection(String connectionUuid) {

        LOGGER.info("Deleting DB connection with uuid: {}", connectionUuid);
        ConnectionCredentialsEntity connectionEntity = repo.getByUuid(connectionUuid).orElseThrow(() -> new DbConnectionException("Database connection not found"));
        repo.delete(connectionEntity);
        LOGGER.info("Database connection with uuid {} successfully has been deleted", connectionUuid);

        return ResponseEntity.ok(GlobalResponse.success("Database connection deleted successfully"));
    }

    @Transactional
    public ResponseEntity<GlobalResponse> updateConnection(ConnectionCredentialsDto request) {
        try {
            LOGGER.info("Updating DB connection with alias: {}", request.alias());

            ConnectionCredentialsEntity connectionEntity = repo.getByUuid(request.uuid()).orElseThrow(() -> new DbConnectionException("Database connection not found"));

            connectionEntity.setDbType(request.dbType());
            connectionEntity.setHost(request.host());
            connectionEntity.setPort(request.port());
            connectionEntity.setDbName(request.dbName());
            connectionEntity.setUsername(request.username());
            connectionEntity.setPassword(EncryptorUtils.encrypt(request.password()));

            repo.save(connectionEntity);
            LOGGER.info("Database connection with alias {} successfully has been updated", request.alias());
        } catch (Exception e) {
            LOGGER.error("Error updating DB connection with alias: {}", request.alias(), e);
            throw new DbConnectionException("Failed to update database connection credentials");
        }

        return ResponseEntity.ok(GlobalResponse.success("Database connection updated successfully"));
    }
}
