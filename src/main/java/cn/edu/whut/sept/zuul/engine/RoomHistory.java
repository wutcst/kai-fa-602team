package cn.edu.whut.sept.zuul.engine;

import cn.edu.whut.sept.zuul.model.Room;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 记录玩家经过的房间，支持多层 back 回退。
 */
public class RoomHistory
{
    private final Deque<Room> history = new ArrayDeque<>();

    public void push(Room room)
    {
        history.push(room);
    }

    public boolean canGoBack()
    {
        return !history.isEmpty();
    }

    public Room pop()
    {
        return history.pop();
    }

    public void clear()
    {
        history.clear();
    }
}
