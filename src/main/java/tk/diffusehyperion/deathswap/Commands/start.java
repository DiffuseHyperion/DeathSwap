package tk.diffusehyperion.deathswap.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.diffusehyperion.deathswap.DeathSwap;
import tk.diffusehyperion.deathswap.States.Main;

import java.util.ArrayList;
import java.util.List;

import static tk.diffusehyperion.deathswap.DeathSwap.gm;
import static tk.diffusehyperion.deathswap.DeathSwap.state;

public class start implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (state != DeathSwap.States.PREGAME) {
            sender.sendMessage(ChatColor.RED + "The game has already started!");
            return true;
        }
        if (team.redTeam.getSize() < 1) {
            sender.sendMessage(ChatColor.RED + "Red team has no players...");
            return true;
        }
        if (team.blueTeam.getSize() < 1) {
            sender.sendMessage(ChatColor.RED + "Blue team has no players...");
            return true;
        }
        List<String> notReady = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            String pname = p.getDisplayName();
            if (!(team.redTeam.hasEntry(pname) || team.blueTeam.hasEntry(pname))) {
                notReady.add(pname);
            }
        }
        if (!notReady.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Not all players are ready! Those who are not ready:");
            for (String s : notReady) {
                sender.sendMessage(ChatColor.YELLOW + s);
            }
            return true;
        }
        state = DeathSwap.States.MAIN;
        gm.GamePlayer.playSoundToAll(Sound.ENTITY_WITHER_DEATH);
        new Main().start();
        return true;
    }
}
