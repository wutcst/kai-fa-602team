package cn.edu.whut.sept.zuul.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 内存存档实现，供开发与测试；数据库组员可替换为 JDBC 等实现。
 */
public class InMemoryGameStateRepository implements GameStateRepository
{
    private final Map<String, GameSnapshot> store = new HashMap<>();

    @Override
    public void save(String slotId, GameSnapshot snapshot)
    {
        store.put(slotId, snapshot);
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
    }
}
