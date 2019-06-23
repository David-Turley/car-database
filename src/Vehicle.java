import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Statement;

import javax.swing.JOptionPane;

public class Vehicle implements dbInterface
{
	protected Connection con;
	protected String nxt = "', '";
	
	protected String Make, Model, licenseNumber;
	protected int vehicleYear, Mileage;
	
	//initialize with placeholder data
	public Vehicle()
	{
		this("N/A","N/A","N/A",-1,-1);
	}
	
	//make connection and initialize with specific attributes
	public Vehicle(String license, String make, String model, int vYear, int mileage)
	{
		try {
			Class.forName(JDBC_DRIVER);
			con = DriverManager.getConnection(url);
			
			this.setLicense(license);
			this.setMake(make);
			this.setModel(model);
			this.setYear(vYear);
			this.setMileage(mileage);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error setting connection");
			e.printStackTrace();
		}
	}
	
	//insert maintenance information into database
	public void insert()
	{
		String upd = "INSERT into Vehicles(licenseNumber, Make,"
				+ " Model, vehicleYear, Mileage) VALUES(?,?,?,?,?)";
		try{
			PreparedStatement pstmt = con.prepareStatement(upd);
			pstmt.setString(1, getLicense());
			pstmt.setString(2, getMake());
			pstmt.setString(3, getModel());
			pstmt.setInt(4, getYear());
			pstmt.setInt(5, getMileage());
			pstmt.executeUpdate();
			
		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, "Error inserting Vehicle");
			e.printStackTrace();
		}
		
	}
	
	//update maintenance record
	public void update()
	{
		String upd = "UPDATE Vehicles SET Make = ?, Model = ?, vehicleYear = ?,"
				+ " Mileage = ? WHERE licenseNumber = ?";
		try
		{
			PreparedStatement pstmt = con.prepareStatement(upd);
			pstmt.setString(1, getMake());
			pstmt.setString(2, getModel());
			pstmt.setInt(3, getYear());
			pstmt.setInt(4, getMileage());
			pstmt.setString(5, getLicense());
			
			pstmt.executeUpdate();
		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, "Error updating vehicle");
			e.printStackTrace();
		}
	}
	
	//search for a license and assign attributes
	public boolean searchLicense(String license) //returns false if no data found	
	{
		try{
			String sel = "SELECT * FROM Vehicles WHERE licenseNumber = ?";
			
			PreparedStatement pstmt = con.prepareStatement(sel);
			pstmt.setString(1, license);
			System.out.println(license);
			ResultSet rs = pstmt.executeQuery();
			
			if (!rs.isBeforeFirst() ) {    
			     pstmt.close();
			     return false;
			} 
			
			while(rs.next())
			{
				setMake(rs.getString(2));
				setModel(rs.getString(3));
				setYear(rs.getInt(4));
				setMileage(rs.getInt(5));
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
		int action = 0;
		try {
			String del = "DELETE from Vehicles WHERE licenseNumber = ?";
			PreparedStatement pstmt = con.prepareStatement(del);
			pstmt.setString(1, license);
			action = pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(action != 0)
		{
			return true;
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Error deleting vehicle");
			return false;
		}
	}
	
	//release resources
	public void release()
	{
		try {
			con.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Resource release error");
			e.printStackTrace();
		}
	}
	
	public String getMake()
	{
		return Make;
	}
	public String getModel()
	{
		return Model;
	}
	public String getLicense()
	{
		return licenseNumber;
	}
	public int getYear()
	{
		return vehicleYear;
	}
	public int getMileage()
	{
		return Mileage;
	}
	
	public void setMake(String make)
	{
		Make = make;
	}
	public void setModel(String model)
	{
		Model = model;
	}
	public void setLicense(String license)
	{
		licenseNumber = license;
	}
	public void setYear(int year)
	{
		vehicleYear = year;
	}
	public void setMileage(int mileage)
	{
		Mileage = mileage;
	}
}
