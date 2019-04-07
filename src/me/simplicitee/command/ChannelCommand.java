package me.simplicitee.command;

import java.io.File;
import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.simplicitee.Channel;
import me.simplicitee.ChatChannels;
import me.simplicitee.FileManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

public class ChannelCommand implements CommandExecutor{
	
	private String[] toggle = {"toggle", "tog", "t"};
	private String[] version = {"version", "v"};
	private String[] reload = {"reload", "r"};
	private String[] list = {"list", "l"};
	private String[] help = {"help", "h"};
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "------------{ " + ChatColor.AQUA + "ChatChannels Commands" + ChatColor.RED + " }------------");
			sender.sendMessage(ChatColor.RED + "/channel " + ChatColor.AQUA + "-" + ChatColor.RED + " This page");
			sender.sendMessage(ChatColor.RED + "/channel reload " + ChatColor.AQUA + "-" + ChatColor.RED + " Reloads the files for the plugin");
			sender.sendMessage(ChatColor.RED + "/channel version " + ChatColor.AQUA + "-" + ChatColor.RED + " Sends you the plugin version");
			sender.sendMessage(ChatColor.RED + "/channel help " + ChatColor.AQUA + "-" + ChatColor.RED + " Shows how to talk in a channel");
			sender.sendMessage(ChatColor.RED + "/channel toggle <channel> " + ChatColor.AQUA + "-" + ChatColor.RED + " Toggles your chat into <channel>");
			sender.sendMessage(ChatColor.RED + "/channel list " + ChatColor.AQUA + "-" + ChatColor.RED + " Shows a list of channels you can talk in");
		}
		if (args.length == 1) {
			if (Arrays.asList(version).contains(args[0].toLowerCase())) {
				String version = ChatChannels.get().getDescription().getVersion();
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.AQUA + "[ChatChannels] Version: " + version);
				} else {
					Player player = (Player) sender;
					if (player.hasPermission(ChatChannels.getVersionPermission())) {
						sender.sendMessage(ChatColor.AQUA + "[ChatChannels] Version: " + version);
					}
				}
			}
			else if (Arrays.asList(reload).contains(args[0].toLowerCase())) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "[ChatChannels] Reloding plugin");
					reload(sender);
				} else {
					Player player = (Player) sender;
					if (player.hasPermission(ChatChannels.getReloadPermission())) {
						player.sendMessage(ChatColor.RED + "[ChatChannels] Reloading plugin");
						reload(sender);
					}
				}
			}
			else if (Arrays.asList(help).contains(args[0].toLowerCase())) {
				if (sender.hasPermission(ChatChannels.getHelpPermission())) {
					sender.sendMessage(ChatColor.AQUA + "For whatever channel you want to speak in, type the symbol in chat first, and then without adding a space between the symbol and the first word of your message type your message. For example:"
						+ ChatColor.RED + "\n$Hi, my name is Steve"
						+ ChatColor.RED + "\nsends -> (Admin Chat) Steve: Hi, my name is Steve"
						+ ChatColor.AQUA + "\nOnly people with the chatchannels.admin.read permission node would be able to see the message. Please also note this is just an example and may not work.");
				}
			}
			else if (Arrays.asList(list).contains(args[0].toLowerCase())) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c<&b&kO&r&c>-< &bChannels &c>-<&b&kO&r&c>"));
				for (String name : Channel.getChannels().keySet()) {
					Channel channel = Channel.getChannels().get(name);
					if (sender.hasPermission(channel.getSendPermission()) || sender.hasPermission(channel.getAllPermission())) {
						TextComponent component = new TextComponent("- " + name);
						component.setColor(ChatColor.AQUA);
						component.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new TextComponent[] {new TextComponent("Symbol: " + channel.getSymbol() + "\nDistance: " + channel.getDistance())}));
						sender.spigot().sendMessage(component);
					}
				}
			}
		}
		if (args.length == 2) {
			if (Arrays.asList(toggle).contains(args[0].toLowerCase())) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "[ChatChannels] Only a player can use that command");
					return true;
				}
				Player player = (Player) sender;
				for (String channel : Channel.getChannels().keySet()) {
					if (args[1].equalsIgnoreCase(channel)) {
						if (!Channel.getToggledPlayers().containsKey(player.getUniqueId())) {
							sender.sendMessage(ChatColor.AQUA + "[ChatChannels] You have toggled into the " + channel + " channel!");
							Channel.getToggledPlayers().put(player.getUniqueId(), Channel.getChannels().get(channel));
							return true;
						} else {
							if (Channel.getToggledPlayers().get(player.getUniqueId()).equals(Channel.getChannels().get(channel))) {
								sender.sendMessage(ChatColor.AQUA + "[ChatChannels] You have toggled out of the " + channel + " channel!");
								Channel.getToggledPlayers().remove(player.getUniqueId());
							} else {
								sender.sendMessage(ChatColor.YELLOW + "[ChatChannels] You have switched your toggled channel to " + channel + "!");
								Channel.getToggledPlayers().replace(player.getUniqueId(), Channel.getChannels().get(channel));
							}
							return true;
						}
					}
				}
				sender.sendMessage(ChatColor.RED + "[ChatChannels] Selected channel not found");
				return true;
			}
		}
		return true;
	}
	
	private void reload(final CommandSender sender) {
		Channel.getChannels().clear();
		BukkitRunnable run = new BukkitRunnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				FileManager.reloadFiles(new File(ChatChannels.get().getDataFolder() + File.separator + new File("/Channels/")));
				sender.sendMessage(ChatColor.AQUA + "[ChatChannels] Plugin reloaded");
			}
			
		};
		run.runTaskLater(ChatChannels.get(), 5);
	}
}
