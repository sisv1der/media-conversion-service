package ru.yarigo.mediaconversionservice.conversion.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface ConversionJobRepository extends JpaRepository<ConversionJobEntity, UUID> {
}
