package cn.edu.whut.sept.zuul.engine;

import cn.edu.whut.sept.zuul.model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class RoomHistoryTest {

    private RoomHistory roomHistory;
    private Room room1;
    private Room room2;
    private Room room3;

    @BeforeEach
    void setUp() {
        roomHistory = new RoomHistory();
        room1 = new Room("room1", "第一个房间");
        room2 = new Room("room2", "第二个房间");
        room3 = new Room("room3", "第三个房间");
    }

    // ==================== push 和 pop 基础测试 ====================

    @Test
    void testPushAndPop() {
        roomHistory.push(room1);
        roomHistory.push(room2);

        Room popped = roomHistory.pop();
        assertEquals(room2, popped, "pop 应返回最后 push 的房间 (LIFO)");

        popped = roomHistory.pop();
        assertEquals(room1, popped, "第二次 pop 应返回第一个房间");
    }

    @Test
    void testPushSingleRoom() {
        roomHistory.push(room1);

        assertEquals(room1, roomHistory.pop(), "push 一个房间后 pop 应返回该房间");
    }

    @Test
    void testPushNullRoom() {
        // ArrayDeque 不允许 push null，会抛出 NullPointerException
        assertThrows(NullPointerException.class,
                () -> roomHistory.push(null),
                "push null 应抛出 NullPointerException");

        // 验证历史仍然为空
        assertFalse(roomHistory.canGoBack());
    }

    // ==================== canGoBack 测试 ====================

    @Test
    void testCanGoBack_InitiallyFalse() {
        assertFalse(roomHistory.canGoBack(), "初始状态不应能返回");
    }

    @Test
    void testCanGoBack_AfterPush() {
        roomHistory.push(room1);
        assertTrue(roomHistory.canGoBack(), "push 后应能返回");
    }

    @Test
    void testCanGoBack_AfterPushAndPop() {
        roomHistory.push(room1);
        roomHistory.pop();
        assertFalse(roomHistory.canGoBack(), "pop 所有房间后应不能返回");
    }

    @Test
    void testCanGoBack_AfterMultiplePushes() {
        roomHistory.push(room1);
        roomHistory.push(room2);
        roomHistory.push(room3);

        assertTrue(roomHistory.canGoBack(), "有多个房间时应能返回");

        roomHistory.pop();
        assertTrue(roomHistory.canGoBack(), "还有剩余房间时应能返回");

        roomHistory.pop();
        assertTrue(roomHistory.canGoBack(), "还有剩余房间时应能返回");

        roomHistory.pop();
        assertFalse(roomHistory.canGoBack(), "pop 所有房间后应不能返回");
    }

    @Test
    void testCanGoBack_AfterClear() {
        roomHistory.push(room1);
        roomHistory.push(room2);
        roomHistory.clear();

        assertFalse(roomHistory.canGoBack(), "clear 后应不能返回");
    }

    // ==================== pop 异常测试 ====================

    @Test
    void testPopOnEmptyHistoryThrowsException() {
        assertThrows(NoSuchElementException.class,
                () -> roomHistory.pop(),
                "从空历史 pop 应抛出 NoSuchElementException");
    }

    @Test
    void testPopOnEmptyHistoryAfterOperations() {
        roomHistory.push(room1);
        roomHistory.pop();

        assertThrows(NoSuchElementException.class,
                () -> roomHistory.pop(),
                "从空历史 pop 应抛出 NoSuchElementException");
    }

    // ==================== clear 测试 ====================

    @Test
    void testClear() {
        roomHistory.push(room1);
        roomHistory.push(room2);
        roomHistory.push(room3);

        assertTrue(roomHistory.canGoBack(), "clear 前应能返回");

        roomHistory.clear();

        assertFalse(roomHistory.canGoBack(), "clear 后应不能返回");
        assertThrows(NoSuchElementException.class,
                () -> roomHistory.pop(),
                "clear 后 pop 应抛出异常");
    }

    @Test
    void testClearOnEmptyHistory() {
        assertDoesNotThrow(() -> roomHistory.clear(), "清空空历史不应抛异常");
        assertFalse(roomHistory.canGoBack());
    }

    @Test
    void testClearMultipleTimes() {
        roomHistory.push(room1);
        roomHistory.clear();
        roomHistory.clear();

        assertFalse(roomHistory.canGoBack());
        assertThrows(NoSuchElementException.class, () -> roomHistory.pop());
    }

    // ==================== LIFO 顺序测试 ====================

    @Test
    void testLifoOrder() {
        roomHistory.push(room1);
        roomHistory.push(room2);
        roomHistory.push(room3);

        assertEquals(room3, roomHistory.pop(), "第一个 pop 应返回 room3");
        assertEquals(room2, roomHistory.pop(), "第二个 pop 应返回 room2");
        assertEquals(room1, roomHistory.pop(), "第三个 pop 应返回 room1");
    }

    @Test
    void testPushPopInterleaved() {
        roomHistory.push(room1);
        roomHistory.push(room2);

        assertEquals(room2, roomHistory.pop(), "pop 应返回 room2");

        roomHistory.push(room3);

        assertEquals(room3, roomHistory.pop(), "pop 应返回 room3");
        assertEquals(room1, roomHistory.pop(), "pop 应返回 room1");
    }

    // ==================== 与游戏逻辑结合的测试 ====================

    @Test
    void testTypicalGameNavigation() {
        // 修正：模拟玩家移动的正确方式
        // 初始在起始房间（假设是 outside）

        // 移动到 room1：记录上一个房间（起始房间）
        roomHistory.push(room1);  // 记录 room1 作为上一个房间
        // 当前在 room2

        // 移动到 room2：记录 room1
        roomHistory.push(room2);
        // 当前在 room3

        // 玩家执行 back 命令，应该回到 room2（上一个房间）
        assertTrue(roomHistory.canGoBack());
        Room previousRoom = roomHistory.pop();  // 应返回 room2
        assertEquals(room2, previousRoom);

        // 再次 back，应该回到 room1
        previousRoom = roomHistory.pop();  // 应返回 room1
        assertEquals(room1, previousRoom);

        // 不能再 back
        assertFalse(roomHistory.canGoBack());
    }

    @Test
    void testHistoryDoesNotContainCurrentRoom() {
        // history 应该记录上一个房间
        // 当前在 room1，移动到 room2 时记录 room1
        roomHistory.push(room1);  // 记录 room1
        // 现在当前房间是 room2

        assertTrue(roomHistory.canGoBack());
        assertEquals(room1, roomHistory.pop());
    }

    @Test
    void testMultipleBacks() {
        roomHistory.push(room1);
        roomHistory.push(room2);
        roomHistory.push(room3);

        assertEquals(room3, roomHistory.pop());
        assertEquals(room2, roomHistory.pop());
        assertEquals(room1, roomHistory.pop());

        assertFalse(roomHistory.canGoBack());
    }

    // ==================== 边界条件测试 ====================

    @Test
    void testPushAndPopManyRooms() {
        int numRooms = 100;
        Room[] rooms = new Room[numRooms];

        for (int i = 0; i < numRooms; i++) {
            rooms[i] = new Room("room" + i, "房间 " + i);
            roomHistory.push(rooms[i]);
        }

        assertTrue(roomHistory.canGoBack());

        for (int i = numRooms - 1; i >= 0; i--) {
            assertEquals(rooms[i], roomHistory.pop(),
                    "pop 顺序应为 push 顺序的逆序，索引 " + i);
        }

        assertFalse(roomHistory.canGoBack());
    }

    @Test
    void testPushSameRoomMultipleTimes() {
        roomHistory.push(room1);
        roomHistory.push(room1);
        roomHistory.push(room1);

        assertTrue(roomHistory.canGoBack());

        assertEquals(room1, roomHistory.pop());
        assertEquals(room1, roomHistory.pop());
        assertEquals(room1, roomHistory.pop());

        assertFalse(roomHistory.canGoBack());
    }

    @Test
    void testHistoryIndependence() {
        RoomHistory history1 = new RoomHistory();
        RoomHistory history2 = new RoomHistory();

        history1.push(room1);
        history2.push(room2);

        assertTrue(history1.canGoBack());
        assertTrue(history2.canGoBack());

        assertEquals(room1, history1.pop());
        assertEquals(room2, history2.pop());

        assertFalse(history1.canGoBack());
        assertFalse(history2.canGoBack());
    }

    // ==================== 额外场景测试 ====================

    @Test
    void testBackFromStartingRoom() {
        // 从起始房间，没有历史，back 应该无效
        assertFalse(roomHistory.canGoBack());

        // 移动到第一个房间后，记录起始房间
        roomHistory.push(room1);

        // 可以返回起始房间
        assertTrue(roomHistory.canGoBack());
        assertEquals(room1, roomHistory.pop());
    }

    @Test
    void testHistorySize() {
        // 通过反射或间接验证历史大小
        roomHistory.push(room1);
        roomHistory.push(room2);
        roomHistory.push(room3);

        // pop 两次后应该还剩一个
        roomHistory.pop();
        roomHistory.pop();

        assertTrue(roomHistory.canGoBack());
        assertEquals(room1, roomHistory.pop());
        assertFalse(roomHistory.canGoBack());
    }
}