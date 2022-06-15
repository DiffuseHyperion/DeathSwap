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
import tk.yjservers.deathswap.Commands.team;
import tk.yjservers.deathswap.DeathSwap;
import tk.yjservers.deathswap.States.Main;
import tk.yjservers.gamemaster.GamePlayer;

import static tk.yjservers.deathswap.Commands.team.team1;
import static tk.yjservers.deathswap.Commands.team.team2;
import static tk.yjservers.deathswap.DeathSwap.gm;
import static tk.yjservers.deathswap.DeathSwap.state;

public class onPlayerLeave implements Listener {

    @EventHandler
    public void OnPlayerLeave(PlayerQuitEvent e) {
        if (state != DeathSwap.States.PREGAME) {
            Player p = e.getPlayer();
            String pname = p.getDisplayName();
            if (!(team1.hasEntry(pname) || team2.hasEntry(pname))) {
                BossBar bar = gm.GamePlayer.timer(60,
                        pname + " has disconnected! He will be kicked from the game in " + GamePlayer.timerReplacement.TIME_LEFT.getString() + " seconds!",
                        BarColor.WHITE, BarStyle.SEGMENTED_10, new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (team1.hasEntry(pname)) {
                                    team1.removeEntry(pname);
                                    Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "Red" + ChatColor.RESET + ChatColor.YELLOW + " has " + team1.getSize() + " players left!");
                                    if (team1.getSize() <= 0) {
                                        new Main().endGame(team.Teams.team2);
                                    }
                                } else if (team2.hasEntry(pname)) {
                                    team2.removeEntry(pname);
                                    Bukkit.broadcastMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Blue" + ChatColor.RESET + ChatColor.YELLOW + " has " + team2.getSize() + " players left!");
                                    if (team2.getSize() <= 0) {
                                        new Main().endGame(team.Teams.team1);
                                    }
                                }
                            }
                        });
                for (Player p1 : Bukkit.getOnlinePlayers()) {
                    bar.addPlayer(p1);
                }
            }
        }
    }
}
