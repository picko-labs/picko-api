package com.picko.api.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * users 테이블
 *
 * 목적: 서비스 사용자 계정 관리
 * 설명: Apple/Google/Line OAuth 소셜 로그인을 지원한다.
 *       name은 실명이 아닌 서비스 닉네임이다.
 *       nationality는 "같은 국적 트렌딩" 피드 산출에 사용된다.
 *       livingInKorea는 한국 거주 여부 인증 상태를 나타낸다.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity extends BaseEntity {

    /** 사용자 고유 식별자 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 서비스 닉네임 */
    @Column(nullable = false)
    private String name;

    /** 로그인 이메일 주소 (UNIQUE) */
    @Column(nullable = false, unique = true)
    private String email;

    /** 인증 제공자 (APPLE | GOOGLE | LINE) */
    @Column(name = "auth_provider", nullable = false, length = 20)
    private String authProvider;

    /** OAuth 소셜 로그인 고유 식별자 (subject / UID) */
    @Column(name = "auth_provider_id")
    private String authProviderId;

    /** 사용자 국적 — ISO 3166-1 alpha-2 코드 (예: KR, US, JP). "같은 국적 트렌딩" 피드 산출에 사용 */
    @Column(length = 10)
    private String nationality;

    /** 한국 거주 인증 여부 (false: 미인증, true: 인증) */
    @Column(name = "living_in_korea", nullable = false)
    private Boolean livingInKorea = false;
}
