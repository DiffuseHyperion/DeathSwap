package tk.yjservers.deathswap.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import tk.yjservers.deathswap.DeathSwap;
import tk.yjservers.deathswap.States.Main;

import static tk.yjservers.deathswap.DeathSwap.config;
import static tk.yjservers.deathswap.DeathSwap.state;

public class swap implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (state != DeathSwap.States.MAIN) {
            sender.sendMessage(ChatColor.RED + "There is no running game currently!");
            return true;
        }
        new Main().swapPlayers();
        if (config.getBoolean("game.swap.informforceswap")) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "An operator has triggered a forced swap!");
        }
        return true;
    }
}
