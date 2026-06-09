-- ============================================================
-- K-SPOT Schema DDL
-- MySQL / InnoDB  |  charset utf8mb4
-- ============================================================

-- ────────────────────────────────────────────────────────────
-- 1. users
--
-- 목적: 서비스 사용자 계정 관리
-- 설명: Apple/Google/Line OAuth 소셜 로그인을 지원한다.
--       name 은 실명이 아닌 서비스 닉네임이다.
--       nationality 는 "같은 국적 트렌딩" 피드 산출에 사용된다.
--       living_in_korea 는 한국 거주 여부 인증 상태를 나타낸다.
-- ────────────────────────────────────────────────────────────
CREATE TABLE users (
    id               BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '사용자 고유 식별자 (PK)',
    name             VARCHAR(255)  NOT NULL                 COMMENT '서비스 닉네임',
    email            VARCHAR(255)  NOT NULL                 COMMENT '로그인 이메일 주소 (UNIQUE)',
    auth_provider    VARCHAR(20)   NOT NULL                 COMMENT '인증 제공자 (APPLE | GOOGLE | LINE)',
    auth_provider_id VARCHAR(255)  NULL                     COMMENT 'OAuth 소셜 로그인 고유 식별자 (subject / UID)',
    nationality      VARCHAR(10)   NULL                     COMMENT '사용자 국적 — ISO 3166-1 alpha-2 코드 (예: KR, US, JP). "같은 국적 트렌딩" 피드 산출에 사용',
    living_in_korea  TINYINT(1)    NOT NULL DEFAULT 0       COMMENT '한국 거주 인증 여부 (0: 미인증, 1: 인증)',
    created_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP             COMMENT '계정 생성 일시',
    updated_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
                         ON UPDATE CURRENT_TIMESTAMP                              COMMENT '계정 정보 최종 수정 일시',
    deleted_at       TIMESTAMP     NULL                                            COMMENT '계정 삭제 일시 (soft delete)',
    PRIMARY KEY (id),
    UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='서비스 사용자 계정';

-- ────────────────────────────────────────────────────────────
-- 2. admins
--
-- 목적: 내부 어드민 계정 관리
-- 설명: 서비스 운영자용 계정으로 이메일/비밀번호 기반 로그인을 사용한다.
--       스팟 등록, 트렌딩 설정 등 운영 기능에 접근한다.
-- ────────────────────────────────────────────────────────────
CREATE TABLE admins (
    id         BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '어드민 고유 식별자 (PK)',
    name       VARCHAR(255)  NOT NULL                 COMMENT '어드민 이름',
    email      VARCHAR(255)  NOT NULL                 COMMENT '로그인 이메일 주소 (UNIQUE)',
    password   VARCHAR(255)  NOT NULL                 COMMENT '해시된 비밀번호',
    created_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP             COMMENT '계정 생성 일시',
    updated_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
                   ON UPDATE CURRENT_TIMESTAMP                              COMMENT '계정 정보 최종 수정 일시',
    deleted_at TIMESTAMP     NULL                                            COMMENT '계정 삭제 일시 (soft delete)',
    PRIMARY KEY (id),
    UNIQUE KEY uq_admins_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='내부 어드민 계정';

-- ────────────────────────────────────────────────────────────
-- 3. spot_address
--
-- 목적: 장소 분류에 사용되는 주소 정보 체계 관리
-- 설명: 사이드바 Nationwide 탭의 지역별 트렌딩 카드에 사용된다.
-- ────────────────────────────────────────────────────────────
CREATE TABLE spot_address (
    id             BIGINT         NOT NULL AUTO_INCREMENT  COMMENT '주소 고유 식별자 (PK)',
    code           VARCHAR(50)    NOT NULL                 COMMENT '주소 코드 — 영문 소문자 (예: seoul, busan, jeju). 프로그래밍 식별자로 사용',
    region         VARCHAR(100)   NOT NULL                 COMMENT '시/도 (예: 서울특별시, 경기도)',
    city           VARCHAR(100)   NULL                     COMMENT '시/군/구 (예: 강남구, 수원시)',
    town           VARCHAR(100)   NULL                     COMMENT '읍/면/동 (예: 역삼동, 판교읍)',
    postal_code    VARCHAR(10)    NULL                     COMMENT '우편번호',
    address        VARCHAR(500)   NULL                     COMMENT '기본주소 (도로명/지번 주소)',
    address_detail VARCHAR(255)   NULL                     COMMENT '상세주소',
    latitude       DECIMAL(10, 7) NULL                     COMMENT '위도 (WGS84)',
    longitude      DECIMAL(10, 7) NULL                     COMMENT '경도 (WGS84)',
    created_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP             COMMENT '레코드 생성 일시',
    updated_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
                       ON UPDATE CURRENT_TIMESTAMP                               COMMENT '레코드 최종 수정 일시',
    deleted_at     TIMESTAMP      NULL                                            COMMENT '삭제 일시 (soft delete)',
    PRIMARY KEY (id),
    UNIQUE KEY uq_spot_address_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='장소 주소 정보 체계 (Nationwide 트렌딩 탭 기준)';

-- ────────────────────────────────────────────────────────────
-- 4. spot_categories
--
-- 목적: 장소 분류 카테고리 관리
-- 설명: 지도 상단 필터 칩 및 스팟 등록 폼의 카테고리 선택지로 사용된다.
--       현재 정의된 값: Cafe, Food, Shopping, Culture, Nightlife
-- ────────────────────────────────────────────────────────────
CREATE TABLE spot_categories (
    id         BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '카테고리 고유 식별자 (PK)',
    code       VARCHAR(50)  NOT NULL                 COMMENT '카테고리 코드 — 영문 소문자 (예: cafe, food, shopping, culture, nightlife). 프로그래밍 식별자로 사용',
    name       VARCHAR(100) NOT NULL                 COMMENT '카테고리 표시명 (예: Cafe, Food, Shopping)',
    icon       VARCHAR(50)  NULL                     COMMENT '카테고리 대표 아이콘 — 이모지 또는 아이콘 키 (예: ☕, 🍜)',
    sort_order INT          NOT NULL DEFAULT 0       COMMENT '화면 노출 순서 (오름차순 정렬)',
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP             COMMENT '레코드 생성 일시',
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
                   ON UPDATE CURRENT_TIMESTAMP                             COMMENT '레코드 최종 수정 일시',
    deleted_at TIMESTAMP    NULL                                            COMMENT '삭제 일시 (soft delete)',
    PRIMARY KEY (id),
    UNIQUE KEY uq_spot_categories_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='장소 카테고리 (Cafe / Food / Shopping / Culture / Nightlife)';

-- ────────────────────────────────────────────────────────────
-- 5. spots
--
-- 목적: K-SPOT 서비스의 핵심 장소 정보 관리
-- 설명: 지도 위 마커, 사이드바 랭킹 카드, 상세 카드의 데이터 원천이다.
--       카테고리는 spot_category_mappings 를 통해 N개를 가질 수 있다.
--       핀 수는 비정규화하지 않고 user_pins 를 집계해 산출한다.
--       is_trending 은 운영자가 직접 지정하는 강조 플래그다.
--       user_id / admin_id 중 하나만 세팅되며, 둘 다 NULL이면 배치 등록이다.
-- ────────────────────────────────────────────────────────────
CREATE TABLE spots (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '장소 고유 식별자 (PK)',
    name            VARCHAR(255)    NOT NULL                 COMMENT '장소명 (예: Seongsu Cafe Street)',
    spot_address_id BIGINT          NULL                     COMMENT '주소 분류 — spot_address.id 참조. NULL이면 미분류',
    description     TEXT            NULL                     COMMENT '장소 소개 문구 — 상세 카드 본문에 표시',
    image_url       VARCHAR(500)    NULL                     COMMENT '대표 이미지 URL — 마커 썸네일 및 카드 헤더에 사용',
    is_trending     TINYINT(1)      NOT NULL DEFAULT 0       COMMENT '트렌딩 강조 표시 여부 (0: 일반, 1: 트렌딩). 마커 테두리 색상 변경에 반영',
    user_id         BIGINT          NULL                     COMMENT '사용자 등록자 — users.id 참조. 사용자가 등록한 경우에만 세팅',
    admin_id        BIGINT          NULL                     COMMENT '어드민 등록자 — admins.id 참조. 어드민이 등록한 경우에만 세팅',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP             COMMENT '스팟 등록 일시',
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
                        ON UPDATE CURRENT_TIMESTAMP                                COMMENT '스팟 정보 최종 수정 일시',
    deleted_at      TIMESTAMP       NULL                                            COMMENT '스팟 삭제 일시 (soft delete)',
    PRIMARY KEY (id),
    KEY idx_spots_spot_address (spot_address_id),
    KEY idx_spots_trending     (is_trending),
    CONSTRAINT fk_spots_spot_address FOREIGN KEY (spot_address_id) REFERENCES spot_address (id),
    CONSTRAINT fk_spots_user         FOREIGN KEY (user_id)         REFERENCES users (id),
    CONSTRAINT fk_spots_admin        FOREIGN KEY (admin_id)        REFERENCES admins (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='K-SPOT 장소 (지도 마커 / 랭킹 카드 / 상세 카드 데이터 원천)';

-- ────────────────────────────────────────────────────────────
-- 6. spot_category_mappings
--
-- 목적: 장소와 카테고리의 M:N 연결 관계 관리
-- 설명: 하나의 스팟이 여러 카테고리에 속할 수 있고,
--       하나의 카테고리에 여러 스팟이 연결될 수 있다.
-- ────────────────────────────────────────────────────────────
CREATE TABLE spot_category_mappings (
    spot_id          BIGINT    NOT NULL  COMMENT '연결된 장소 — spots.id 참조',
    spot_category_id BIGINT    NOT NULL  COMMENT '연결된 카테고리 — spot_categories.id 참조',
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP             COMMENT '연결 생성 일시',
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                         ON UPDATE CURRENT_TIMESTAMP                          COMMENT '연결 수정 일시',
    deleted_at       TIMESTAMP NULL                                            COMMENT '연결 삭제 일시 (soft delete)',
    PRIMARY KEY (spot_id, spot_category_id),
    KEY idx_scm_spot_category (spot_category_id),
    CONSTRAINT fk_scm_spot          FOREIGN KEY (spot_id)          REFERENCES spots (id)           ON DELETE CASCADE,
    CONSTRAINT fk_scm_spot_category FOREIGN KEY (spot_category_id) REFERENCES spot_categories (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='장소 ↔ 카테고리 M:N 연결';

-- ────────────────────────────────────────────────────────────
-- 7. user_pin_categories
--
-- 목적: 사용자가 직접 정의하는 핀 카테고리 관리
-- 설명: 사용자가 자신의 핀(북마크)을 분류하는 커스텀 카테고리다.
--       user_pins.user_pin_category_id 로 연결된다.
-- ────────────────────────────────────────────────────────────
CREATE TABLE user_pin_categories (
    id         BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '핀 카테고리 고유 식별자 (PK)',
    user_id    BIGINT       NOT NULL                 COMMENT '카테고리를 소유한 사용자 — users.id 참조',
    name       VARCHAR(255) NOT NULL                 COMMENT '핀 카테고리명',
    sort_order INT          NOT NULL DEFAULT 0       COMMENT '카테고리 노출 순서 (오름차순 정렬)',
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP             COMMENT '등록 일시',
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
                   ON UPDATE CURRENT_TIMESTAMP                             COMMENT '최종 수정 일시',
    deleted_at TIMESTAMP    NULL                                            COMMENT '삭제 일시 (soft delete)',
    PRIMARY KEY (id),
    KEY idx_user_pin_categories_user (user_id),
    CONSTRAINT fk_user_pin_categories_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='사용자 커스텀 핀 카테고리';

-- ────────────────────────────────────────────────────────────
-- 8. user_pins
--
-- 목적: 사용자의 스팟 픽(북마크) 관계 관리
-- 설명: 사이드바 Pick 탭 및 프로필 "My Pins" 목록의 데이터 원천이다.
--       픽 수가 필요한 경우 이 테이블을 집계해 산출한다.
--       (user_id, spot_id) 복합 유니크 키로 중복 픽을 방지한다.
-- ────────────────────────────────────────────────────────────
CREATE TABLE user_pins (
    id                   BIGINT    NOT NULL AUTO_INCREMENT  COMMENT '픽 고유 식별자 (PK)',
    user_id              BIGINT    NOT NULL                 COMMENT '픽한 사용자 — users.id 참조',
    spot_id              BIGINT    NOT NULL                 COMMENT '픽된 장소 — spots.id 참조',
    user_pin_category_id BIGINT    NULL                     COMMENT '핀 카테고리 — user_pin_categories.id 참조. NULL이면 미분류',
    created_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP             COMMENT '픽 등록 일시',
    updated_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                             ON UPDATE CURRENT_TIMESTAMP                          COMMENT '픽 수정 일시',
    deleted_at           TIMESTAMP NULL                                            COMMENT '픽 삭제 일시 (soft delete)',
    PRIMARY KEY (id),
    UNIQUE KEY uq_user_pins (user_id, spot_id),
    KEY idx_user_pins_spot         (spot_id),
    KEY idx_user_pins_pin_category (user_pin_category_id),
    CONSTRAINT fk_user_pins_user         FOREIGN KEY (user_id)              REFERENCES users              (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_pins_spot         FOREIGN KEY (spot_id)              REFERENCES spots              (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_pins_pin_category FOREIGN KEY (user_pin_category_id) REFERENCES user_pin_categories(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='사용자 ↔ 장소 픽(북마크) 관계 (Pick 탭 / My Pins 데이터 원천)';

-- ────────────────────────────────────────────────────────────
-- 9. spot_hashtags
--
-- 목적: 지도 상단 가로 스크롤 필터 칩 관리
-- 설명: Hot Place, Rising, K-POP, Cafe, Food, Photo, Shopping,
--       Culture, Nightlife, Nature 등의 해시태그 필터를 관리한다.
--       spot_hashtag_mappings 를 통해 스팟과 M:N 관계를 맺는다.
-- ────────────────────────────────────────────────────────────
CREATE TABLE spot_hashtags (
    id         BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '해시태그 고유 식별자 (PK)',
    code       VARCHAR(50)  NOT NULL                 COMMENT '태그 코드 — 영문 소문자 (예: hot, rising, kpop, cafe). 프로그래밍 식별자로 사용',
    name       VARCHAR(100) NOT NULL                 COMMENT '태그 표시명 (예: Hot Place, Rising, K-POP)',
    icon       VARCHAR(50)  NULL                     COMMENT '태그 대표 아이콘 — 이모지 또는 아이콘 키 (예: 🔥, 🌟, 🎤)',
    sort_order INT          NOT NULL DEFAULT 0       COMMENT '필터 칩 노출 순서 (오름차순 정렬)',
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP             COMMENT '레코드 생성 일시',
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
                   ON UPDATE CURRENT_TIMESTAMP                             COMMENT '레코드 최종 수정 일시',
    deleted_at TIMESTAMP    NULL                                            COMMENT '삭제 일시 (soft delete)',
    PRIMARY KEY (id),
    UNIQUE KEY uq_spot_hashtags_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='지도 상단 해시태그 필터 (Hot Place / Rising / K-POP 등)';

-- ────────────────────────────────────────────────────────────
-- 10. spot_hashtag_mappings
--
-- 목적: 장소와 해시태그의 M:N 연결 관계 관리
-- 설명: 하나의 스팟이 여러 해시태그에 속할 수 있고,
--       하나의 태그에 여러 스팟이 연결될 수 있다.
--       필터 칩 선택 시 해당 태그에 연결된 스팟만 지도에 표시한다.
-- ────────────────────────────────────────────────────────────
CREATE TABLE spot_hashtag_mappings (
    spot_id         BIGINT    NOT NULL  COMMENT '연결된 장소 — spots.id 참조',
    spot_hashtag_id BIGINT    NOT NULL  COMMENT '연결된 해시태그 — spot_hashtags.id 참조',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP             COMMENT '연결 생성 일시',
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                        ON UPDATE CURRENT_TIMESTAMP                          COMMENT '연결 수정 일시',
    deleted_at      TIMESTAMP NULL                                            COMMENT '연결 삭제 일시 (soft delete)',
    PRIMARY KEY (spot_id, spot_hashtag_id),
    KEY idx_shm_spot_hashtag (spot_hashtag_id),
    CONSTRAINT fk_shm_spot         FOREIGN KEY (spot_id)         REFERENCES spots (id)         ON DELETE CASCADE,
    CONSTRAINT fk_shm_spot_hashtag FOREIGN KEY (spot_hashtag_id) REFERENCES spot_hashtags (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='장소 ↔ 해시태그 M:N 연결';
