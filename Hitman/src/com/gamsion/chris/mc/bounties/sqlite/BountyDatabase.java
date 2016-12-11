package com.gamsion.chris.mc.bounties.sqlite;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

import com.gamsion.chris.mc.bounties.Bounties;

public class BountyDatabase {
	private final Bounties b;
	/**<p>
	 * The table name used in the database.
	 * </p>
	 */
	private String tableName = "BOUNTIES";

	/**<p>
	 * The folder of 
	 * </p>
	 */
	private File dataFile;
	PreparedStatement isEmpty;
	
	public Connection conn;
	public int tokens = 0;
	public BountyDatabase(Bounties p) {
		this.b = p;
		this.dataFile = new File(p.getDataFolder(),"/saves/"+b.getConfig().getString("save.databasename"));
	}

	public Connection getSQLConnection() {

		if(!this.dataFile.exists()){
			this.dataFile.getParentFile().mkdirs();
			try {
				this.dataFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			if (conn != null && !conn.isClosed()) {
				return conn;
			}
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + dataFile.getName());
			return conn;
		} catch (SQLException ex) {
			b.getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
		} catch (ClassNotFoundException ex) {
			b.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
		}
		return null;
	}
	
	public boolean isEmpty(){
		
		try {
			return !isEmpty.executeQuery().next();
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
	}
	
	
	
	
	public void prepareStatements(){
		try {
			isEmpty = conn.prepareStatement(String.format("SELECT id FROM `%s`", tableName));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
}
