package cn.edu.whut.sept.zuul.model;

<<<<<<< HEAD
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

public class InventoryTest {

    private Inventory inventory;
    private Item cookie;
    private Item heavyBook;

    @BeforeEach
    public void setUp() {
        // 1. 初始化一个最大负重为 10.0 千克的背包
        inventory = new Inventory(10.0);

        // 2. 准备测试用的物品（通过构造函数实例化，假设Item构造方法为：Item(名称, 描述, 重量)）
        cookie = new Item("cookie", "一块好吃的魔法饼干", 2.0);
        heavyBook = new Item("book", "一本沉重的软件工程大作", 9.0);
    }

    @Test
    public void testGetMaxWeightAndSetMaxWeight() {
        // 验证初始最大负重
        assertEquals(10.0, inventory.getMaxWeight(), 0.001);

        // 验证修改最大负重（比如吃了饼干后扩容）
        inventory.setMaxWeight(20.0);
        assertEquals(20.0, inventory.getMaxWeight(), 0.001);
    }

    @Test
    public void testAddItemWithinWeightLimit() {
        // 动作：放入一个 2.0 千克的饼干（未超重）
        boolean result = inventory.add(cookie);

        // 断言：应当放入成功
        assertTrue(result, "在负重范围内应当成功放入物品");
        assertEquals(2.0, inventory.getTotalWeight(), 0.001, "背包总重量应当更新为 2.0");

        // 验证列表包含该物品
        List<Item> currentItems = inventory.getItems();
        assertEquals(1, currentItems.size());
        assertEquals("cookie", currentItems.get(0).getName());
    }

    @Test
    public void testAddItemExceedingWeightLimit() {
        // 1. 先放入一个饼干（当前重量变成 2.0）
        inventory.add(cookie);

        // 2. 动作：尝试放入 9.0 千克的书（2.0 + 9.0 = 11.0 > 10.0 超重）
        assertFalse(inventory.canAdd(heavyBook), "canAdd 应当识别出物品超重");

        boolean result = inventory.add(heavyBook);

        // 3. 断言：应当放入失败，且重量保持不变
        assertFalse(result, "超重的物品应当被拒绝放入背包");
        assertEquals(2.0, inventory.getTotalWeight(), 0.001, "背包总重量不应当被错误改动");
        assertEquals(1, inventory.getItems().size(), "背包中的物品数量不应该增加");
    }

    @Test
    public void testRemoveItemSuccess() {
        // 1. 放入物品
        inventory.add(cookie);

        // 2. 动作：根据名字移除物品（支持大小写模糊匹配，输入大写 COOKIE 验证健壮性）
        Optional<Item> removedOpt = inventory.remove("COOKIE");

        // 3. 断言：应当成功移除并拿到物品
        assertTrue(removedOpt.isPresent(), "移除已存在的物品应该返回包含物品的Optional");
        assertEquals("cookie", removedOpt.get().getName());

        // 4. 验证背包已被清空
        assertEquals(0.0, inventory.getTotalWeight(), 0.001);
        assertTrue(inventory.getItems().isEmpty());
    }

    @Test
    public void testRemoveNonExistentItemShouldReturnEmpty() {
        // 动作：移除一个背包里根本没有的物品
        Optional<Item> removedOpt = inventory.remove("ghost_item");

        // 断言：应当优雅地返回空 Optional
        assertTrue(removedOpt.isEmpty(), "移除不存在的物品应该返回空的Optional");
    }

    @Test
    public void testFindItemByName() {
        // 1. 放入物品
        inventory.add(cookie);

        // 2. 动作：查找存在的物品（测试大小写不敏感）
        Optional<Item> foundOpt = inventory.find("CoOkIe");
        assertTrue(foundOpt.isPresent(), "应当能根据名字查找到物品");
        assertEquals("cookie", foundOpt.get().getName());

        // 3. 动作：查找不存在的物品
        Optional<Item> notFoundOpt = inventory.find("key");
        assertTrue(notFoundOpt.isEmpty(), "查找不存在的物品应当返回空Optional");
    }

    @Test
    public void testClearInventory() {
        // 1. 塞满物品
        inventory.add(cookie);
        assertEquals(1, inventory.getItems().size());

        // 2. 动作：一键清空背包
        inventory.clear();

        // 3. 断言
        assertTrue(inventory.getItems().isEmpty(), "调用clear后背包物品列表应当为空");
        assertEquals(0.0, inventory.getTotalWeight(), 0.001, "清空后总重量应当重置为0");
    }

    @Test
    public void testDescribeEmptyInventory() {
        // 验证空背包的描述输出
        assertEquals("（空）", inventory.describe());
    }
}
=======
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InventoryTest
{
    @Test
    void rejectsItemWhenOverWeightLimit()
    {
        Inventory inventory = new Inventory(5);
        assertTrue(inventory.add(new Item("轻物", "轻", 3)));
        assertFalse(inventory.add(new Item("重物", "重", 3)));
    }
}
>>>>>>> 210fdf462ccacad1294a3b412c05a259a656f9cf
