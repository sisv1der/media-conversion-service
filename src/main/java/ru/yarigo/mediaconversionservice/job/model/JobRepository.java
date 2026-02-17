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
    @Modifying
    @Query(
            value = """
                UPDATE conversion_jobs
                SET status = 'PROCESSING'
                WHERE id = (
                    SELECT id
                    FROM conversion_jobs
                    WHERE status = 'PENDING'
                    FOR UPDATE SKIP LOCKED
                    LIMIT :limit
                )
                RETURNING *
            """,
            nativeQuery = true
    )
    List<JobEntity> findByStatus(@Param("limit") int limit);
}
