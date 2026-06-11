package com.picko.api.admin.domain;

import com.picko.api.common.domain.BaseEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * admins 테이블
 *
 * 목적: 내부 어드민 계정 관리
 * 설명: 서비스 운영자용 계정으로 이메일/비밀번호 기반 로그인을 사용한다.
 *       스팟 등록, 트렌딩 설정 등 운영 기능에 접근한다.
 */
@Entity
@Table(name = "admins")
@SQLDelete(sql = "UPDATE admins SET deleted_at = NOW() WHERE id = ?")
@Getter
@NoArgsConstructor
public class AdminEntity extends BaseEntity {

    /** 어드민 고유 식별자 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 어드민 이름 */
    @Column(nullable = false)
    private String name;

    /** 로그인 이메일 주소 (UNIQUE) */
    @Column(nullable = false, unique = true)
    private String email;

    /** 해시된 비밀번호 */
    @Column(nullable = false)
    private String password;
}
