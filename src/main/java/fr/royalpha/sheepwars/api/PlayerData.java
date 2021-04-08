package fr.royalpha.sheepwars.api;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.data.DataManager;
import fr.royalpha.sheepwars.core.exception.UnknownKitException;
import fr.royalpha.sheepwars.core.handler.PlayableMap;
import fr.royalpha.sheepwars.core.handler.Sounds;
import fr.royalpha.sheepwars.core.kit.NoneKit;
import fr.royalpha.sheepwars.core.kit.RandomKit;
import fr.royalpha.sheepwars.core.manager.ConfigManager;
import fr.royalpha.sheepwars.core.manager.ExceptionManager;
import fr.royalpha.sheepwars.core.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;

public class PlayerData extends DataManager {

    private static Map<OfflinePlayer, PlayerData> dataMap;
    private static ArrayList<OfflinePlayer> particlePlayers;

    static {
        dataMap = new HashMap<>();
        particlePlayers = new ArrayList<>();
    }

    private boolean loaded = false;

    private OfflinePlayer player;
    private String uid;
    private String name;
    private Language language;
    private boolean particle;
    private boolean cancelMove;
    private int wins;
    private int kills;
    private int actualKills;
    private int deaths;
    private int games;
    private int sheepThrown;
    private int sheepKilled;
    private int totalTime;
    private boolean wasRandomKit;
    private SheepWarsKit kit;
    private Map<SheepWarsKit, Integer> kits;
    private SheepWarsTeam team;
    private String winRate;
    private String kdRatio;
    private PlayableMap votedMap;
    private Date updatedAt;
    private Date createdAt;

    public PlayerData(final OfflinePlayer player) {
        this.player = player;
        this.uid = player.getUniqueId().toString().replace("-", "");
        this.name = player.getName();
        this.language = Language.getDefaultLanguage();
        this.particle = true;
        this.wins = 0;
        this.kills = 0;
        this.actualKills = 0;
        this.deaths = 0;
        this.games = 0;
        this.sheepThrown = 0;
        this.sheepKilled = 0;
        this.totalTime = 0;
        this.wasRandomKit = false;
        this.kit = new NoneKit();
        this.kits = new HashMap<>();
        this.kits.put(this.kit, 0);
        this.team = SheepWarsTeam.NULL;
        this.winRate = "0.0";
        this.kdRatio = "0.0";
        this.votedMap = null;
        final Date now = new Date(System.currentTimeMillis());
        this.updatedAt = now;
        this.createdAt = now;
    }

    public OfflinePlayer getOfflinePlayer() {
        return this.player;
    }

    public Player getPlayer() {
        return this.player.getPlayer();
    }

    public String getUID() {
        return this.uid;
    }

    public String getName() {
        return this.name;
    }

    public Language getLanguage() {
        return this.language;
    }

    public Boolean getAllowedParticles() {
        return this.particle;
    }

    public int getWins() {
        return this.wins;
    }

    public int getKills() {
        return this.kills;
    }

    public int getActualKills() {
        return this.actualKills;
    }

    public boolean wasRandomKitSelection() {
        return this.wasRandomKit;
    }

    public SheepWarsKit getKit() {
        return this.kit;
    }

    public PlayableMap getVotedMap() {
        return this.votedMap;
    }

    public List<SheepWarsKit> getKits() {
        List<SheepWarsKit> kits = new ArrayList<>(this.kits.keySet());
        for (SheepWarsKit kit : this.kits.keySet())
            if (kit.isFreeKit())
                kits.remove(kit);
        return kits;
    }

    private String getKitsString() {
        return getKitsString(false);
    }

    private String getKitsString(boolean allKitsForFree) {
        StringBuilder output = new StringBuilder("");
        for (SheepWarsKit k : this.kits.keySet()) {
            if (k.getId() != this.kit.getId() && !k.isFreeKit()) {
                output.append(k.getId());
                output.append(",");
                output.append(this.kits.get(k));
                output.append("-");
            }
        }
        output.append(this.kit.getId());
        output.append(",");
        output.append(getKitLevel(this.kit));
        if (wasRandomKit) {
            output.append("-");
            output.append(new RandomKit().getId());
            output.append(",0");
        }
        return output.toString().trim();
    }

    public SheepWarsTeam getTeam() {
        return this.team;
    }

    public int getDeaths() {
        return this.deaths;
    }

    public int getGames() {
        return this.games;
    }

    public int getSheepThrown() {
        return this.sheepThrown;
    }

    public int getSheepKilled() {
        return this.sheepKilled;
    }

    public int getTotalTime() {
        return this.totalTime;
    }

    public String getKDRatio() {
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        this.kdRatio = decimalFormat.format((double) this.kills / this.deaths);
        return this.kdRatio;
    }

    public String getWinRate() {
        DecimalFormat decimalFormat = new DecimalFormat("###.##");
        this.winRate = decimalFormat.format((double) this.wins * 100 / this.games);
        return this.winRate;
    }

    public Date getUpdatedAt() {
        return this.updatedAt;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public void setUpdatedAt(final Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(final Date createdAt) {
        this.createdAt = createdAt;
    }

    public void disableMovements(final boolean bool) {
        this.cancelMove = bool;
        if (this.player.isOnline()) {
            SheepWarsPlugin.getVersionManager().getNMSUtils().cancelMove(getPlayer(), bool);
            if (bool)
                SheepWarsPlugin.getVersionManager().getTitleUtils().actionBarPacket(getPlayer(), ""); // Empeche de voir le message 'press E to dismount' de l'armorstand
        }
    }

    public boolean hasMovementsDisabled() {
        return this.cancelMove;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setAllowParticles(final Boolean particle) {
        this.particle = particle;
        if (particle) {
            if (!particlePlayers.contains(this.player))
                particlePlayers.add(this.player);
            if (this.player.isOnline())
                this.getPlayer().resetPlayerWeather();
        } else {
            if (particlePlayers.contains(this.player))
                particlePlayers.remove(this.player);
            if (this.player.isOnline())
                this.getPlayer().setPlayerWeather(WeatherType.CLEAR);
        }
    }

    public void setVotedMap(final PlayableMap map) {
        this.votedMap = map;
    }

    public void setLanguage(final Language lang) {
        this.language = lang;
        if (player.isOnline()) {
            if (GameState.isStep(GameState.WAITING)) {
                lang.equipPlayer(getPlayer());
            }
            getPlayer().sendMessage(ChatColor.GRAY + lang.getIntro());
        }
        getPlayer().setScoreboard(lang.getScoreboardWrapper().getScoreboard());
    }

    public void setKills(final int i) {
        this.kills = i;
    }

    public int getKitLevel(SheepWarsKit kit) {
        if (ConfigManager.getBoolean(ConfigManager.Field.ENABLE_ALL_KITS))
            return kit.getLevels().size() - 1;
        if (hasKit(kit)) {
            return this.kits.get(kit);
        } else {
            return -1;
        }
    }

    public int getKitLevel() {
        return getKitLevel(this.kit);
    }

    public boolean hasKit(SheepWarsKit kit) {
        if (ConfigManager.getBoolean(ConfigManager.Field.ENABLE_ALL_KITS))
            return true;
        return this.kits.containsKey(kit);
    }

    public void setRandomKitSelection(boolean bool) {
        this.wasRandomKit = bool;
    }

    public void setKit(SheepWarsKit kit, Integer level) {
        setKit(kit, level, false);
    }

    public void setKit(SheepWarsKit kit, Integer level, boolean lastOne) {
        try {
            kit = SheepWarsKit.getInstanceKit(kit);
        } catch (UnknownKitException e) {
            ExceptionManager.register(e, true);
            return;
        }
        this.kit = kit;
        if (!ConfigManager.getBoolean(ConfigManager.Field.ENABLE_ALL_KITS))
            addKit(this.kit, level);
        if (this.player.isOnline()) {
            String message;
            if (!lastOne) {
                message = Message.getMessage(this.player.getPlayer(), Message.Messages.KIT_CHOOSE_MESSAGE);
            } else {
                message = Message.getMessage(this.player.getPlayer(), Message.Messages.KIT_LAST_SELECTED);
            }
            message = message.replaceAll("%KIT_NAME%", kit.getName(this.language));
            String levelMessage;
            if (kit.getLevels().size() > 1) {
                levelMessage = new String(kit.getLevel(level).getName(this.language));
            } else {
                levelMessage = "";
            }
            message = message.replaceAll("%LEVEL_NAME%", levelMessage);
            this.player.getPlayer().sendMessage(message);
            Sounds.playSound(getPlayer(), Sounds.STEP_WOOD, 1f, 0f);
        }
    }

    public void addKit(SheepWarsKit kit, Integer level) {
        try {
            kit = SheepWarsKit.getInstanceKit(kit);
        } catch (UnknownKitException e) {
            ExceptionManager.register(e, true);
            return;
        }
        if (this.kits.containsKey(kit))
            this.kits.remove(kit);
        this.kits.put(kit, level);
    }

    public void removeKit(SheepWarsKit kit) {
        try {
            kit = SheepWarsKit.getInstanceKit(kit);
        } catch (UnknownKitException e) {
            ExceptionManager.register(e, true);
            return;
        }
        if (this.kits.containsKey(kit))
            this.kits.remove(kit);
        if (this.kit == kit)
            setKit(new NoneKit(), 0);
    }

    public void setTeam(SheepWarsTeam team) {
        if (this.player.isOnline()) {
            if (this.team != null)
                this.team.removePlayer(this.player.getPlayer());
            this.team = team;
            team.addPlayer(this.player.getPlayer());
        }
    }

    public void setWins(final int i) {
        this.wins = i;
    }

    public void setDeaths(final int i) {
        this.deaths = i;
    }

    public void setGames(final int i) {
        this.games = i;
    }

    public void setSheepThrown(final int i) {
        this.sheepThrown = i;
    }

    public void setSheepKilled(final int i) {
        this.sheepKilled = i;
    }

    public void setTotalTime(final int i) {
        this.totalTime = i;
    }

    public void increaseWins(final int wins) {
        this.wins += wins;
    }

    public void increaseKills(final int kills) {
        this.kills += kills;
        this.actualKills++;
    }

    public void increaseDeaths(final int deaths) {
        this.deaths += deaths;
    }

    public void increaseGames(final int games) {
        this.games += games;
    }

    public void increaseSheepThrown(final int sheepThrown) {
        this.sheepThrown += sheepThrown;
    }

    public void increaseSheepKilled(final int sheepKilled) {
        this.sheepKilled += sheepKilled;
    }

    public void increaseTotalTime(final int totalTime) {
        this.totalTime += totalTime;
    }

    public boolean hasTeam() {
        return (this.team != null && this.team != SheepWarsTeam.SPEC && this.team != SheepWarsTeam.NULL);
    }

    public boolean isSpectator() {
        return (this.team != null && this.team == SheepWarsTeam.SPEC);
    }

    @Override
    public String toString() {
        return "PlayerData(" + "uid=" + this.uid + ", name=" + this.name + ", locale=" + this.language.getLocale() + ", particle=" + this.particle + ", wins=" + this.wins + ", kills=" + this.kills + ", deaths=" + this.deaths + ", games=" + this.games + ", sheepThrown=" + this.sheepThrown + ", sheepKilled=" + this.sheepKilled + ", totalTime=" + this.totalTime + ", actualKills=" + this.actualKills + ", lastKit=" + this.kit.getId() + ", winRate=" + this.winRate + ", kdRatio=" + this.kdRatio + ", updatedAt=" + this.updatedAt + ", createdAt=" + this.createdAt + ")";
    }

    /**
     * Get player's data.
     *
     * @return player's data.
     */
    public static PlayerData getPlayerData(final OfflinePlayer player) {
        PlayerData playerData = new PlayerData(player);
        if (!dataMap.containsKey(player))
            playerData.loadData();
        return dataMap.get(player);
    }

    @Override
    public void loadData() {
        if (connectedToDatabase) {
            SheepWarsPlugin.debug("Fetching data for " + player.getName() + " ...");
            final String table = ConfigManager.getString(ConfigManager.Field.MYSQL_TABLE);
            new Thread(() -> {
                // Check validity
                checkConnection();
                // Select identifier
                String identifier = getIdentifier();
                // Manage data
                try {
                    ResultSet res = database.querySQL("SELECT * FROM " + table + " WHERE " + identifier);
                    if (res.first()) {
                        setDeaths(res.getInt("deaths"));
                        setGames(res.getInt("games"));
                        setKills(res.getInt("kills"));
                        setAllowParticles(res.getInt("particles") == 1);
                        setWins(res.getInt("wins"));
                        setSheepThrown(res.getInt("sheep_thrown"));
                        setSheepKilled(res.getInt("sheep_killed"));
                        setTotalTime(res.getInt("total_time"));
                        final String kits = res.getString("kits");
                        if (!kits.trim().equals("")) {
                            if (kits.contains("-")) {
                                String[] availableKits = kits.split("-");
                                for (int i = 0; i < availableKits.length - 1; i++) {
                                    String kitString = availableKits[i];
                                    String[] kitStringSplitted = kitString.split((kitString.contains(",") ? "," : ""));
                                    final int kitId = Integer.parseInt(kitStringSplitted[0]);
                                    final int kitLevel = Integer.parseInt(kitStringSplitted[1]);
                                    if (SheepWarsKit.existKit(kitId)) {
                                        addKit(SheepWarsKit.getFromId(kitId), kitLevel);
                                    }
                                }
                                String[] lastKit = availableKits[availableKits.length - 1].split((availableKits[availableKits.length - 1].contains(",") ? "," : ""));
                                setKit(SheepWarsKit.getFromId(Integer.parseInt(lastKit[0])), Integer.parseInt(lastKit[1]), true);
                            } else if (kits.contains(",")) {
                                String[] kit = kits.split(",");
                                if (SheepWarsKit.existKit(Integer.parseInt(kit[0]))) {
                                    setKit(SheepWarsKit.getFromId(Integer.parseInt(kit[0])), Integer.parseInt(kit[1]), true);
                                }
                            } else {
                                String[] availableKits = kits.split("");
                                for (String kitId : availableKits)
                                    if (SheepWarsKit.existKit(Integer.parseInt(kitId)))
                                        addKit(SheepWarsKit.getFromId(Integer.parseInt(kitId)), 0);
                                setKit(SheepWarsKit.getFromId(Integer.parseInt(availableKits[availableKits.length - 1])), 0, true);
                            }
                        }
                        setUpdatedAt(res.getDate("updated_at"));
                        setCreatedAt(res.getDate("created_at"));
                    }
                    res.close();
                } catch (ClassNotFoundException | SQLException | NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                    ExceptionManager.register(ex, true);
                }
                this.loaded = true;
            }).start();
            SheepWarsPlugin.debug("Data fetched for " + player.getName() + "!");
        } else {
            this.loaded = true;
        }
        particlePlayers.add(player);
        dataMap.put(player, this);
    }

    @Override
    public void uploadData() {
        if (connectedToDatabase) {
            final String table = ConfigManager.getString(ConfigManager.Field.MYSQL_TABLE);
            SheepWarsPlugin.debug("Uploading data for " + player.getName() + " ...");
            //if (!uploadingData.contains(this))
            //    uploadingData.add(this);
            // Check validity
            checkConnection();
            // Select identifier
            String identifier = getIdentifier();
            String uuid = this.uid;
            // Manage data
            try {
                ResultSet res = database.querySQL("SELECT * FROM " + table + " WHERE " + identifier);
                if (res.first()) {
                    database.updateSQL("UPDATE " + table + " SET uuid='" + uuid + "', name='" + this.name + "', wins=" + this.wins + ", kills=" + this.kills + ", deaths=" + this.deaths + ", games=" + this.games + ", sheep_thrown=" + this.sheepThrown + ", sheep_killed=" + this.sheepKilled + ", total_time=" + this.totalTime + ", particles=" + (this.particle ? "1" : "0") + ", kits='" + getKitsString(ConfigManager.getBoolean(ConfigManager.Field.ENABLE_ALL_KITS)) + "'" + ", updated_at=NOW() WHERE " + identifier);
                } else {
                    database.updateSQL("INSERT INTO " + table + "(name, uuid, wins, kills, deaths, games, sheep_thrown, sheep_killed, total_time, particles, kits, created_at, updated_at) VALUES('" + this.name + "', '" + uuid + "', " + this.wins + ", " + this.kills + ", " + this.deaths + ", " + this.games + ", " + this.sheepThrown + ", " + this.sheepKilled + ", " + this.totalTime + ", " + (this.particle ? "1" : "0") + ", '" + getKitsString(ConfigManager.getBoolean(ConfigManager.Field.ENABLE_ALL_KITS)) + "', NOW(), NOW())");
                }
                res.close();
            } catch (ClassNotFoundException | SQLException ex) {
                ExceptionManager.register(ex, true);
            } finally {
                //if (uploadingData.contains(this))
                //    uploadingData.remove(this);
                SheepWarsPlugin.debug("Data uploaded for " + player.getName() + "!");
            }
        }
    }

    public void asyncUploadData() {
        new Thread(() -> {
            uploadData();
        }).start();
    }

    public String getIdentifier() {
        String identifier = "name='" + this.name + "'";
        if (Bukkit.getServer().getOnlineMode()) {
            try {
                ResultSet resIdentifier = database.querySQL("SELECT * FROM players WHERE " + identifier);
                if (resIdentifier.first()) {
                    String uuidStr = resIdentifier.getString("uuid");
                    if (!uuidStr.split("")[12].equals("3")) { // Online UUID
                        identifier = "uuid='" + uuidStr + "'"; // Dans ce cas on peut se baser sur l'UUID
                    }
                }
                resIdentifier.close();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
        return identifier;
    }

    public static List<OfflinePlayer> getParticlePlayers() {
        return particlePlayers;
    }

    public static Set<OfflinePlayer> getPlayers() {
        return dataMap.keySet();
    }

    public static Set<Entry<OfflinePlayer, PlayerData>> getEntries() {
        return dataMap.entrySet();
    }

    public static Collection<PlayerData> getDatas() {
        return dataMap.values();
    }

    public static boolean hasEnabledParticles(Player player) {
        return particlePlayers.contains(player);
    }

    public enum DataType {

        GAMES_PLAYED(0, Message.Messages.STATS_GAME_PLAYED, "games"),
        TOTAL_DEATHS(1, Message.Messages.STATS_DEATH, "deaths"),
        PLAYERS_KILLED(2, Message.Messages.STATS_KILL, "kills"),
        SHEEP_THROWN(3, Message.Messages.STATS_SHEEP_THROWN, "sheep_thrown"),
        SHEEP_KILLED(4, Message.Messages.STATS_SHEEP_KILLED, "sheep_killed"),
        TOTAL_TIME(5, Message.Messages.STATS_TOTAL_TIME, "total_time"),
        GAMES_WON(6, Message.Messages.STATS_VICTORY, "wins");

        private int id;
        private Message message;
        private String tableColumn;
        private LinkedHashMap<String, Integer> playerTop;

        private DataType(int id, Message.Messages msgEnum, String tableColumn) {
            this.id = id;
            this.message = Message.getMessage(msgEnum);
            this.tableColumn = tableColumn;
            this.playerTop = new LinkedHashMap<>();
        }

        public Message getMessage() {
            return this.message;
        }

        public int getTopSize() {
            return this.playerTop.size();
        }

        public int after() {
            int after = this.id + 1;
            if (after >= values().length) {
                return 0;
            } else {
                return after;
            }
        }

        public int before() {
            int before = this.id - 1;
            if (before < 0) {
                return (values().length - 1);
            } else {
                return before;
            }
        }

        public void generateRanking() {
            ResultSet res = null;
            try {
                res = database.querySQL("SELECT `name`,`" + this.tableColumn + "` FROM `players` ORDER BY `" + this.tableColumn + "` DESC ;");
                while (res.next()) {
                    this.playerTop.put(res.getString("name"), res.getInt(this.tableColumn)); // Il voit la bonne liste, dans l'ordre
                }
            } catch (SQLException | ClassNotFoundException ex) {
                ExceptionManager.register(ex, true);
            } finally {
                try {
                    if (res != null)
                        res.close();
                } catch (SQLException ex) {
                    ExceptionManager.register(ex, true);
                }
            }
        }

        public Map<String, Integer> getRanking(int limit) {
            Map<String, Integer> output = new LinkedHashMap<>();
            int i = 0;
            for (Entry<String, Integer> entry : this.playerTop.entrySet()) {
                if (i >= limit)
                    break;
                output.put(entry.getKey(), entry.getValue());
                i++;
            }
            return output;

        }

        public static DataType getFromId(int id) {
            for (DataType data : values())
                if (data.id == id)
                    return data;
            return null;
        }
    }
}
