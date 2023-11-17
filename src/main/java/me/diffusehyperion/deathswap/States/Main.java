package me.diffusehyperion.deathswap.States;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import me.diffusehyperion.deathswap.Commands.team;

import java.util.*;

import static me.diffusehyperion.deathswap.DeathSwap.*;

import me.diffusehyperion.gamemaster.Components.GamePlayer;

public class Main {

    public static BukkitRunnable swapTask;

    public void start() {
        for (String s : team.redTeam.getEntries()) {
            Objects.requireNonNull(Bukkit.getPlayer(s)).teleport(ds1.getSpawnLocation().add(0.5, 0, 0.5));
        }
        for (String s : team.blueTeam.getEntries()) {
            Objects.requireNonNull(Bukkit.getPlayer(s)).teleport(ds2.getSpawnLocation().add(0.5, 0, 0.5));
        }
        int swapMin = config.getInt("game.swap.swaptimer.min");
        int swapMax = config.getInt("game.swap.swaptimer.max");
        BossBar bar = GamePlayer.timer(config.getInt("game.swap.starttimer"),
                "Game has started! Swaps happen every " + swapMin + " - " + swapMax + " seconds.",
                BarColor.YELLOW, BarStyle.SOLID).getValue0();
        for (Player p : Bukkit.getOnlinePlayers()) {
            bar.addPlayer(p);
            p.setHealth(20);
            p.setFoodLevel(20);
            p.setSaturation(5);
            p.setGameMode(GameMode.SURVIVAL);
        }


        int[] timer = {0};
        int[] delay = {new Random().nextInt(swapMin, swapMax + 1)};
        Bukkit.getLogger().info("Next swap in " + delay[0] + " seconds!");
        swapTask = new BukkitRunnable() {
            @Override
            public void run() {
                timer[0]++;
                if (timer[0] >= delay[0]) {
                    delay[0] = new Random().nextInt(swapMin, swapMax + 1);
                    Bukkit.getLogger().info("Next swap in " + delay[0] + " seconds!");
                    timer[0] = 0;
                    swapPlayers();
                }
            }
        };
        swapTask.runTaskTimer(plugin, 0, 20);

        GamePlayer.playSoundToAll(Sound.ENTITY_ENDER_DRAGON_GROWL);
    }

    public void swapPlayers() {
        boolean redEmpty = true;
        for (String s : team.redTeam.getEntries()) {
            if (!Objects.isNull(Bukkit.getPlayer(s))) {
                // at least 1 person is online in red
                redEmpty = false;
                break;
            }
        }
        boolean blueEmpty = true;
        for (String s : team.blueTeam.getEntries()) {
            if (!Objects.isNull(Bukkit.getPlayer(s))) {
                // at least 1 person is online in blue
                blueEmpty = false;
                break;
            }
        }

        if (redEmpty) {
            Bukkit.getLogger().info("Attempting to start a swap, but red team has no players...");
            return;
        }
        if (blueEmpty) {
            Bukkit.getLogger().info("Attempting to start a swap, but blue team has no players...");
            return;
        }
        if (team.redTeam.getSize() == 0) {
            Bukkit.getLogger().info("Attempting to start a swap, but red team has no players...");
            return;
        }
        if (team.blueTeam.getSize() == 0) {
            Bukkit.getLogger().info("Attempting to start a swap, but blue team has no players...");
            return;
        }
        GamePlayer.playSoundToAll(Sound.ENTITY_WITHER_SPAWN);
        int time = config.getInt("game.swap.time");
        BossBar bar = GamePlayer.timer(time,
                "A swap is happening in " + GamePlayer.timerReplacement.TIME_LEFT + " seconds!",
                BarColor.RED, BarStyle.SOLID, new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (state == States.MAIN) {
                            HashMap<Location, String> team1Locs = new HashMap<>();
                            HashMap<Location, String> team2Locs = new HashMap<>();
                            for (String s : team.redTeam.getEntries()) {
                                team1Locs.put(Objects.requireNonNull(Bukkit.getPlayer(s)).getLocation(), s);
                            }
                            for (String s : team.blueTeam.getEntries()) {
                                team2Locs.put(Objects.requireNonNull(Bukkit.getPlayer(s)).getLocation(), s);
                            }

                            for (String s : team.redTeam.getEntries()) {
                                Player p = Objects.requireNonNull(Bukkit.getPlayer(s));

                                Set<Location> arraySet = team2Locs.keySet();
                                Location loc = (Location) arraySet.toArray()[new Random().nextInt(arraySet.size())];

                                p.teleport(loc);
                                p.sendMessage(ChatColor.RED + "Teleporting to " + ChatColor.BOLD + team2Locs.get(loc) + "!");
                            }
                            for (String s : team.blueTeam.getEntries()) {
                                Player p = Objects.requireNonNull(Bukkit.getPlayer(s));

                                Set<Location> arraySet = team1Locs.keySet();
                                Location loc = (Location) arraySet.toArray()[new Random().nextInt(arraySet.size())];

                                p.teleport(loc);
                                p.sendMessage(ChatColor.RED + "Teleporting to " + ChatColor.BOLD + team1Locs.get(loc) + "!");
                            }
                        }
                    }
                }).getValue0();
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
