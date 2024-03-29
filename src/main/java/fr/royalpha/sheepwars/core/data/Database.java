package fr.royalpha.sheepwars.core.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Database {
	protected Connection connection;

	protected Database() {
		this.connection = null;
	}

	public abstract Connection openConnection() throws SQLException, ClassNotFoundException;

	public boolean checkConnection() throws SQLException {
		return (this.connection != null) && (!this.connection.isClosed());
	}

	public Connection getConnection() {
		return this.connection;
	}

	public Connection getCheckedConnection() throws SQLException, ClassNotFoundException {
		if (!checkConnection())
			openConnection();
		return this.connection;
	}

	public boolean closeConnection() throws SQLException {
		if (this.connection == null) {
			return false;
		}
		this.connection.close();
		return true;
	}

	public ResultSet querySQL(String query) throws SQLException, ClassNotFoundException {
		return getCheckedConnection().createStatement().executeQuery(query);
	}

	public int updateSQL(String query) throws SQLException, ClassNotFoundException {
		return getCheckedConnection().createStatement().executeUpdate(query);
	}
}
