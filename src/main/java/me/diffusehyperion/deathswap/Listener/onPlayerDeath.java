package me.diffusehyperion.deathswap.Listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import me.diffusehyperion.deathswap.Commands.team;
import me.diffusehyperion.deathswap.DeathSwap;
import me.diffusehyperion.deathswap.States.Main;

import static me.diffusehyperion.deathswap.DeathSwap.state;

import me.diffusehyperion.gamemaster.Components.GamePlayer;

public class onPlayerDeath implements Listener {

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
        if (state == DeathSwap.States.MAIN) {
            e.setDeathMessage(ChatColor.RED + e.getDeathMessage());

            Player p = e.getEntity();
            p.setGameMode(GameMode.SPECTATOR);
            String pname = p.getDisplayName();
            if (team.redTeam.hasEntry(pname)) {
                team.redTeam.removeEntry(pname);
                Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "Red" + ChatColor.RESET + ChatColor.YELLOW + " has " + team.redTeam.getSize() + " players left!");
                if (team.redTeam.getSize() <= 0) {
                    new Main().endGame(team.Teams.blueTeam);
                }
            } else if (team.blueTeam.hasEntry(pname)) {
                team.blueTeam.removeEntry(pname);
                Bukkit.broadcastMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Blue" + ChatColor.RESET + ChatColor.YELLOW + " has " + team.blueTeam.getSize() + " players left!");
                if (team.blueTeam.getSize() <= 0) {
                    new Main().endGame(team.Teams.redTeam);
                }
            }

            GamePlayer.playSoundToAll(Sound.ENTITY_WITHER_DEATH);
            p.sendMessage(ChatColor.GRAY + "You have died! Use spectator's mode teleport to see players.");
        }
    }
}
