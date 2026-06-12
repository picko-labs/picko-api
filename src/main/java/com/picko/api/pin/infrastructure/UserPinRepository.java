package com.picko.api.pin.infrastructure;

import com.picko.api.pin.domain.UserPinEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPinRepository extends JpaRepository<UserPinEntity, Long> {

    List<UserPinEntity> findByUserIdAndDeletedAtIsNull(Long userId);

    Optional<UserPinEntity> findByIdAndDeletedAtIsNull(Long id);

    Optional<UserPinEntity> findByUserIdAndSpotIdAndDeletedAtIsNull(Long userId, Long spotId);

    long countBySpotIdAndDeletedAtIsNull(Long spotId);

    boolean existsByUserIdAndSpotIdAndDeletedAtIsNull(Long userId, Long spotId);
}
