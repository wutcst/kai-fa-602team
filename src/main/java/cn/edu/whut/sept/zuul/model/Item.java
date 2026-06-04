package cn.edu.whut.sept.zuul.model;

/**
 * 可拾取的游戏物件。
 */
public class Item
{
    private final String name;
    private final String description;
    private final double weight;
    private final boolean magicCookie;

    public Item(String name, String description, double weight)
    {
        this(name, description, weight, false);
    }

    public Item(String name, String description, double weight, boolean magicCookie)
    {
        this.name = name;
        this.description = description;
        this.weight = weight;
        this.magicCookie = magicCookie;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public double getWeight()
    {
        return weight;
    }

    public boolean isMagicCookie()
    {
        return magicCookie;
    }

    public String getDisplayLine()
    {
        return name + " (" + weight + "千克) - " + description;
    }
}
