package dev.wilsonblanco.reportgenerator.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "report_history")
public class ReportHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "report_name", nullable = false)
    private String reportName;

    @Column(name = "query", columnDefinition = "TEXT", nullable = false)
    private String query;

    @Column(name = "report_format", nullable = false) // CSV, PDF, XLSX
    private String reportFormat;

    @Column(name = "status", nullable = false) // PENDING, IN_PROGRESS, COMPLETED, FAILED
    private String status;

    @Column(name = "generated_at", nullable = false)
    @CreationTimestamp
    private Timestamp generatedAt;

    @Column(name = "generation_duration")
    private String generationDuration;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "connection_uuid", nullable = false)
    private String connectionUuid;

    @Column(name = "Details", columnDefinition = "TEXT")
    private String details;

}