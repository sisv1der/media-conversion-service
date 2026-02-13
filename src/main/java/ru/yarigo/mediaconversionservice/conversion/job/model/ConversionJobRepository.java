package ru.yarigo.mediaconversionservice.conversion.job.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ConversionJobRepository extends JpaRepository<ConversionJobEntity, UUID> {
}
