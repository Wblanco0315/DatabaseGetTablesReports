package dev.wilsonblanco.reportgenerator.repositories;

import dev.wilsonblanco.reportgenerator.models.ReportTemplates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportTemplatesRepository extends JpaRepository<ReportTemplates, Long> {

}
