import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Statement;

import javax.swing.JOptionPane;

public class Maintenance implements dbInterface{
	protected Connection con;
	protected String nxt = "', '";
	
	protected String licenseNumber;
	protected int maintenanceInterval, lastMileage, monthlyMiles;
	protected final double laborCost = 125.0;
	
	//initialize with placeholder data
	public Maintenance()
	{
		this("N/A",-1,-1,-1);
	}
	
	//make connection and initialize with specific attributes
	public Maintenance(String license, int LastMileage, int MaintenanceInterval, int MonthlyMiles)
	{
		try {
			Class.forName(JDBC_DRIVER);
			con = DriverManager.getConnection(url);
			
			this.setLicense(license);
			setLastMileage(LastMileage);
			setMaintInterval(MaintenanceInterval);
			setMonthlyMiles(MonthlyMiles);
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error setting connection");
			e.printStackTrace();
		}
	}
	
	//insert maintenance information into database
	public void insert()
	{
		String upd = "INSERT into Maintenance(licenseNumber, maintenanceInterval,"
			+ " lastMileage, nextMileage, monthlyMiles) VALUES(?,?,?,?,?)";
		String upd2 = "INSERT into Estimates(licenseNumber, MonthlyCost) VALUES(?,?)";
		try{
			PreparedStatement pstmt = con.prepareStatement(upd);
			pstmt.setString(1, getLicense());
			pstmt.setInt(2, getMaintInterval());
			pstmt.setInt(3, getLastMileage());
			pstmt.setInt(4, getNextMileage());
			pstmt.setInt(5, getMonthlyMiles());
			pstmt.executeUpdate();
			
			pstmt = con.prepareStatement(upd2);
			pstmt.setString(1, getLicense());
			pstmt.setDouble(2, (calculateMonthlyMaintenance() * laborCost));
			pstmt.executeUpdate();
			pstmt.close();
			
		}catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, "Error inserting maintenance record");
			e.printStackTrace();
		}
	}
	
	//update maintenance record
	public void update()
	{
		String upd = "UPDATE Maintenance SET maintenanceInterval = ?, lastMileage = ?,"
				+ " nextMileage = ?, monthlyMiles = ? WHERE licenseNumber = ?";
		String upd2 = "UPDATE Estimates SET licenseNumber = ?, MonthlyCost = ? WHERE licenseNumber = ?";
		try
		{
			PreparedStatement pstmt = con.prepareStatement(upd);
			pstmt.setInt(1, getMaintInterval());
			pstmt.setInt(2, getLastMileage());
			pstmt.setInt(3, getNextMileage());
			pstmt.setInt(4, getMonthlyMiles());
			pstmt.setString(5, getLicense());
			pstmt.executeUpdate();

			pstmt = con.prepareStatement(upd2);
			pstmt.setString(1, getLicense());
			pstmt.setDouble(2, (calculateMonthlyMaintenance() * laborCost));
			pstmt.setString(3, getLicense());
			pstmt.executeUpdate();
			pstmt.close();
		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, "Error updating maintenance record");
			e.printStackTrace();
		}
	}
	
	//calculate the monthly maintenance cost by dividing monthly mileage by maintenance interval
	private double calculateMonthlyMaintenance()
	{
		return ((double) getMonthlyMiles()) / ((double) getMaintInterval());
	}
	
	//search for a license and assign attributes
	public boolean searchLicense(String license) //returns false if no data found
	{
		try{
			String sel = "SELECT * FROM Maintenance WHERE licenseNumber = ?";
			
			PreparedStatement pstmt = con.prepareStatement(sel);
			pstmt.setString(1, license);
			System.out.println(license);
			ResultSet rs = pstmt.executeQuery();
			
			if (!rs.isBeforeFirst() ) { 
				System.out.println("No rows");
				pstmt.close();
			    return false;
			} 
			
			while(rs.next())
			{
				System.out.println(rs.getString(1));
				setMaintInterval(rs.getInt(2));
				setLastMileage(rs.getInt(3));
				setMonthlyMiles(rs.getInt(5));
			}
			
			pstmt.close();
			return true;
		}catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, "Error finding license");
			e.printStackTrace();
			return false;
		}
	}
	
	//delete entry via license. returns true if successful, false if not
	public boolean delete(String license)
	{
		int action1 = 0, action2 = 0;
		try {
			String del = "DELETE from Maintenance WHERE licenseNumber = ?";
			PreparedStatement pstmt = con.prepareStatement(del);
			pstmt.setString(1, license);
			action1 = pstmt.executeUpdate();
			
			del =  "DELETE from Estimates WHERE licenseNumber = ?";
			pstmt = con.prepareStatement(del);
			pstmt.setString(1, license);
			action2 = pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(action1 != 0 && action2 != 0)
		{
			return true;
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Error deleting maintenance or estimate record");
			return false;
		}
	}
	
	//release resources
	public void release()
	{
		try {
			con.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Resource release error");
			e.printStackTrace();
		}
	}
	
	public String getLicense()
	{
		return licenseNumber;
	}
	public int getMaintInterval()
	{
		return maintenanceInterval;
	}
	public int getLastMileage()
	{
		return lastMileage;
	}
	public int getNextMileage()
	{
		return (lastMileage + maintenanceInterval);
	}
	public int getMonthlyMiles()
	{
		return monthlyMiles;
	}
	
	public void setLicense(String license)
	{
		licenseNumber = license;
	}
	public void setLastMileage(int miles)
	{
		lastMileage = miles;
	}
	public void setMonthlyMiles(int miles)
	{
		monthlyMiles = miles;
	}
	public void setMaintInterval(int miles)
	{
		maintenanceInterval = miles;
	}
	
}
