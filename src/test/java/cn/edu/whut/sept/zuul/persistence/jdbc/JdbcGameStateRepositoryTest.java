package cn.edu.whut.sept.zuul.persistence.jdbc;

import cn.edu.whut.sept.zuul.persistence.GameSnapshot;
import cn.edu.whut.sept.zuul.persistence.PersistenceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcGameStateRepositoryTest
{
    private DatabaseManager databaseManager;
    private JdbcGameStateRepository repository;

    @BeforeEach
    void setUp() throws PersistenceException
    {
        DatabaseManager.resetInstance();
        databaseManager = new DatabaseManager("jdbc:h2:mem:zuul_test;DB_CLOSE_DELAY=-1;MODE=MySQL");
        repository = new JdbcGameStateRepository(databaseManager);
        databaseManager.ensureInitialized();
    }

    @AfterEach
    void tearDown()
    {
        DatabaseManager.resetInstance();
    }

    @Test
    void saveLoadAndDeleteRoundTrip() throws PersistenceException
    {
        GameSnapshot snapshot = new GameSnapshot();
        snapshot.setPlayerName("探险者");
        snapshot.setCurrentRoomId("lab");
        snapshot.setMaxCarryWeight(20.0);
        snapshot.setAteMagicCookie(true);
        snapshot.setCarriedItemNames(Arrays.asList("钥匙", "讲义"));

        Map<String, List<String>> roomItems = new HashMap<>();
        roomItems.put("theater", Arrays.asList("讲义"));
        roomItems.put("pub", Arrays.asList("啤酒杯", "魔法饼干"));
        snapshot.setRoomItemNamesByRoomId(roomItems);

        repository.save("slot1", snapshot);
        assertTrue(repository.exists("slot1"));

        Optional<GameSnapshot> loaded = repository.load("slot1");
        assertTrue(loaded.isPresent());
        GameSnapshot result = loaded.get();
        assertEquals("探险者", result.getPlayerName());
        assertEquals("lab", result.getCurrentRoomId());
        assertEquals(20.0, result.getMaxCarryWeight());
        assertTrue(result.isAteMagicCookie());
        assertEquals(Arrays.asList("钥匙", "讲义"), result.getCarriedItemNames());
        assertEquals(Arrays.asList("讲义"), result.getRoomItemNamesByRoomId().get("theater"));
        assertEquals(Arrays.asList("啤酒杯", "魔法饼干"),
                result.getRoomItemNamesByRoomId().get("pub"));

        repository.delete("slot1");
        assertFalse(repository.exists("slot1"));
        assertTrue(repository.load("slot1").isEmpty());
    }
}
