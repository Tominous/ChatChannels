package me.simplicitee.storage;

import me.simplicitee.ChatChannels;

public class DBConnection {

	public static Database sql;

	public static String host = ChatChannels.get().getConfig().getString("Storage.MySQL.host");
	public static int port = ChatChannels.get().getConfig().getInt("Storage.MySQL.port");
	public static String db = ChatChannels.get().getConfig().getString("Storage.MySQL.db");
	public static String user = ChatChannels.get().getConfig().getString("Storage.MySQL.user");
	public static String pass = ChatChannels.get().getConfig().getString("Storage.MySQL.pass");
	public static boolean isOpen = false;

	public static void init() {
		open();
		if (!isOpen) {
			ChatChannels.get().getLogger().info("Failed to connect to database.");
			return;
		}
		if (!sql.tableExists("chatchannels")) {
			ChatChannels.get().getLogger().info("Creating chatchannels table");
			String query = "";
			if (ChatChannels.get().getConfig().getString("Storage.engine").equalsIgnoreCase("mysql")) {
				query = "CREATE TABLE `chatchannels` (" + "`uuid` varchar (255), " + "`player` varchar (255), " + "`message` varchar (255), " + "`channel` varchar (255)" + ");";
			} else {
				query = "CREATE TABLE `chatchannels` (" + "`uuid` TEXT (255), " + "`player` TEXT (255), " + "`message` TEXT (255), " + "`channel` TEXT (255)" + " );";
			}
			sql.modifyQuery(query);
		}
	}
	
	public static void close() {
		sql.close();
		isOpen = false;
	}
	
	public static void open() {
		if (ChatChannels.get().getConfig().getString("Storage.engine").equalsIgnoreCase("mysql")) {
			sql = new MySQL(ChatChannels.get().getLogger(), "Establishing MySQL Connection...", host, port, user, pass, db);
			if (((MySQL) sql).open() == null) {
				return;
			}

			isOpen = true;
			ChatChannels.get().getLogger().info("Database connection established.");
		} else {
			sql = new SQLite(ChatChannels.get().getLogger(), "Establishing SQLite Connection.", "channels.db", ChatChannels.get().getDataFolder().getAbsolutePath());
			if (((SQLite) sql).open() == null) {
				ChatChannels.get().getLogger().severe("Disabling due to database error");
				return;
			}

			isOpen = true;
			ChatChannels.get().getLogger().info("Database connection established");
		}
	}

	public static boolean isOpen() {
		return isOpen;
	}
}
