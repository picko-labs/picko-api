package com.picko.api.spot.domain;

import com.picko.api.common.domain.BaseEntity;
import com.picko.api.spot.domain.id.SpotCategoryMappingId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

/**
 * spot_category_mappings 테이블
 *
 * 목적: 장소와 카테고리의 M:N 연결 관계 관리
 * 설명: 하나의 스팟이 여러 카테고리에 속할 수 있고,
 *       하나의 카테고리에 여러 스팟이 연결될 수 있다.
 */
@Entity
@Table(name = "spot_category_mappings")
@SQLDelete(sql = "UPDATE spot_category_mappings SET deleted_at = NOW() WHERE spot_id = ? AND spot_category_id = ?")
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

    public SpotCategoryMappingEntity(SpotEntity spot, SpotCategoryEntity spotCategory) {
        this.id = new SpotCategoryMappingId(spot.getId(), spotCategory.getId());
        this.spot = spot;
        this.spotCategory = spotCategory;
    }
}
