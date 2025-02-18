USE payment;

CREATE TABLE orders
(
    id                  BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id           BIGINT      NOT NULL,
    coupon_id           BIGINT      DEFAULT NULL,
    contracted_class_id BIGINT      NOT NULL,
    count               INT         NOT NULL,
    amount              DECIMAL(10,2) NOT NULL,
    merchant_uid        VARCHAR(50) NOT NULL,
    imp_uid             VARCHAR(50) DEFAULT NULL,
    status              ENUM('PAID', 'CANCELLED') NOT NULL DEFAULT 'PAID',
    created_at          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at          DATETIME    DEFAULT NULL
);