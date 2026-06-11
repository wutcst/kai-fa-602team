package cn.edu.whut.sept.zuul.persistence;

import java.util.List;
import java.util.Map;

/**
 * 游戏存档快照（与具体存储实现解耦，便于数据库组员接入）。
 */
public class GameSnapshot
{
    private String playerName;
    private String currentRoomId;
    private double maxCarryWeight;
    private boolean ateMagicCookie;
    private List<String> carriedItemNames;
    private Map<String, List<String>> roomItemNamesByRoomId;

    public GameSnapshot()
    {
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public void setPlayerName(String playerName)
    {
        this.playerName = playerName;
    }

    public String getCurrentRoomId()
    {
        return currentRoomId;
    }

    public void setCurrentRoomId(String currentRoomId)
    {
        this.currentRoomId = currentRoomId;
    }

    public double getMaxCarryWeight()
    {
        return maxCarryWeight;
    }

    public void setMaxCarryWeight(double maxCarryWeight)
    {
        this.maxCarryWeight = maxCarryWeight;
    }

    public boolean isAteMagicCookie()
    {
        return ateMagicCookie;
    }

    public void setAteMagicCookie(boolean ateMagicCookie)
    {
        this.ateMagicCookie = ateMagicCookie;
    }

    public List<String> getCarriedItemNames()
    {
        return carriedItemNames;
    }

    public void setCarriedItemNames(List<String> carriedItemNames)
    {
        this.carriedItemNames = carriedItemNames;
    }

    public Map<String, List<String>> getRoomItemNamesByRoomId()
    {
        return roomItemNamesByRoomId;
    }

    public void setRoomItemNamesByRoomId(Map<String, List<String>> roomItemNamesByRoomId)
    {
        this.roomItemNamesByRoomId = roomItemNamesByRoomId;
    }
}
