package cn.edu.whut.sept.zuul.engine;

import cn.edu.whut.sept.zuul.command.Command;
import cn.edu.whut.sept.zuul.command.CommandWords;
import cn.edu.whut.sept.zuul.io.Parser;
import cn.edu.whut.sept.zuul.model.Item;
import cn.edu.whut.sept.zuul.model.Player;
import cn.edu.whut.sept.zuul.model.Room;
import cn.edu.whut.sept.zuul.model.RoomType;
import cn.edu.whut.sept.zuul.model.World;
import cn.edu.whut.sept.zuul.persistence.GameSnapshot;
import cn.edu.whut.sept.zuul.persistence.GameStateRepository;
import cn.edu.whut.sept.zuul.persistence.InMemoryGameStateRepository;
import cn.edu.whut.sept.zuul.persistence.PersistenceException;
import cn.edu.whut.sept.zuul.persistence.SaveSlotSummary;
import cn.edu.whut.sept.zuul.persistence.jdbc.ItemCatalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 游戏核心引擎：领域逻辑与命令调度，不依赖控制台或 JavaFX。
 */
public class GameEngine
{
    public static final double DEFAULT_MAX_WEIGHT = 10.0;
    public static final double COOKIE_BOOSTED_MAX_WEIGHT = 20.0;

    private final World world;
    private final Player player;
    private final RoomHistory roomHistory;
    private final CommandWords commandWords;
    private final Parser parser;
    private final GameStateRepository stateRepository;
    private final ItemCatalog itemCatalog;

    public GameEngine()
    {
        this(new World(), "探险者", new InMemoryGameStateRepository(), null);
    }

    public GameEngine(World world, String playerName, GameStateRepository stateRepository)
    {
        this(world, playerName, stateRepository, null);
    }

    public GameEngine(World world, String playerName, GameStateRepository stateRepository,
                      ItemCatalog itemCatalog)
    {
        this.world = world;
        this.player = new Player(playerName, world.getStartRoom(), DEFAULT_MAX_WEIGHT);
        this.roomHistory = new RoomHistory();
        this.commandWords = new CommandWords();
        this.parser = new Parser();
        this.stateRepository = stateRepository;
        this.itemCatalog = itemCatalog;
    }

    public Player getPlayer()
    {
        return player;
    }

    public World getWorld()
    {
        return world;
    }

    public CommandWords getCommandWords()
    {
        return commandWords;
    }

    public Parser getParser()
    {
        return parser;
    }

    public GameStateRepository getStateRepository()
    {
        return stateRepository;
    }

    public String getWelcomeMessage()
    {
        return "欢迎来到祖尔世界！\n"
                + "在校园里探索，收集物品，寻找魔法饼干增强负重。\n"
                + "输入「帮助」查看命令，或点击界面按钮操作。";
    }

    public CommandResult processCommandLine(String inputLine)
    {
        Command command = parser.parseCommand(inputLine, commandWords);
        if (command == null) {
            return CommandResult.ongoingWithoutStateChange(
                    Arrays.asList("我不明白你的意思...", getValidCommandsHint()));
        }
        return command.execute(this);
    }

    public String getValidCommandsHint()
    {
        return "可用命令: " + String.join(", ", commandWords.getCommandNames());
    }

    public CommandResult look()
    {
        return CommandResult.ongoingWithoutStateChange(
                Arrays.asList(player.getCurrentRoom().getLongDescription()));
    }

    public CommandResult go(String direction)
    {
        if (direction == null || direction.isBlank()) {
            return CommandResult.ongoingWithoutStateChange(
                    Collections.singletonList("要去哪个方向？例如：去 东"));
        }
        Room current = player.getCurrentRoom();
        Room next = current.getExit(direction.trim());
        if (next == null) {
            return CommandResult.ongoingWithoutStateChange(
                    Collections.singletonList("那个方向没有出口！"));
        }

        roomHistory.push(current);
        movePlayerTo(next, false);
        return CommandResult.ongoing(buildEnterRoomMessages(next));
    }

    public CommandResult back()
    {
        if (!roomHistory.canGoBack()) {
            return CommandResult.ongoingWithoutStateChange(
                    Collections.singletonList("没有可以返回的房间了。"));
        }
        Room previous = roomHistory.pop();
        movePlayerTo(previous, true);
        return CommandResult.ongoing(
                prepend("你回到了上一个房间。", buildEnterRoomMessages(previous)));
    }

    public CommandResult take(String itemName)
    {
        if (itemName == null || itemName.isBlank()) {
            return CommandResult.ongoingWithoutStateChange(
                    Collections.singletonList("要拾取什么？例如：拾取 钥匙"));
        }
        Room room = player.getCurrentRoom();
        Optional<Item> itemOpt = room.removeItem(itemName.trim());
        if (itemOpt.isEmpty()) {
            return CommandResult.ongoingWithoutStateChange(
                    Collections.singletonList("房间里没有叫「" + itemName + "」的物品。"));
        }
        Item item = itemOpt.get();
        if (!player.getInventory().canAdd(item)) {
            room.addItem(item);
            return CommandResult.ongoingWithoutStateChange(Collections.singletonList(
                    "太重了！你最多携带 " + player.getInventory().getMaxWeight()
                            + " 千克，当前 " + player.getInventory().getTotalWeight() + " 千克。"));
        }
        player.getInventory().add(item);
        return CommandResult.ongoing(Collections.singletonList(
                "你拾取了 " + item.getName() + "（" + item.getWeight() + " 千克）。"));
    }

    public CommandResult drop(String itemName)
    {
        Room room = player.getCurrentRoom();
        if (itemName == null || itemName.isBlank()) {
            if (player.getInventory().getItems().isEmpty()) {
                return CommandResult.ongoingWithoutStateChange(
                        Collections.singletonList("你身上没有任何物品。"));
            }
            List<String> msgs = new ArrayList<>();
            List<Item> carried = new ArrayList<>(player.getInventory().getItems());
            for (Item item : carried) {
                player.getInventory().remove(item.getName());
                room.addItem(item);
                msgs.add("你丢下了 " + item.getName() + "。");
            }
            return CommandResult.ongoing(msgs);
        }

        Optional<Item> itemOpt = player.getInventory().remove(itemName.trim());
        if (itemOpt.isEmpty()) {
            return CommandResult.ongoingWithoutStateChange(
                    Collections.singletonList("你身上没有「" + itemName + "」。"));
        }
        Item item = itemOpt.get();
        room.addItem(item);
        return CommandResult.ongoing(Collections.singletonList(
                "你丢下了 " + item.getName() + "。"));
    }

    public CommandResult listItems()
    {
        List<String> lines = new ArrayList<>();
        lines.add("=== 房间物品 ===");
        lines.add(player.getCurrentRoom().getItemsDescription());
        lines.add("");
        lines.add("=== 随身物品 ===");
        lines.add(player.getInventory().describe());
        return CommandResult.ongoingWithoutStateChange(lines);
    }

    public CommandResult eatCookie()
    {
        if (player.hasEatenMagicCookie()) {
            return CommandResult.ongoingWithoutStateChange(
                    Collections.singletonList("你已经吃过魔法饼干了，负重上限已提升。"));
        }
        Optional<Item> cookie = player.getInventory().find("魔法饼干");
        if (cookie.isEmpty()) {
            return CommandResult.ongoingWithoutStateChange(
                    Collections.singletonList("你身上没有魔法饼干。在校园里找找看吧！"));
        }
        player.getInventory().remove("魔法饼干");
        player.eatMagicCookie(COOKIE_BOOSTED_MAX_WEIGHT);
        return CommandResult.ongoing(Collections.singletonList(
                "你吃掉了魔法饼干！负重上限提升至 " + COOKIE_BOOSTED_MAX_WEIGHT + " 千克。"));
    }

    public CommandResult help()
    {
        return CommandResult.ongoingWithoutStateChange(Arrays.asList(
                "你在校园里迷路了，靠这些命令探索吧：",
                commandWords.getCommandNames().stream().collect(Collectors.joining(", "))));
    }

    public CommandResult quit()
    {
        return CommandResult.gameOver(Collections.singletonList("感谢游玩，再见！"));
    }

    public GameSnapshot createSnapshot()
    {
        GameSnapshot snapshot = new GameSnapshot();
        snapshot.setPlayerName(player.getName());
        snapshot.setCurrentRoomId(player.getCurrentRoom().getId());
        snapshot.setMaxCarryWeight(player.getInventory().getMaxWeight());
        snapshot.setAteMagicCookie(player.hasEatenMagicCookie());
        snapshot.setCarriedItemNames(
                player.getInventory().getItems().stream()
                        .map(Item::getName)
                        .collect(Collectors.toList()));

        Map<String, List<String>> roomItems = new HashMap<>();
        for (Room room : world.getRooms()) {
            roomItems.put(
                    room.getId(),
                    room.getItems().stream().map(Item::getName).collect(Collectors.toList()));
        }
        snapshot.setRoomItemNamesByRoomId(roomItems);
        return snapshot;
    }

    public static final int MAX_SLOT_NAME_LENGTH = 32;

    public Optional<String> validateSlotName(String slotId)
    {
        if (slotId == null || slotId.isBlank()) {
            return Optional.of("存档名称不能为空。");
        }
        String trimmed = slotId.trim();
        if (trimmed.length() > MAX_SLOT_NAME_LENGTH) {
            return Optional.of("存档名称不能超过 " + MAX_SLOT_NAME_LENGTH + " 个字符。");
        }
        return Optional.empty();
    }

    public List<SaveSlotSummary> listSaveSummaries() throws PersistenceException
    {
        return stateRepository.listSaves();
    }

    public CommandResult listSaves()
    {
        try {
            List<SaveSlotSummary> saves = stateRepository.listSaves();
            if (saves.isEmpty()) {
                return CommandResult.ongoingWithoutStateChange(
                        Collections.singletonList("暂无存档。"));
            }
            List<String> lines = new ArrayList<>();
            lines.add("=== 存档列表 ===");
            for (SaveSlotSummary save : saves) {
                lines.add(formatSaveSummary(save));
            }
            return CommandResult.ongoingWithoutStateChange(lines);
        } catch (PersistenceException e) {
            return CommandResult.ongoingWithoutStateChange(
                    Collections.singletonList("读取存档列表失败: " + e.getMessage()));
        }
    }

    public CommandResult saveGame(String slotId)
    {
        Optional<String> validationError = validateSlotName(slotId);
        if (validationError.isPresent()) {
            return CommandResult.ongoingWithoutStateChange(
                    Collections.singletonList(validationError.get()));
        }
        String normalizedSlot = slotId.trim();
        try {
            stateRepository.save(normalizedSlot, createSnapshot());
            return CommandResult.ongoingWithoutStateChange(
                    Collections.singletonList("游戏已保存为: 「" + normalizedSlot + "」"));
        } catch (PersistenceException e) {
            return CommandResult.ongoingWithoutStateChange(
                    Collections.singletonList("保存失败: " + e.getMessage()));
        }
    }

    public CommandResult loadGame(String slotId)
    {
        Optional<String> validationError = validateSlotName(slotId);
        if (validationError.isPresent()) {
            return CommandResult.ongoingWithoutStateChange(
                    Collections.singletonList(validationError.get()));
        }
        String normalizedSlot = slotId.trim();
        try {
            Optional<GameSnapshot> snapshotOpt = stateRepository.load(normalizedSlot);
            if (snapshotOpt.isEmpty()) {
                return CommandResult.ongoingWithoutStateChange(
                        Collections.singletonList("存档不存在: 「" + normalizedSlot + "」"));
            }
            applySnapshot(snapshotOpt.get());
            roomHistory.clear();
            return CommandResult.ongoing(Arrays.asList(
                    "已读取存档: 「" + normalizedSlot + "」",
                    player.getCurrentRoom().getLongDescription()));
        } catch (PersistenceException e) {
            return CommandResult.ongoingWithoutStateChange(
                    Collections.singletonList("读取失败: " + e.getMessage()));
        }
    }

    public CommandResult deleteGame(String slotId)
    {
        Optional<String> validationError = validateSlotName(slotId);
        if (validationError.isPresent()) {
            return CommandResult.ongoingWithoutStateChange(
                    Collections.singletonList(validationError.get()));
        }
        String normalizedSlot = slotId.trim();
        try {
            if (!stateRepository.exists(normalizedSlot)) {
                return CommandResult.ongoingWithoutStateChange(
                        Collections.singletonList("存档不存在: 「" + normalizedSlot + "」"));
            }
            stateRepository.delete(normalizedSlot);
            return CommandResult.ongoingWithoutStateChange(
                    Collections.singletonList("已删除存档: 「" + normalizedSlot + "」"));
        } catch (PersistenceException e) {
            return CommandResult.ongoingWithoutStateChange(
                    Collections.singletonList("删除失败: " + e.getMessage()));
        }
    }

    public boolean saveExists(String slotId) throws PersistenceException
    {
        return stateRepository.exists(slotId.trim());
    }

    private String formatSaveSummary(SaveSlotSummary save)
    {
        String roomName = findRoomById(save.getCurrentRoomId()) != null
                ? findRoomById(save.getCurrentRoomId()).getDescription()
                : save.getCurrentRoomId();
        String time = save.getSavedAt() != null ? save.getSavedAt().toString().replace('T', ' ') : "未知时间";
        return String.format("「%s」| 玩家:%s | 房间:%s | %s",
                save.getSlotId(), save.getPlayerName(), roomName, time);
    }

    private void applySnapshot(GameSnapshot snapshot) throws PersistenceException
    {
        restoreWorldItems(snapshot);
        restoreCarriedItems(snapshot);

        Room target = findRoomById(snapshot.getCurrentRoomId());
        if (target != null) {
            player.setCurrentRoom(target);
        }
        player.restoreCarryState(snapshot.getMaxCarryWeight(), snapshot.isAteMagicCookie());
    }

    private void restoreWorldItems(GameSnapshot snapshot) throws PersistenceException
    {
        for (Room room : world.getRooms()) {
            room.clearItems();
        }
        if (itemCatalog == null || snapshot.getRoomItemNamesByRoomId() == null) {
            return;
        }
        for (Map.Entry<String, List<String>> entry : snapshot.getRoomItemNamesByRoomId().entrySet()) {
            Room room = findRoomById(entry.getKey());
            if (room == null || entry.getValue() == null) {
                continue;
            }
            for (String itemName : entry.getValue()) {
                itemCatalog.createItem(itemName).ifPresent(room::addItem);
            }
        }
    }

    private void restoreCarriedItems(GameSnapshot snapshot) throws PersistenceException
    {
        player.getInventory().clear();
        if (itemCatalog == null || snapshot.getCarriedItemNames() == null) {
            return;
        }
        for (String itemName : snapshot.getCarriedItemNames()) {
            itemCatalog.createItem(itemName).ifPresent(player.getInventory()::add);
        }
    }

    private Room findRoomById(String id)
    {
        return world.getRooms().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void movePlayerTo(Room next, boolean skipTeleport)
    {
        player.setCurrentRoom(next);
        if (!skipTeleport && next.getRoomType() == RoomType.TELEPORT) {
            Room randomRoom = world.getRandomRoomExcept(next);
            player.setCurrentRoom(randomRoom);
        }
    }

    private List<String> buildEnterRoomMessages(Room room)
    {
        List<String> msgs = new ArrayList<>();
        msgs.add(room.getLongDescription());
        if (room.getRoomType() == RoomType.TELEPORT) {
            msgs.add("⚡ 传送大厅把你随机送到了另一个地方！");
            msgs.add(player.getCurrentRoom().getLongDescription());
        }
        return msgs;
    }

    private static List<String> prepend(String first, List<String> rest)
    {
        List<String> all = new ArrayList<>();
        all.add(first);
        all.addAll(rest);
        return all;
    }

}
