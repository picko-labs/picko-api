package com.picko.api.pin.infrastructure;

import com.picko.api.pin.domain.UserPinCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPinCategoryRepository extends JpaRepository<UserPinCategoryEntity, Long> {

    List<UserPinCategoryEntity> findByUserIdAndDeletedAtIsNullOrderBySortOrderAsc(Long userId);
}
