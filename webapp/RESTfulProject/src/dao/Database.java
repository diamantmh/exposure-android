package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
	
	public Connection Get_Connection() throws Exception
	{
		try
		{
		String connectionURL = "jdbc:sqlserver://vyl5xz64ek.database.windows.net;database=Exposure";
		Connection connection = null;
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
		connection = DriverManager.getConnection(connectionURL, "kekonat@vyl5xz64ek.database.windows.net", "N0REGRETs");

		return connection;
		}
		catch (SQLException e)
		{
			System.out.println("database sql e");
		throw e;	
		}
		catch (Exception e)
		{
			System.out.println("database e");
		throw e;	
		}
	}

}
