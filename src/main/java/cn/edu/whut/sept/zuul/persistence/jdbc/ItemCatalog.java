package cn.edu.whut.sept.zuul.persistence.jdbc;

import cn.edu.whut.sept.zuul.model.Item;
import cn.edu.whut.sept.zuul.persistence.PersistenceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 物品模板目录：从数据库读取物品定义，供读档时还原背包与房间物品分布。
 */
public class ItemCatalog
{
    private final DatabaseManager databaseManager;
    private final Map<String, ItemTemplate> cache = new HashMap<>();

    private volatile boolean preloaded;

    public ItemCatalog(DatabaseManager databaseManager)
    {
        this.databaseManager = databaseManager;
    }

    public void preloadAll() throws PersistenceException
    {
        if (preloaded) {
            return;
        }
        synchronized (this) {
            if (preloaded) {
                return;
            }
            databaseManager.ensureInitialized();
            String sql = "SELECT item_name, description, weight, magic_cookie FROM item_catalog";
            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("item_name");
                    cache.put(name, new ItemTemplate(
                            name,
                            rs.getString("description"),
                            rs.getDouble("weight"),
                            rs.getBoolean("magic_cookie")));
                }
                preloaded = true;
            } catch (SQLException e) {
                throw new PersistenceException("预加载物品模板失败", e);
            }
        }
    }

    public Optional<Item> createItem(String itemName) throws PersistenceException
    {
        if (!preloaded) {
            preloadAll();
        }
        ItemTemplate template = cache.get(itemName);
        if (template == null) {
            return Optional.empty();
        }
        return Optional.of(template.toItem());
    }

    private static final class ItemTemplate
    {
        private final String name;
        private final String description;
        private final double weight;
        private final boolean magicCookie;

        private ItemTemplate(String name, String description, double weight, boolean magicCookie)
        {
            this.name = name;
            this.description = description;
            this.weight = weight;
            this.magicCookie = magicCookie;
        }

        private Item toItem()
        {
            return new Item(name, description, weight, magicCookie);
        }
    }
}
