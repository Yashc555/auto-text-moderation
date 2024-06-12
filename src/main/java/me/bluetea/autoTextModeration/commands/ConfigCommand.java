package me.bluetea.autoTextModeration.commands;

import me.bluetea.autoTextModeration.AutoTextModeration;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigCommand implements CommandExecutor, TabCompleter {

    private static final List<String> FIRST_ARGUMENTS = Arrays.asList(
            "MinorThreshold",
            "MajorThreshold",
            "MinorPunishmentCommand",
            "MajorPunishmentCommand",
            "PunishmentToggle"
    );

    public static boolean isPositiveInteger(String str) {
        if (str.matches("\\d+")) {
            try {
                int number = Integer.parseInt(str);
                return number > 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Usage: " + ChatColor.AQUA + "/setconfig <MinorThreshold|MajorThreshold|MinorPunishmentCommand|MajorPunishmentCommand> <value>");
            return false;
        }

        String key = args[0];
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Please provide a value for " + key);
            return false;
        }

        switch (key.toLowerCase()) {
            case "minorthreshold":
                if (isPositiveInteger(args[1])) {
                    AutoTextModeration.getPlugin().getConfig().set("minor-threshold", Integer.parseInt(args[1]));
                    AutoTextModeration.getPlugin().saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "MinorThreshold updated to " + args[1]);
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid value for MinorThreshold. It must be a positive integer.");
                    return false;
                }
            case "majorthreshold":
                if (isPositiveInteger(args[1])) {
                    AutoTextModeration.getPlugin().getConfig().set("major-threshold", Integer.parseInt(args[1]));
                    AutoTextModeration.getPlugin().saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "MajorThreshold updated to " + args[1]);
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid value for MajorThreshold. It must be a positive integer.");
                    return false;
                }
            case "minorpunishmentcommand":
                String minorCmd = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                AutoTextModeration.getPlugin().getConfig().set("minor-punishment-command", minorCmd.trim());
                AutoTextModeration.getPlugin().saveConfig();
                sender.sendMessage(ChatColor.GREEN + "MinorPunishmentCommand updated to " + minorCmd);
                return true;
            case "majorpunishmentcommand":
                String majorCmd = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                AutoTextModeration.getPlugin().getConfig().set("major-punishment-command", majorCmd.trim());
                AutoTextModeration.getPlugin().saveConfig();
                sender.sendMessage(ChatColor.GREEN + "MajorPunishmentCommand updated to " + majorCmd);
                return true;
            case "punishmenttoggle":
                String toggle = args[1];
                if(toggle.equalsIgnoreCase("true") || toggle.equalsIgnoreCase("false")) {
                    AutoTextModeration.getPlugin().getConfig().set("punishment-toggle", toggle);
                    AutoTextModeration.getPlugin().saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "PunishmentToggle updated to " + toggle);
                    return true;
                }
                else{
                    return false;
                }
            default:
                sender.sendMessage(ChatColor.RED + "Unknown configuration key: " + key);
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return getMatchingArguments(args[0], FIRST_ARGUMENTS);
        }
        return new ArrayList<>();
    }

    private List<String> getMatchingArguments(String input, List<String> possibleArguments) {
        List<String> matches = new ArrayList<>();
        for (String arg : possibleArguments) {
            if (arg.toLowerCase().startsWith(input.toLowerCase())) {
                matches.add(arg);
            }
        }
        return matches;
    }
}
