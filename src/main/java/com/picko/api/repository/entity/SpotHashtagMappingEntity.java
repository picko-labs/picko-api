package com.picko.api.repository.entity;

import com.picko.api.repository.entity.id.SpotHashtagMappingId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * spot_hashtag_mappings 테이블
 *
 * 목적: 장소와 해시태그의 M:N 연결 관계 관리
 * 설명: 하나의 스팟이 여러 해시태그에 속할 수 있고,
 *       하나의 태그에 여러 스팟이 연결될 수 있다.
 *       필터 칩 선택 시 해당 태그에 연결된 스팟만 지도에 표시한다.
 */
@Entity
@Table(name = "spot_hashtag_mappings")
@Getter
@NoArgsConstructor
public class SpotHashtagMappingEntity extends BaseEntity {

    @EmbeddedId
    private SpotHashtagMappingId id;

    /** 연결된 장소 — spots.id 참조 */
    @MapsId("spotId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_id")
    private SpotEntity spot;

    /** 연결된 해시태그 — spot_hashtags.id 참조 */
    @MapsId("spotHashtagId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_hashtag_id")
    private SpotHashtagEntity spotHashtag;
}
