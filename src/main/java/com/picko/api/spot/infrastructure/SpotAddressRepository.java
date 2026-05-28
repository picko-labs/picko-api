package com.picko.api.spot.infrastructure;

import com.picko.api.spot.domain.SpotAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpotAddressRepository extends JpaRepository<SpotAddressEntity, Long> {

    Optional<SpotAddressEntity> findByCode(String code);
}
