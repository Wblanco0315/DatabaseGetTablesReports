package dev.wilsonblanco.reportgenerator.repositories;

import dev.wilsonblanco.reportgenerator.models.ReportHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportHistoryEntityRepository extends JpaRepository<ReportHistoryEntity, Long> {
}
