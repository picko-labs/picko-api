package com.picko.api.spot.domain;

import com.picko.api.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * spot_address 테이블
 *
 * 목적: 장소 분류에 사용되는 주소 정보 체계 관리
 * 설명: 사이드바 Nationwide 탭의 지역별 트렌딩 카드에 사용된다.
 */
@Entity
@Table(name = "spot_address")
@Getter
@Setter
@NoArgsConstructor
public class SpotAddressEntity extends BaseEntity {

    /** 주소 고유 식별자 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 주소 코드 — 영문 소문자 (예: seoul, busan, jeju). 프로그래밍 식별자로 사용 */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    /** 주소 한글 표기명 (예: 서울, 부산, 제주) */
    @Column(nullable = false, length = 50)
    private String name;

    /** 주소 영문 표기명 (예: Seoul, Busan, Jeju) */
    @Column(name = "name_en", nullable = false, length = 100)
    private String nameEn;
}
