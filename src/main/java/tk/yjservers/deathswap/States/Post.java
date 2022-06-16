package tk.yjservers.deathswap.States;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import tk.yjservers.deathswap.Commands.team;
import tk.yjservers.gamemaster.GamePlayer;

import static tk.yjservers.deathswap.DeathSwap.config;
import static tk.yjservers.deathswap.DeathSwap.gm;

public class Post {
    public void start(team.Teams winner) {
        gm.GamePlayer.playSoundToAll(Sound.UI_TOAST_CHALLENGE_COMPLETE);
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
        BossBar bar = gm.GamePlayer.timer(config.getInt("game.post.restart"), "Server is restarting in " + GamePlayer.timerReplacement.TIME_LEFT.getString() + " seconds!", BarColor.WHITE, BarStyle.SEGMENTED_10, new BukkitRunnable() {
            @Override
            public void run() {
                gm.GameServer.restart();
            }
        });
        for (Player p : Bukkit.getOnlinePlayers()) {
            bar.addPlayer(p);
            p.setGameMode(GameMode.SPECTATOR);
        }
    }
}
