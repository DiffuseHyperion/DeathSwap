package tk.yjservers.deathswap.States;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import tk.yjservers.deathswap.Commands.team;
import tk.yjservers.gamemaster.GameMaster;
import tk.yjservers.gamemaster.GamePlayer;

public class Post {

    private final GameMaster gm;

    public Post() {
        gm = new GameMaster();
    }
    public void start(team.Teams winner) {
        if (winner.equals(team.Teams.team1)) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle(ChatColor.RED + "Red has won the game!", "Congratulations!", 10, 70, 20);
            }
        } else {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle(ChatColor.RED + "Blue has won the game!", "Congratulations!", 10, 70, 20);
            }
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setGameMode(GameMode.CREATIVE);
            gm.GamePlayer.timer(p, 60, "Server is restarting in " + GamePlayer.timerReplacement.TIME_LEFT + " seconds!", BarColor.WHITE, BarStyle.SEGMENTED_10, new BukkitRunnable() {
                @Override
                public void run() {
                    gm.GameServer.restart();
                }
            });
        }
    }
}
