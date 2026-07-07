package com.picko.api.spot.domain;

import com.picko.api.common.domain.BaseEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * spot_categories 테이블
 *
 * 목적: 장소 분류 카테고리 관리
 * 설명: parent_id 자기참조 FK 로 계층 구조를 표현한다.
 *       서비스 레이어에서 최대 2단계(루트·자식)까지만 허용한다.
 */
@Entity
@Table(name = "spot_categories")
@SQLDelete(sql = "UPDATE spot_categories SET deleted_at = NOW() WHERE id = ?")
@Getter
@NoArgsConstructor
public class SpotCategoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String icon;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    /** 상위 카테고리 — NULL이면 1단계(루트) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private SpotCategoryEntity parent;

    /** 하위 카테고리 목록 — 최대 1단계 자식만 허용 (서비스 레이어 검증) */
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<SpotCategoryEntity> children = new ArrayList<>();

    public static SpotCategoryEntity create(String code, String name, String icon, Integer sortOrder) {
        SpotCategoryEntity entity = new SpotCategoryEntity();
        entity.code = code;
        entity.name = name;
        entity.icon = icon;
        if (sortOrder != null) {
            entity.sortOrder = sortOrder;
        }
        return entity;
    }

    public void update(String code, String name, String icon, Integer sortOrder) {
        this.code = code;
        this.name = name;
        this.icon = icon;
        if (sortOrder != null) {
            this.sortOrder = sortOrder;
        }
    }

    public void assignParent(SpotCategoryEntity parent) {
        this.parent = parent;
    }
}
