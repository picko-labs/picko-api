package com.picko.api.spot.infrastructure;

import com.picko.api.spot.domain.SpotRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotRequestRepository extends JpaRepository<SpotRequestEntity, Long> {
}
