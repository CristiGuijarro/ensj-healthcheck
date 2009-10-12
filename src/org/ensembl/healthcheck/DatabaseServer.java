package org.ensembl.healthcheck;

import java.sql.Connection;

import org.ensembl.healthcheck.util.DBUtils;

/**
 * Class to represent a physical database server.
 * 
 * @author glennproctor
 * 
 */
public class DatabaseServer {

	String driver;

	String databaseURL;

	String host;

	String port;

	String user;

	String pass;

	Connection connection; // connection to this server, not a specific named database - use getDatabaseConnection for that

	public DatabaseServer(String host, String port, String user, String pass, String driver) {

		this.driver = driver;

		this.host = host;

		this.port = port;

		this.user = user;

		this.pass = pass;

		this.databaseURL = buildDatabaseURL();

		this.connection = DBUtils.openConnection(driver, databaseURL, user, pass);

	}

	// -------------------------------------------------------------------------

	public Connection getDatabaseConnection(String databaseName) {

		return DBUtils.openConnection(driver, databaseURL + databaseName, user, pass);

	}

	// -------------------------------------------------------------------------

	public Connection getServerConnection() {

		return DBUtils.openConnection(driver, databaseURL, user, pass);

	}

	// -------------------------------------------------------------------------

	private String buildDatabaseURL() {

		databaseURL = "jdbc:mysql://" + host + ":" + port + "/";

		System.out.println("Database URL: " + databaseURL);

		return databaseURL;

	}

	// -------------------------------------------------------------------------

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getDatabaseURL() {
		return databaseURL;
	}

	public void setDatabaseURL(String databaseURL) {
		this.databaseURL = databaseURL;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String toString() {

		String driverStr = driver.contains("mysql") ? "MySQL" : "Unknown";

		return driverStr + " database on " + host + ":" + port + " as " + user;

	}
}
