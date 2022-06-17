package tk.yjservers.deathswap.States;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import tk.yjservers.deathswap.Commands.team;
import tk.yjservers.gamemaster.GamePlayer;

import java.util.*;

import static tk.yjservers.deathswap.Commands.team.redTeam;
import static tk.yjservers.deathswap.Commands.team.blueTeam;
import static tk.yjservers.deathswap.DeathSwap.*;

public class Main {

    public static BukkitRunnable swapTask;

    public void start() {
        for (String s : redTeam.getEntries()) {
            Objects.requireNonNull(Bukkit.getPlayer(s)).teleport(ds1.getSpawnLocation().add(0.5, 0, 0.5));
        }
        for (String s : blueTeam.getEntries()) {
            Objects.requireNonNull(Bukkit.getPlayer(s)).teleport(ds2.getSpawnLocation().add(0.5, 0, 0.5));
        }
        int swapMin = config.getInt("game.swap.swaptimer.min");
        int swapMax = config.getInt("game.swap.swaptimer.max");
        BossBar bar = gm.GamePlayer.timer(config.getInt("game.swap.starttimer"),
                "Game has started! Swaps happen every " + swapMin + " - " + swapMax + " seconds.",
                BarColor.YELLOW, BarStyle.SOLID);
        for (Player p : Bukkit.getOnlinePlayers()) {
            bar.addPlayer(p);
            p.setHealth(20);
            p.setFoodLevel(20);
            p.setSaturation(5);
        }

        swapTask = new BukkitRunnable() {
            @Override
            public void run() {
                swapPlayers();
            }
        };
        swapTask.runTaskTimer(plugin, config.getInt("game.swap.startdelay") * 20L, new Random().nextInt(swapMin, swapMax + 1) * 20L);
        gm.GamePlayer.playSoundToAll(Sound.ENTITY_ENDER_DRAGON_GROWL);
    }

    public void swapPlayers() {
        if (redTeam.getSize() <= 0) {
            Bukkit.getLogger().info("Attempting to start a swap, but " + ChatColor.RED + "red" + ChatColor.RESET + " team has no players...");
            return;
        }
        if (blueTeam.getSize() <= 0) {
            Bukkit.getLogger().info("Attempting to start a swap, but " + ChatColor.BLUE + "blue" + ChatColor.RESET + " team has no players...");
            return;
        }
        gm.GamePlayer.playSoundToAll(Sound.ENTITY_WITHER_SPAWN);
        int time = config.getInt("game.swap.time");
        BossBar bar = gm.GamePlayer.timer(time,
                "A swap is happening in " + GamePlayer.timerReplacement.TIME_LEFT + " seconds!",
                BarColor.RED, BarStyle.SOLID, new BukkitRunnable() {
                    @Override
                    public void run() {
                        HashMap<Location, String> team1Locs = new HashMap<>();
                        HashMap<Location, String> team2Locs = new HashMap<>();
                        for (String s : redTeam.getEntries()) {
                            team1Locs.put(Objects.requireNonNull(Bukkit.getPlayer(s)).getLocation(), s);
                        }
                        for (String s : blueTeam.getEntries()) {
                            team2Locs.put(Objects.requireNonNull(Bukkit.getPlayer(s)).getLocation(), s);
                        }

                        for (String s : redTeam.getEntries()) {
                            Player p = Objects.requireNonNull(Bukkit.getPlayer(s));

                            Set<Location> arraySet = team2Locs.keySet();
                            Location loc = (Location) arraySet.toArray()[new Random().nextInt(arraySet.size())];

                            p.teleport(loc);
                            p.sendMessage(ChatColor.RED + "Teleporting to " + ChatColor.BOLD + team2Locs.get(loc) + "!");
                        }
                        for (String s : blueTeam.getEntries()) {
                            Player p = Objects.requireNonNull(Bukkit.getPlayer(s));

                            Set<Location> arraySet = team1Locs.keySet();
                            Location loc = (Location) arraySet.toArray()[new Random().nextInt(arraySet.size())];

                            p.teleport(loc);
                            p.sendMessage(ChatColor.RED + "Teleporting to " + ChatColor.BOLD + team1Locs.get(loc) + "!");
                        }
                    }
                });
        Bukkit.broadcastMessage(ChatColor.YELLOW + "A swap is happening in " + ChatColor.BOLD + time + " seconds!");
        for (Player p : Bukkit.getOnlinePlayers()) {
            bar.addPlayer(p);
        }
    }

    public void endGame(team.Teams winner) {
        swapTask.cancel();
        state = States.POSTGAME;
        new Post().start(winner);
    }

}
