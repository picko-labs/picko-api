package com.picko.api.pin.infrastructure;

import com.picko.api.pin.domain.UserPinEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPinRepository extends JpaRepository<UserPinEntity, Long> {

    List<UserPinEntity> findByUserIdAndDeletedAtIsNull(Long userId);

    /** 유저가 핀한 spotId 목록을 projection 으로 한 번에 조회한다 (개인화 isPinned 판정용). */
    @Query("SELECT p.spot.id FROM UserPinEntity p WHERE p.user.id = :userId AND p.deletedAt IS NULL")
    List<Long> findPinnedSpotIds(@Param("userId") Long userId);

    Optional<UserPinEntity> findByIdAndDeletedAtIsNull(Long id);

    Optional<UserPinEntity> findByUserIdAndSpotIdAndDeletedAtIsNull(Long userId, Long spotId);

    long countBySpotIdAndDeletedAtIsNull(Long spotId);

    boolean existsByUserIdAndSpotIdAndDeletedAtIsNull(Long userId, Long spotId);
}
