package me.simplicitee;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FileManager {
	
	private static ChatChannels plugin;

	public FileManager() {
		plugin = ChatChannels.get();
		loadFiles();
	}
	
	public static void createNewFile(File file) {
		File file2 = new File(new File("/Channels/") + File.separator + file);
		if (!file2.exists()) {
			try {
				file2.createNewFile();
				plugin.getLogger().info("Created new " + file.getName() + " file!");
			} catch (Exception e) {
				plugin.getLogger().info("Failed to create new " + file.getName() + " file!");
				e.printStackTrace();
			}
		} else {
			plugin.getLogger().info(file.getName() + " file already exists!");
		}
	}
	
	public static void deleteFile(File file) {
		File file2 = new File(new File("/Channels/") + File.separator + file);
		if (file2.exists()) {
			file2.delete();
		}
	}
	
	public static void loadFiles() {
		if (!plugin.getDataFolder().exists()) {
			try {
				plugin.getDataFolder().mkdirs();
				plugin.getLogger().info("Generating new directory!");
			} catch (Exception e) {
				plugin.getLogger().info("Failed to generate new directory!");
				e.printStackTrace();
			}
		}
		File folder = new File(plugin.getDataFolder() + File.separator + new File("/Channels/"));
		reloadFiles(folder);
	}
	
	public static void loadStaff(File folder) {
		File staff = new File(folder, "Staff.yml");
		
		FileConfiguration staffyaml = YamlConfiguration.loadConfiguration(staff);
		
		if (!staff.exists()) {
			try {
				staff.createNewFile();
				plugin.getLogger().info("Generating new default Staff file!");
			} catch (IOException e) {
				plugin.getLogger().info("Failed to generate new default Staff file!");
				e.printStackTrace();
			}
		}
		
		reloadFile(staffyaml, staff);
		
		staffyaml.addDefault("name", "Staff");
		staffyaml.addDefault("prefix", "&c(Staff Chat)");
		staffyaml.addDefault("symbol", "$");
		staffyaml.addDefault("format", "<prefix> &r<player>: <message>");
		staffyaml.addDefault("alert-sound", "ENTITY_EXPERIENCE_ORB_PICKUP");
		staffyaml.addDefault("distance", -1);
		staffyaml.addDefault("send-permission", "chatchannels.staff.send");
		staffyaml.addDefault("read-permission", "chatchannels.staff.read");
		staffyaml.addDefault("all-permission", "chatchannels.staff.*");
		
		staffyaml.options().copyDefaults(true);
		try {
			staffyaml.save(staff);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadLocal(File folder) {
		File local = new File(folder, "Local.yml");
		
		FileConfiguration localyaml = YamlConfiguration.loadConfiguration(local);
		
		if (!local.exists()) {
			try {
				local.createNewFile();
				plugin.getLogger().info("Generating new default Local file!");
			} catch (IOException e) {
				plugin.getLogger().info("Failed to generate new default Local file!");
				e.printStackTrace();
			}
		}
		
		reloadFile(localyaml, local);
		
		localyaml.addDefault("name", "Local");
		localyaml.addDefault("prefix", "&9(Local Chat)");
		localyaml.addDefault("symbol", "@");
		localyaml.addDefault("format", "<prefix> &r<player>: <message>");
		localyaml.addDefault("distance", 50);
		localyaml.addDefault("alert-sound", "BLOCK_LAVA_POP");
		localyaml.addDefault("send-permission", "chatchannels.local.send");
		localyaml.addDefault("read-permission", "chatchannels.local.read");
		localyaml.addDefault("all-permission", "chatchannels.local.*");
		
		localyaml.options().copyDefaults(true);
		try {
			localyaml.save(local);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadFeudal(File folder) {
		File feudal = new File(folder, "Kingdom.yml");
		
		FileConfiguration yaml = YamlConfiguration.loadConfiguration(feudal);
		
		if (!feudal.exists()) {
			try {
				feudal.createNewFile();
				plugin.getLogger().info("Generating new default Kingdom file!");
			} catch (IOException e) {
				plugin.getLogger().info("Failed to generate new default Kingdom file!");
				e.printStackTrace();
			}
		}
		
		reloadFile(yaml, feudal);
		
		yaml.addDefault("name", "Kingdom");
		yaml.addDefault("prefix", "&e(&b<kingdom> &eChat)");
		yaml.addDefault("symbol", "k:");
		yaml.addDefault("format", "<prefix> &r<player>: <message>");
		yaml.addDefault("distance", -1);
		yaml.addDefault("alert-sound", "BLOCK_LAVA_POP");
		yaml.addDefault("send-permission", "chatchannels.kingdom.send");
		yaml.addDefault("read-permission", "chatchannels.kingdom.read");
		yaml.addDefault("all-permission", "chatchannels.kingdom.*");
		
		yaml.options().copyDefaults(true);
		try {
			yaml.save(feudal);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadFactions(File folder) {
		File faction = new File(folder, "Faction.yml");
		
		FileConfiguration yaml = YamlConfiguration.loadConfiguration(faction);
		
		if (!faction.exists()) {
			try {
				faction.createNewFile();
				plugin.getLogger().info("Generating new default Faction file!");
			} catch (IOException e) {
				plugin.getLogger().info("Failed to generate new default Faction file!");
				e.printStackTrace();
			}
		}
		
		reloadFile(yaml, faction);
		
		yaml.addDefault("name", "Faction");
		yaml.addDefault("prefix", "&e(&b<faction> &eChat)");
		yaml.addDefault("symbol", "f:");
		yaml.addDefault("format", "<prefix> &r<player>: <message>");
		yaml.addDefault("distance", -1);
		yaml.addDefault("alert-sound", "BLOCK_LAVA_POP");
		yaml.addDefault("send-permission", "chatchannels.faction.send");
		yaml.addDefault("read-permission", "chatchannels.faction.read");
		yaml.addDefault("all-permission", "chatchannels.faction.*");
		
		yaml.options().copyDefaults(true);
		try {
			yaml.save(faction);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveFiles() {
		for (String chat : Channel.getChannels().keySet()) {
			Channel channel = Channel.getChannels().get(chat);
			FileConfiguration config = channel.getYaml();
			
			try {
				config.options().copyDefaults(true);
				config.save(channel.getFile());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void reloadFile(FileConfiguration config, File file) {
		try {
			config.load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void reloadFiles(File folder) {
		if (!folder.exists()) {
			try {
				folder.mkdirs();
				plugin.getLogger().info("Generating new Channels folder and default files!");
			} catch (Exception e) {
				plugin.getLogger().info("Failed to generate new Channels folder and default files!");
			}
			loadStaff(folder);
			loadLocal(folder);
		}
		
		if (ChatChannels.isUsingFeudal()) {
			loadFeudal(folder);
		}
		
		if (ChatChannels.isUsingFactions()) {
			loadFactions(folder);
		}
		
		for (File file : folder.listFiles()) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			reloadFile(config, file);
			new Channel(file);
		}
	}
}
