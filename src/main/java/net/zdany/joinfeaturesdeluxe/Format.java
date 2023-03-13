package net.zdany.joinfeaturesdeluxe;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Format {

    public static String getColor(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String getPAPI(Player p, String s) {
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return PlaceholderAPI.setPlaceholders(p, s);
        }
        return s;
    }
}
