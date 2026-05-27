package com.picko.api.repository;

import com.picko.api.repository.entity.SpotCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpotCategoryRepository extends JpaRepository<SpotCategoryEntity, Long> {

    Optional<SpotCategoryEntity> findByCode(String code);

    List<SpotCategoryEntity> findAllByDeletedAtIsNullOrderBySortOrderAsc();
}
