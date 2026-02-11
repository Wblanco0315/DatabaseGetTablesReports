package dev.wilsonblanco.reportgenerator.repositories;

import dev.wilsonblanco.reportgenerator.models.ReportTemplatesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportTemplatesRepository extends JpaRepository<ReportTemplatesEntity, Long>, JpaSpecificationExecutor<ReportTemplatesEntity> {

}
