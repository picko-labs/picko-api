package com.picko.api.repository;

import com.picko.api.repository.entity.SpotCategoryMappingEntity;
import com.picko.api.repository.entity.id.SpotCategoryMappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpotCategoryMappingRepository extends JpaRepository<SpotCategoryMappingEntity, SpotCategoryMappingId> {

    List<SpotCategoryMappingEntity> findByIdSpotId(Long spotId);
}
