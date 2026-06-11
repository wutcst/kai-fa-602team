package cn.edu.whut.sept.zuul.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 游戏世界：房间拓扑、初始物品与特殊房间配置。
 */
public class World
{
    public static final String DIR_EAST = "东";
    public static final String DIR_WEST = "西";
    public static final String DIR_NORTH = "北";
    public static final String DIR_SOUTH = "南";

    private final List<Room> rooms = new ArrayList<>();
    private Room startRoom;

    public World()
    {
        buildDefaultWorld();
    }

    private void buildDefaultWorld()
    {
        Room outside = new Room("outside", "大学主入口外");
        Room theater = new Room("theater", "一间演讲厅");
        Room pub = new Room("pub", "校园酒吧");
        Room lab = new Room("lab", "计算机实验室");
        Room office = new Room("office", "计算机管理办公室");
        Room teleport = new Room("teleport", "神秘的传送大厅");
        Room storage = new Room("storage", "旧仓库");

        outside.setExit(DIR_EAST, theater);
        outside.setExit(DIR_SOUTH, lab);
        outside.setExit(DIR_WEST, pub);
        outside.setExit(DIR_NORTH, teleport);

        theater.setExit(DIR_WEST, outside);
        pub.setExit(DIR_EAST, outside);
        lab.setExit(DIR_NORTH, outside);
        lab.setExit(DIR_EAST, office);
        office.setExit(DIR_WEST, lab);
        teleport.setExit(DIR_SOUTH, outside);
        storage.setExit(DIR_SOUTH, pub);

        pub.setExit(DIR_NORTH, storage);

        teleport.setRoomType(RoomType.TELEPORT);

        theater.addItem(new Item("讲义", "一叠泛黄的课程讲义", 1.2));
        lab.addItem(new Item("键盘", "机械键盘，沉甸甸的", 2.5));
        pub.addItem(new Item("啤酒杯", "沾着泡沫的酒杯", 0.8));
        office.addItem(new Item("钥匙", "管理员办公室备用钥匙", 0.3));
        storage.addItem(new Item("木箱", "装满旧书的木箱", 4.0));

        List<Room> cookieCandidates = new ArrayList<>();
        cookieCandidates.add(pub);
        cookieCandidates.add(storage);
        cookieCandidates.add(office);
        Room cookieRoom = cookieCandidates.get(new Random().nextInt(cookieCandidates.size()));
        cookieRoom.addItem(new Item("魔法饼干", "散发着微光的饼干，吃了能增强负重", 0.5, true));

        Collections.addAll(rooms, outside, theater, pub, lab, office, teleport, storage);
        startRoom = outside;
    }

    public Room getStartRoom()
    {
        return startRoom;
    }

    public List<Room> getRooms()
    {
        return Collections.unmodifiableList(rooms);
    }

    /**
     * 传送房间：随机传送到除当前房间外的任一房间。
     */
    public Room getRandomRoomExcept(Room current)
    {
        List<Room> candidates = new ArrayList<>();
        for (Room room : rooms) {
            if (room != current) {
                candidates.add(room);
            }
        }
        if (candidates.isEmpty()) {
            return current;
        }
        return candidates.get(new Random().nextInt(candidates.size()));
    }
}
