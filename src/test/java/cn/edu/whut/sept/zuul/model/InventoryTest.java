package cn.edu.whut.sept.zuul.model;

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
