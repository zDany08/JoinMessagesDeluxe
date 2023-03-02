package net.zdany.joinmessagesdeluxe.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import net.zdany.joinmessagesdeluxe.Format;
import net.zdany.joinmessagesdeluxe.JoinMessagesDeluxe;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Level;

public class Quit implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(p.hasPermission("joinmessagesdeluxe.public.join-quit")) {
            if(JoinMessagesDeluxe.getInstance().getConfig().getBoolean("join-messages.public.join-quit.enabled")) {
                e.setQuitMessage(Format.getColor(PlaceholderAPI.setPlaceholders(p, JoinMessagesDeluxe.getInstance().getConfig().getString("join-messages.public.join-quit.quit"))));
            }
        }
        sendQuitCommands(p);
    }

    private void sendQuitCommands(Player p) {
        if(!JoinMessagesDeluxe.getInstance().getConfig().getBoolean("join-commands.enabled")) return;
        if(!p.hasPermission("joinmessagesdeluxe.join-commands.quit")) return;
        for(String key : JoinMessagesDeluxe.getInstance().getConfig().getConfigurationSection("join-commands.quit").getKeys(false)) {
            ConfigurationSection cmd = JoinMessagesDeluxe.getInstance().getConfig().getConfigurationSection("join-commands.quit." + key);
            switch(cmd.getString("executor")) {
                case "C":
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(p, cmd.getString("cmd")));
                    break;
                case "P":
                    p.chat("/" + PlaceholderAPI.setPlaceholders(p, cmd.getString("cmd")));
                    break;
                default:
                    Bukkit.getLogger().log(Level.SEVERE, "\"join-commands -> quit -> " + key + " -> executor\" must be \"C\" or \"P\"!");
                    break;
            }
        }
    }
}
