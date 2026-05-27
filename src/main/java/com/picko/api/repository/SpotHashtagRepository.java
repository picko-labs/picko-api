package com.picko.api.repository;

import com.picko.api.repository.entity.SpotHashtagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpotHashtagRepository extends JpaRepository<SpotHashtagEntity, Long> {

    Optional<SpotHashtagEntity> findByCode(String code);

    List<SpotHashtagEntity> findAllByDeletedAtIsNullOrderBySortOrderAsc();
}
