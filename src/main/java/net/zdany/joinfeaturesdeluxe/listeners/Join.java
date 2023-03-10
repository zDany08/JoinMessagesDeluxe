package net.zdany.joinfeaturesdeluxe.listeners;

import net.zdany.joinfeaturesdeluxe.Format;
import net.zdany.joinfeaturesdeluxe.JoinFeaturesDeluxe;
import net.zdany.joinfeaturesdeluxe.Reflection;
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
        if(p.hasPermission("joinfeaturesdeluxe.public.join-quit")) {
            if(JoinFeaturesDeluxe.getInstance().getConfig().getBoolean("join-messages.public.join-quit.enabled")) {
                e.setJoinMessage(Format.getColor(Format.getPAPI(p, JoinFeaturesDeluxe.getInstance().getConfig().getString("join-messages.public.join-quit.join"))));
            }
        }
        sendPublicFirstJoinMessage(p);
        sendPrivateJoinMessage(p);
        sendPrivateFirstJoinMessage(p);
        sendJoinTitle(p);
        sendJoinCommands(p);
    }

    private void sendPublicFirstJoinMessage(Player p) {
        if(!p.hasPermission("joinfeaturesdeluxe.public.first-join")) return;
        if(p.hasPlayedBefore()) return;
        if(!JoinFeaturesDeluxe.getInstance().getConfig().getBoolean("join-messages.public.first-join.enabled")) return;
        for(Player other : Bukkit.getOnlinePlayers()) {
            other.sendMessage(Format.getColor(Format.getPAPI(p, JoinFeaturesDeluxe.getInstance().getConfig().getString("join-messages.public.first-join.message"))));
        }
    }

    private void sendPrivateJoinMessage(Player p) {
        if(!p.hasPermission("joinfeaturesdeluxe.private.join")) return;
        if(!JoinFeaturesDeluxe.getInstance().getConfig().getBoolean("join-messages.private.join.enabled")) return;
        for(String line : JoinFeaturesDeluxe.getInstance().getConfig().getStringList("join-messages.private.join.lines")) {
            p.sendMessage(Format.getColor(Format.getPAPI(p, line)));
        }
    }

    private void sendPrivateFirstJoinMessage(Player p) {
        if(!p.hasPermission("joinfeaturesdeluxe.private.first-join")) return;
        if(p.hasPlayedBefore()) return;
        if(!JoinFeaturesDeluxe.getInstance().getConfig().getBoolean("join-messages.private.first-join.enabled")) return;
        for(String line : JoinFeaturesDeluxe.getInstance().getConfig().getStringList("join-messages.private.first-join.lines")) {
            p.sendMessage(Format.getColor(Format.getPAPI(p, line)));
        }
    }

    private void sendJoinTitle(Player p) {
        if(!p.hasPermission("joinfeaturesdeluxe.join-title")) return;
        if(!JoinFeaturesDeluxe.getInstance().getConfig().getBoolean("join-messages.join-title.enabled")) return;
        String title, subtitle;
        int fadeIn, stay, fadeOut;
        if(!p.hasPlayedBefore()) {
            title = JoinFeaturesDeluxe.getInstance().getConfig().getString(Format.getColor(Format.getPAPI(p, "join-messages.join-title.first-join.title")));
            subtitle = JoinFeaturesDeluxe.getInstance().getConfig().getString(Format.getColor(Format.getPAPI(p, "join-messages.join-title.first-join.subtitle")));
            fadeIn = JoinFeaturesDeluxe.getInstance().getConfig().getInt("join-messages.join-title.first-join.fadeIn");
            stay = JoinFeaturesDeluxe.getInstance().getConfig().getInt("join-messages.join-title.first-join.stay");
            fadeOut = JoinFeaturesDeluxe.getInstance().getConfig().getInt("join-messages.join-title.first-join.fadeOut");
        }else {
            title = JoinFeaturesDeluxe.getInstance().getConfig().getString(Format.getColor(Format.getPAPI(p, "join-messages.join-title.join.title")));
            subtitle = JoinFeaturesDeluxe.getInstance().getConfig().getString(Format.getColor(Format.getPAPI(p, "join-messages.join-title.join.subtitle")));
            fadeIn = JoinFeaturesDeluxe.getInstance().getConfig().getInt("join-messages.join-title.join.fadeIn");
            stay = JoinFeaturesDeluxe.getInstance().getConfig().getInt("join-messages.join-title.join.stay");
            fadeOut = JoinFeaturesDeluxe.getInstance().getConfig().getInt("join-messages.join-title.join.fadeOut");
        }
        if(JoinFeaturesDeluxe.getInstance().getVersion() >= 10) {
            p.sendTitle(Format.getColor(Format.getPAPI(p, title)), Format.getColor(Format.getPAPI(p, subtitle)), fadeIn, stay, fadeOut);
        }else {
            sendPacketTitle(p, title, fadeIn, stay, fadeOut);
            sendPacketSubtitle(p, subtitle, fadeIn, stay, fadeOut);
        }
    }

    private void sendPacketTitle(Player p, String title, int fadeIn, int stay, int fadeOut) {
        try {
            Object enumTitle = Reflection.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
            Object titleFormat = Reflection.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + Format.getColor(Format.getPAPI(p, title)) + "\"}");
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
            Object subtitleFormat = Reflection.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + Format.getColor(Format.getPAPI(p, subtitle)) + "\"}");
            Constructor<?> subtitleConstructor = Reflection.getNMSClass("PacketPlayOutTitle").getConstructor(Reflection.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], Reflection.getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
            Object subtitlePacket = subtitleConstructor.newInstance(enumSubtitle, subtitleFormat, fadeIn, stay, fadeOut);
            Reflection.sendPacket(p, subtitlePacket);
        }catch(Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error while sending a packet subtitle to \"" + p.getName() + "\": " + e);
        }
    }

    private void sendJoinCommands(Player p) {
        if(!JoinFeaturesDeluxe.getInstance().getConfig().getBoolean("join-commands.enabled")) return;
        if(!p.hasPermission("joinfeaturesdeluxe.join-commands.join")) return;
        for(String key : JoinFeaturesDeluxe.getInstance().getConfig().getConfigurationSection("join-commands.join").getKeys(false)) {
            ConfigurationSection cmd = JoinFeaturesDeluxe.getInstance().getConfig().getConfigurationSection("join-commands.join." + key);
            switch(cmd.getString("executor")) {
                case "C":
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Format.getPAPI(p, cmd.getString("cmd")));
                    break;
                case "P":
                    p.chat("/" + Format.getPAPI(p, cmd.getString("cmd")));
                    break;
                default:
                    Bukkit.getLogger().log(Level.SEVERE, "\"join-commands -> join -> " + key + " -> executor\" must be \"C\" or \"P\"!");
                    break;
            }
        }
    }
}
