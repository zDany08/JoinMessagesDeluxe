package net.zdany.joinmessagesdeluxe.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import net.zdany.joinmessagesdeluxe.Format;
import net.zdany.joinmessagesdeluxe.JoinMessagesDeluxe;
import net.zdany.joinmessagesdeluxe.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.lang.reflect.Constructor;
import java.util.logging.Level;

public class Join implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        e.setJoinMessage(Format.getColor(PlaceholderAPI.setPlaceholders(p, JoinMessagesDeluxe.getInstance().getConfig().getString("join-messages.join"))));
        sendPlayerJoinMessage(p);
        sendPlayerJoinTitle(p);
    }

    private void sendPlayerJoinMessage(Player p) {
        if(!JoinMessagesDeluxe.getInstance().getConfig().getBoolean("join-messages.player-join-message.enabled")) return;
        for(String line : JoinMessagesDeluxe.getInstance().getConfig().getStringList("join-messages.player-join-message.lines")) {
            p.sendMessage(Format.getColor(PlaceholderAPI.setPlaceholders(p, line)));
        }
    }

    private void sendPlayerJoinTitle(Player p) {
        if(!JoinMessagesDeluxe.getInstance().getConfig().getBoolean("join-messages.player-join-title.enabled")) return;
        String title = JoinMessagesDeluxe.getInstance().getConfig().getString("join-messages.player-join-title.title"), subtitle = JoinMessagesDeluxe.getInstance().getConfig().getString("join-messages.player-join-title.subtitle");
        int fadeIn = JoinMessagesDeluxe.getInstance().getConfig().getInt("join-messages.player-join-title.fadeIn"), stay = JoinMessagesDeluxe.getInstance().getConfig().getInt("join-messages.player-join-title.stay"), fadeOut = JoinMessagesDeluxe.getInstance().getConfig().getInt("join-messages.player-join-title.fadeOut");
        if(JoinMessagesDeluxe.getInstance().getVersion() >= 10) {
            p.sendTitle(Format.getColor(PlaceholderAPI.setPlaceholders(p, title)), Format.getColor(PlaceholderAPI.setPlaceholders(p, subtitle)), fadeIn, stay, fadeOut);
        }else {
            sendPacketTitle(p, title, fadeIn, stay, fadeOut);
            sendPacketSubtitle(p, subtitle, fadeIn, stay, fadeOut);
        }
    }

    private void sendPacketTitle(Player p, String title, int fadeIn, int stay, int fadeOut) {
        try {
            Object enumTitle = Reflection.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
            Object titleFormat = Reflection.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + Format.getColor(PlaceholderAPI.setPlaceholders(p, title)) + "\"}");
            Constructor<?> titleConstructor = Reflection.getNMSClass("PacketPlayOutTitle").getConstructor(Reflection.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], Reflection.getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
            Object titlePacket = titleConstructor.newInstance(enumTitle, titleFormat, fadeIn, stay, fadeOut);
            Reflection.sendPacket(p, titlePacket);
        }catch(Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error while sending a packet title to \"" + p.getName() + "\": " + e);
        }
    }

    private void sendPacketSubtitle(Player p, String subtitle, int fadeIn, int stay, int fadeOut) {
        try {
            Object enumSubtitle = Reflection.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
            Object subtitleFormat = Reflection.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + Format.getColor(PlaceholderAPI.setPlaceholders(p, subtitle)) + "\"}");
            Constructor<?> subtitleConstructor = Reflection.getNMSClass("PacketPlayOutTitle").getConstructor(Reflection.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], Reflection.getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
            Object subtitlePacket = subtitleConstructor.newInstance(enumSubtitle, subtitleFormat, fadeIn, stay, fadeOut);
            Reflection.sendPacket(p, subtitlePacket);
        }catch(Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error while sending a packet subtitle to \"" + p.getName() + "\": " + e);
        }
    }
}
