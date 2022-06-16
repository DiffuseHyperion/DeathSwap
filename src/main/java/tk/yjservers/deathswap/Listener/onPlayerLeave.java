package tk.yjservers.deathswap.Listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.javatuples.Pair;
import tk.yjservers.deathswap.Commands.team;
import tk.yjservers.deathswap.DeathSwap;
import tk.yjservers.deathswap.States.Main;
import tk.yjservers.gamemaster.GamePlayer;

import java.util.HashMap;

import static tk.yjservers.deathswap.Commands.team.redTeam;
import static tk.yjservers.deathswap.Commands.team.blueTeam;
import static tk.yjservers.deathswap.DeathSwap.*;

public class onPlayerLeave implements Listener {

    public static HashMap<String, Pair<BossBar, BukkitRunnable>> dcPlayers = new HashMap<>();

    @EventHandler
    public void OnPlayerLeave(PlayerQuitEvent e) {
        if (state != DeathSwap.States.PREGAME) {
            Player p = e.getPlayer();
            String pname = p.getDisplayName();
            if (redTeam.hasEntry(pname) || blueTeam.hasEntry(pname)) {
                Pair<BossBar, BukkitRunnable> pair = gm.GamePlayer.timerWithTask(config.getInt("game.reconnect.grace"),
                        pname + " has disconnected! He will be kicked from the game in " + GamePlayer.timerReplacement.TIME_LEFT + " seconds!",
                        BarColor.WHITE, BarStyle.SEGMENTED_10, new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (redTeam.hasEntry(pname)) {
                                    redTeam.removeEntry(pname);
                                    Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "Red" + ChatColor.RESET + ChatColor.YELLOW + " has " + redTeam.getSize() + " players left!");
                                    if (redTeam.getSize() <= 0) {
                                        new Main().endGame(team.Teams.blueTeam);
                                    }
                                } else if (blueTeam.hasEntry(pname)) {
                                    blueTeam.removeEntry(pname);
                                    Bukkit.broadcastMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Blue" + ChatColor.RESET + ChatColor.YELLOW + " has " + blueTeam.getSize() + " players left!");
                                    if (blueTeam.getSize() <= 0) {
                                        new Main().endGame(team.Teams.redTeam);
                                    }
                                }
                            }
                        });
                BossBar bar = pair.getValue0();
                dcPlayers.put(pname, pair);
                for (Player p1 : Bukkit.getOnlinePlayers()) {
                    bar.addPlayer(p1);
                }
            }
        }
    }
}
