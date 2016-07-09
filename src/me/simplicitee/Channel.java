package me.simplicitee;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Channel {

	private static ConcurrentHashMap<String, Channel> channels = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<UUID, Channel> toggled = new ConcurrentHashMap<>();
	
	private File file;
	private FileConfiguration yaml;
	private Permission send, read, all;
	private String name, prefix, format, symbol;
	private double distance;
	private Sound alert;
	
	public Channel(File file) {
		this.file = file;
		this.yaml = YamlConfiguration.loadConfiguration(file);
		name = yaml.getString("name");
		prefix = yaml.getString("prefix");
		symbol = yaml.getString("symbol");
		format = yaml.getString("format");
		if (yaml.contains("distance"))
			distance = yaml.getDouble("distance");
		else
			distance = -1;
		send = new Permission(yaml.getString("send-permission"));
		read = new Permission(yaml.getString("read-permission"));
		all = new Permission(yaml.getString("all-permission"));
		alert = Sound.valueOf(yaml.getString("alert-sound"));
		channels.put(name, this);
	}
	
	public Sound getAlert() {
		return alert;
	}
	
	public Permission getAllPermission() {
		return all;
	}
	
	public double getDistance() {
		return distance;
	}
	
	public File getFile() {
		return file;
	}
	
	public String getFormat() {
		return format;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPrefix() {
		return ChatColor.translateAlternateColorCodes('&', prefix);
	}
	
	public Permission getReadPermission() {
		return read;
	}
	
	public Permission getSendPermission() {
		return send;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public FileConfiguration getYaml() {
		return yaml;
	}
	
	public static ConcurrentHashMap<String, Channel> getChannels() {
		return channels;
	}
	
	public static ConcurrentHashMap<UUID, Channel> getToggledPlayers() {
		return toggled;
	}
}
