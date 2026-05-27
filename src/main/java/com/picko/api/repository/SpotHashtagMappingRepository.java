package com.picko.api.repository;

import com.picko.api.repository.entity.SpotHashtagMappingEntity;
import com.picko.api.repository.entity.id.SpotHashtagMappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpotHashtagMappingRepository extends JpaRepository<SpotHashtagMappingEntity, SpotHashtagMappingId> {

    List<SpotHashtagMappingEntity> findByIdSpotId(Long spotId);
}
