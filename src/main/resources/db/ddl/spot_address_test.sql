-- ============================================================
-- K-SPOT 테스트 데이터
-- spot_address 10건 → spots 10건 순으로 삽입
-- ============================================================

-- ── spot_address ──────────────────────────────────────────────
INSERT INTO spot_address (code, region, city, town, postal_code, address, address_detail, latitude, longitude)
VALUES
    ('seongsu',    '서울특별시', '성동구',   '성수동1가', '04783', '서울특별시 성동구 성수이로 78',     NULL, 37.5443941, 127.0556370),
    ('ikseon',     '서울특별시', '종로구',   '익선동',    '03148', '서울특별시 종로구 익선동 166-3',    NULL, 37.5748752, 126.9918011),
    ('yeonnam',    '서울특별시', '마포구',   '연남동',    '03992', '서울특별시 마포구 연남로 97',       NULL, 37.5661962, 126.9241309),
    ('hongdae',    '서울특별시', '마포구',   '서교동',    '04003', '서울특별시 마포구 와우산로 94',     NULL, 37.5563079, 126.9224714),
    ('hannam',     '서울특별시', '용산구',   '한남동',    '04417', '서울특별시 용산구 한남대로 42길 7', NULL, 37.5355292, 126.9988127),
    ('insadong',   '서울특별시', '종로구',   '관훈동',    '03145', '서울특별시 종로구 인사동길 44',     NULL, 37.5742802, 126.9857685),
    ('mangwon',    '서울특별시', '마포구',   '망원동',    '04074', '서울특별시 마포구 망원로 62',       NULL, 37.5549441, 126.9073898),
    ('apgujeong',  '서울특별시', '강남구',   '압구정동',  '06014', '서울특별시 강남구 압구정로 179',    NULL, 37.5273822, 127.0282270),
    ('gwangalli',  '부산광역시', '수영구',   '광안동',    '48309', '부산광역시 수영구 광안해변로 219',  NULL, 35.1529641, 129.1186178),
    ('hyeopjae',   '제주특별자치도', '제주시', '한림읍',  '63016', '제주특별자치도 제주시 한림읍 한림로 329', NULL, 33.3939847, 126.2393514);

-- ── spots ─────────────────────────────────────────────────────
INSERT INTO spots (name, spot_address_id, description, image_url, is_trending, user_id, admin_id)
VALUES
    ('성수 카페거리',
     (SELECT id FROM spot_address WHERE code = 'seongsu'),
     '뚝섬역 인근 감성 카페와 팝업 스토어가 밀집한 서울의 핫플레이스.',
     'https://example.com/images/seongsu.jpg', 1, NULL, NULL),

    ('익선동 한옥마을',
     (SELECT id FROM spot_address WHERE code = 'ikseon'),
     '조선시대 한옥을 개조한 카페·식당이 골목마다 들어선 레트로 감성 거리.',
     'https://example.com/images/ikseon.jpg', 1, NULL, NULL),

    ('연남동 경의선숲길',
     (SELECT id FROM spot_address WHERE code = 'yeonnam'),
     '폐철도 부지를 공원으로 탈바꿈한 산책로. 주변 카페와 음식점이 풍부하다.',
     'https://example.com/images/yeonnam.jpg', 0, NULL, NULL),

    ('홍대 걷고싶은거리',
     (SELECT id FROM spot_address WHERE code = 'hongdae'),
     '인디 문화와 클럽, 스트리트 패션이 공존하는 젊음의 거리.',
     'https://example.com/images/hongdae.jpg', 1, NULL, NULL),

    ('한남동 유엔빌리지',
     (SELECT id FROM spot_address WHERE code = 'hannam'),
     '고급 레스토랑과 편집숍이 늘어선 한강뷰 언덕 골목.',
     'https://example.com/images/hannam.jpg', 0, NULL, NULL),

    ('인사동 쌈지길',
     (SELECT id FROM spot_address WHERE code = 'insadong'),
     '전통 공예품·갤러리·전통 찻집이 나선형 통로를 따라 이어지는 문화 공간.',
     'https://example.com/images/insadong.jpg', 0, NULL, NULL),

    ('망원동 망리단길',
     (SELECT id FROM spot_address WHERE code = 'mangwon'),
     '한강 망원지구 인근 로컬 카페와 맛집이 밀집한 조용한 골목.',
     'https://example.com/images/mangwon.jpg', 0, NULL, NULL),

    ('압구정 로데오거리',
     (SELECT id FROM spot_address WHERE code = 'apgujeong'),
     '럭셔리 브랜드 편집숍과 뷰티 살롱이 집중된 강남 패션 1번지.',
     'https://example.com/images/apgujeong.jpg', 1, NULL, NULL),

    ('광안리해수욕장',
     (SELECT id FROM spot_address WHERE code = 'gwangalli'),
     '광안대교 야경이 한눈에 보이는 부산의 대표 해변. 해산물 식당가가 인접해 있다.',
     'https://example.com/images/gwangalli.jpg', 1, NULL, NULL),

    ('협재해변',
     (SELECT id FROM spot_address WHERE code = 'hyeopjae'),
     '에메랄드빛 바다와 비양도 전망이 어우러진 제주 서쪽 대표 해변.',
     'https://example.com/images/hyeopjae.jpg', 0, NULL, NULL);
