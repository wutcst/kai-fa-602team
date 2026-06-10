package cn.edu.whut.sept.zuul.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 玩家随身物品栏。
 */
public class Inventory
{
    private final List<Item> items = new ArrayList<>();
    private double maxWeight;

    public Inventory(double maxWeight)
    {
        this.maxWeight = maxWeight;
    }

    public double getMaxWeight()
    {
        return maxWeight;
    }

    public void setMaxWeight(double maxWeight)
    {
        this.maxWeight = maxWeight;
    }

    public double getTotalWeight()
    {
        return items.stream().mapToDouble(Item::getWeight).sum();
    }

    public List<Item> getItems()
    {
        return Collections.unmodifiableList(items);
    }

    public void clear()
    {
        items.clear();
    }

    public boolean canAdd(Item item)
    {
        return getTotalWeight() + item.getWeight() <= maxWeight;
    }

    public boolean add(Item item)
    {
        if (!canAdd(item)) {
            return false;
        }
        items.add(item);
        return true;
    }

    public Optional<Item> remove(String name)
    {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.getName().equalsIgnoreCase(name)) {
                return Optional.of(items.remove(i));
            }
        }
        return Optional.empty();
    }

    public Optional<Item> find(String name)
    {
        return items.stream()
                .filter(item -> item.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public String describe()
    {
        if (items.isEmpty()) {
            return "（空）";
        }
        StringBuilder sb = new StringBuilder();
        for (Item item : items) {
            sb.append("  - ").append(item.getDisplayLine()).append('\n');
        }
        sb.append("总重量: ").append(getTotalWeight()).append(" / ").append(maxWeight).append(" 千克");
        return sb.toString().trim();
    }
}
