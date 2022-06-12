package tk.yjservers.deathswap.States;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import tk.yjservers.deathswap.Commands.team;
import tk.yjservers.gamemaster.GamePlayer;

import java.util.Objects;
import java.util.Random;

import static tk.yjservers.deathswap.Commands.team.team1;
import static tk.yjservers.deathswap.Commands.team.team2;
import static tk.yjservers.deathswap.DeathSwap.*;

public class Main {

    private BukkitRunnable swapTask;

    public void start() {
        for (String s : team1.getEntries()) {
            Objects.requireNonNull(Bukkit.getPlayer(s)).teleport(ds1.getSpawnLocation());
        }
        for (String s : team2.getEntries()) {
            Objects.requireNonNull(Bukkit.getPlayer(s)).teleport(ds2.getSpawnLocation());
        }
        int swapMin = config.getInt("swaptimer.min");
        int swapMax = config.getInt("swaptimer.max");
        for (Player p : Bukkit.getOnlinePlayers()) {
            gm.GamePlayer.timer(p, 15,
                    "Game has started! Swaps happen every " + swapMin + " - " + swapMax + " seconds.",
                    BarColor.YELLOW, BarStyle.SOLID);
        }

        swapTask = new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        swapPlayers();
                    }
                }.runTaskLater(plugin, new Random().nextInt(swapMin, swapMax + 1) * 1000L);
            }
        };
        swapTask.runTaskTimer(plugin, 0, 1);

    }

    public void swapPlayers() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            gm.GamePlayer.timer(p, 10,
                    "Swap is happening in " + GamePlayer.timerReplacement.TIME_LEFT + " seconds!",
                    BarColor.RED, BarStyle.SOLID, new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (String s : team1.getEntries()) {
                                Objects.requireNonNull(Bukkit.getPlayer(s)).teleport(Objects.requireNonNull(Bukkit.getPlayer((String) team2.getEntries().toArray()[new Random().nextInt(team2.getEntries().size())])).getLocation());
                            }
                            for (String s : team2.getEntries()) {
                                Objects.requireNonNull(Bukkit.getPlayer(s)).teleport(Objects.requireNonNull(Bukkit.getPlayer((String) team1.getEntries().toArray()[new Random().nextInt(team1.getEntries().size())])).getLocation());
                            }
                        }
                    });
        }
    }

    public void endGame(team.Teams winner) {
        swapTask.cancel();
        state = States.POSTGAME;
        new Post().start(winner);
    }

}
