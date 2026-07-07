-- spot_categories 계층 구조 지원을 위한 parent_id 컬럼 추가
ALTER TABLE spot_categories
    ADD COLUMN parent_id BIGINT NULL COMMENT '상위 카테고리 — spot_categories.id 자기참조. NULL이면 1단계(루트)'
        AFTER id,
    ADD KEY idx_spot_categories_parent (parent_id),
    ADD CONSTRAINT fk_spot_categories_parent
        FOREIGN KEY (parent_id) REFERENCES spot_categories (id);
