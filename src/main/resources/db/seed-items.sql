MERGE INTO item_catalog (item_name, description, weight, magic_cookie) KEY(item_name)
VALUES ('讲义', '一叠泛黄的课程讲义', 1.2, FALSE);
MERGE INTO item_catalog (item_name, description, weight, magic_cookie) KEY(item_name)
VALUES ('键盘', '机械键盘，沉甸甸的', 2.5, FALSE);
MERGE INTO item_catalog (item_name, description, weight, magic_cookie) KEY(item_name)
VALUES ('啤酒杯', '沾着泡沫的酒杯', 0.8, FALSE);
MERGE INTO item_catalog (item_name, description, weight, magic_cookie) KEY(item_name)
VALUES ('钥匙', '管理员办公室备用钥匙', 0.3, FALSE);
MERGE INTO item_catalog (item_name, description, weight, magic_cookie) KEY(item_name)
VALUES ('木箱', '装满旧书的木箱', 4.0, FALSE);
MERGE INTO item_catalog (item_name, description, weight, magic_cookie) KEY(item_name)
VALUES ('魔法饼干', '散发着微光的饼干，吃了能增强负重', 0.5, TRUE);

MERGE INTO user_settings (settings_key, language, default_player_name, sound_enabled) KEY(settings_key)
VALUES ('default', 'zh_CN', '探险者', TRUE);
