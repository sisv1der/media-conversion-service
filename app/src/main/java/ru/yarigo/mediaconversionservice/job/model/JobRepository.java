package ru.yarigo.mediaconversionservice.job.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, UUID> {
    List<JobEntity> findByIdIn(List<UUID> ids);

    @Modifying(clearAutomatically = true)
    @Query("update JobEntity j set j.status = :newStatus where j.id = :id and j.status = :status")
    int updateJobStatusByIdAndStatus(
            @Param("id") UUID id,
            @Param("status") JobStatus status,
            @Param("newStatus") JobStatus newStatus
    );
}
