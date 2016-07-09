package me.simplicitee.storage;

import me.simplicitee.ChatChannels;

import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public abstract class Database {
	
	protected final Logger log;
	protected final String prefix;
	protected final String dbprefix;
	protected Connection connection = null;

	public Database(Logger log, String prefix, String dbprefix) {
		this.log = log;
		this.prefix = prefix;
        this.dbprefix = dbprefix;
    }
	
	/**
	 * Checks database to see if a column exists.
	 * 
	 * @param tableName Table being checked
	 * @param columnName Column being checked for
	 * @return true if column exists, else false
	 */
	
	protected boolean columnExists(String tableName, String columnName) {
		try {
			DatabaseMetaData dmd = connection.getMetaData();
			ResultSet rs = dmd.getColumns(null, null, tableName, columnName);
			
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

    /**
     * Print information to console.
     *
     * @param message The string to print to console
     */
    protected void printInfo(String message) {
        log.info(prefix + dbprefix + message);
    }

    /**
     * Print error to console.
     *
     * @param message The string to print to console
     * @param severe If {@param severe} is true print an error, else print a warning
     */
    protected void printErr(String message, boolean severe) {
        if (severe) log.severe(prefix + dbprefix + message);
        else log.warning(prefix + dbprefix + message);
    }

    /**
     * Returns the current Connection.
     *
     * @return Connection if exists, else null
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Opens connection to Database.
     *
     * @return Connection if successful
     */
    abstract Connection open();

    /**
     * Close connection to Database.
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            this.printErr("There was no SQL connection open.", false);
        }
    }

    /**
     * Queries the Database, for queries which modify data.
     *
     * @param query Query to run
     */
    public void modifyQuery(final String query) {
    	new BukkitRunnable() {
    		@Override
    		public void run() {
    			try {
    				PreparedStatement stmt = connection.prepareStatement(query);
    				stmt.execute();
    				stmt.close();
    			} catch (SQLException e) {
    				e.printStackTrace();
    			}
    		}
    	}.runTaskAsynchronously(ChatChannels.get());
    }

    /**
     * Queries the Database, for queries which return results.
     *
     * @param query Query to run
     * @return Result set of ran query
     */
    public ResultSet readQuery(String query) {
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            return rs;
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Check database to see if a table exists.
     * 
     * @param table Table name to check
     * @return true if table exists, else false
     */
    public boolean tableExists(String table) {
        try {
            DatabaseMetaData dmd = connection.getMetaData();
            ResultSet rs = dmd.getTables(null, null, table, null);
            
            return rs.next();
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
