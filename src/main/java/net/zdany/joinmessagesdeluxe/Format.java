package net.zdany.joinmessagesdeluxe;

import org.bukkit.ChatColor;

public class Format {

    public static String getColor(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
