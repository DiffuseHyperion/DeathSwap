package tk.yjservers.deathswap.Listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import tk.yjservers.deathswap.Commands.team;
import tk.yjservers.deathswap.DeathSwap;
import tk.yjservers.deathswap.States.Main;

import static tk.yjservers.deathswap.Commands.team.team1;
import static tk.yjservers.deathswap.Commands.team.team2;
import static tk.yjservers.deathswap.DeathSwap.gm;
import static tk.yjservers.deathswap.DeathSwap.state;

public class onPlayerDeath implements Listener {

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
        if (state == DeathSwap.States.MAIN) {
            e.setDeathMessage(ChatColor.RED + e.getDeathMessage());

            Player p = e.getEntity();
            p.setGameMode(GameMode.SPECTATOR);
            String pname = p.getDisplayName();
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

            gm.GamePlayer.playSoundToAll(Sound.ENTITY_WITHER_DEATH);
            p.sendMessage(ChatColor.GRAY + "You have died!. Use spectator's mode teleport to see players.");
        }
    }
}
