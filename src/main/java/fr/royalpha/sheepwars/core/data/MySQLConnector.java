package fr.royalpha.sheepwars.core.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnector extends Database {

	private final String user, database, password, port, hostname, options;

	public MySQLConnector(String hostname, Integer port, String database, String username, String password, String options) {
		this(hostname, Integer.toString(port), database, username, password, options);
	}
	
	public MySQLConnector(String hostname, String port, String database, String username, String password, String options) {
		super();
		this.hostname = hostname;
		this.port = port;
		this.database = database;
		this.user = username;
		this.password = password;
		this.options = options;
	}
	
	public Connection openConnection() throws SQLException, ClassNotFoundException {
		if (checkConnection()) {
			return this.connection;
		}
		Class.forName("com.mysql.jdbc.Driver");
		this.connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database + (options != null && options != "" ? "?" + options : ""), this.user, this.password);
		return this.connection;
	}
}
