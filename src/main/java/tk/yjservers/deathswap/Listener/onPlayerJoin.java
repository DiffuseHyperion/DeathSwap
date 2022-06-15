package tk.yjservers.deathswap.Listener;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.javatuples.Pair;
import tk.yjservers.deathswap.DeathSwap;

import java.util.AbstractMap;

import static tk.yjservers.deathswap.Commands.team.team1;
import static tk.yjservers.deathswap.Commands.team.team2;
import static tk.yjservers.deathswap.DeathSwap.lobby;
import static tk.yjservers.deathswap.DeathSwap.state;
import static tk.yjservers.deathswap.Listener.onPlayerLeave.dcPlayers;

public class onPlayerJoin implements Listener {

    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String pname = p.getDisplayName();
        switch (state) {
            case PREGAME:
                p.setGameMode(GameMode.ADVENTURE);
                p.teleport(lobby.getSpawnLocation().add(0.5, 0, 0.5));
                p.setHealth(20);
                p.setFoodLevel(20);
                break;
            case MAIN:
                if (!(team1.hasEntry(pname) || team2.hasEntry(pname))) {
                    e.getPlayer().setGameMode(GameMode.SPECTATOR);
                    e.getPlayer().sendMessage(ChatColor.GRAY + "The game has already started. Use spectator's mode teleport to see players.");
                } else {
                    Pair<BossBar, BukkitRunnable> pair = dcPlayers.get(pname);
                    pair.getValue0().removeAll();
                    pair.getValue1().cancel();
                }
                break;
            case POSTGAME:
                p.setGameMode(GameMode.SPECTATOR);
                p.teleport(lobby.getSpawnLocation().add(0.5, 0, 0.5));
                p.setHealth(20);
                p.setFoodLevel(20);
                p.sendMessage(ChatColor.GRAY + "The game has already ended! Join back soon to play the next game!");
                break;
        }
    }
}
