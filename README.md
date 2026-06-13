# World of Zuul — 功能说明、测试与扩展指南

本文档说明本项目在样例工程基础上**新增的游戏内容、UI 界面、代码结构**，以及如何**测试**与**后续扩展**。面向小组开发与实训验收，与根目录 `README.md`（实训任务要求）互补。

---

## 1. 架构概览

项目采用**分层结构**，核心逻辑与界面分离，便于分工与单元测试。

```
cn.edu.whut.sept.zuul
├── Main.java              # 入口：默认 JavaFX；--console 为文本模式
├── model/                 # 领域模型（房间、玩家、物品、地图）
├── engine/                # 游戏引擎（规则、历史、命令结果）
├── command/               # 命令模式（各游戏指令）
├── io/                    # 输入解析（不含界面输出）
├── console/               # 控制台模式
├── ui/                    # JavaFX 界面
└── persistence/           # 存档接口（供数据库模块接入）
```

**数据流（简化）：**

```
用户操作（按钮 / 命令行）
    → Parser 解析为 Command
    → Command.execute(GameEngine)
    → CommandResult（消息 + 是否结束 + 是否刷新状态）
    → Console 打印 或 GameController 刷新界面
```

---

## 2. 新增游戏内容

### 2.1 地图与房间

共 **7 个房间**，起点为「大学主入口外」（`outside`）。

| 房间 ID | 描述 | 特殊说明 |
|---------|------|----------|
| outside | 大学主入口外 | 起点 |
| theater | 演讲厅 | 含物品「讲义」 |
| pub | 校园酒吧 | 含「啤酒杯」；可通往仓库 |
| lab | 计算机实验室 | 含「键盘」 |
| office | 管理办公室 | 含「钥匙」 |
| storage | 旧仓库 | 含「木箱」（较重） |
| teleport | 传送大厅 | **进入后随机传送到其他房间** |

**主要连通关系：**

- outside ↔ east/theater，south/lab，west/pub，north/teleport  
- lab ↔ east/office  
- pub ↔ north/storage  

地图与初始物品在 `model/World.java` 的 `buildDefaultWorld()` 中定义，修改剧情或布局时优先改此类。

### 2.2 物品与负重

- 每个物品有：**名称、描述、重量（kg）**（见 `model/Item.java`）。
- 玩家默认负重上限：**10 kg**（`GameEngine.DEFAULT_MAX_WEIGHT`）。
- 拾取超重时拒绝并提示，物品仍留在房间内。
- **魔法饼干**：每次启动游戏时随机出现在 `pub`、`storage`、`office` 之一；重量 0.5 kg。
- 食用后（`吃 魔法饼干`）：负重上限提升至 **20 千克**，且不可重复食用。

### 2.3 玩家

`model/Player.java` 保存：

- 姓名（默认「探险者」）
- 当前房间
- 背包（`Inventory`）
- 是否已吃魔法饼干

### 2.4 命令一览

| 命令 | 作用 | 示例 |
|------|------|------|
| `去 <方向>` | 移动到相邻房间（会压入回退栈） | `去 东` |
| `查看` | 查看当前房间详情与物品 | `查看` |
| `返回` | 返回上一房间（可连续多次回退） | `返回` |
| `拾取 <物品名>` | 拾取房间物品 | `拾取 钥匙` |
| `丢弃 <物品名>` | 丢弃身上指定物品 | `丢弃 讲义` |
| `丢弃` | 丢弃身上全部物品 | `丢弃` |
| `物品` | 列出房间与背包物品及重量 | `物品` |
| `吃 魔法饼干` | 食用魔法饼干 | `吃 魔法饼干` |
| `帮助` | 显示可用命令 | `帮助` |
| `存档 [槽位]` | 保存游戏（默认槽位 `默认`） | `存档 槽位1` |
| `读档 [槽位]` | 读取存档 | `读档 槽位1` |
| `退出` | 退出游戏 | `退出` |

方向为：**东、西、南、北**（见 `World.DIR_*` 常量）。

命令在 `command/CommandWords.java` 注册；业务实现在 `engine/GameEngine.java` 或各 `*Command.java`。

### 2.5 回退与传送

- **back**：`engine/RoomHistory.java` 使用栈记录经过的房间；`go` 成功移动时入栈，`back` 时出栈。已在起点且无历史时提示无法返回。
- **传送房间**：进入 `teleport` 后，引擎将玩家**随机**放到其他任一房间（见 `World.getRandomRoomExcept`），日志中会提示传送结果。

---

## 3. UI 界面说明（JavaFX）

### 3.1 相关文件

| 文件 | 作用 |
|------|------|
| `ui/GameApp.java` | JavaFX 应用入口，加载 FXML 与样式 |
| `ui/GameController.java` | 界面逻辑：按钮事件、调用引擎、刷新视图 |
| `resources/fxml/main.fxml` | 界面布局 |
| `resources/css/game.css` | 样式（配色、按钮等） |

### 3.2 界面区域

- **顶部**：标题、玩家名  
- **左侧**：当前房间描述、**动态出口按钮**（根据房间出口生成）、房间物品列表、拾取 / 查看物品  
- **右侧**：背包列表、负重进度条、丢弃 / 全部丢弃、吃魔法饼干、存档 / 读档  
- **底部**：游戏日志、命令输入框、执行 / 帮助 / 环顾 / 返回 / 退出  

### 3.3 操作与命令的关系

界面按钮内部统一调用 `GameEngine.processCommandLine(...)`，与控制台模式共用同一套规则，避免「界面一套、命令一套」的不一致。

例如：

- 点击 `东` → 等价 `去 东`  
- 选中房间物品后点「拾取」→ `拾取 <物品名>`  
- 「存档」→ `存档 默认`  

### 3.4 启动方式

**环境要求：** JDK **11 及以上**（`pom.xml` 已配置 Java 11 + JavaFX 21）。

```bash
# 图形界面（推荐）
mvn javafx:run

# 在 IDE 中直接运行 cn.edu.whut.sept.zuul.Main

# 控制台文本模式
mvn exec:java -Dexec.mainClass="cn.edu.whut.sept.zuul.Main" -Dexec.args="--console"
```

打包：

```bash
mvn package
java -jar target/zuul-1.0-SNAPSHOT.jar
```

> 若本机未配置 `mvn` 命令，请安装 Maven 并将 `bin` 加入 PATH，或在 IDE 中使用内置 Maven 运行上述目标。

---

## 4. 持久化接口（数据库组员）

当前默认使用 `persistence/InMemoryGameStateRepository`（进程内内存，重启后丢失）。

### 4.1 扩展接口

| 类型 | 说明 |
|------|------|
| `GameStateRepository` | 存档 CRUD 接口，数据库实现此接口即可 |
| `GameSnapshot` | 与存储介质无关的快照 DTO |
| `PersistenceException` | 持久化异常 |

### 4.2 接入步骤

1. 新建类，例如 `JdbcGameStateRepository`，实现 `GameStateRepository` 的 `save` / `load` / `exists` / `delete`。  
2. 在 `GameController.init()`（或统一工厂类）中注入：

   ```java
   engine = new GameEngine(new World(), "探险者", new JdbcGameStateRepository());
   ```

3. UI 的「存档」「读档」及命令 `save` / `load` **无需修改**。  
4. 完整恢复物品分布时，可在引擎的 `applySnapshot` 中扩展逻辑，或结合数据库中的物品模板表实现。

`GameEngine.createSnapshot()` 已收集：玩家名、当前房间 ID、负重上限、是否吃过饼干、各房间物品名列表等，供持久化层序列化。

---

## 5. 如何测试

### 5.1 自动化单元测试

测试代码位于 `src/test/java`，使用 **JUnit 5**。

| 测试类 | 覆盖内容 |
|--------|----------|
| `model/InventoryTest` | 背包超重时拒绝添加物品 |
| `engine/GameEngineTest` | `去 东` 进入演讲厅；`返回` 返回主入口 |

运行：

```bash
mvn test
```

通过即表示核心领域逻辑正常。建议在新增命令或修改 `GameEngine` 后补充对应用例。

### 5.2 控制台模式手工测试

```bash
mvn exec:java -Dexec.mainClass="cn.edu.whut.sept.zuul.Main" -Dexec.args="--console"
```

建议按下列清单逐项验证：

1. 启动后显示欢迎语与 outside 房间描述。  
2. `去 东` → 进入演讲厅；`返回` → 回到主入口外。  
3. `拾取 讲义` → 成功；`物品` → 背包含讲义。  
4. `丢弃 讲义` → 物品回到房间（若仍在演讲厅）。  
5. 拾取超重物品（如先拿木箱再拿键盘）→ 提示超重。  
6. 找到魔法饼干后 `吃 魔法饼干` → 负重上限变为 20。  
7. `去 北` 进入传送大厅 → 随机到其他房间。  
8. `存档 默认` / `读档 默认` → 内存存档提示成功（重启进程后内存档消失，属预期）。  
9. `退出` → 正常退出。

### 5.3 图形界面手工测试

使用 `mvn javafx:run` 或 IDE 运行 `Main`：

1. 启动后左侧显示房间信息，出口按钮与当前房间一致。  
2. 点击方向按钮，描述与按钮组随之更新。  
3. 在房间物品列表选中一项 →「拾取」→ 右侧背包出现，负重条变化。  
4. 背包选中 →「丢弃」→ 物品回到房间列表。  
5. 「吃魔法饼干」在无饼干时提示；有饼干后负重上限与标签更新。  
6. 底部输入 `帮助`、`查看` 等，日志区有输出。  
7. 「存档」「读档」在日志中有反馈。  
8. 「退出」关闭窗口。

### 5.4 建议补充的测试（后续）

| 方向 | 说明 |
|------|------|
| `TakeCommand` / `BackCommand` | 对命令类的集成测试 |
| `World` 传送房间 |  mock 或统计随机结果 |
| UI 测试 | TestFX（可选，实训非必须） |
| 持久化 | 数据库实现类 + H2/MySQL 集成测试 |

---

## 6. 后续功能如何扩展

### 6.1 增加新命令

1. 在 `command` 包新建 `XxxCommand extends Command`。  
2. 在 `CommandWords` 构造函数中 `register("xxx", new XxxCommand())`。  
3. 在 `GameEngine` 中实现具体逻辑，返回 `CommandResult`。  
4. 如需 GUI 按钮，在 `GameController` 与 `main.fxml` 增加按钮并调用 `runCommand("xxx ...")`。  
5. 在 `src/test/java` 增加测试。

### 6.2 增加房间、物品或剧情

- 编辑 `model/World.java` 的 `buildDefaultWorld()`。  
- 新房间类型：扩展 `RoomType` 枚举，在 `GameEngine.movePlayerTo` 或进入房间后的逻辑中分支处理。  
- 上锁房间、NPC、任务等：建议在 `model` 增加实体类，由 `GameEngine` 调度，避免写在 UI 里。

### 6.3 调整 UI

- 布局：改 `resources/fxml/main.fxml`。  
- 样式：改 `resources/css/game.css`。  
- 新面板 / 地图可视化：可加 FXML 子面板，Controller 中订阅 `GameEngine` 状态刷新。  
- **原则：** Controller 只负责展示与转发输入，规则仍在 `GameEngine`。

### 6.4 多人网络（README 可选需求）

建议新建包 `network`，复用 `GameEngine` 或抽取纯逻辑服务：

- 服务端维护权威 `GameEngine` 实例。  
- 客户端 UI 发送命令字符串或 DTO，接收 `CommandResult` 同步状态。  
- 不要直接在 JavaFX 线程做 Socket 阻塞 IO。

### 6.5 数据库与配置

- 实现 `GameStateRepository`，必要时增加 `ItemCatalog` 等表结构恢复完整背包。  
- 用户设置、语言包可放 `resources/i18n` + `Preferences` 或数据库，与引擎解耦。

### 6.6 CI / 打包（实训要求）

在仓库 `.github/workflows` 中可增加：

```yaml
# 示例步骤
- run: mvn -B test
- run: mvn -B package
```

确保 PR 合并前 `mvn test` 通过；发布时使用 `target/zuul-1.0-SNAPSHOT.jar` 或 `javafx-maven-plugin` 打可执行包。

---

## 7. 目录速查

```
src/
├── 项目开发指南.md          # 本文档
├── main/java/.../zuul/     # 源代码（见第 1 节包结构）
├── main/resources/
│   ├── fxml/main.fxml
│   └── css/game.css
└── test/java/.../          # JUnit 测试
```

---

## 8. 常见问题

**Q：运行 Main 提示 JavaFX 相关错误？**  
A：确认 JDK ≥ 11，且使用 `mvn javafx:run` 或 IDE 正确引入 OpenJFX 依赖（见 `pom.xml`）。

**Q：存档后重启游戏存档没了？**  
A：默认 `InMemoryGameStateRepository` 仅保存在内存。需由数据库组员实现 `GameStateRepository` 才能持久化到磁盘/数据库。

**Q：魔法饼干找不到？**  
A：每次启动随机落在 pub / storage / office 之一，需探索这三个区域。

**Q：中文物品名拾取失败？**  
A：使用完整名称，如 `拾取 魔法饼干`；GUI 拾取会从列表解析名称，一般无此问题。

---

## 9. 修订记录

| 日期 | 说明 |
|------|------|
| 2026-06 | 初版：JavaFX UI、物品/玩家/传送/back、持久化接口、JUnit 测试 |

如有新功能合并，请在本节补充日期与变更摘要，便于小组报告与答辩引用。
