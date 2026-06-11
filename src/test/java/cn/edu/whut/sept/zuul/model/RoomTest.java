package cn.edu.whut.sept.zuul.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class RoomTest {

    private Room currentRoom;
    private Room nextRoom;
    private Item cookie;
    private Item key;

    @BeforeEach
    public void setUp() {
        // 1. 初始化测试房间
        currentRoom = new Room("lab", "计算机软件工程实验室");
        nextRoom = new Room("theater", "一号大阶梯报告厅");

        // 2. 准备测试用的物品
        cookie = new Item("cookie", "魔法饼干", 2.0);
        key = new Item("key", "一把沉甸甸的钥匙", 1.5);
    }

    @Test
    public void testGetIdAndDescription() {
        assertEquals("lab", currentRoom.getId());
        assertEquals("计算机软件工程实验室", currentRoom.getDescription());
    }

    @Test
    public void testSetAndGetRoomType() {
        // 验证默认类型为 NORMAL
        assertEquals(RoomType.NORMAL, currentRoom.getRoomType());

        // 验证修改类型（比如修改为传送大厅类型）
        currentRoom.setRoomType(RoomType.TELEPORT);
        assertEquals(RoomType.TELEPORT, currentRoom.getRoomType());
    }

    @Test
    public void testSetAndGetExit() {
        // 动作：为当前房间设置向东的出口连接到 nextRoom
        currentRoom.setExit("东", nextRoom);

        // 断言：往东走应该到达 nextRoom
        Room result = currentRoom.getExit("东");
        assertNotNull(result, "对应的出口应当存在");
        assertEquals("theater", result.getId(), "往东走应该到达正确的目标房间");

        // 验证方向去空格处理（normalizeDirection 健壮性测试）
        assertEquals("theater", currentRoom.getExit(" 东 ").getId(), "传入带空格的方向也应该正确识别");

        // 验证不存在的出口或传入 null 时返回 null
        assertNull(currentRoom.getExit("西"), "不存在该出口时应该返回 null");
        assertNull(currentRoom.getExit(null), "传入 null 方向时应该返回 null");
    }

    @Test
    public void testGetExitDirections() {
        currentRoom.setExit("东", nextRoom);
        currentRoom.setExit("北", new Room("pub", "小酒吧"));

        Set<String> directions = currentRoom.getExitDirections();

        // 断言：应当包含两个方向
        assertEquals(2, directions.size());
        assertTrue(directions.contains("东"));
        assertTrue(directions.contains("北"));
        assertFalse(directions.contains("南"));
    }

    @Test
    public void testAddAndGetItems() {
        // 动作：放入两个物品
        currentRoom.addItem(cookie);
        currentRoom.addItem(key);

        List<Item> items = currentRoom.getItems();

        // 断言验证
        assertEquals(2, items.size());
        assertEquals(3.5, currentRoom.getItemsTotalWeight(), 0.001, "房间物品总重量计算应正确");
    }

    @Test
    public void testRemoveItemSuccess() {
        // 1. 放入物品
        currentRoom.addItem(cookie);

        // 2. 动作：移除存在的物品（测试大小写模糊匹配）
        Optional<Item> removedOpt = currentRoom.removeItem("CoOkIe");

        // 3. 断言
        assertTrue(removedOpt.isPresent());
        assertEquals("cookie", removedOpt.get().getName());
        assertTrue(currentRoom.getItems().isEmpty(), "移除成功后房间应当不再包含该物品");
        assertEquals(0.0, currentRoom.getItemsTotalWeight(), 0.001);
    }

    @Test
    public void testRemoveNonExistentItemShouldReturnEmpty() {
        Optional<Item> removedOpt = currentRoom.removeItem("ghost_item");
        assertTrue(removedOpt.isEmpty(), "移除不存在的物品应该优雅地返回空Optional");
    }

    @Test
    public void testFindItem() {
        currentRoom.addItem(key);

        // 查找存在的物品
        Optional<Item> foundOpt = currentRoom.findItem("KEY");
        assertTrue(foundOpt.isPresent());
        assertEquals("key", foundOpt.get().getName());

        // 查找不存在的物品
        assertTrue(currentRoom.findItem("cookie").isEmpty());
    }

    @Test
    public void testClearItems() {
        currentRoom.addItem(cookie);
        currentRoom.addItem(key);
        assertEquals(2, currentRoom.getItems().size());

        // 动作：一键清空房间物品
        currentRoom.clearItems();

        assertTrue(currentRoom.getItems().isEmpty(), "调用clearItems后房间应当被彻底清空");
        assertEquals(0.0, currentRoom.getItemsTotalWeight(), 0.001);
    }

    @Test
    public void testGetExitStringAndLongDescription() {
        // 1. 验证没有任何出口和物品时的基本输出
        assertTrue(currentRoom.getExitString().contains("无出口"));
        assertTrue(currentRoom.getLongDescription().contains("房间内没有物品"));

        // 2. 挂载出口和物品
        currentRoom.setExit("东", nextRoom);
        currentRoom.addItem(cookie);

        String longDescription = currentRoom.getLongDescription();

        // 3. 验证复合拼接出的长描述信息是否包含关键要素
        assertAll("验证长描述信息包含完整的空间和物品特征",
                () -> assertTrue(longDescription.contains("计算机软件工程实验室"), "描述中应包含房间本身的名字"),
                () -> assertTrue(longDescription.contains("东"), "描述中应包含有效出口"),
                () -> assertTrue(longDescription.contains("cookie"), "描述中应包含房间内的物品信息"),
                () -> assertTrue(longDescription.contains("2.0"), "描述中应包含物品的总重量")
        );
    }
}