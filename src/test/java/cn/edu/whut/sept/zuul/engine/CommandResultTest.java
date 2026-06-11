package cn.edu.whut.sept.zuul.engine;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandResultTest {

    // ==================== ongoing(String) 测试 ====================

    @Test
    void testOngoingWithSingleMessage() {
        CommandResult result = CommandResult.ongoing("游戏进行中");

        assertFalse(result.isFinished(), "ongoing 不应结束游戏");
        assertTrue(result.isStateChanged(), "ongoing 应该标记状态已改变");

        List<String> messages = result.getMessages();
        assertEquals(1, messages.size(), "应包含 1 条消息");
        assertEquals("游戏进行中", messages.get(0), "消息内容应正确");

        String combined = result.getMessageText();
        assertEquals("游戏进行中", combined, "合并消息应正确");
    }

    @Test
    void testOngoingWithNullMessage() {
        CommandResult result = CommandResult.ongoing((String) null);

        assertFalse(result.isFinished());
        assertTrue(result.isStateChanged());

        List<String> messages = result.getMessages();
        assertEquals(1, messages.size());
        assertNull(messages.get(0));
    }

    @Test
    void testOngoingWithEmptyString() {
        CommandResult result = CommandResult.ongoing("");

        List<String> messages = result.getMessages();
        assertEquals(1, messages.size());
        assertEquals("", messages.get(0));
    }

    // ==================== ongoing(List<String>) 测试 ====================

    @Test
    void testOngoingWithMultipleMessages() {
        List<String> inputMessages = Arrays.asList("第一行", "第二行", "第三行");
        CommandResult result = CommandResult.ongoing(inputMessages);

        assertFalse(result.isFinished());
        assertTrue(result.isStateChanged());

        List<String> messages = result.getMessages();
        assertEquals(3, messages.size());
        assertEquals("第一行", messages.get(0));
        assertEquals("第二行", messages.get(1));
        assertEquals("第三行", messages.get(2));

        String combined = result.getMessageText();
        assertEquals("第一行\n第二行\n第三行", combined);
    }

    @Test
    void testOngoingWithEmptyList() {
        CommandResult result = CommandResult.ongoing(Collections.emptyList());

        assertFalse(result.isFinished());
        assertTrue(result.isStateChanged());

        List<String> messages = result.getMessages();
        assertTrue(messages.isEmpty());
        assertEquals("", result.getMessageText());
    }

    @Test
    void testOngoingWithSingletonList() {
        List<String> inputMessages = Collections.singletonList("单条消息");
        CommandResult result = CommandResult.ongoing(inputMessages);

        assertEquals(1, result.getMessages().size());
        assertEquals("单条消息", result.getMessageText());
    }

    @Test
    void testOngoingWithListContainingNull() {
        List<String> inputMessages = Arrays.asList("正常", null, "消息");
        CommandResult result = CommandResult.ongoing(inputMessages);

        List<String> messages = result.getMessages();
        assertEquals(3, messages.size());
        assertEquals("正常", messages.get(0));
        assertNull(messages.get(1));
        assertEquals("消息", messages.get(2));

        assertEquals("正常\nnull\n消息", result.getMessageText());
    }

    // ==================== ongoingWithoutStateChange 测试 ====================

    @Test
    void testOngoingWithoutStateChange() {
        List<String> inputMessages = Arrays.asList("查看", "信息");
        CommandResult result = CommandResult.ongoingWithoutStateChange(inputMessages);

        assertFalse(result.isFinished());
        assertFalse(result.isStateChanged());

        List<String> messages = result.getMessages();
        assertEquals(2, messages.size());
        assertEquals("查看", messages.get(0));
        assertEquals("信息", messages.get(1));
    }

    @Test
    void testOngoingWithoutStateChangeWithEmptyList() {
        CommandResult result = CommandResult.ongoingWithoutStateChange(Collections.emptyList());

        assertFalse(result.isFinished());
        assertFalse(result.isStateChanged());
        assertTrue(result.getMessages().isEmpty());
    }

    @Test
    void testOngoingWithoutStateChangeWithNull() {
        assertThrows(NullPointerException.class,
                () -> CommandResult.ongoingWithoutStateChange(null));
    }

    // ==================== gameOver 测试 ====================

    @Test
    void testGameOver() {
        List<String> inputMessages = Arrays.asList("游戏结束", "你输了");
        CommandResult result = CommandResult.gameOver(inputMessages);

        assertTrue(result.isFinished());
        assertTrue(result.isStateChanged());

        List<String> messages = result.getMessages();
        assertEquals(2, messages.size());
        assertEquals("游戏结束", messages.get(0));
        assertEquals("你输了", messages.get(1));
    }

    @Test
    void testGameOverWithEmptyList() {
        CommandResult result = CommandResult.gameOver(Collections.emptyList());

        assertTrue(result.isFinished());
        assertTrue(result.isStateChanged());
        assertTrue(result.getMessages().isEmpty());
    }

    @Test
    void testGameOverWithSingleMessage() {
        CommandResult result = CommandResult.gameOver(Collections.singletonList("胜利！"));

        assertTrue(result.isFinished());
        assertEquals("胜利！", result.getMessageText());
    }

    // ==================== getMessages 不可变性测试 ====================

    @Test
    void testGetMessagesReturnsUnmodifiableList() {
        CommandResult result = CommandResult.ongoing("测试消息");
        List<String> messages = result.getMessages();

        assertThrows(UnsupportedOperationException.class,
                () -> messages.add("新消息"));

        assertThrows(UnsupportedOperationException.class,
                () -> messages.remove(0));
    }

    @Test
    void testGetMessagesDoesNotReflectOriginalListChanges() {
        List<String> original = new ArrayList<>(Arrays.asList("消息1", "消息2"));
        CommandResult result = CommandResult.ongoing(original);

        original.add("消息3");

        assertEquals(2, result.getMessages().size());
    }

    // ==================== getMessageText 测试 ====================

    @Test
    void testGetMessageTextWithMultipleLines() {
        CommandResult result = CommandResult.ongoing(Arrays.asList("Line 1", "Line 2", "Line 3"));
        assertEquals("Line 1\nLine 2\nLine 3", result.getMessageText());
    }

    @Test
    void testGetMessageTextWithEmptyList() {
        CommandResult result = CommandResult.ongoing(Collections.emptyList());
        assertEquals("", result.getMessageText());
    }

    @Test
    void testGetMessageTextWithWhitespaceOnly() {
        CommandResult result = CommandResult.ongoing(Arrays.asList("  ", "\t", "\n"));
        assertEquals("  \n\t\n\n", result.getMessageText());
    }

    // ==================== 组合测试 ====================

    @Test
    void testDifferentResultTypesHaveDifferentFlags() {
        CommandResult ongoing = CommandResult.ongoing("进行中");
        CommandResult ongoingNoState = CommandResult.ongoingWithoutStateChange(Collections.singletonList("无状态变化"));
        CommandResult over = CommandResult.gameOver(Collections.singletonList("结束"));

        assertFalse(ongoing.isFinished());
        assertTrue(ongoing.isStateChanged());

        assertFalse(ongoingNoState.isFinished());
        assertFalse(ongoingNoState.isStateChanged());

        assertTrue(over.isFinished());
        assertTrue(over.isStateChanged());
    }

    @Test
    void testMultipleResultsAreIndependent() {
        CommandResult result1 = CommandResult.ongoing("结果1");
        CommandResult result2 = CommandResult.gameOver(Collections.singletonList("结果2"));

        assertFalse(result1.isFinished());
        assertTrue(result2.isFinished());

        assertNotSame(result1.getMessages(), result2.getMessages());
    }

    // ==================== 边界值测试 ====================

    @Test
    void testVeryLongMessage() {
        String longMessage = "A".repeat(10000);
        CommandResult result = CommandResult.ongoing(longMessage);

        assertEquals(longMessage, result.getMessageText());
        assertEquals(1, result.getMessages().size());
        assertEquals(longMessage, result.getMessages().get(0));
    }

    @Test
    void testManyMessages() {
        List<String> manyMessages = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            manyMessages.add("消息 " + i);
        }

        CommandResult result = CommandResult.ongoing(manyMessages);

        assertEquals(1000, result.getMessages().size());

        // 修正：使用 startsWith 而不是 substring
        String messageText = result.getMessageText();
        assertTrue(messageText.startsWith("消息 0\n消息 1\n消息 2"),
                "消息文本应以正确内容开头，实际开头: " + messageText.substring(0, Math.min(30, messageText.length())));

        // 验证包含最后一条消息
        assertTrue(messageText.contains("消息 999"),
                "消息文本应包含消息 999");
    }

    @Test
    void testMessageWithNewlines() {
        CommandResult result = CommandResult.ongoing("第一行\n第二行");

        assertEquals("第一行\n第二行", result.getMessageText());
        assertEquals(1, result.getMessages().size());
        assertEquals("第一行\n第二行", result.getMessages().get(0));
    }
}