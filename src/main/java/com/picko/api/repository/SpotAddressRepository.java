package com.picko.api.repository;

import com.picko.api.repository.entity.SpotAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpotAddressRepository extends JpaRepository<SpotAddressEntity, Long> {

    Optional<SpotAddressEntity> findByCode(String code);
}
