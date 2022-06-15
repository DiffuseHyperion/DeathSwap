package tk.yjservers.deathswap.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import tk.yjservers.deathswap.DeathSwap;

import java.util.Objects;

import static tk.yjservers.deathswap.DeathSwap.state;

public class team implements CommandExecutor {

    public static Team redTeam;
    public static Team blueTeam;

    public enum Teams {
        redTeam,
        blueTeam
    }

    public team() {
        Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        redTeam = scoreboard.registerNewTeam("Red");
        blueTeam = scoreboard.registerNewTeam("Blue");

        redTeam.setAllowFriendlyFire(false);
        redTeam.setColor(ChatColor.RED);
        redTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OTHER_TEAMS);

        blueTeam.setAllowFriendlyFire(false);
        blueTeam.setColor(ChatColor.BLUE);
        blueTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OTHER_TEAMS);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You can only use this command as an player!");
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "You need to specify a team!");
            return false;
        }
        if (state != DeathSwap.States.PREGAME) {
            sender.sendMessage(ChatColor.RED + "You can only change teams before the game starts!");
            return true;
        }
        Player p = (Player) sender;
        String pname = p.getDisplayName();
        switch (args[0]) {
            case "red":
                redTeam.addEntry(pname);
                Bukkit.broadcastMessage(ChatColor.YELLOW + pname + " has joined the " + ChatColor.RED + ChatColor.BOLD + "red" + ChatColor.RESET + ChatColor.YELLOW + " team!");
                return true;
            case "blue":
                blueTeam.addEntry(pname);
                Bukkit.broadcastMessage(ChatColor.YELLOW + pname + " has joined the " + ChatColor.BLUE + ChatColor.BOLD + "blue" + ChatColor.RESET + ChatColor.YELLOW + " team!");
                return true;
            default:
                p.sendMessage(ChatColor.RED + "Unknown team!");
                return false;
        }
    }
}
