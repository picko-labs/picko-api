package com.picko.api.spot.domain.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * spot_category_mappings 복합 PK
 */
@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class SpotCategoryMappingId implements Serializable {

    /** 연결된 장소 — spots.id 참조 */
    @Column(name = "spot_id")
    private Long spotId;

    /** 연결된 카테고리 — spot_categories.id 참조 */
    @Column(name = "spot_category_id")
    private Long spotCategoryId;
}
