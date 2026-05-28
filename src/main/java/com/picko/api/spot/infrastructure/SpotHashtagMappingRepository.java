package com.picko.api.spot.infrastructure;

import com.picko.api.spot.domain.SpotHashtagMappingEntity;
import com.picko.api.spot.domain.id.SpotHashtagMappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpotHashtagMappingRepository extends JpaRepository<SpotHashtagMappingEntity, SpotHashtagMappingId> {

    List<SpotHashtagMappingEntity> findByIdSpotId(Long spotId);
}
