package cn.edu.whut.sept.zuul.persistence.jdbc; // 确保包路径对应你们的 jdbc 包

import cn.edu.whut.sept.zuul.persistence.GameSnapshot;
import cn.edu.whut.sept.zuul.persistence.GameStateRepository;
import cn.edu.whut.sept.zuul.persistence.PersistenceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JdbcGameStateRepositoryTest
{
    private DatabaseManager databaseManager;
    private GameStateRepository repository; // 保持使用接口类型，体现面向接口编程
    private GameSnapshot testSnapshot;

    @BeforeEach
    void setUp() throws PersistenceException
    {
        // 1. 切换为 H2 内存数据库环境初始化
        DatabaseManager.resetInstance();
        databaseManager = new DatabaseManager("jdbc:h2:mem:zuul_test;DB_CLOSE_DELAY=-1;MODE=MySQL");
        repository = new JdbcGameStateRepository(databaseManager);
        databaseManager.ensureInitialized();

        // 2. 准备丰富、高规格的复杂测试快照数据
        testSnapshot = new GameSnapshot();
        testSnapshot.setPlayerName("探险者");
        testSnapshot.setCurrentRoomId("lab");
        testSnapshot.setMaxCarryWeight(20.0);
        testSnapshot.setAteMagicCookie(true);
        testSnapshot.setCarriedItemNames(Arrays.asList("钥匙", "讲义"));

        Map<String, List<String>> roomItems = new HashMap<>();
        roomItems.put("theater", Arrays.asList("讲义"));
        roomItems.put("pub", Arrays.asList("啤酒杯", "魔法饼干"));
        testSnapshot.setRoomItemNamesByRoomId(roomItems);
    }

    @AfterEach
    void tearDown()
    {
        // 每个测试方法跑完，都将单例和连接断开重置，保证绝对的数据隔离
        DatabaseManager.resetInstance();
    }

    @Test
    void testSaveAndLoadGameSuccess() throws PersistenceException
    {
        // 执行保存到数据库
        repository.save("slot_1", testSnapshot);

        // 验证数据库中是否存在该槽位
        assertTrue(repository.exists("slot_1"), "数据库中slot_1记录应该存在");

        // 从数据库执行读取
        Optional<GameSnapshot> loadedOpt = repository.load("slot_1");
        assertTrue(loadedOpt.isPresent(), "从数据库读取到的Optional不应该为空");

        // 深度验证数据库读写前后，集合和基础字段的数据完整性
        GameSnapshot loaded = loadedOpt.get();
        assertAll("深度验证数据库快照各项数据的一致性",
                () -> assertEquals("探险者", loaded.getPlayerName()),
                () -> assertEquals("lab", loaded.getCurrentRoomId()),
                () -> assertEquals(20.0, loaded.getMaxCarryWeight()),
                () -> assertTrue(loaded.isAteMagicCookie()),
                () -> assertEquals(Arrays.asList("钥匙", "讲义"), loaded.getCarriedItemNames(), "随身物品列表不匹配"),
                () -> assertEquals(Arrays.asList("讲义"), loaded.getRoomItemNamesByRoomId().get("theater"), "放映室物品不匹配"),
                () -> assertEquals(Arrays.asList("啤酒杯", "魔法饼干"), loaded.getRoomItemNamesByRoomId().get("pub"), "酒吧物品不匹配")
        );
    }

    @Test
    void testSaveShouldOverwriteExistingSlot() throws PersistenceException
    {
        // 1. 先存入第一个高级快照（探险者）
        repository.save("slot_1", testSnapshot);

        // 2. 创建第二个快照（张三），存入同一个数据库槽位
        GameSnapshot secondSnapshot = new GameSnapshot();
        secondSnapshot.setPlayerName("法外狂徒张三");
        secondSnapshot.setCurrentRoomId("theater");

        repository.save("slot_1", secondSnapshot);

        // 3. 从数据库读取出来，验证 SQL 的 UPDATE 逻辑是否成功覆盖了旧数据
        GameSnapshot loaded = repository.load("slot_1").get();
        assertAll("验证数据库旧记录确实被新记录覆盖",
                () -> assertEquals("法外狂徒张三", loaded.getPlayerName()),
                () -> assertEquals("theater", loaded.getCurrentRoomId())
        );
    }

    @Test
    void testLoadNonExistentSlotShouldReturnEmpty() throws PersistenceException
    {
        // 健壮性测试：直接去数据库查询一条不存在的 slotId
        Optional<GameSnapshot> loadedOpt = repository.load("empty_slot");

        // 验证 JDBC 处理结果集为空时，是否返回了空的 Optional
        assertTrue(loadedOpt.isEmpty(), "查询数据库不存在的记录应该返回空的Optional");
        assertFalse(repository.exists("empty_slot"));
    }

    @Test
    void testDeleteSlotSuccess() throws PersistenceException
    {
        // 1. 存入并确认存在
        repository.save("slot_to_delete", testSnapshot);
        assertTrue(repository.exists("slot_to_delete"));

        // 2. 执行数据库 DELETE 操作
        repository.delete("slot_to_delete");

        // 3. 验证该行记录是否彻底从物理表中抹去
        assertFalse(repository.exists("slot_to_delete"), "删除后，exists应该返回false");
        assertTrue(repository.load("slot_to_delete").isEmpty(), "删除后，再次load应该返回空");
    }
}