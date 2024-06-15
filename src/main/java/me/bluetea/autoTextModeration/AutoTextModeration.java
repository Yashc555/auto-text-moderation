package me.bluetea.autoTextModeration;

import me.bluetea.autoTextModeration.commands.ConfigCommand;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public final class AutoTextModeration extends JavaPlugin implements Listener {

    private static AutoTextModeration plugin;

    MinorMajor MnrMjr = new MinorMajor();
    public static AutoTextModeration getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("----------------------------");
        getLogger().info("Auto Text Moderation STARTED");
        getLogger().info("----------------------------");
        getCommand("configAutoTextMod").setExecutor(new ConfigCommand());
        getCommand("configAutoTextMod").setTabCompleter(new ConfigCommand());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (!p.getPersistentDataContainer().has(new NamespacedKey(this, "MinorCounter"), PersistentDataType.INTEGER)) {
            p.getPersistentDataContainer().set(new NamespacedKey(this, "MinorCounter"), PersistentDataType.INTEGER, 0);
        }
        if (!p.getPersistentDataContainer().has(new NamespacedKey(this, "MajorCounter"), PersistentDataType.INTEGER)) {
            p.getPersistentDataContainer().set(new NamespacedKey(this, "MajorCounter"), PersistentDataType.INTEGER, 0);
        }
    }

    @EventHandler
    public void chatMessageSend(AsyncPlayerChatEvent e) {
        String message = e.getMessage();
        Player p = e.getPlayer();
        if (checkString(p, message)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory anvilInventory = event.getInventory();
        ItemStack result = event.getResult();

        if (result != null) {
            ItemMeta meta = result.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                String newName = meta.getDisplayName();

                Player player = getPlayerUsingAnvil(anvilInventory);

                if (player != null) {
                    if (checkString(player, newName)) {
                        event.setResult(null);
                    }
                }
            }
        }
    }

    private Player getPlayerUsingAnvil(AnvilInventory anvilInventory) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.getOpenInventory() != null && player.getOpenInventory().getTopInventory() != null &&
                    player.getOpenInventory().getTopInventory().equals(anvilInventory)) {
                return player;
            }
        }
        return null;
    }

    @EventHandler
    public void signChange(SignChangeEvent e) {
        Player p = e.getPlayer();
        StringBuilder finalMessage = new StringBuilder();
        for (String line : e.getLines()) {
            finalMessage.append(line).append(" ");
        }
        if (checkString(p, finalMessage.toString())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();

        if (checkString(player, command)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBookEdit(PlayerEditBookEvent event) {
        Player player = event.getPlayer();
        String signerName = event.getNewBookMeta().getTitle();
        List<String> pages = event.getNewBookMeta().getPages();
        StringBuilder finalText = new StringBuilder();
        for (String page : pages) {
            finalText.append(page).append(" ");
        }

        if (checkString(player, finalText.toString()) || checkString(player, signerName)) {
            event.setCancelled(true);
        }
    }

    public boolean checkString(Player p, String st) {
        List<String> minorRegexPatterns = getConfig().getStringList("minor-regex-list");
        List<String> majorRegexPatterns = getConfig().getStringList("major-regex-list");
        for (String regexpattern : minorRegexPatterns) {
            Pattern pt = Pattern.compile(regexpattern);
            Matcher mt = pt.matcher(st);
            if (mt.find()) {
                MnrMjr.setData(0);
                MnrMjr.punish(p);
                return true;
            }
        }

        for (String regexpattern : majorRegexPatterns) {
            Pattern pt = Pattern.compile(regexpattern);
            Matcher mt = pt.matcher(st);
            if (mt.find()) {
                MnrMjr.setData(1);
                MnrMjr.punish(p);
                return true;
            }
        }
        return false;
    }
}
