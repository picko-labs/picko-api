package com.picko.api.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * spot_categories 테이블
 *
 * 목적: 장소 분류 카테고리 관리
 * 설명: 지도 상단 필터 칩 및 스팟 등록 폼의 카테고리 선택지로 사용된다.
 *       현재 정의된 값: Cafe, Food, Shopping, Culture, Nightlife
 */
@Entity
@Table(name = "spot_categories")
@Getter
@Setter
@NoArgsConstructor
public class SpotCategoryEntity extends BaseEntity {

    /** 카테고리 고유 식별자 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 카테고리 코드 — 영문 소문자 (예: cafe, food, shopping, culture, nightlife). 프로그래밍 식별자로 사용 */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    /** 카테고리 표시명 (예: Cafe, Food, Shopping) */
    @Column(nullable = false, length = 100)
    private String name;

    /** 카테고리 대표 아이콘 — 이모지 또는 아이콘 키 (예: ☕, 🍜) */
    @Column(length = 50)
    private String icon;

    /** 화면 노출 순서 (오름차순 정렬) */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
}
