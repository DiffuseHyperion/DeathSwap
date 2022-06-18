package tk.yjservers.deathswap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import tk.yjservers.deathswap.Commands.reloadconfig;
import tk.yjservers.deathswap.Commands.start;
import tk.yjservers.deathswap.Commands.swap;
import tk.yjservers.deathswap.Commands.team;
import tk.yjservers.deathswap.Listener.onPlayerDeath;
import tk.yjservers.deathswap.Listener.onPlayerJoin;
import tk.yjservers.deathswap.Listener.onPlayerLeave;
import tk.yjservers.gamemaster.GameMaster;
import tk.yjservers.gamemaster.GameServer;
import tk.yjservers.gamemaster.GameWorld;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public final class DeathSwap extends JavaPlugin {

    public static States state;
    public static World lobby;
    public static World ds1;
    public static World ds2;
    public static FileConfiguration config;
    public static Plugin plugin;

    public static GameMaster gm;
    public enum States {
        PREGAME,
        MAIN,
        POSTGAME
    }
    @Override
    public void onEnable() {
        gm = (GameMaster) getServer().getPluginManager().getPlugin("GameMaster");
        assert gm != null;

        getLogger().info("Getting configuration...");
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        config = this.getConfig();

        state = States.PREGAME;
        plugin = this;

        String levelname;
        try {
            levelname = new GameServer().readServerProperties("level-name");
            getLogger().info("Found lobby name (level-name in server.properties): " + levelname);
        } catch (IOException e) {
            getLogger().severe("Something happened while reading level-name in server.property! Error logs are below, defaulting to 'world'");
            e.printStackTrace();
            levelname = "world";
        }
        lobby = Bukkit.getWorld(levelname);

        getLogger().info("Deleting deathswap worlds...");
        getLogger().info("Deleting overworld...");
        gm.GameWorld.deleteWorld("deathswap-1");
        gm.GameWorld.deleteWorld("deathswap-2");
        if (!config.getBoolean("server.changeproperties.nether")) {
            getLogger().info("Deleting nether...");
            gm.GameWorld.deleteWorld("deathswap-1-nether");
            gm.GameWorld.deleteWorld("deathswap-2-nether");
        }
        if (!config.getBoolean("server.changeproperties.end")) {
            getLogger().info("Deleting end...");
            gm.GameWorld.deleteWorld("deathswap-1-end");
            gm.GameWorld.deleteWorld("deathswap-2-end");
        }
        getLogger().info("Deleting lobby world...");
        gm.GameWorld.deleteWorld(levelname);
        gm.GameWorld.deleteWorld(levelname + "_nether");
        gm.GameWorld.deleteWorld(levelname + "_the_end");

        if (config.getBoolean("server.setuprestart.enable")) {
            try {
                if (!gm.GameServer.restartSetup()) {
                    getLogger().info("Detected that auto setup restart was enabled! Getting operating system.");

                    GameServer.OSTypes os;
                    if (config.isSet("server.setuprestart.os")) {

                        String configOS = config.getString("server.setuprestart.os");
                        if (containsOS(configOS)) {
                            os = GameServer.OSTypes.valueOf(configOS);
                        } else {
                            getLogger().info("Unknown OS specified in config.yml! Resorting to automatic detection.");
                            os = gm.GameServer.getOS();
                        }

                    } else {
                        getLogger().info("No OS specified in config. Attempting automatic detection.");
                        os = gm.GameServer.getOS();
                    }

                    getLogger().info("Detected operating system: " + os.toString());

                    if (os != GameServer.OSTypes.Unknown) {
                        getLogger().info("Getting server jar's name...");

                        boolean proceed;
                        String serverJar = null;

                        if (config.isSet("server.setuprestart.jarname")){
                            serverJar = config.getString("server.setuprestart.jarname");
                            proceed = true;
                        } else {
                            getLogger().info("No server jar specified in config. Attempting automatic detection.");
                            try {
                                serverJar = gm.GameServer.getServerJar().getName();
                                proceed = true;
                            } catch (NoClassDefFoundError e) {
                                getLogger().severe("The server jar could not be detected!");
                                getLogger().severe("You need to specify it yourself in the config.");
                                getLogger().severe("Aborting restart setup.");
                                proceed = false;
                            }
                        }

                        if (proceed) {
                            getLogger().info("Server jar name: " + serverJar);

                            getLogger().info("Setting up restart...");
                            if (gm.GameServer.setupRestart(os, serverJar)) {
                                getLogger().info("This plugin has created a file called restart.bat/restart.sh, please do not delete it!");
                                getLogger().info("The restart will take effect on next server boot.");
                            }
                        }
                    } else {
                        getLogger().severe("The plugin was unable to find your operating system.");
                        getLogger().severe("You need to specify it yourself in the config.");
                        getLogger().severe("Aborting restart setup.");
                    }
                }
            } catch (IOException | InvalidConfigurationException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            if (gm.GameServer.checkForServerProperties(config.getBoolean("server.changeproperties.protection"),
                    !config.getBoolean("game.world.nether"),
                    !config.getBoolean("game.world.end"),
                    config.getBoolean("server.changeproperties.anticheat"))) {
                getLogger().info("Detected that at least one of the following: Nether, end, spawn protection and minecraft's anticheat wasn't disabled!");
                getLogger().info("The appropriate files has been edited. Restarting server for changes to take effect.");
                gm.GameServer.restart();
            }
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

        getLogger().info("Registering commands and events...");
        Objects.requireNonNull(getCommand("team")).setExecutor(new team());
        Objects.requireNonNull(getCommand("start")).setExecutor(new start());
        Objects.requireNonNull(getCommand("swap")).setExecutor(new swap());
        Objects.requireNonNull(getCommand("reloadconfig")).setExecutor(new reloadconfig());
        getServer().getPluginManager().registerEvents(new onPlayerDeath(), this);
        getServer().getPluginManager().registerEvents(new onPlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new onPlayerLeave(), this);

        getLogger().info("Creating deathswap and lobby worlds...");
        Long seed;
        if (config.isSet("game.world.seed")) {
            getLogger().info("Detected that a seed was set in config.yml!");
            seed = config.getLong("game.world.seed");
        } else {
            seed = new Random().nextLong();
        }
        getLogger().info("Using seed " + seed + " for deathswap worlds!");
        getLogger().info("Creating deathswap worlds, this may take a while...");
        getLogger().info("Creating overworld worlds...");
        ds1 = gm.GameWorld.createWorld("deathswap-1", seed, World.Environment.NORMAL, WorldType.getByName(Objects.requireNonNull(config.getString("game.world.overworld.type"))));
        ds2 = gm.GameWorld.createWorld("deathswap-2", seed, World.Environment.NORMAL, WorldType.getByName(Objects.requireNonNull(config.getString("game.world.overworld.type"))));
        if (config.getBoolean("game.world.nether")) {
            getLogger().info("Creating nether worlds...");
            gm.GameWorld.createWorld("deathswap-1-nether", seed, World.Environment.NETHER, WorldType.NORMAL);
            gm.GameWorld.createWorld("deathswap-2-nether", seed, World.Environment.NETHER, WorldType.NORMAL);
        }
        if (config.getBoolean("game.world.end")) {
            getLogger().info("Creating end worlds...");
            gm.GameWorld.createWorld("deathswap-1-end", seed, World.Environment.THE_END, WorldType.NORMAL);
            gm.GameWorld.createWorld("deathswap-2-end", seed, World.Environment.THE_END, WorldType.NORMAL);
        }
        World lobby = gm.GameWorld.createWorld(levelname);
        gm.GameWorld.setupWorld(lobby, true, 10.0, 0, 0, 0);

        getLogger().info("Finished setup! Have fun!");
    }

    public boolean containsOS(String name) {
        for (GameServer.OSTypes os : GameServer.OSTypes.values()) {
            if (os.name().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDisable() {
        getLogger().warning("You may see errors coming from minecraft after this message! This is fine and can be ignored.");
    }

}
