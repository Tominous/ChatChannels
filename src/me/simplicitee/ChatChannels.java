package me.simplicitee;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import me.simplicitee.command.ChannelCommand;
import me.simplicitee.storage.DBConnection;

public class ChatChannels extends JavaPlugin{
	
	private static ChatChannels plugin;
	private static Permission version;
	private static Permission reload;
	private static Permission help;
	private static Permission toggle;

	@Override
	public void onEnable() {
		plugin = this;
		
		new FileManager();
		new ChatListener();
		CommandExecutor exe = new ChannelCommand();
		this.getCommand("channel").setExecutor(exe);
		
		version = new Permission("chatchannels.command.version");
		reload = new Permission("chatchannels.command.reload");
		help = new Permission("chatchannels.command.help");
		toggle = new Permission("chatchannels.command.toggle");
		version.setDefault(PermissionDefault.OP);
		reload.setDefault(PermissionDefault.OP);
		help.setDefault(PermissionDefault.OP);
		toggle.setDefault(PermissionDefault.FALSE);
		
		loadConfig();
		
		DBConnection.init();
	}
	
	@Override
	public void onDisable() {
		Channel.getChannels().clear();
		FileManager.reloadFiles(new File(getDataFolder() + File.separator + new File("/Channels/")));
		DBConnection.close();
	}
	
	public static ChatChannels get() {
		return plugin;
	}
	
	public static Permission getVersionPermission() {
		return version;
	}
	
	public static Permission getReloadPermission() {
		return reload;
	}
	
	public static Permission getHelpPermission() {
		return help;
	}
	
	public static Permission getTogglePermission() {
		return toggle;
	}
	
	public static boolean isUsingFeudal() {
		return (Bukkit.getPluginManager().getPlugin("Feudal") != null && Bukkit.getPluginManager().getPlugin("Feudal").isEnabled());
	}
	
	public static boolean isUsingFactions() {
		return (Bukkit.getPluginManager().getPlugin("Factions") != null && Bukkit.getPluginManager().getPlugin("Factions").isEnabled());
	}
	
	private void loadConfig() {
		FileConfiguration config = getConfig();
		
		config.addDefault("Storage.enabled", true);
		config.addDefault("Storage.engine", "sqlite");

		config.addDefault("Storage.MySQL.host", "localhost");
		config.addDefault("Storage.MySQL.port", 3306);
		config.addDefault("Storage.MySQL.pass", "");
		config.addDefault("Storage.MySQL.db", "minecraft");
		config.addDefault("Storage.MySQL.user", "root");
		
		config.options().copyDefaults(true);
		saveConfig();
	}
}
