package tk.yjservers.deathswap;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import tk.yjservers.gamemaster.GameMaster;

import java.io.IOException;
import java.util.Random;

public final class DeathSwap extends JavaPlugin {

    private String levelname;
    public static States state;
    public static World ds1;
    public static World ds2;
    public static FileConfiguration config;
    public static Plugin plugin;
    public enum States {
        PREGAME,
        MAIN,
        POSTGAME;
    }
    @Override
    public void onEnable() {
        GameMaster gm = new GameMaster();
        Long seed = new Random().nextLong();
        ds1 = gm.GameWorld.createWorld("deathswap-1", seed);
        ds2 = gm.GameWorld.createWorld("deathswap-2", seed);
        World lobby = gm.GameWorld.createWorld(levelname);
        gm.GameWorld.setupWorld(lobby, true, 10.0, 0, 0, 0);

        saveDefaultConfig();
        config = getConfig();
        state = States.PREGAME;
        plugin = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public void onLoad() {
        GameMaster gm = new GameMaster();
        gm.GameWorld.deleteWorld("deathswap-1");
        gm.GameWorld.deleteWorld("deathswap-2");
        try {
            levelname = gm.GameServer.readServerProperties("level-name");
        } catch (IOException e) {
            getLogger().severe("Something happened while reading level-name in server.property! Error logs are below, defaulting to 'world'");
            e.printStackTrace();
            levelname = "world";
        }
        gm.GameWorld.deleteWorld(levelname);
    }
}
