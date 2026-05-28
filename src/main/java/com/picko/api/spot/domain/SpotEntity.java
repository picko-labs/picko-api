package com.picko.api.spot.domain;

import com.picko.api.admin.domain.AdminEntity;
import com.picko.api.common.domain.BaseEntity;
import com.picko.api.user.domain.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * spots 테이블
 *
 * 목적: K-SPOT 서비스의 핵심 장소 정보 관리
 * 설명: 지도 위 마커, 사이드바 랭킹 카드, 상세 카드의 데이터 원천이다.
 *       카테고리는 spot_category_mappings를 통해 N개를 가질 수 있다.
 *       핀 수는 비정규화하지 않고 user_pins를 집계해 산출한다.
 *       isTrending은 운영자가 직접 지정하는 강조 플래그다.
 *       user / admin 중 하나만 세팅되며, 둘 다 NULL이면 배치 등록이다.
 */
@Entity
@Table(name = "spots")
@Getter
@Setter
@NoArgsConstructor
public class SpotEntity extends BaseEntity {

    /** 장소 고유 식별자 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 장소명 (예: Seongsu Cafe Street) */
    @Column(nullable = false)
    private String name;

    /** 주소 분류 — spot_address.id 참조. NULL이면 미분류 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_address_id")
    private SpotAddressEntity spotAddress;

    /** 동/구 단위 지역명 (예: Seongsu-dong). 지도 카드 하단 거리 정보에 표시 */
    @Column(length = 255)
    private String location;

    /** 도로명 전체 주소 (예: 123 Seongsu-ro, Seongdong-gu, Seoul) */
    @Column(length = 500)
    private String address;

    /** 장소 소개 문구 — 상세 카드 본문에 표시 */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** 위도 (WGS84). 지도 마커 위치 계산에 사용 */
    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    /** 경도 (WGS84). 지도 마커 위치 계산에 사용 */
    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    /** 대표 이미지 URL — 마커 썸네일 및 카드 헤더에 사용 */
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /** 트렌딩 강조 표시 여부 (false: 일반, true: 트렌딩). 마커 테두리 색상 변경에 반영 */
    @Column(name = "is_trending", nullable = false)
    private Boolean isTrending = false;

    /** 사용자 등록자 — users.id 참조. 사용자가 등록한 경우에만 세팅 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    /** 어드민 등록자 — admins.id 참조. 어드민이 등록한 경우에만 세팅 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private AdminEntity admin;
}
