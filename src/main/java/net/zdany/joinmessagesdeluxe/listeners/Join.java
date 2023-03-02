package net.zdany.joinmessagesdeluxe.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import net.zdany.joinmessagesdeluxe.Format;
import net.zdany.joinmessagesdeluxe.JoinMessagesDeluxe;
import net.zdany.joinmessagesdeluxe.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
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
        if(p.hasPermission("joinmessagesdeluxe.public.join-quit")) {
            if(JoinMessagesDeluxe.getInstance().getConfig().getBoolean("join-messages.public.join-quit.enabled")) {
                e.setJoinMessage(Format.getColor(PlaceholderAPI.setPlaceholders(p, JoinMessagesDeluxe.getInstance().getConfig().getString("join-messages.public.join-quit.join"))));
            }
        }
        sendPublicFirstJoinMessage(p);
        sendPrivateJoinMessage(p);
        sendPrivateFirstJoinMessage(p);
        sendJoinTitle(p);
        sendJoinCommands(p);
    }

    private void sendPublicFirstJoinMessage(Player p) {
        if(!p.hasPermission("joinmessagesdeluxe.public.first-join")) return;
        if(p.hasPlayedBefore()) return;
        if(!JoinMessagesDeluxe.getInstance().getConfig().getBoolean("join-messages.public.first-join.enabled")) return;
        for(Player other : Bukkit.getOnlinePlayers()) {
            other.sendMessage(Format.getColor(PlaceholderAPI.setPlaceholders(p, JoinMessagesDeluxe.getInstance().getConfig().getString("join-messages.public.first-join.message"))));
        }
    }

    private void sendPrivateJoinMessage(Player p) {
        if(!p.hasPermission("joinmessagesdeluxe.private.join")) return;
        if(!JoinMessagesDeluxe.getInstance().getConfig().getBoolean("join-messages.private.join.enabled")) return;
        for(String line : JoinMessagesDeluxe.getInstance().getConfig().getStringList("join-messages.private.join.lines")) {
            p.sendMessage(Format.getColor(PlaceholderAPI.setPlaceholders(p, line)));
        }
    }

    private void sendPrivateFirstJoinMessage(Player p) {
        if(!p.hasPermission("joinmessagesdeluxe.private.first-join")) return;
        if(p.hasPlayedBefore()) return;
        if(!JoinMessagesDeluxe.getInstance().getConfig().getBoolean("join-messages.private.first-join.enabled")) return;
        for(String line : JoinMessagesDeluxe.getInstance().getConfig().getStringList("join-messages.private.first-join.lines")) {
            p.sendMessage(Format.getColor(PlaceholderAPI.setPlaceholders(p, line)));
        }
    }

    private void sendJoinTitle(Player p) {
        if(!p.hasPermission("joinmessagesdeluxe.join-title")) return;
        if(!JoinMessagesDeluxe.getInstance().getConfig().getBoolean("join-messages.join-title.enabled")) return;
        String title, subtitle;
        int fadeIn, stay, fadeOut;
        if(!p.hasPlayedBefore()) {
            title = JoinMessagesDeluxe.getInstance().getConfig().getString(Format.getColor(PlaceholderAPI.setPlaceholders(p, "join-messages.join-title.first-join.title")));
            subtitle = JoinMessagesDeluxe.getInstance().getConfig().getString(Format.getColor(PlaceholderAPI.setPlaceholders(p, "join-messages.join-title.first-join.subtitle")));
            fadeIn = JoinMessagesDeluxe.getInstance().getConfig().getInt("join-messages.join-title.first-join.fadeIn");
            stay = JoinMessagesDeluxe.getInstance().getConfig().getInt("join-messages.join-title.first-join.stay");
            fadeOut = JoinMessagesDeluxe.getInstance().getConfig().getInt("join-messages.join-title.first-join.fadeOut");
        }else {
            title = JoinMessagesDeluxe.getInstance().getConfig().getString(Format.getColor(PlaceholderAPI.setPlaceholders(p, "join-messages.join-title.join.title")));
            subtitle = JoinMessagesDeluxe.getInstance().getConfig().getString(Format.getColor(PlaceholderAPI.setPlaceholders(p, "join-messages.join-title.join.subtitle")));
            fadeIn = JoinMessagesDeluxe.getInstance().getConfig().getInt("join-messages.join-title.join.fadeIn");
            stay = JoinMessagesDeluxe.getInstance().getConfig().getInt("join-messages.join-title.join.stay");
            fadeOut = JoinMessagesDeluxe.getInstance().getConfig().getInt("join-messages.join-title.join.fadeOut");
        }
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

    private void sendJoinCommands(Player p) {
        if(!JoinMessagesDeluxe.getInstance().getConfig().getBoolean("join-commands.enabled")) return;
        if(!p.hasPermission("joinmessagesdeluxe.join-commands.join")) return;
        for(String key : JoinMessagesDeluxe.getInstance().getConfig().getConfigurationSection("join-commands.join").getKeys(false)) {
            ConfigurationSection cmd = JoinMessagesDeluxe.getInstance().getConfig().getConfigurationSection("join-commands.join." + key);
            switch(cmd.getString("executor")) {
                case "C":
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(p, cmd.getString("cmd")));
                    break;
                case "P":
                    p.chat("/" + PlaceholderAPI.setPlaceholders(p, cmd.getString("cmd")));
                    break;
                default:
                    Bukkit.getLogger().log(Level.SEVERE, "\"join-commands -> join -> " + key + " -> executor\" must be \"C\" or \"P\"!");
                    break;
            }
        }
    }
}
