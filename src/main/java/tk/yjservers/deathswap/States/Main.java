package tk.yjservers.deathswap.States;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import tk.yjservers.deathswap.Commands.team;
import tk.yjservers.gamemaster.GamePlayer;

import java.util.*;

import static tk.yjservers.deathswap.Commands.team.team1;
import static tk.yjservers.deathswap.Commands.team.team2;
import static tk.yjservers.deathswap.DeathSwap.*;

public class Main {

    private static BukkitRunnable swapTask;

    public void start() {
        for (String s : team1.getEntries()) {
            Objects.requireNonNull(Bukkit.getPlayer(s)).teleport(ds1.getSpawnLocation());
        }
        for (String s : team2.getEntries()) {
            Objects.requireNonNull(Bukkit.getPlayer(s)).teleport(ds2.getSpawnLocation());
        }
        int swapMin = config.getInt("game.swap.swaptimer.min");
        int swapMax = config.getInt("game.swap.swaptimer.max");
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
                            HashMap<Location, String> team1Locs = new HashMap<>();
                            HashMap<Location, String> team2Locs = new HashMap<>();
                            for (String s : team1.getEntries()) {
                                team1Locs.put(Objects.requireNonNull(Bukkit.getPlayer(s)).getLocation(), s);
                            }
                            for (String s : team2.getEntries()) {
                                team2Locs.put(Objects.requireNonNull(Bukkit.getPlayer(s)).getLocation(), s);
                            }

                            for (String s : team1.getEntries()) {
                                Player p = Objects.requireNonNull(Bukkit.getPlayer(s));
                                Set<Location> arraySet = team2Locs.keySet();
                                Location loc = (Location) arraySet.toArray()[new Random().nextInt(arraySet.size())];
                                p.teleport(loc);
                                p.sendMessage(ChatColor.RED + "Teleporting to " + ChatColor.BOLD + team2Locs.get(loc) + "!");
                            }
                            for (String s : team2.getEntries()) {
                                Player p = Objects.requireNonNull(Bukkit.getPlayer(s));
                                Set<Location> arraySet = team1Locs.keySet();
                                Location loc = (Location) arraySet.toArray()[new Random().nextInt(arraySet.size())];
                                p.teleport(loc);
                                p.sendMessage(ChatColor.RED + "Teleporting to " + ChatColor.BOLD + team1Locs.get(loc) + "!");
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
