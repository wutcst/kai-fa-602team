package cn.edu.whut.sept.zuul.persistence.jdbc;

import cn.edu.whut.sept.zuul.persistence.GameSnapshot;
import cn.edu.whut.sept.zuul.persistence.GameStateRepository;
import cn.edu.whut.sept.zuul.persistence.PersistenceException;
import cn.edu.whut.sept.zuul.persistence.SaveSlotSummary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 基于 H2/JDBC 的游戏存档实现。
 */
public class JdbcGameStateRepository implements GameStateRepository
{
    private final DatabaseManager databaseManager;

    public JdbcGameStateRepository(DatabaseManager databaseManager)
    {
        this.databaseManager = databaseManager;
    }

    @Override
    public void save(String slotId, GameSnapshot snapshot) throws PersistenceException
    {
        databaseManager.ensureInitialized();
        try (Connection conn = databaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                deleteSaveData(conn, slotId);
                insertSave(conn, slotId, snapshot);
                insertCarriedItems(conn, slotId, snapshot.getCarriedItemNames());
                insertRoomItems(conn, slotId, snapshot.getRoomItemNamesByRoomId());
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new PersistenceException("保存存档失败: " + slotId, e);
        }
    }

    @Override
    public Optional<GameSnapshot> load(String slotId) throws PersistenceException
    {
        databaseManager.ensureInitialized();
        String sql = "SELECT player_name, current_room_id, max_carry_weight, ate_magic_cookie "
                + "FROM game_save WHERE slot_id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, slotId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                GameSnapshot snapshot = new GameSnapshot();
                snapshot.setPlayerName(rs.getString("player_name"));
                snapshot.setCurrentRoomId(rs.getString("current_room_id"));
                snapshot.setMaxCarryWeight(rs.getDouble("max_carry_weight"));
                snapshot.setAteMagicCookie(rs.getBoolean("ate_magic_cookie"));
                snapshot.setCarriedItemNames(loadCarriedItems(conn, slotId));
                snapshot.setRoomItemNamesByRoomId(loadRoomItems(conn, slotId));
                return Optional.of(snapshot);
            }
        } catch (SQLException e) {
            throw new PersistenceException("读取存档失败: " + slotId, e);
        }
    }

    @Override
    public boolean exists(String slotId) throws PersistenceException
    {
        databaseManager.ensureInitialized();
        String sql = "SELECT 1 FROM game_save WHERE slot_id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, slotId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenceException("检查存档失败: " + slotId, e);
        }
    }

    @Override
    public void delete(String slotId) throws PersistenceException
    {
        databaseManager.ensureInitialized();
        try (Connection conn = databaseManager.getConnection()) {
            deleteSaveData(conn, slotId);
        } catch (SQLException e) {
            throw new PersistenceException("删除存档失败: " + slotId, e);
        }
    }

    @Override
    public List<SaveSlotSummary> listSaves() throws PersistenceException
    {
        databaseManager.ensureInitialized();
        String sql = "SELECT slot_id, player_name, current_room_id, max_carry_weight, "
                + "ate_magic_cookie, saved_at FROM game_save ORDER BY saved_at DESC";
        List<SaveSlotSummary> summaries = new ArrayList<>();
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SaveSlotSummary summary = new SaveSlotSummary();
                summary.setSlotId(rs.getString("slot_id"));
                summary.setPlayerName(rs.getString("player_name"));
                summary.setCurrentRoomId(rs.getString("current_room_id"));
                summary.setMaxCarryWeight(rs.getDouble("max_carry_weight"));
                summary.setAteMagicCookie(rs.getBoolean("ate_magic_cookie"));
                Timestamp savedAt = rs.getTimestamp("saved_at");
                if (savedAt != null) {
                    summary.setSavedAt(savedAt.toLocalDateTime());
                }
                summaries.add(summary);
            }
        } catch (SQLException e) {
            throw new PersistenceException("读取存档列表失败", e);
        }
        return summaries;
    }

    private void deleteSaveData(Connection conn, String slotId) throws SQLException
    {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM game_save WHERE slot_id = ?")) {
            ps.setString(1, slotId);
            ps.executeUpdate();
        }
    }

    private void insertSave(Connection conn, String slotId, GameSnapshot snapshot) throws SQLException
    {
        String sql = "INSERT INTO game_save "
                + "(slot_id, player_name, current_room_id, max_carry_weight, ate_magic_cookie) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, slotId);
            ps.setString(2, snapshot.getPlayerName());
            ps.setString(3, snapshot.getCurrentRoomId());
            ps.setDouble(4, snapshot.getMaxCarryWeight());
            ps.setBoolean(5, snapshot.isAteMagicCookie());
            ps.executeUpdate();
        }
    }

    private void insertCarriedItems(Connection conn, String slotId, List<String> itemNames)
            throws SQLException
    {
        if (itemNames == null || itemNames.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO save_carried_item (slot_id, item_name, sort_order) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < itemNames.size(); i++) {
                ps.setString(1, slotId);
                ps.setString(2, itemNames.get(i));
                ps.setInt(3, i);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void insertRoomItems(Connection conn, String slotId, Map<String, List<String>> roomItems)
            throws SQLException
    {
        if (roomItems == null || roomItems.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO save_room_item (slot_id, room_id, item_name, sort_order) "
                + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Map.Entry<String, List<String>> entry : roomItems.entrySet()) {
                String roomId = entry.getKey();
                List<String> names = entry.getValue();
                if (names == null) {
                    continue;
                }
                for (int i = 0; i < names.size(); i++) {
                    ps.setString(1, slotId);
                    ps.setString(2, roomId);
                    ps.setString(3, names.get(i));
                    ps.setInt(4, i);
                    ps.addBatch();
                }
            }
            ps.executeBatch();
        }
    }

    private List<String> loadCarriedItems(Connection conn, String slotId) throws SQLException
    {
        String sql = "SELECT item_name FROM save_carried_item "
                + "WHERE slot_id = ? ORDER BY sort_order";
        List<String> items = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, slotId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(rs.getString("item_name"));
                }
            }
        }
        return items;
    }

    private Map<String, List<String>> loadRoomItems(Connection conn, String slotId) throws SQLException
    {
        String sql = "SELECT room_id, item_name FROM save_room_item "
                + "WHERE slot_id = ? ORDER BY room_id, sort_order";
        Map<String, List<String>> roomItems = new HashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, slotId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String roomId = rs.getString("room_id");
                    roomItems.computeIfAbsent(roomId, key -> new ArrayList<>())
                            .add(rs.getString("item_name"));
                }
            }
        }
        return roomItems;
    }
}
