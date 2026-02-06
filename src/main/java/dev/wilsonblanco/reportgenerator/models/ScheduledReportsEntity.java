package dev.wilsonblanco.reportgenerator.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@Entity
@Table(name = "scheduled_reports_entity")
public class ScheduledReportsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "report_name", nullable = false)
    private String reportName;

    @Column(name = "frecuency", nullable = false) // DAILY, WEEKLY, MONTHLY
    private String frecuency;

    @Column(name = "report_format", nullable = false) // CSV, PDF, XLSX
    private String reportFormat;

    @Column(name = "cron_expression", nullable = false) // CRON expression
    private String cronExpression;

    @Column(name = "last_execution_time")
    private Date lastExecutionTime;

    @Column(name = "next_execution_time")
    private Date nextExecutionTime;

    @Column(name = "connection_uuid", nullable = false)
    private String connectionUuid;

    @Column(name = "is_active", nullable = false)
    private boolean IsActive;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_template_id", referencedColumnName = "id")
    private ReportTemplates reportTemplate;

}