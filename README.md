---

## 📄 MyStatsExpansion

一个轻量级的 **PlaceholderAPI 扩展**，直接从 Minecraft 原版统计文件（`world/stats/*.json`）读取数据，为排行榜插件（如 TAB、ScoreboardStats 等）提供**前 N 名玩家名和数值**的占位符变量。

---

### 🎯 功能特点

- ✅ **直接读取 `stats` 文件** — 无需额外数据库，即装即用
- ✅ **支持 6 大榜单** — 挖掘榜、放置榜、死亡榜、击杀生物榜、击杀玩家榜、在线时间榜
- ✅ **累加多 UUID 数据** — 自动合并同一玩家的正版/离线数据
- ✅ **无终端日志** — 静默运行，不污染控制台
- ✅ **完全兼容 PAPI** — 提供标准 PlaceholderAPI 变量

---

### 📦 安装方法

1. **下载** `MyStatsExpansion-1.0-SNAPSHOT.jar`
2. 放入服务器目录：`plugins/PlaceholderAPI/expansions/`
3. 执行 `/papi reload` 加载扩展
4. 验证：`/papi list` 应显示 `mystats`

---

### 🔧 变量格式

```
%mystats_<榜单名>_<排名>_name%    # 返回指定排名的玩家名
%mystats_<榜单名>_<排名>_value%   # 返回指定排名的数值
```

#### 支持的榜单名

| 榜单名 | 说明 | 数据来源 |
| :--- | :--- | :--- |
| `挖掘榜` | 累计挖掘方块总数 | `minecraft:mined` |
| `放置榜` | 累计放置方块总数 | `minecraft:used` |
| `死亡榜` | 累计死亡次数 | `minecraft:custom` → `deaths` |
| `击杀生物榜` | 累计击杀生物总数 | `minecraft:custom` → `mob_kills` |
| `击杀玩家榜` | 累计击杀玩家总数 | `minecraft:custom` → `player_kills` |
| `在线时间榜` | 累计在线时长（tick） | `minecraft:custom` → `play_time` |

#### 示例

```bash
/papi parse nihility_C %mystats_挖掘榜_1_name%   # → nihility_C
/papi parse nihility_C %mystats_挖掘榜_1_value%  # → 338752
```

---

### 🧩 与 TAB 插件配合使用

在 `plugins/TAB/config.yml` 的 `scoreboard` 部分使用变量：

```yaml
scoreboards:
  mining:
    title: "&c&l⛏️ 挖掘排行榜"
    lines:
      - "&f%mystats_挖掘榜_1_name%||&c%mystats_挖掘榜_1_value%"
      - "&f%mystats_挖掘榜_2_name%||&c%mystats_挖掘榜_2_value%"
      # ... 直至第10名
```

> `||` 是 TAB 1.20.3+ 的左右分隔符，左侧左对齐，右侧右对齐。

---

### ⚙️ 自定义榜单名称

如需修改榜单名称（例如将 `挖掘榜` 改为 `挖矿榜`），编辑 `StatsManager.java` 中的常量：

```java
private static final String LIST_MINE = "挖矿榜";  // 改为你想要的名称
```

修改后重新编译打包即可。

---

### 🔨 自行编译

```bash
git clone <你的仓库地址>
cd MyStatsExpansion
mvn clean package
```

生成的 `.jar` 文件位于 `target/MyStatsExpansion-1.0-SNAPSHOT.jar`

---

### 📁 项目结构

```
MyStatsExpansion/
├── pom.xml
└── src/main/java/xyz/cuberliu/myStatsExpansion/
    ├── MyStatsExpansion.java   # PAPI 扩展主类
    └── StatsManager.java       # stats 文件读取与缓存
```

---

### 📝 依赖

| 依赖 | 用途 | 版本要求 |
| :--- | :--- | :--- |
| Paper API | 服务端 API | 1.21+ |
| PlaceholderAPI | 占位符框架 | 2.11+ |
| Gson | JSON 解析 | 2.10+ |

---

### ⚠️ 注意事项

- 玩家**必须至少登录过一次**才会生成 `stats` 文件，否则不会出现在榜单中。
- 同一玩家的正版/离线 UUID 数据会自动累加。
- 本插件不依赖 PlayerTopList，可完全替代其排行榜变量功能。

---

### 📬 反馈与贡献

如有问题或建议，欢迎提交 Issue 或 Pull Request。
