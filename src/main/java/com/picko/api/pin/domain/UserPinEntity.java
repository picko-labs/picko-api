package com.picko.api.pin.domain;

import com.picko.api.common.domain.BaseEntity;
import com.picko.api.spot.domain.SpotEntity;
import com.picko.api.user.domain.UserEntity;
import org.hibernate.annotations.SQLDelete;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * user_pins 테이블
 *
 * 목적: 사용자의 스팟 픽(북마크) 관계 관리
 * 설명: 사이드바 Pick 탭 및 프로필 "My Pins" 목록의 데이터 원천이다.
 *       픽 수가 필요한 경우 이 테이블을 집계해 산출한다.
 *       (userId, spotId) 복합 유니크 키로 중복 픽을 방지한다.
 */
@Entity
@Table(name = "user_pins")
@SQLDelete(sql = "UPDATE user_pins SET deleted_at = NOW() WHERE id = ?")
@Getter
@NoArgsConstructor
public class UserPinEntity extends BaseEntity {

    /** 픽 고유 식별자 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 픽한 사용자 — users.id 참조 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /** 픽된 장소 — spots.id 참조 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_id", nullable = false)
    private SpotEntity spot;

    /** 핀 카테고리 — user_pin_categories.id 참조. NULL이면 미분류 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pin_category_id")
    private UserPinCategoryEntity userPinCategory;

    public static UserPinEntity create(UserEntity user, SpotEntity spot, UserPinCategoryEntity category) {
        UserPinEntity entity = new UserPinEntity();
        entity.user = user;
        entity.spot = spot;
        entity.userPinCategory = category;
        return entity;
    }

    public void changeCategory(UserPinCategoryEntity category) {
        this.userPinCategory = category;
    }
}
