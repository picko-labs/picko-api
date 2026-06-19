package com.picko.api.spot.domain;

import com.picko.api.admin.domain.AdminEntity;
import com.picko.api.common.domain.BaseEntity;
import com.picko.api.user.domain.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * spot_requests 테이블
 *
 * 목적: 사용자가 신청한 스팟 등록 요청 관리
 * 설명: 어드민이 승인하면 spots 테이블에 저장된다.
 *       place_id는 추후 Google Maps 연동 시 활용한다.
 */
@Entity
@Table(name = "spot_requests")
@SQLDelete(sql = "UPDATE spot_requests SET deleted_at = NOW() WHERE id = ?")
@Getter
@NoArgsConstructor
public class SpotRequestEntity extends BaseEntity {

    public enum Status { PENDING, APPROVED, REJECTED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "place_id", length = 255)
    private String placeId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(length = 500)
    private String address;

    @Column(length = 100)
    private String region;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String town;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.PENDING;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_admin_id")
    private AdminEntity reviewedAdmin;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    /** 승인 후 생성된 spots.id 참조 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_id")
    private SpotEntity spot;

    public static SpotRequestEntity create(UserEntity user, String placeId, String name,
                                           String description, String imageUrl,
                                           BigDecimal latitude, BigDecimal longitude,
                                           String address, String region, String city,
                                           String town, String postalCode) {
        SpotRequestEntity entity = new SpotRequestEntity();
        entity.user = user;
        entity.placeId = placeId;
        entity.name = name;
        entity.description = description;
        entity.imageUrl = imageUrl;
        entity.latitude = latitude;
        entity.longitude = longitude;
        entity.address = address;
        entity.region = region;
        entity.city = city;
        entity.town = town;
        entity.postalCode = postalCode;
        return entity;
    }

    public void approve(AdminEntity admin, SpotEntity createdSpot) {
        this.status = Status.APPROVED;
        this.reviewedAdmin = admin;
        this.reviewedAt = LocalDateTime.now();
        this.spot = createdSpot;
    }

    public void reject(AdminEntity admin, String reason) {
        this.status = Status.REJECTED;
        this.reviewedAdmin = admin;
        this.reviewedAt = LocalDateTime.now();
        this.rejectReason = reason;
    }
}
