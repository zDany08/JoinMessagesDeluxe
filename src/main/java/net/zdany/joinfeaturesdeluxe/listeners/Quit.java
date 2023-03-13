package net.zdany.joinfeaturesdeluxe.listeners;

import net.zdany.joinfeaturesdeluxe.Format;
import net.zdany.joinfeaturesdeluxe.JoinFeaturesDeluxe;
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
        if(p.hasPermission("joinfeaturesdeluxe.public.join-quit")) {
            if(JoinFeaturesDeluxe.getInstance().getConfig().getBoolean("join-messages.public.join-quit.enabled")) {
                e.setQuitMessage(Format.getColor(Format.getPAPI(p, JoinFeaturesDeluxe.getInstance().getConfig().getString("join-messages.public.join-quit.quit"))));
            }
        }
        sendQuitCommands(p);
    }

    private void sendQuitCommands(Player p) {
        if(!JoinFeaturesDeluxe.getInstance().getConfig().getBoolean("join-commands.enabled")) return;
        if(!p.hasPermission("joinfeaturesdeluxe.join-commands.quit")) return;
        for(String key : JoinFeaturesDeluxe.getInstance().getConfig().getConfigurationSection("join-commands.quit").getKeys(false)) {
            ConfigurationSection cmd = JoinFeaturesDeluxe.getInstance().getConfig().getConfigurationSection("join-commands.quit." + key);
            switch(cmd.getString("executor")) {
                case "C":
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Format.getPAPI(p, cmd.getString("cmd")));
                    break;
                case "P":
                    p.chat("/" + Format.getPAPI(p, cmd.getString("cmd")));
                    break;
                default:
                    Bukkit.getLogger().log(Level.SEVERE, "\"join-commands -> quit -> " + key + " -> executor\" must be \"C\" or \"P\"!");
                    break;
            }
        }
    }
}
