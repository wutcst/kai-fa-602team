package cn.edu.whut.sept.zuul.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private static final double DEFAULT_MAX_WEIGHT = 20.0;
    private static final double COOKIE_MAX_WEIGHT = 30.0;

    private Room startRoom;
    private Player player;

    @BeforeEach
    void setUp() {
        startRoom = new Room("start", "起始房间");
        player = new Player("测试玩家", startRoom, DEFAULT_MAX_WEIGHT);
    }

    // ==================== 基础 Getter/Setter 测试 ====================

    @Test
    void testGetName() {
        assertEquals("测试玩家", player.getName(), "玩家名称应正确返回");
    }

    @Test
    void testGetCurrentRoom_Initial() {
        assertEquals(startRoom, player.getCurrentRoom(), "初始房间应正确设置");
    }

    @Test
    void testSetCurrentRoom() {
        Room newRoom = new Room("new", "新房间");
        player.setCurrentRoom(newRoom);
        assertEquals(newRoom, player.getCurrentRoom(), "setCurrentRoom 后房间应改变");
    }

    @Test
    void testGetInventory_NotNull() {
        assertNotNull(player.getInventory(), "背包不应为 null");
    }

    @Test
    void testInventoryHasCorrectMaxWeight() {
        assertEquals(DEFAULT_MAX_WEIGHT, player.getInventory().getMaxWeight(),
                "背包最大重量应与构造参数一致");
    }

    // ==================== 物品拾取测试（修正版） ====================

    @Test
    void testPlayerCanPickupItemWithinWeightLimit() {
        Item lightItem = new Item("小石头", "一块小石头", 1.0);
        boolean added = player.getInventory().add(lightItem);  // 注意是 add() 不是 addItem()

        assertTrue(added, "负重未超限时应能添加物品");
        assertEquals(1.0, player.getInventory().getTotalWeight(), "当前负重应正确");
        assertTrue(player.getInventory().getItems().contains(lightItem), "物品应在背包中");
    }

    @Test
    void testPlayerCannotPickupItemExceedingWeightLimit() {
        // 默认最大负重 20.0，创建一个超过限制的物品
        Item heavyItem = new Item("大石头", "一块大石头", 25.0);
        boolean added = player.getInventory().add(heavyItem);  // 注意是 add() 不是 addItem()

        assertFalse(added, "超过负重限制时应无法添加物品");
        assertFalse(player.getInventory().getItems().contains(heavyItem), "超过负重限制的物品不应在背包中");
    }

    @Test
    void testPickupAndDropItemsAffectWeight() {
        Item item1 = new Item("物品1", "测试物品", 5.0);
        Item item2 = new Item("物品2", "测试物品", 8.0);

        player.getInventory().add(item1);
        player.getInventory().add(item2);

        assertEquals(13.0, player.getInventory().getTotalWeight(), "总负重应为 13.0");

        player.getInventory().remove("物品1");
        assertEquals(8.0, player.getInventory().getTotalWeight(), "移除后负重应为 8.0");
    }

    @Test
    void testCanAddMethodChecksWeightLimit() {
        Item lightItem = new Item("小石头", "一块小石头", 1.0);
        Item heavyItem = new Item("大石头", "一块大石头", 25.0);

        assertTrue(player.getInventory().canAdd(lightItem), "轻物品应可添加");
        assertFalse(player.getInventory().canAdd(heavyItem), "重物品应不可添加");
    }

    // ==================== 魔法饼干测试 ====================

    @Test
    void testHasEatenMagicCookie_InitiallyFalse() {
        assertFalse(player.hasEatenMagicCookie(), "初始状态应未吃过魔法饼干");
    }

    @Test
    void testEatMagicCookie_SetsFlagTrue() {
        player.eatMagicCookie(COOKIE_MAX_WEIGHT);
        assertTrue(player.hasEatenMagicCookie(), "吃下魔法饼干后标志应为 true");
    }

    @Test
    void testEatMagicCookie_IncreasesMaxWeight() {
        assertEquals(DEFAULT_MAX_WEIGHT, player.getInventory().getMaxWeight(),
                "初始最大负重应为 " + DEFAULT_MAX_WEIGHT);

        player.eatMagicCookie(COOKIE_MAX_WEIGHT);

        assertEquals(COOKIE_MAX_WEIGHT, player.getInventory().getMaxWeight(),
                "吃下魔法饼干后最大负重应增加到 " + COOKIE_MAX_WEIGHT);
    }

    @Test
    void testEatCookieThenPickupMoreItems() {
        // 初始最大负重 20，添加一个 18 重量的物品
        Item heavyItem = new Item("重物", "很重的物品", 18.0);
        assertTrue(player.getInventory().add(heavyItem), "18.0 <= 20.0 应能添加");

        // 再添加一个 3.0 的物品会超重
        Item extraItem = new Item("额外", "额外物品", 3.0);
        assertFalse(player.getInventory().add(extraItem), "18+3=21 > 20 应无法添加");

        // 吃下魔法饼干，负重上限增加到 30
        player.eatMagicCookie(30.0);

        // 现在可以添加了
        assertTrue(player.getInventory().add(extraItem), "吃饼干后负重上限增加，应能添加");
        assertEquals(21.0, player.getInventory().getTotalWeight(), "总负重应为 21.0");
    }

    // ==================== restoreCarryState 测试 ====================

    @Test
    void testRestoreCarryState_RestoresMaxWeightAndCookieFlag() {
        // 先改变状态
        player.eatMagicCookie(COOKIE_MAX_WEIGHT);
        assertTrue(player.hasEatenMagicCookie());
        assertEquals(COOKIE_MAX_WEIGHT, player.getInventory().getMaxWeight());

        // 模拟从存档恢复
        player.restoreCarryState(DEFAULT_MAX_WEIGHT, false);

        assertFalse(player.hasEatenMagicCookie(), "恢复后标志应为 false");
        assertEquals(DEFAULT_MAX_WEIGHT, player.getInventory().getMaxWeight(),
                "恢复后最大负重应为 " + DEFAULT_MAX_WEIGHT);
    }

    @Test
    void testRestoreCarryState_WithCookieEatenTrue() {
        player.restoreCarryState(COOKIE_MAX_WEIGHT, true);

        assertTrue(player.hasEatenMagicCookie(), "恢复后标志应为 true");
        assertEquals(COOKIE_MAX_WEIGHT, player.getInventory().getMaxWeight(),
                "恢复后最大负重应为 " + COOKIE_MAX_WEIGHT);
    }

    // ==================== 边界条件测试 ====================

    @Test
    void testPlayerWithZeroMaxWeight() {
        Player zeroWeightPlayer = new Player("瘦弱玩家", startRoom, 0.0);
        Item item = new Item("羽毛", "轻如羽毛", 0.1);

        boolean added = zeroWeightPlayer.getInventory().add(item);
        assertFalse(added, "最大负重为 0 时不应能添加任何物品");

        assertFalse(zeroWeightPlayer.getInventory().canAdd(item), "canAdd 应返回 false");
    }

    @Test
    void testMultiplePlayersHaveIndependentState() {
        Player player1 = new Player("玩家1", startRoom, 10.0);
        Player player2 = new Player("玩家2", startRoom, 20.0);

        Item item = new Item("共享物品", "测试", 5.0);

        player1.getInventory().add(item);
        assertTrue(player1.getInventory().getItems().contains(item), "玩家1 应有物品");
        assertFalse(player2.getInventory().getItems().contains(item), "玩家2 不应有物品");

        player2.eatMagicCookie(30.0);
        assertTrue(player2.hasEatenMagicCookie());
        assertFalse(player1.hasEatenMagicCookie(), "玩家1 不应受玩家2 吃饼干影响");
    }

    @Test
    void testInventoryDescribe_Empty() {
        String description = player.getInventory().describe();
        assertEquals("（空）", description, "空背包描述应为（空）");
    }

    @Test
    void testInventoryDescribe_WithItems() {
        player.getInventory().add(new Item("测试物品", "描述", 5.0));
        String description = player.getInventory().describe();

        assertTrue(description.contains("测试物品"), "描述应包含物品名");
        assertTrue(description.contains("5.0"), "描述应包含重量");
        assertTrue(description.contains("20.0"), "描述应包含最大负重");
    }
}