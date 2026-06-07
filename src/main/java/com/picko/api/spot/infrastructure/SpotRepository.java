package com.picko.api.spot.infrastructure;

import com.picko.api.spot.domain.SpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpotRepository extends JpaRepository<SpotEntity, Long> {

    @Query("SELECT DISTINCT s FROM SpotEntity s " +
           "WHERE (:addressCode IS NULL OR s.spotAddress.code = :addressCode) " +
           "AND (:isTrending IS NULL OR s.isTrending = :isTrending) " +
           "AND (:categoryCode IS NULL OR EXISTS (" +
           "    SELECT m FROM SpotCategoryMappingEntity m " +
           "    WHERE m.spot = s AND m.spotCategory.code = :categoryCode)) " +
           "AND (:hashtagCode IS NULL OR EXISTS (" +
           "    SELECT m FROM SpotHashtagMappingEntity m " +
           "    WHERE m.spot = s AND m.spotHashtag.code = :hashtagCode)) " +
           "ORDER BY s.createdAt DESC")
    List<SpotEntity> findByFilters(
            @Param("addressCode") String addressCode,
            @Param("isTrending") Boolean isTrending,
            @Param("categoryCode") String categoryCode,
            @Param("hashtagCode") String hashtagCode
    );
}
