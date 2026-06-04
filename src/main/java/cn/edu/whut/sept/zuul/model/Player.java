package cn.edu.whut.sept.zuul.model;

/**
 * 玩家实体：姓名、位置、背包与负重上限。
 */
public class Player
{
    private final String name;
    private Room currentRoom;
    private final Inventory inventory;
    private boolean ateMagicCookie;

    public Player(String name, Room startRoom, double maxCarryWeight)
    {
        this.name = name;
        this.currentRoom = startRoom;
        this.inventory = new Inventory(maxCarryWeight);
        this.ateMagicCookie = false;
    }

    public String getName()
    {
        return name;
    }

    public Room getCurrentRoom()
    {
        return currentRoom;
    }

    public void setCurrentRoom(Room room)
    {
        this.currentRoom = room;
    }

    public Inventory getInventory()
    {
        return inventory;
    }

    public boolean hasEatenMagicCookie()
    {
        return ateMagicCookie;
    }

    public void eatMagicCookie(double newMaxWeight)
    {
        ateMagicCookie = true;
        inventory.setMaxWeight(newMaxWeight);
    }
}
