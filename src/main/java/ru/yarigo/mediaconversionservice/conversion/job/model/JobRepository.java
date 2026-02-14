package ru.yarigo.mediaconversionservice.conversion.job.model;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, UUID> {
    List<JobEntity> findByStatus(JobStatus jobStatus);
    List<JobEntity> findByStatus(JobStatus jobStatus, Pageable pageable);
}
