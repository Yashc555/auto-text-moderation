package me.bluetea.autoTextModeration;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class MinorMajor implements Listener {

    static String thresh = "";
    static String counter = "";
    static String cmd = "";

    public static void setData(int a){
        if(a==0){
            thresh = "minor-threshold";
            counter = "MinorCounter";
            cmd = "minor-punishment-command";
        }
        else{
            counter = "MajorCounter";
            cmd = "major-punishment-command";
            thresh = "major-threshold";
        }
    }

    public static void punish(Player p) {
        int max = AutoTextModeration.getPlugin().getConfig().getInt(thresh);
        PersistentDataContainer data = p.getPersistentDataContainer();
        if(AutoTextModeration.getPlugin().getConfig().getString("punishment-toggle").equalsIgnoreCase("true")) {
            if (data.get(new NamespacedKey(AutoTextModeration.getPlugin(), counter), PersistentDataType.INTEGER) == max) {
                data.set(new NamespacedKey(AutoTextModeration.getPlugin(), counter), PersistentDataType.INTEGER, 0);
                command(p);
            } else {
                int value = 1 + data.get(new NamespacedKey(AutoTextModeration.getPlugin(), counter), PersistentDataType.INTEGER);
                data.set(new NamespacedKey(AutoTextModeration.getPlugin(), counter), PersistentDataType.INTEGER, value);
                p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You attempted to enter inappropriate content and it has been blocked");
                p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Warning " + ChatColor.DARK_RED + "" + ChatColor.BOLD + value + ChatColor.DARK_RED + "" + ChatColor.BOLD + "/" + ChatColor.DARK_RED + "" + ChatColor.BOLD + max);
            }
        }
    }

    public static void command(Player p) {
        String command = AutoTextModeration.getPlugin().getConfig().getString(cmd);
        if (!(command == null)) {
            if (command.contains("%player%"))
                command = command.replace("%player%", p.getDisplayName());
        }
        String finalCommand = command;
        AutoTextModeration.getPlugin().getServer().getScheduler().runTask(AutoTextModeration.getPlugin(), () -> {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), finalCommand);
        });
    }
}

