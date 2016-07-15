package com.example;

import java.sql.SQLException;
import java.util.Properties;

import org.flywaydb.core.Flyway;
import org.hsqldb.Database;
import org.hsqldb.Session;
import org.hsqldb.TransactionManager;
import org.hsqldb.jdbc.JDBCConnection;
import org.hsqldb.jdbc.JDBCDataSource;

public class FlywayHsqldbHangApplication {

	public static void main(String[] args) throws SQLException {
		JDBCDataSource dataSource = new JDBCDataSource();
		dataSource.setDatabase("jdbc:hsqldb:mem:hang");
		if (Boolean.getBoolean("mvcc")) {
			configureMvcc(dataSource);
		}
		System.out.println("HSQLDB transaction manager: " + getTransactionManager(dataSource));

		Flyway flyway = new Flyway();
		flyway.setDataSource(dataSource);
		flyway.migrate();
	}

	private static void configureMvcc(JDBCDataSource dataSource) throws SQLException {
		Properties properties = new Properties();
		properties.setProperty("hsqldb.tx", "mvcc");
		dataSource.setProperties(properties);
	}

	private static TransactionManager getTransactionManager(JDBCDataSource dataSource) throws SQLException {
		JDBCConnection connection = null;
		try {
			connection = (JDBCConnection) dataSource.getConnection();
			Database database = ((Session) connection.getSession()).getDatabase();
			return database.txManager;
		} finally {
			connection.close();
		}
	}

}
