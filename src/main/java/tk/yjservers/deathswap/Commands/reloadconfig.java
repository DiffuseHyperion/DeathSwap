package tk.yjservers.deathswap.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static tk.yjservers.deathswap.DeathSwap.config;
import static tk.yjservers.deathswap.DeathSwap.plugin;

public class reloadconfig implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.reloadConfig();
        config = plugin.getConfig();
        sender.sendMessage("Config reloaded!");
        return true;
    }
}
