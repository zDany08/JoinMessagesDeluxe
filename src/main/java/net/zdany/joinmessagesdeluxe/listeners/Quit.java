package net.zdany.joinmessagesdeluxe.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import net.zdany.joinmessagesdeluxe.Format;
import net.zdany.joinmessagesdeluxe.JoinMessagesDeluxe;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class Quit implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        e.setQuitMessage(Format.getColor(PlaceholderAPI.setPlaceholders(p, JoinMessagesDeluxe.getInstance().getConfig().getString("join-messages.quit"))));
    }
}
