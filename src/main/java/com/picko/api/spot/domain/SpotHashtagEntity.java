package com.picko.api.spot.domain;

import com.picko.api.common.domain.BaseEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * spot_hashtags 테이블
 *
 * 목적: 지도 상단 가로 스크롤 필터 칩 관리
 * 설명: Hot Place, Rising, K-POP, Cafe, Food, Photo, Shopping,
 *       Culture, Nightlife, Nature 등의 해시태그 필터를 관리한다.
 *       spot_hashtag_mappings를 통해 스팟과 M:N 관계를 맺는다.
 */
@Entity
@Table(name = "spot_hashtags")
@SQLDelete(sql = "UPDATE spot_hashtags SET deleted_at = NOW() WHERE id = ?")
@Getter
@NoArgsConstructor
public class SpotHashtagEntity extends BaseEntity {

    /** 해시태그 고유 식별자 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 태그 코드 — 영문 소문자 (예: hot, rising, kpop, cafe). 프로그래밍 식별자로 사용 */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    /** 태그 표시명 (예: Hot Place, Rising, K-POP) */
    @Column(nullable = false, length = 100)
    private String name;

    /** 태그 대표 아이콘 — 이모지 또는 아이콘 키 (예: 🔥, 🌟, 🎤) */
    @Column(length = 50)
    private String icon;

    /** 필터 칩 노출 순서 (오름차순 정렬) */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
}
