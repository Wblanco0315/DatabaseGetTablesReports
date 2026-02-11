package dev.wilsonblanco.reportgenerator.specifications;

import dev.wilsonblanco.reportgenerator.dto.requests.filters.ReportTemplateFilter;
import dev.wilsonblanco.reportgenerator.models.ReportTemplatesEntity;
import jakarta.persistence.criteria.Predicate;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class ReportTemplateSpecs {

    public static Specification<ReportTemplatesEntity> withFilter(ReportTemplateFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter == null) {
                return cb.conjunction();
            }

            if (hasValue(filter.search())) {
                String term = "%" + filter.search().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("templateName")), term));
            }

            if (filter.startDate() != null) {
                Timestamp start = Timestamp.valueOf(filter.startDate().atStartOfDay());
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), start));
            }

            if (filter.endDate() != null) {
                Timestamp end = Timestamp.valueOf(filter.endDate().atTime(LocalTime.MAX));
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), end));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static boolean hasValue(String str) {
        return str != null && !str.trim().isEmpty();
    }

}
