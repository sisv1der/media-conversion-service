package ru.yarigo.mediaconversionservice.conversion.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "conversion_jobs")
@Data
public class ConversionJobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, name = "input_s3_key")
    private String inputS3Key;

    @Column(name = "output_s3_key")
    private String outputS3Key;

    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(nullable = false, columnDefinition = "media_format", name = "input_format")
    private MediaFormat inputFormat;

    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(nullable = false, columnDefinition = "media_format", name = "output_format")
    private MediaFormat outputFormat;

    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(nullable = false, columnDefinition = "job_status")
    private JobStatus status;

    private String errorMessage;

    @Column(nullable = false, updatable = false, name = "created_at")
    private Instant createdAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @PrePersist
    protected void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (status == null) {
            status = JobStatus.PENDING;
        }
    }
}