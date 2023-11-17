package me.diffusehyperion.deathswap.Listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import me.diffusehyperion.deathswap.Commands.team;

import static me.diffusehyperion.deathswap.DeathSwap.lobby;
import static me.diffusehyperion.deathswap.DeathSwap.state;

import me.diffusehyperion.gamemaster.Utility.Pair;

public class onPlayerJoin implements Listener {

    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String pname = p.getDisplayName();
        switch (state) {
            case PREGAME:
                Bukkit.getLogger().warning("Minecraft may start complaining about not being able to save data for " + pname + ", this is fine and can be disregarded.");
                p.setGameMode(GameMode.ADVENTURE);
                p.teleport(lobby.getSpawnLocation().add(0.5, 0, 0.5));
                p.setHealth(20);
                p.setFoodLevel(20);
                p.setSaturation(5);
                p.sendMessage(ChatColor.YELLOW + "Welcome to DeathSwap! Use /team red or /team blue to choose a team!");
                break;
            case MAIN:
                if (!(team.redTeam.hasEntry(pname) || team.blueTeam.hasEntry(pname))) {
                    e.getPlayer().setGameMode(GameMode.SPECTATOR);
                    e.getPlayer().sendMessage(ChatColor.GRAY + "The game has already started. Use spectator's mode teleport to see players.");
                } else {
                    Pair<BossBar, BukkitRunnable> pair = onPlayerLeave.dcPlayers.get(pname);
                    pair.getValue0().removeAll();
                    pair.getValue1().cancel();
                }
                break;
            case POSTGAME:
                p.setGameMode(GameMode.SPECTATOR);
                p.teleport(lobby.getSpawnLocation().add(0.5, 0, 0.5));
                p.setHealth(20);
                p.setFoodLevel(20);
                p.setSaturation(5);
                p.sendMessage(ChatColor.GRAY + "The game has already ended! Join back soon to play the next game!");
                break;
        }
    }
}
