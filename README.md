[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/u1xW62gh)

# 祖尔世界（World of Zuul）

武汉理工大学软件工程实训项目——602 小组在经典文本冒险游戏 **World of Zuul** 样例基础上扩展而成的 **JavaFX 图形化校园迷宫游戏**。项目采用分层架构，实现了完整游戏逻辑、中文交互界面，以及基于 **H2 数据库** 的存档持久化与用户设置。

---

## 小组成员与分工

| 成员 | 主要负责 |
|------|----------|
| **张泽龙** | 整体架构搭建、领域模型与游戏引擎、命令体系、JavaFX UI 设计与实现 |
| **杨昊** | 数据库集成：H2/JDBC 存档、物品模板表、用户设置、存档管理对话框 |
| **付光宁** | 项目测试：单元测试与集成测试编写、用例覆盖与质量保障 |

---

## 功能概览

### 游戏玩法

- 7 个中文场景房间，支持 **东 / 西 / 南 / 北** 移动
- 房间物品拾取与丢弃、负重上限、**魔法饼干** 增重
- **返回** 多层回退、**传送大厅** 随机传送
- 全中文命令与提示

### 图形界面（JavaFX）

- 房间描述、方向按钮（固定顺序：东 → 西 → 南 → 北）
- 房间物品 / 背包列表、负重进度条、游戏日志
- **存档 / 读档 / 删档** 对话框（`SaveSlotsDialog`）
- 启动时后台初始化数据库，失败时自动降级为内存存档

### 数据持久化（已实现）

- **H2 文件数据库**，默认路径：`./data/zuul.mv.db`
- `JdbcGameStateRepository`：保存玩家位置、背包、各房间物品分布、魔法饼干状态等
- `ItemCatalog`：从 `item_catalog` 表还原完整物品对象
- `user_settings` 表：默认玩家名、语言等（`UserSettingsService` / `I18n`）
- 控制台与 GUI 均通过 `GameBootstrap` 统一注入 JDBC 仓库

### 测试

- **24 个测试类**，覆盖 model、engine、command、persistence.jdbc 等包
- 含 `JdbcGameStateRepositoryTest`（H2 内存库集成测试）及大量引擎/命令用例

更细的玩法说明、扩展指南见 [`src/项目开发指南.md`](src/项目开发指南.md)。

---

## 技术栈

| 类别 | 选型 |
|------|------|
| 语言 | Java 17 |
| 构建 | Maven |
| 界面 | JavaFX 21（FXML + CSS） |
| 数据库 | H2 2.2（JDBC，文件模式） |
| 测试 | JUnit 5 |

---

## 项目结构

```
src/main/java/cn/edu/whut/sept/zuul/
├── Main.java                 # 入口（默认 GUI；--console 文本模式）
├── GameBootstrap.java        # 启动装配：数据库 + 引擎
├── model/                    # 房间、玩家、物品、地图
├── engine/                   # GameEngine、CommandResult、RoomHistory
├── command/                  # 中文命令（去/拾取/存档/删档…）
├── io/                       # 命令解析
├── console/                  # 控制台模式
├── ui/                       # GameApp、GameController、SaveSlotsDialog
├── persistence/              # GameStateRepository 接口
│   └── jdbc/                 # DatabaseManager、JdbcGameStateRepository、ItemCatalog
└── settings/                 # 用户设置与国际化

src/main/resources/
├── fxml/main.fxml
├── css/game.css
├── db/schema.sql             # 表结构
├── db/seed-items.sql         # 物品模板初始数据
└── i18n/                     # 中英文文案

src/test/java/                # JUnit 测试
data/                         # H2 运行时数据库文件（本地生成）
```

---

## 快速开始

### 环境要求

- **JDK 17+**（与 `pom.xml` 一致）
- **Maven 3.6+**（或使用 IDE 内置 Maven）

### 运行图形界面（推荐）

```bash
mvn javafx:run
```

或在 IDE 中运行 `cn.edu.whut.sept.zuul.Main`（VS Code 可使用 `.vscode/launch.json` 中的 **Zuul (JavaFX)** 配置）。

### 运行控制台模式

```bash
mvn exec:java -Dexec.mainClass=cn.edu.whut.sept.zuul.Main -Dexec.args="--console"
```

### 打包

```bash
mvn package
```

---

## 命令一览

| 命令 | 说明 | 示例 |
|------|------|------|
| `去 <方向>` | 移动 | `去 东` |
| `查看` | 查看当前房间 | `查看` |
| `返回` | 回到上一房间 | `返回` |
| `拾取 <物品>` | 拾取 | `拾取 钥匙` |
| `丢弃 [物品]` | 丢弃（无参数则全部丢弃） | `丢弃 讲义` |
| `物品` | 查看房间与背包物品 | `物品` |
| `吃 魔法饼干` | 食用魔法饼干 | `吃 魔法饼干` |
| `存档 <槽位>` | 保存到数据库 | `存档 存档1` |
| `读档 <槽位>` | 从数据库读取 | `读档 存档1` |
| `删档 <槽位>` | 删除存档 | `删档 存档1` |
| `存档列表` | 列出所有存档 | `存档列表` |
| `帮助` | 显示命令 | `帮助` |
| `退出` | 退出游戏 | `退出` |

GUI 中「存档 / 读档 / 删档」按钮会打开对话框选择槽位，无需手输槽位名。

---

## 数据库与存档说明

1. 首次运行时在项目根目录 `data/` 下创建 H2 数据库文件。
2. 启动时执行 `schema.sql` 与 `seed-items.sql` 初始化表结构与物品模板。
3. 读档时通过 `ItemCatalog` 将物品名称还原为带描述、重量的完整 `Item` 对象。
4. 若数据库初始化失败，程序会提示并 **降级** 为 `InMemoryGameStateRepository`（重启后存档丢失）；正常环境下存档 **持久保存**。

核心类：

- `persistence.jdbc.DatabaseManager` — 连接与脚本初始化
- `persistence.jdbc.JdbcGameStateRepository` — 存档 CRUD 与列表
- `ui.SaveSlotsDialog` — 图形化存读档管理

---

## 运行测试

```bash
mvn test
```

主要测试覆盖：

| 包 / 类 | 说明 |
|---------|------|
| `model/*Test` | 房间、玩家、背包、地图 |
| `engine/*Test` | 引擎逻辑、回退栈、命令结果 |
| `command/*Test` | 各中文命令 |
| `persistence.jdbc.JdbcGameStateRepositoryTest` | JDBC 存读档、删档、列表 |

本地未安装 Maven 时，可在 IDE 中右键 `src/test/java` 运行全部测试。

---

## 常见问题

**Q：存档重启后还在吗？**  
A：在。默认使用 H2 文件库 `./data/zuul.mv.db`，存档写入磁盘。仅当数据库初始化失败降级到内存模式时，存档不会保留。

**Q：JavaFX 启动报错？**  
A：确认 JDK ≥ 17，且 Maven 已下载 OpenJFX 依赖；IDE 运行需添加模块 `--add-modules javafx.controls,javafx.fxml`（见 `.vscode/launch.json`）。

**Q：魔法饼干在哪？**  
A：每次新开局随机出现在酒吧、仓库或办公室之一，需探索寻找。

**Q：如何修改默认玩家名或语言？**  
A：保存在 `user_settings` 表中，由 `UserSettingsService` 读取；也可在后续扩展设置界面。

---

## 相关文档

- [`src/项目开发指南.md`](src/项目开发指南.md) — 架构细节、手工测试清单、后续扩展说明
- 实训任务要求见课程 GitHub Classroom 说明

---

## 修订记录

| 日期 | 说明 |
|------|------|
| 2026-06-04 | 张泽龙：架构、游戏功能扩展、JavaFX UI |
| 2026-06 | 杨昊：H2/JDBC 持久化、存档对话框、用户设置 |
| 2026-06 | 付光宁：单元测试与集成测试体系 |
| 2026-06 | 更新 README：反映数据库已实现、补充分工说明 |
