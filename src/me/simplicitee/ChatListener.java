package me.simplicitee;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.simplicitee.storage.DBConnection;

public class ChatListener implements Listener{
	
	public ChatListener() {
		ChatChannels.get().getServer().getPluginManager().registerEvents(this, ChatChannels.get());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent event) {
		if (event.isCancelled()) return;
		if (!event.isAsynchronous()) return;
		
		Player player = event.getPlayer();
		
		for (String chat : Channel.getChannels().keySet()) {
			Channel channel = Channel.getChannels().get(chat);
			
			if (event.getMessage() == null) break;
			if (!player.hasPermission(channel.getSendPermission()) && !player.hasPermission(channel.getAllPermission())) continue;
			
			Iterator<Player> recipients = event.getRecipients().iterator();
			while (recipients.hasNext()) {
				Player recipient = recipients.next();
			
				if (player.getUniqueId() == recipient.getUniqueId()) {
					continue;
				} 
				
				if (!recipient.hasPermission(channel.getReadPermission())) {
					recipients.remove();
					continue;
				}
				
				if (channel.getDistance() != -1) {
					if (player.getWorld() != recipient.getWorld()) { 
						recipients.remove();
						continue;
					}
					if (player.getLocation().distance(recipient.getLocation()) > channel.getDistance()) {
						recipients.remove();
						continue;
					}
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
		
		format = format.replace("<prefix>", channel.getPrefix());
		format = format.replace("<player-displayname>", event.getPlayer().getDisplayName());
		format = format.replace("<player-username>", "%1$s");
		format = format.replace("<message>", "%2$s");
		event.setFormat(ChatColor.translateAlternateColorCodes('&', format));
		
		if (symbol) {
			String message = event.getMessage().substring(channel.getSymbol().length(), event.getMessage().length());
			event.setMessage(message.trim());
		}
	}
	
	public void storeMessage(String text, Player player, Channel channel, boolean used) {
		if (used) {
			text = text.substring(channel.getSymbol().length(), text.length());
		}
		
		text = text.replace("'", "");
		
		DBConnection.sql.modifyQuery("INSERT INTO chatchannels (uuid, player, channel, message) VALUES ('" + player.getUniqueId() + "', '" + player.getName() + "', '" + channel.getName() + "', '" + text + "');");
	}
}
