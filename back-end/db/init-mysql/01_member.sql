USE member;

CREATE TABLE members (
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    kakao_id      VARCHAR(255) NOT NULL UNIQUE,
    name          VARCHAR(50)  NOT NULL,
    nickname      VARCHAR(50)  NOT NULL UNIQUE,
    gender        ENUM('MALE', 'FEMALE') DEFAULT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    profile_image VARCHAR(255) DEFAULT NULL,
    birthdate     DATE         NOT NULL,
    role          ENUM('GUEST', 'STUDENT', 'TEACHER', 'ADMIN') NOT NULL DEFAULT 'GUEST',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at    DATETIME     DEFAULT NULL
);

CREATE TABLE teachers (
    id                   BIGINT UNSIGNED PRIMARY KEY,
    description          TEXT DEFAULT NULL,
    career_description   TEXT DEFAULT NULL,
    class_contents       TEXT DEFAULT NULL,
    class_progress       TEXT DEFAULT NULL,
    total_class_count    INT NOT NULL DEFAULT 0,
    total_class_hours    INT NOT NULL DEFAULT 0,
    is_wanted            BOOLEAN NOT NULL DEFAULT TRUE,
    bank                 ENUM(
        'KB', 'SC', 'KYUNGNAM', 'GWANGJU', 'IBK', 'NONGHYUP', 'DAEGU', 'BUSAN',
        'KDB', 'SAEMAUL', 'SUHYUP', 'SHINHAN', 'SHINHYEOP', 'KEB', 'WOORI',
        'POST', 'JEONBUK', 'CHUKHYUP', 'KAKAO', 'KEBANK', 'HANA', 'CITI', 'TOSS'
    ) NOT NULL,
    account              VARCHAR(20) NOT NULL,
    price                INT NOT NULL DEFAULT 10000
);

CREATE TABLE students (
    id          BIGINT UNSIGNED PRIMARY KEY,
    is_exposed  BOOLEAN NOT NULL DEFAULT TRUE,
    description TEXT    DEFAULT NULL
);

CREATE TABLE category (
    id   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE member_category (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT UNSIGNED NOT NULL,
    category_id BIGINT UNSIGNED NOT NULL
);

CREATE TABLE likes (
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    liker      BIGINT UNSIGNED NOT NULL,
    likee      BIGINT UNSIGNED NOT NULL
);
