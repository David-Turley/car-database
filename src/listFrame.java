import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.*;

public class listFrame extends JFrame implements dbInterface{
	protected Connection con;

	private JButton select, find;
	private JList<String> licList; //license list
	
	private String license;
	
	//initialize JFrame and make connection
	public listFrame(String name)
	{
		setTitle(name);
		
		try
		{
			Class.forName(JDBC_DRIVER);
			con = DriverManager.getConnection(url);
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, "Error making connection");
			e.printStackTrace();
		}
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		getContentPane().add(makeList(), BorderLayout.NORTH);
		getContentPane().add(makeButtons(), BorderLayout.SOUTH);
		pack();
		
		setVisible(false);
	}
	
	//create license list and add it to a scrollpane, then add to panel and return panel
	public JPanel makeList()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		licList = new JList<String>(licenseQuery(false));
		licList.setLayoutOrientation(JList.VERTICAL);
		
		JScrollPane licScroller = new JScrollPane(licList);
		licScroller.setPreferredSize(new Dimension(200,300));
		
		panel.add(licScroller);
		
		return panel;
	}
	//return a panel with select and filter buttons
	public JPanel makeButtons()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));
		
		select = new JButton("Select Vehicle");
		select.addActionListener(new ButtonHandler());
		panel.add(select);
		find = new JButton("Show Out Of Date Only");
		find.addActionListener(new ButtonHandler());
		panel.add(find);
		
		return panel;
	}
	
	//release resources
	protected void release()
	{
		try
		{
			con.close();
		}catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, "Resource release error");
			e.printStackTrace();
		}
	}
	
	//search for a license. returns a DefaultListModel containing ordered results
	protected DefaultListModel<String> licenseQuery(boolean outOfDate) //true to filter for out of date values only
	{
		DefaultListModel<String> model = new DefaultListModel<String>();
		try {
				PreparedStatement pstmt;
				if(outOfDate)
				{
					String sel = "SELECT Vehicles.licenseNumber FROM Vehicles"
							+ " INNER JOIN Maintenance ON Maintenance.licenseNumber = Vehicles.licenseNumber"
							+ " INNER JOIN Estimates ON Estimates.licenseNumber = Vehicles.licenseNumber"
							+ " WHERE Vehicles.Mileage >= Maintenance.NextMileage"
							+ " ORDER BY Estimates.monthlyCost DESC";
					pstmt = con.prepareStatement(sel);
					
					ResultSet rs = pstmt.executeQuery();
					
					while(rs.next())
					{
						model.addElement(rs.getString(1));
					}
				}
				else
				{
					String sel = "SELECT Vehicles.licenseNumber FROM Vehicles "
							+ "INNER JOIN Estimates ON Estimates.licenseNumber = Vehicles.licenseNumber"
							+ " ORDER BY Estimates.monthlyCost DESC";
					
					pstmt = con.prepareStatement(sel);
					
					ResultSet rs = pstmt.executeQuery();
					
					while(rs.next())
					{
						model.addElement(rs.getString(1));
					}
				}
				pstmt.close();
			}catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Error finding vehicles");
				e.printStackTrace();
			}
		return model;
	}
	
	//handle button press
	private class ButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			switch(e.getActionCommand())
			{
				case "Select Vehicle":
					license = licList.getSelectedValue();
					listFrame.this.dispatchEvent(new WindowEvent(listFrame.this, WindowEvent.WINDOW_CLOSING));
					break;
				case "Show Out Of Date Only":
					licenseQuery(true);
					licList.setModel(licenseQuery(true));
					find.setEnabled(false);
					break;
			}
		}
	}
	
	public String getLicense()
	{
		return license;
	}
	public void setLicense(String License)
	{
		license = License;
	}
	
}
