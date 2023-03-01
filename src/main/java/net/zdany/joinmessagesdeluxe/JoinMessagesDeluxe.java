package net.zdany.joinmessagesdeluxe;

import net.zdany.joinmessagesdeluxe.listeners.Join;
import net.zdany.joinmessagesdeluxe.listeners.Quit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class JoinMessagesDeluxe extends JavaPlugin {

    private static JoinMessagesDeluxe instance;

    @Override
    public void onEnable() {
        instance = this;
        registerEvents(new Join(), new Quit());
        saveDefaultConfig();
        getServer().getConsoleSender().sendMessage(ChatColor.GOLD + getName() + ChatColor.GREEN + " has been enabled!");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.GOLD + getName() + ChatColor.RED + " has been disabled!");
    }

    private void registerEvents(Listener... listeners) {
        for(Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    public String getStrVersion() {
        String packageName = getServer().getClass().getPackage().getName();
        String strVer = packageName.substring(packageName.lastIndexOf(".") + 1);
        return strVer;
    }

    public int getVersion() {
        String packageName = getServer().getClass().getPackage().getName();
        String strVer = packageName.substring(packageName.lastIndexOf(".") + 1);
        String version = strVer.split("_")[1];
        return Integer.valueOf(version);
    }

    public int getRelease() {
        String packageName = getServer().getClass().getPackage().getName();
        String strVer = packageName.substring(packageName.lastIndexOf(".") + 1);
        String release = strVer.split("R")[1];
        return Integer.valueOf(release);
    }

    public static JoinMessagesDeluxe getInstance() {
        return instance;
    }
}
