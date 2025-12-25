package dev.wilsonblanco.reportgenerator.repositories;

import dev.wilsonblanco.reportgenerator.models.ConnectionCredentialsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConnectionCredentialsRepository extends JpaRepository<ConnectionCredentialsEntity, Long> {
    boolean existsByAlias(String alias);

    @Query(value = "SELECT * FROM db_connections d WHERE d.public_id = :uuid", nativeQuery = true)
    Optional<ConnectionCredentialsEntity> getByUuid(String uuid);
}
