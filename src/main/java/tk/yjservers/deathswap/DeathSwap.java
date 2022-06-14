package tk.yjservers.deathswap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import tk.yjservers.deathswap.Commands.start;
import tk.yjservers.deathswap.Commands.team;
import tk.yjservers.deathswap.Listener.onPlayerDeath;
import tk.yjservers.gamemaster.GameMaster;
import tk.yjservers.gamemaster.GameServer;
import tk.yjservers.gamemaster.GameWorld;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public final class DeathSwap extends JavaPlugin {

    private String levelname;
    public static States state;
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
        saveDefaultConfig();
        config = getConfig();
        state = States.PREGAME;
        plugin = this;
        try {
            levelname = new GameServer().readServerProperties("level-name");
            getLogger().info("Found lobby name (level-name in server.properties): " + levelname);
        } catch (IOException e) {
            getLogger().severe("Something happened while reading level-name in server.property! Error logs are below, defaulting to 'world'");
            e.printStackTrace();
            levelname = "world";
        }

        if (config.getBoolean("server.setuprestart.enable")) {
            getLogger().info("Detected that auto setup restart was enabled! Getting operating system.");

            GameServer.OSTypes os;
            if (config.contains("server.setuprestart.os")) {
                String configOS = config.getString("server.setuprestart.os");
                if (containsOS(configOS)) {
                    os = GameServer.OSTypes.valueOf(configOS);
                } else {
                    getLogger().info("Unknown OS specified in config.yml! Resorting to automatic detection.");
                    os = gm.GameServer.getOS();
                }
            } else {
                os = gm.GameServer.getOS();
            }
            getLogger().info("Detected operating system: " + os.toString());

            if (os != GameServer.OSTypes.Unknown) {
                getLogger().info("Getting server jar's name...");
                String serverJar;
                if (config.contains("server.setuprestart.jarname")){
                    serverJar = config.getString("server.setuprestart.jarname");
                } else {
                    serverJar = gm.GameServer.getServerJar().getName();
                }

                getLogger().info("Server jar name: " + serverJar);

                getLogger().info("Checking if restarting is configured in your server...");
                try {
                    if (gm.GameServer.setupRestart(os, serverJar)) {
                        getLogger().info("Detected that it wasn't! It has been rectified.");
                        getLogger().info("This plugin has created a file called restart.bat/restart.sh, please do not delete it!");
                        getLogger().info("The server will now shutdown for changes to take effect.");
                        Bukkit.shutdown();
                    }
                } catch (IOException | InvalidConfigurationException e) {
                    throw new RuntimeException(e);
                }
            } else {
                getLogger().info("The plugin was unable to find your operating system. Aborting restart setup.");
            }
        }

        try {
            if (gm.GameServer.checkForServerProperties(config.getBoolean("server.changeproperties.protection"),
                    config.getBoolean("server.changeproperties.nether"),
                    config.getBoolean("server.changeproperties.end"),
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
        getServer().getPluginManager().registerEvents(new onPlayerDeath(), this);

        getLogger().info("Creating deathswap and lobby worlds...");
        Long seed = new Random().nextLong();
        getLogger().info("Using seed " + seed + " for deathswap worlds!");
        getLogger().info("Creating deathswap worlds, this may take a while...");
        getLogger().info("Creating overworld worlds...");
        ds1 = gm.GameWorld.createWorld("deathswap-1", seed);
        ds2 = gm.GameWorld.createWorld("deathswap-2", seed);
        if (!config.getBoolean("server.changeproperties.nether")) {
            getLogger().info("Creating nether worlds...");
            ds1 = gm.GameWorld.createWorld("deathswap-1-nether", seed, World.Environment.NETHER, WorldType.NORMAL);
            ds2 = gm.GameWorld.createWorld("deathswap-2-nether", seed, World.Environment.NETHER, WorldType.NORMAL);
        }
        if (!config.getBoolean("server.changeproperties.end")) {
            getLogger().info("Creating end worlds...");
            ds1 = gm.GameWorld.createWorld("deathswap-1-end", seed, World.Environment.THE_END, WorldType.NORMAL);
            ds2 = gm.GameWorld.createWorld("deathswap-2-end", seed, World.Environment.THE_END, WorldType.NORMAL);
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
        GameWorld gw = new GameWorld();
        getLogger().info("Deleting deathswap worlds...");
        getLogger().info("Deleting overworld...");
        gw.deleteWorld("deathswap-1");
        gw.deleteWorld("deathswap-2");
        if (!config.getBoolean("server.changeproperties.nether")) {
            getLogger().info("Deleting nether...");
            gw.deleteWorld("deathswap-1-nether");
            gw.deleteWorld("deathswap-2-nether");
        }
        if (!config.getBoolean("server.changeproperties.end")) {
            getLogger().info("Deleting end...");
            gw.deleteWorld("deathswap-1-end");
            gw.deleteWorld("deathswap-2-end");
        }
        getLogger().info("Deleting lobby world...");
        gw.deleteWorld(levelname);
        gw.deleteWorld(levelname + "_nether");
        gw.deleteWorld(levelname + "_the_end");
        getLogger().warning("You may see errors coming from minecraft after this message! This is fine and can be ignored.");
        getLogger().info("Finished disabling. Zzz...");
    }

}
