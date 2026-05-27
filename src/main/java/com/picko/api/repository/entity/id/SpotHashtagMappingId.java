package com.picko.api.repository.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * spot_hashtag_mappings 복합 PK
 */
@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class SpotHashtagMappingId implements Serializable {

    /** 연결된 장소 — spots.id 참조 */
    @Column(name = "spot_id")
    private Long spotId;

    /** 연결된 해시태그 — spot_hashtags.id 참조 */
    @Column(name = "spot_hashtag_id")
    private Long spotHashtagId;
}
