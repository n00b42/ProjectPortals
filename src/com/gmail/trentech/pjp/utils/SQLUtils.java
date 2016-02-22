package com.gmail.trentech.pjp.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.spongepowered.api.service.sql.SqlService;

import com.gmail.trentech.pjp.Main;

public abstract class SQLUtils {

    protected static SqlService sql;

    protected static DataSource getDataSource() throws SQLException {
	    if (sql == null) {
	        sql = Main.getGame().getServiceManager().provide(SqlService.class).get();
	    }
	    
        return sql.getDataSource("jdbc:h2:./config/projectportals/data");
	}

	public static void createTables() {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Portals (Name TEXT, Frame TEXT, Fill TEXT, Destination TEXT, Particle TEXT)");
			statement.executeUpdate();
			
			statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Buttons (Name TEXT, Destination TEXT)");
			statement.executeUpdate();
			
			statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Doors (Name TEXT, Destination TEXT)");
			statement.executeUpdate();
			
			statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Levers (Name TEXT, Destination TEXT)");
			statement.executeUpdate();
			
			statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Plates (Name TEXT, Destination TEXT)");
			statement.executeUpdate();
			
			statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Warps (Name TEXT, Destination TEXT)");
			statement.executeUpdate();
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
}