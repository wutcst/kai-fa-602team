package cn.edu.whut.sept.zuul.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 迷宫中的房间，可包含出口、物品与特殊类型。
 */
public class Room
{
    private final String id;
    private final String description;
    private final Map<String, Room> exits = new HashMap<>();
    private final List<Item> items = new ArrayList<>();
    private RoomType roomType = RoomType.NORMAL;

    public Room(String id, String description)
    {
        this.id = id;
        this.description = description;
    }

    public String getId()
    {
        return id;
    }

    public String getDescription()
    {
        return description;
    }

    public RoomType getRoomType()
    {
        return roomType;
    }

    public void setRoomType(RoomType roomType)
    {
        this.roomType = roomType;
    }

    public void setExit(String direction, Room neighbor)
    {
        exits.put(normalizeDirection(direction), neighbor);
    }

    public Room getExit(String direction)
    {
        if (direction == null) {
            return null;
        }
        return exits.get(normalizeDirection(direction));
    }

    private static String normalizeDirection(String direction)
    {
        return direction == null ? "" : direction.trim();
    }

    public Set<String> getExitDirections()
    {
        return exits.keySet();
    }

    public void addItem(Item item)
    {
        items.add(item);
    }

    public List<Item> getItems()
    {
        return Collections.unmodifiableList(items);
    }

    public double getItemsTotalWeight()
    {
        return items.stream().mapToDouble(Item::getWeight).sum();
    }

    public Optional<Item> removeItem(String name)
    {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.getName().equalsIgnoreCase(name)) {
                return Optional.of(items.remove(i));
            }
        }
        return Optional.empty();
    }

    public Optional<Item> findItem(String name)
    {
        return items.stream()
                .filter(item -> item.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public String getExitString()
    {
        if (exits.isEmpty()) {
            return "（无出口）";
        }
        StringBuilder sb = new StringBuilder();
        for (String exit : exits.keySet()) {
            sb.append(' ').append(exit);
        }
        return sb.toString().trim();
    }

    public String getItemsDescription()
    {
        if (items.isEmpty()) {
            return "房间内没有物品。";
        }
        StringBuilder sb = new StringBuilder("房间物品:\n");
        for (Item item : items) {
            sb.append("  - ").append(item.getDisplayLine()).append('\n');
        }
        sb.append("房间物品总重量: ").append(getItemsTotalWeight()).append(" 千克");
        return sb.toString().trim();
    }

    public String getLongDescription()
    {
        return "你位于 " + description + "。\n"
                + "出口:" + getExitString() + "\n"
                + getItemsDescription();
    }
}
