package com.picko.api.repository.entity;

import com.picko.api.repository.entity.id.SpotCategoryMappingId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * spot_category_mappings 테이블
 *
 * 목적: 장소와 카테고리의 M:N 연결 관계 관리
 * 설명: 하나의 스팟이 여러 카테고리에 속할 수 있고,
 *       하나의 카테고리에 여러 스팟이 연결될 수 있다.
 */
@Entity
@Table(name = "spot_category_mappings")
@Getter
@NoArgsConstructor
public class SpotCategoryMappingEntity extends BaseEntity {

    @EmbeddedId
    private SpotCategoryMappingId id;

    /** 연결된 장소 — spots.id 참조 */
    @MapsId("spotId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_id")
    private SpotEntity spot;

    /** 연결된 카테고리 — spot_categories.id 참조 */
    @MapsId("spotCategoryId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_category_id")
    private SpotCategoryEntity spotCategory;
}
