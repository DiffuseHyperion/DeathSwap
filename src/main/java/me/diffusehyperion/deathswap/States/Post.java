package me.diffusehyperion.deathswap.States;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import me.diffusehyperion.deathswap.Commands.team;

import static me.diffusehyperion.deathswap.DeathSwap.config;

import me.diffusehyperion.gamemaster.Components.GamePlayer;
import me.diffusehyperion.gamemaster.Components.GameServer;

public class Post {
    public void start(team.Teams winner) {
        GamePlayer.playSoundToAll(Sound.UI_TOAST_CHALLENGE_COMPLETE);
        Main.swapTask.cancel();
        if (winner.equals(team.Teams.redTeam)) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle(ChatColor.RED + "Red" + ChatColor.YELLOW + " has won the game!", ChatColor.YELLOW + "Congratulations!", 10, 70, 20);
            }
            Bukkit.broadcastMessage(ChatColor.RED + "Red" + ChatColor.YELLOW + " has won the game!");
        } else {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle(ChatColor.BLUE + "Blue" + ChatColor.YELLOW + " has won the game!", ChatColor.YELLOW + "Congratulations!", 10, 70, 20);
            }
            Bukkit.broadcastMessage(ChatColor.BLUE + "Blue" + ChatColor.YELLOW + " has won the game!");
        }
        BossBar bar = GamePlayer.timer(config.getInt("game.post.restart"), "Server is restarting in " + GamePlayer.timerReplacement.TIME_LEFT + " seconds!", BarColor.WHITE, BarStyle.SEGMENTED_10, new BukkitRunnable() {
            @Override
            public void run() {
                GameServer.restart();
            }
        }).getValue0();
        for (Player p : Bukkit.getOnlinePlayers()) {
            bar.addPlayer(p);
            p.setGameMode(GameMode.SPECTATOR);
        }
    }
}
