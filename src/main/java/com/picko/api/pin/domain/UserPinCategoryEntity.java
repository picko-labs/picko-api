package com.picko.api.pin.domain;

import com.picko.api.common.domain.BaseEntity;
import com.picko.api.user.domain.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

/**
 * user_pin_categories 테이블
 *
 * 목적: 사용자가 직접 정의하는 핀 카테고리 관리
 * 설명: 사용자가 자신의 핀(북마크)을 분류하는 커스텀 카테고리다.
 *       user_pins.user_pin_category_id 로 연결된다.
 */
@Entity
@Table(name = "user_pin_categories")
@SQLDelete(sql = "UPDATE user_pin_categories SET deleted_at = NOW() WHERE id = ?")
@Getter
@NoArgsConstructor
public class UserPinCategoryEntity extends BaseEntity {

    /** 핀 카테고리 고유 식별자 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 카테고리를 소유한 사용자 — users.id 참조 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /** 핀 카테고리명 */
    @Column(nullable = false)
    private String name;

    /** 카테고리 노출 순서 (오름차순 정렬) */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    public static UserPinCategoryEntity create(UserEntity user, String name, Integer sortOrder) {
        UserPinCategoryEntity entity = new UserPinCategoryEntity();
        entity.user = user;
        entity.name = name;
        if (sortOrder != null) {
            entity.sortOrder = sortOrder;
        }
        return entity;
    }

    public void update(String name, Integer sortOrder) {
        if (name != null) {
            this.name = name;
        }
        if (sortOrder != null) {
            this.sortOrder = sortOrder;
        }
    }
}
