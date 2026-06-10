-- 物品模板表：读档时根据名称还原完整 Item 对象
CREATE TABLE IF NOT EXISTS item_catalog (
    item_name       VARCHAR(64) PRIMARY KEY,
    description     VARCHAR(255) NOT NULL,
    weight          DOUBLE NOT NULL,
    magic_cookie    BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS game_save (
    slot_id           VARCHAR(64) PRIMARY KEY,
    player_name       VARCHAR(64) NOT NULL,
    current_room_id   VARCHAR(64) NOT NULL,
    max_carry_weight  DOUBLE NOT NULL,
    ate_magic_cookie  BOOLEAN NOT NULL DEFAULT FALSE,
    saved_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS save_carried_item (
    slot_id     VARCHAR(64) NOT NULL,
    item_name   VARCHAR(64) NOT NULL,
    sort_order  INT NOT NULL,
    PRIMARY KEY (slot_id, sort_order),
    FOREIGN KEY (slot_id) REFERENCES game_save(slot_id) ON DELETE CASCADE,
    FOREIGN KEY (item_name) REFERENCES item_catalog(item_name)
);

CREATE TABLE IF NOT EXISTS save_room_item (
    slot_id     VARCHAR(64) NOT NULL,
    room_id     VARCHAR(64) NOT NULL,
    item_name   VARCHAR(64) NOT NULL,
    sort_order  INT NOT NULL,
    PRIMARY KEY (slot_id, room_id, sort_order),
    FOREIGN KEY (slot_id) REFERENCES game_save(slot_id) ON DELETE CASCADE,
    FOREIGN KEY (item_name) REFERENCES item_catalog(item_name)
);

-- 用户设置（与游戏引擎解耦）
CREATE TABLE IF NOT EXISTS user_settings (
    settings_key        VARCHAR(64) PRIMARY KEY,
    language            VARCHAR(16) NOT NULL DEFAULT 'zh_CN',
    default_player_name VARCHAR(64) NOT NULL DEFAULT '探险者',
    sound_enabled       BOOLEAN NOT NULL DEFAULT TRUE
);
