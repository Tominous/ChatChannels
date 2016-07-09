package me.simplicitee;

import java.util.ConcurrentModificationException;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.MPlayerColl;

import me.simplicitee.storage.DBConnection;
import us.forseth11.feudal.core.Feudal;
import us.forseth11.feudal.kingdoms.Kingdom;
import us.forseth11.feudal.user.User;

public class ChatListener implements Listener{
	
	public ChatListener() {
		ChatChannels.get().getServer().getPluginManager().registerEvents(this, ChatChannels.get());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent event) {
		if (event.isCancelled()) return;
		
		Player player = event.getPlayer();
		Set<Player> recipients;
		try {
			recipients = event.getRecipients();
		} catch (ConcurrentModificationException e) {
			return;
		}
		
		for (String chat : Channel.getChannels().keySet()) {
			Channel channel = Channel.getChannels().get(chat);
			
			if (event.getMessage() == null) break;
			
			if (channel.getName().equals("Kingdom")) {
				if (ChatChannels.isUsingFeudal()) {
					
				}
				return;
			}
			
			if (channel.getName().equals("Faction")) {
				if (ChatChannels.isUsingFactions()) {
					
				}
				return;
			}
			
			if (!player.hasPermission(channel.getSendPermission()) && !player.hasPermission(channel.getAllPermission())) continue;
			
			for (Player recipient : event.getRecipients()) {
				if (player.getUniqueId() == recipient.getUniqueId()) {
					continue;
				}
				if (channel.getDistance() != -1) {
					if (player.getWorld() != recipient.getWorld()) { 
						recipients.remove(recipient);
						continue;
					}
					if (player.getLocation().distance(recipient.getLocation()) > channel.getDistance()) {
						recipients.remove(recipient);
						continue;
					}
				}
				if (!recipient.hasPermission(channel.getReadPermission())) {
					recipients.remove(recipient);
					continue;
				}
			}
			
			if (Channel.getToggledPlayers().containsKey(player.getUniqueId())) {
				if (channel == Channel.getToggledPlayers().get(player.getUniqueId())) {
					formatMessage(event, channel, false);
					storeMessage(event.getMessage(), player, channel, false);
					return;
				}
			}
			
			if (channel.getSymbol() == null) continue;
			if (!event.getMessage().startsWith(channel.getSymbol())) continue;
			
			formatMessage(event, channel, true);
			storeMessage(event.getMessage(), player, channel, true);
			return;
		}
	}
	
	public void formatMessage(AsyncPlayerChatEvent event, Channel channel, boolean symbol) {
		String format = channel.getFormat();
		String prefix = channel.getPrefix();
		if (channel.getName().equals("Kingdom")) {
			if (ChatChannels.isUsingFeudal()) {
				User user = Feudal.getUser(event.getPlayer().getUniqueId().toString());
				Kingdom kingdom = Feudal.getKingdom(user.getKingdomUUID());
				prefix = prefix.replace("<kingdom>", kingdom.getName());
			}
		}
		
		if (channel.getName().equals("Faction")) {
			if (ChatChannels.isUsingFactions()) {
				MPlayer user = MPlayerColl.get().getByName(event.getPlayer().getName());
				Faction faction = FactionColl.get().getByName(user.getFactionName());
				prefix = prefix.replace("<faction>", faction.getName());
			}
		}
		
		format = format.replace("<prefix>", prefix);
		format = format.replace("<player>", "%1$s");
		format = format.replace("<message>", "%2$s");
		event.setFormat(ChatColor.translateAlternateColorCodes('&', format));
		
		if (symbol) {
			String message = event.getMessage().substring(channel.getSymbol().length(), event.getMessage().length());
			event.setMessage(message);
		}
	}
	
	public void storeMessage(String text, Player player, Channel channel, boolean used) {
		if (used) {
			text = text.substring(1, text.length());
		}
		DBConnection.sql.modifyQuery("INSERT INTO chatchannels (uuid, player, channel, message) VALUES ('" + player.getUniqueId() + "', '" + player.getName() + "', '" + channel.getName() + "', '" + text + "');");
	}
}
