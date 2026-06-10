package cn.edu.whut.sept.zuul.persistence;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 内存存档实现，供开发与测试；数据库组员可替换为 JDBC 等实现。
 */
public class InMemoryGameStateRepository implements GameStateRepository
{
    private final Map<String, GameSnapshot> store = new HashMap<>();
    private final Map<String, LocalDateTime> savedAtBySlot = new HashMap<>();

    @Override
    public void save(String slotId, GameSnapshot snapshot)
    {
        store.put(slotId, snapshot);
        savedAtBySlot.put(slotId, LocalDateTime.now());
    }

    @Override
    public Optional<GameSnapshot> load(String slotId)
    {
        return Optional.ofNullable(store.get(slotId));
    }

    @Override
    public boolean exists(String slotId)
    {
        return store.containsKey(slotId);
    }

    @Override
    public void delete(String slotId)
    {
        store.remove(slotId);
        savedAtBySlot.remove(slotId);
    }

    @Override
    public List<SaveSlotSummary> listSaves()
    {
        List<SaveSlotSummary> summaries = new ArrayList<>();
        for (Map.Entry<String, GameSnapshot> entry : store.entrySet()) {
            GameSnapshot snapshot = entry.getValue();
            SaveSlotSummary summary = new SaveSlotSummary();
            summary.setSlotId(entry.getKey());
            summary.setPlayerName(snapshot.getPlayerName());
            summary.setCurrentRoomId(snapshot.getCurrentRoomId());
            summary.setMaxCarryWeight(snapshot.getMaxCarryWeight());
            summary.setAteMagicCookie(snapshot.isAteMagicCookie());
            summary.setSavedAt(savedAtBySlot.getOrDefault(entry.getKey(), LocalDateTime.now()));
            summaries.add(summary);
        }
        summaries.sort(Comparator.comparing(SaveSlotSummary::getSavedAt).reversed());
        return summaries;
    }
}
