package com.picko.api.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 모든 테이블의 공통 타임스탬프 컬럼을 정의하는 기반 클래스.
 *
 * 목적: 생성/수정/삭제 일시를 일관되게 관리
 * 설명: 데이터는 hard delete하지 않으며, 삭제 시 deletedAt에 현재 시각을 기록한다 (soft delete).
 *       유효 레코드 조회 시 항상 deletedAt IS NULL 조건을 포함해야 한다.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity {

    /** 레코드 생성 일시 */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 레코드 최종 수정 일시 */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** 삭제 일시 — NULL이면 유효 레코드 (soft delete) */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
