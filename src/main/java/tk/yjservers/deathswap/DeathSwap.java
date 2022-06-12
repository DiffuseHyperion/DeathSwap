package tk.yjservers.deathswap;

import org.bukkit.World;
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

        GameServer.OSTypes os = gm.GameServer.getOS();
        if (os != GameServer.OSTypes.Unknown) {
            try {
                getLogger().info("Checking if restarting is configured in your server...");
                if (gm.GameServer.setupRestart(os)) {
                    getLogger().info("Detected that it wasn't!");
                    getLogger().info("This plugin has created a file called restart.bat/restart.sh, please do not delete it!");
                }
                getLogger().info("Checking if nether, end, spawn protection and minecraft's anticheat is disabled!");
                if (gm.GameServer.checkForServerProperties()) {
                    getLogger().info("Detected that at least one wasn't!");
                    getLogger().info("The appropriate files has been edited. Restarting server for changes to take effect.");
                    gm.GameServer.restart();
                }
            } catch (IOException | InvalidConfigurationException e) {
                throw new RuntimeException(e);
            }
        }

        getLogger().info("Registering commands and events...");
        Objects.requireNonNull(getCommand("team")).setExecutor(new team());
        Objects.requireNonNull(getCommand("start")).setExecutor(new start());
        getServer().getPluginManager().registerEvents(new onPlayerDeath(), this);

        getLogger().info("Creating deathswap and lobby worlds...");
        Long seed = new Random().nextLong();
        getLogger().info("Using seed " + seed + " for deathswap worlds!");
        getLogger().info("Creating deathswap-1, this may take a while...");
        ds1 = gm.GameWorld.createWorld("deathswap-1", seed);
        ds2 = gm.GameWorld.createWorld("deathswap-2", seed);
        try {
            levelname = new GameServer().readServerProperties("level-name");
            getLogger().info("Found lobby name (level-name in server.properties): " + levelname);
        } catch (IOException e) {
            getLogger().severe("Something happened while reading level-name in server.property! Error logs are below, defaulting to 'world'");
            e.printStackTrace();
            levelname = "world";
        }
        World lobby = gm.GameWorld.createWorld(levelname);
        gm.GameWorld.setupWorld(lobby, true, 10.0, 0, 0, 0);

        getLogger().info("Getting configuration...");
        saveDefaultConfig();
        config = getConfig();
        state = States.PREGAME;
        plugin = this;

        getLogger().info("Finished setup! Have fun!");
    }

    @Override
    public void onDisable() {
        GameWorld gw = new GameWorld();
        getLogger().info("Deleting deathswap worlds...");
        gw.deleteWorld("deathswap-1");
        gw.deleteWorld("deathswap-2");
        getLogger().info("Deleting lobby world...");
        gw.deleteWorld(levelname);
        getLogger().warning("You may see errors coming from minecraft after this message! This is fine and can be ignored.");
        getLogger().info("Finished disabling. Zzz...");
    }

}
