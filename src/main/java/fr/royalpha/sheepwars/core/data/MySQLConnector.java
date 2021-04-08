package fr.royalpha.sheepwars.core.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnector extends Database {

	private final String user;
	private final String database;
	private final String password;
	private final String port;
	private final String hostname;

	public MySQLConnector(String hostname, Integer port, String database, String username, String password) {
		this(hostname, Integer.toString(port), database, username, password);
	}
	
	public MySQLConnector(String hostname, String port, String database, String username, String password) {
		super();
		this.hostname = hostname;
		this.port = port;
		this.database = database;
		this.user = username;
		this.password = password;
	}
	
	public Connection openConnection() throws SQLException, ClassNotFoundException {
		if (checkConnection()) {
			return this.connection;
		}
		Class.forName("com.mysql.jdbc.Driver");
		this.connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database + "?useSSL=false&autoReconnect=true", this.user, this.password);
		return this.connection;
	}
}
