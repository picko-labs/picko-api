package com.picko.api.spot.infrastructure;

import com.picko.api.spot.domain.SpotEntity;
import org.springframework.data.domain.Pageable;
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

    // Haversine 공간 연산이 필요해 Native Query 사용.
    // user_pins GROUP BY JOIN으로 pinCount를 단일 쿼리에서 집계한다.
    @Query(value = """
            SELECT s.id          AS id,
                   sub.pin_count AS pin_count,
                   (6371 * acos(LEAST(1.0,
                       cos(radians(:centerLat)) * cos(radians(sa.latitude))
                       * cos(radians(sa.longitude) - radians(:centerLng))
                       + sin(radians(:centerLat)) * sin(radians(sa.latitude))
                   )))            AS distance_km
            FROM spots s
            INNER JOIN spot_address sa
                ON s.spot_address_id = sa.id AND sa.deleted_at IS NULL
            INNER JOIN (
                SELECT spot_id, COUNT(*) AS pin_count
                FROM user_pins
                WHERE deleted_at IS NULL
                GROUP BY spot_id
                HAVING COUNT(*) >= :minPinCount
            ) sub ON sub.spot_id = s.id
            WHERE s.deleted_at IS NULL
              AND (6371 * acos(LEAST(1.0,
                       cos(radians(:centerLat)) * cos(radians(sa.latitude))
                       * cos(radians(sa.longitude) - radians(:centerLng))
                       + sin(radians(:centerLat)) * sin(radians(sa.latitude))
                   ))) <= :radiusKm
            ORDER BY sub.pin_count DESC
            """, nativeQuery = true)
    List<SpotTrendingProjection> findTrendingNearby(
            @Param("centerLat") BigDecimal centerLat,
            @Param("centerLng") BigDecimal centerLng,
            @Param("radiusKm") double radiusKm,
            @Param("minPinCount") long minPinCount,
            Pageable pageable
    );

    // Trending Nearby와 동일한 공간 연산 + 동일 국적 유저의 핀만 집계한다.
    @Query(value = """
            SELECT s.id          AS id,
                   sub.pin_count AS pin_count,
                   (6371 * acos(LEAST(1.0,
                       cos(radians(:centerLat)) * cos(radians(sa.latitude))
                       * cos(radians(sa.longitude) - radians(:centerLng))
                       + sin(radians(:centerLat)) * sin(radians(sa.latitude))
                   )))            AS distance_km
            FROM spots s
            INNER JOIN spot_address sa
                ON s.spot_address_id = sa.id AND sa.deleted_at IS NULL
            INNER JOIN (
                SELECT up.spot_id, COUNT(*) AS pin_count
                FROM user_pins up
                INNER JOIN users u ON u.id = up.user_id AND u.deleted_at IS NULL
                WHERE up.deleted_at IS NULL
                  AND u.nationality = :nationality
                GROUP BY up.spot_id
                HAVING COUNT(*) >= :minPinCount
            ) sub ON sub.spot_id = s.id
            WHERE s.deleted_at IS NULL
              AND (6371 * acos(LEAST(1.0,
                       cos(radians(:centerLat)) * cos(radians(sa.latitude))
                       * cos(radians(sa.longitude) - radians(:centerLng))
                       + sin(radians(:centerLat)) * sin(radians(sa.latitude))
                   ))) <= :radiusKm
            ORDER BY sub.pin_count DESC
            """, nativeQuery = true)
    List<SpotTrendingProjection> findTrendingByNationality(
            @Param("centerLat") BigDecimal centerLat,
            @Param("centerLng") BigDecimal centerLng,
            @Param("radiusKm") double radiusKm,
            @Param("nationality") String nationality,
            @Param("minPinCount") long minPinCount,
            Pageable pageable
    );
}
