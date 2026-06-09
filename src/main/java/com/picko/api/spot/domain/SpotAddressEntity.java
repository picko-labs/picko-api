package com.picko.api.spot.domain;

import com.picko.api.common.domain.BaseEntity;
import com.picko.api.spot.domain.vo.Coordinate;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
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
@SQLDelete(sql = "UPDATE spot_address SET deleted_at = NOW() WHERE id = ?")
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

    /** 시/도 (예: 서울특별시, 경기도) */
    @Column(nullable = false, length = 100)
    private String region;

    /** 시/군/구 (예: 강남구, 수원시) */
    @Column(length = 100)
    private String city;

    /** 읍/면/동 (예: 역삼동, 판교읍) */
    @Column(length = 100)
    private String town;

    /** 우편번호 */
    @Column(name = "postal_code", length = 10)
    private String postalCode;

    /** 기본주소 (도로명/지번 주소) */
    @Column(length = 500)
    private String address;

    /** 상세주소 */
    @Column(name = "address_detail", length = 255)
    private String addressDetail;

    /** WGS84 좌표 (위도·경도) */
    @Embedded
    private Coordinate coordinate;
}
