package dev.wilsonblanco.reportgenerator.specifications;

import dev.wilsonblanco.reportgenerator.dto.requests.filters.ReportHistoryFilter;
import dev.wilsonblanco.reportgenerator.models.ReportHistoryEntity;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class ReportHistorySpecs {

    public static Specification<ReportHistoryEntity> withFilter(ReportHistoryFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter == null) {
                return cb.conjunction();
            }

            if (hasValue(filter.status())) {
                predicates.add(cb.equal(root.get("status"), filter.status()));
            }

            if (hasValue(filter.search())) {
                String term = "%" + filter.search().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("reportName")), term));
            }

            if (filter.startDate() != null) {
                Timestamp start = Timestamp.valueOf(filter.startDate().atStartOfDay());
                predicates.add(cb.greaterThanOrEqualTo(root.get("generatedAt"), start));
            }

            if (filter.endDate() != null) {
                Timestamp end = Timestamp.valueOf(filter.endDate().atTime(LocalTime.MAX));
                predicates.add(cb.lessThanOrEqualTo(root.get("generatedAt"), end));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static boolean hasValue(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
