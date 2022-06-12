package tk.yjservers.deathswap.Listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import tk.yjservers.deathswap.Commands.team;
import tk.yjservers.deathswap.DeathSwap;
import tk.yjservers.deathswap.States.Main;

import static tk.yjservers.deathswap.Commands.team.team1;
import static tk.yjservers.deathswap.Commands.team.team2;
import static tk.yjservers.deathswap.DeathSwap.state;

public class onPlayerDeath implements Listener {

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
        if (state == DeathSwap.States.MAIN) {
            e.setDeathMessage(ChatColor.RED + e.getDeathMessage());

            Player p = e.getEntity();
            p.setGameMode(GameMode.SPECTATOR);
            String pname = p.getDisplayName();
            if (team1.removeEntry(pname)) {
                if (team1.getSize() < 1) {
                    new Main().endGame(team.Teams.team2);
                }
                Bukkit.broadcastMessage(ChatColor.RED + "Red has " + team1.getSize() + " players left!");
            }
            if (team2.removeEntry(pname)) {
                if (team1.getSize() < 1) {
                    new Main().endGame(team.Teams.team1);
                }
                Bukkit.broadcastMessage(ChatColor.RED + "Blue has " + team2.getSize() + " players left!");
            }
        }
    }
}
