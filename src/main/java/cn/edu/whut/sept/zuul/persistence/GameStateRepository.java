package cn.edu.whut.sept.zuul.persistence;

import java.util.Optional;

/**
 * 游戏状态持久化接口。
 * 数据库模块实现此接口即可接入存档/读档，无需修改引擎与 UI 代码。
 */
public interface GameStateRepository
{
    /**
     * @param slotId 存档位标识（如用户名、存档编号）
     */
    void save(String slotId, GameSnapshot snapshot) throws PersistenceException;

    Optional<GameSnapshot> load(String slotId) throws PersistenceException;

    boolean exists(String slotId) throws PersistenceException;

    void delete(String slotId) throws PersistenceException;
}
