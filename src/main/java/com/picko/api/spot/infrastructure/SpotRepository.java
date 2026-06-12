package com.picko.api.spot.infrastructure;

import com.picko.api.spot.domain.SpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SpotRepository extends JpaRepository<SpotEntity, Long> {

    @Query("SELECT DISTINCT s FROM SpotEntity s " +
           "LEFT JOIN s.spotAddress sa " +
           "WHERE s.deletedAt IS NULL " +
           "AND sa.coordinate.latitude BETWEEN :swLat AND :neLat " +
           "AND sa.coordinate.longitude BETWEEN :swLng AND :neLng " +
           "AND (:categoryCode IS NULL OR EXISTS (" +
           "    SELECT m FROM SpotCategoryMappingEntity m " +
           "    WHERE m.spot = s AND m.spotCategory.code = :categoryCode AND m.deletedAt IS NULL))")
    List<SpotEntity> findByViewport(
            @Param("swLat") BigDecimal swLat,
            @Param("swLng") BigDecimal swLng,
            @Param("neLat") BigDecimal neLat,
            @Param("neLng") BigDecimal neLng,
            @Param("categoryCode") String categoryCode
    );
}
