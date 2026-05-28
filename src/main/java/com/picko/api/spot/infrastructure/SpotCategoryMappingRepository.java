package com.picko.api.spot.infrastructure;

import com.picko.api.spot.domain.SpotCategoryMappingEntity;
import com.picko.api.spot.domain.id.SpotCategoryMappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpotCategoryMappingRepository extends JpaRepository<SpotCategoryMappingEntity, SpotCategoryMappingId> {

    List<SpotCategoryMappingEntity> findByIdSpotId(Long spotId);
}
