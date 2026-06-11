package cn.edu.whut.sept.zuul.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WorldTest {

    private World world;

    @BeforeEach
    void setUp() {
        world = new World();
    }

    @Test
    void testGetStartRoomNotNull() {
        Room startRoom = world.getStartRoom();
        assertNotNull(startRoom, "起始房间不应为 null");
    }

    @Test
    void testGetStartRoomHasExits() {
        Room startRoom = world.getStartRoom();
        Set<String> exitDirections = startRoom.getExitDirections();
        assertFalse(exitDirections.isEmpty(), "起始房间应至少有一个出口");
    }

    @Test
    void testGetRoomsReturnsAllRooms() {
        List<Room> rooms = world.getRooms();
        assertEquals(7, rooms.size(), "应包含 7 个房间");
    }

    @Test
    void testGetRoomsIsUnmodifiable() {
        List<Room> rooms = world.getRooms();
        assertThrows(UnsupportedOperationException.class, () -> rooms.add(new Room("extra", "额外房间")));
    }

    @Test
    void testGetRandomRoomExceptCurrentExcludesCurrent() {
        Room current = world.getStartRoom();
        Room randomRoom = world.getRandomRoomExcept(current);
        assertNotEquals(current, randomRoom, "随机房间不应是当前房间");
    }

    @RepeatedTest(10)
    void testGetRandomRoomExceptCurrentWorksWhenMultipleRooms() {
        List<Room> allRooms = world.getRooms();
        Room current = allRooms.get(0);
        Room randomRoom = world.getRandomRoomExcept(current);
        assertNotNull(randomRoom);
        assertNotEquals(current, randomRoom);
        assertTrue(allRooms.contains(randomRoom));
    }

    @Test
    void testTeleportRoomHasTeleportType() {
        List<Room> rooms = world.getRooms();
        Room teleportRoom = rooms.stream()
                .filter(r -> "teleport".equals(r.getId()))
                .findFirst()
                .orElse(null);
        assertNotNull(teleportRoom, "应存在传送房间 (id=teleport)");
        assertEquals(RoomType.TELEPORT, teleportRoom.getRoomType(), "传送房间类型应为 TELEPORT");
    }

    @Test
    void testMagicCookieExistsInOneOfRooms() {
        List<Room> rooms = world.getRooms();
        boolean cookieFound = rooms.stream()
                .flatMap(room -> room.getItems().stream())
                .anyMatch(item -> "魔法饼干".equals(item.getName()) && item.isMagicCookie());
        assertTrue(cookieFound, "魔法饼干应存在于某个房间中");
    }

    @Test
    void testStartRoomExitsNavigation() {
        Room start = world.getStartRoom();

        // 测试四个方向出口都存在
        assertNotNull(start.getExit("东"), "起始房间应有东出口");
        assertNotNull(start.getExit("南"), "起始房间应有南出口");
        assertNotNull(start.getExit("西"), "起始房间应有西出口");
        assertNotNull(start.getExit("北"), "起始房间应有北出口");

        // 验证出口房间类型正确
        Room eastRoom = start.getExit("东");
        assertEquals("theater", eastRoom.getId(), "东边应是剧院");

        Room southRoom = start.getExit("南");
        assertEquals("lab", southRoom.getId(), "南边应是实验室");

        Room westRoom = start.getExit("西");
        assertEquals("pub", westRoom.getId(), "西边应是酒吧");

        Room northRoom = start.getExit("北");
        assertEquals("teleport", northRoom.getId(), "北边应是传送大厅");
    }

    @Test
    void testStorageAndPubConnection() {
        Room start = world.getStartRoom();
        Room pub = start.getExit("西");
        assertNotNull(pub, "酒吧应存在");

        Room storage = pub.getExit("北");
        assertNotNull(storage, "酒吧北边应是仓库");
        assertEquals("storage", storage.getId(), "北边应是仓库");

        // 验证仓库南边返回酒吧
        Room backToPub = storage.getExit("南");
        assertNotNull(backToPub, "仓库南边应返回酒吧");
        assertEquals("pub", backToPub.getId(), "仓库南边应是酒吧");
    }

    @Test
    void testLabAndOfficeConnection() {
        Room start = world.getStartRoom();
        Room lab = start.getExit("南");
        assertNotNull(lab, "实验室应存在");

        Room office = lab.getExit("东");
        assertNotNull(office, "实验室东边应是办公室");
        assertEquals("office", office.getId(), "实验室东边应是办公室");

        // 验证办公室西边返回实验室
        Room backToLab = office.getExit("西");
        assertNotNull(backToLab, "办公室西边应返回实验室");
        assertEquals("lab", backToLab.getId(), "办公室西边应是实验室");
    }

    @Test
    void testAllRoomsHaveUniqueIds() {
        List<Room> rooms = world.getRooms();
        long distinctCount = rooms.stream().map(Room::getId).distinct().count();
        assertEquals(rooms.size(), distinctCount, "所有房间应有唯一的 ID");
    }

    @Test
    void testNoDuplicateExitDirections() {
        List<Room> rooms = world.getRooms();
        for (Room room : rooms) {
            Set<String> directions = room.getExitDirections();
            assertEquals(directions.size(), directions.stream().distinct().count(),
                    "房间 " + room.getId() + " 的出口方向不应重复");
        }
    }

    @Test
    void testItemMagicCookieMethodExists() {
        List<Room> rooms = world.getRooms();
        // 遍历所有物品，确保魔法饼干调用正确的方法
        for (Room room : rooms) {
            for (Item item : room.getItems()) {
                if ("魔法饼干".equals(item.getName())) {
                    assertTrue(item.isMagicCookie(), "魔法饼干的 isMagicCookie() 应返回 true");
                }
            }
        }
    }

    @Test
    void testNonMagicItemsReturnFalse() {
        List<Room> rooms = world.getRooms();
        for (Room room : rooms) {
            for (Item item : room.getItems()) {
                if (!"魔法饼干".equals(item.getName())) {
                    assertFalse(item.isMagicCookie(),
                            "非魔法饼干物品 " + item.getName() + " 的 isMagicCookie() 应返回 false");
                }
            }
        }
    }

    @Test
    void testItemWeightsArePositive() {
        List<Room> rooms = world.getRooms();
        for (Room room : rooms) {
            for (Item item : room.getItems()) {
                assertTrue(item.getWeight() > 0,
                        "物品 " + item.getName() + " 的重量应大于 0");
            }
        }
    }
}