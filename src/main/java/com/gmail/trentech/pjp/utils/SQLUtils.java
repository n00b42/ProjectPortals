package com.gmail.trentech.pjp.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

public abstract class SQLUtils {

	protected static SqlService sql;

	protected static DataSource getDataSource() throws SQLException {
		if (sql == null) {
			sql = Sponge.getServiceManager().provide(SqlService.class).get();
		}

		return sql.getDataSource("jdbc:h2:./config/" + Resource.ID + "/data");
	}

	public static void createTables() {

		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Portals (Name TEXT, Data TEXT)");
			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}