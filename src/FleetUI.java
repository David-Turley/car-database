import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class FleetUI extends JFrame{
	static private JTextField licenseField = new JTextField(8);
	static private JTextField makeField = new JTextField(16);
	static private JTextField modelField = new JTextField(16);
	static private JTextField yearField = new JTextField(5);
	static private JTextField mileageField = new JTextField(8);
	
	private  JTextField lastMileageField = new JTextField(10);
	private  JTextField nextMileageField = new JTextField(10);
	private  JTextField maintIntervalField = new JTextField(6);
	private  JTextField monthlyMilesField = new JTextField(10);
	
	private JButton insertButton = new JButton("Add/Update Entry");
	private JButton deleteButton = new JButton("Delete Entry");
	private JButton findButton = new JButton("Find Entry by Plate #");
	
	//initialize objects
	private  listFrame lf = new listFrame("Show Vehicle Data");;
	private  Vehicle v = new Vehicle();
	private  Maintenance m = new Maintenance();
	
	WindowAdapter winA = makeWindowAdapter();
	
	ArrayList<Window> windows = new ArrayList<Window>(); //an arraylist containing active windows
	
	String license;
	
	//initialize JFrame
	public FleetUI(String title)
	{
		JPanel fleetPanel = new JPanel();
		fleetPanel.setLayout(new BorderLayout(5,5));
		fleetPanel.add(makeFields(), BorderLayout.NORTH);
		fleetPanel.add(makeMaintFields(), BorderLayout.CENTER);
		fleetPanel.add(makeButtons(), BorderLayout.SOUTH);
		
		addWindowListener(winA);
		
		setTitle(title);
		setResizable(false);
		getContentPane().add(fleetPanel, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		pack();
	}
	
	//return a panel with vehicle fields
	private JPanel makeFields()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		panel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Vehicle Information"));
		
		panel.add(new JLabel("License Number:"));
		panel.add(licenseField);
		
		panel.add(new JLabel("Vehicle Make:"));
		panel.add(makeField);
		
		panel.add(new JLabel("Vehicle Model:"));
		panel.add(modelField);
		
		panel.add(new JLabel("Vehicle Year:"));
		panel.add(yearField);
		
		panel.add(new JLabel("Current Mileage:"));
		panel.add(mileageField);
		
		return panel;
	}
	
	//return a panel with maintenance fields
	private JPanel makeMaintFields()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		panel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Maintenance Information"));
		
		panel.add(new JLabel("Mileage at last maintenance: "));
		panel.add(lastMileageField);
		
		panel.add(new JLabel("Maintenance frequency in miles: "));
		panel.add(maintIntervalField);
		
		panel.add(new JLabel("Mileage at next maintenance: "));
		nextMileageField.setEditable(false);
		panel.add(nextMileageField);
		
		panel.add(new JLabel("Average miles per month: "));
		panel.add(monthlyMilesField);
		
		return panel;
	}
	
	//return a panel with add, find, delete buttons
	private JPanel makeButtons()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));
		
		panel.add(insertButton);
		insertButton.addActionListener(new ButtonHandler());
		
		panel.add(findButton);
		findButton.addActionListener(new ButtonHandler());
		
		panel.add(deleteButton);
		deleteButton.addActionListener(new ButtonHandler());
		return panel;
	}
	
	//clear fields
	public void setFields()
	{
		System.out.println("Resetting fields");
		lf.setLicense("N/A");
		license = "N/A";
		
		v = new Vehicle();
		m = new Maintenance();
		
		licenseField.setText("");
		makeField.setText("");
		modelField.setText("");
		yearField.setText("");
		mileageField.setText("");
		
		lastMileageField.setText("");
		nextMileageField.setText("");
		maintIntervalField.setText("");
		monthlyMilesField.setText("");
	}
	
	//search for license information and populate vehicle and maintenance fields
	public void setFields(String license)
	{
		v.searchLicense(license); 
		m.searchLicense(license);
		
		licenseField.setText(license);
		makeField.setText(v.getMake());
		modelField.setText(v.getModel());
		yearField.setText(Integer.toString(v.getYear()));
		mileageField.setText(Integer.toString(v.getMileage()));
		
		lastMileageField.setText(Integer.toString(m.getLastMileage()));
		nextMileageField.setText(Integer.toString(m.getNextMileage()));
		maintIntervalField.setText(Integer.toString(m.getMaintInterval()));
		monthlyMilesField.setText(Integer.toString(m.getMonthlyMiles()));
	}
	
	//launch license selector window; also known as listFrame
	public void launchSelector()
	{
		lf = new listFrame("Show Vehicle Data");
		lf.addWindowListener(winA);
		lf.setLocationRelativeTo(this);
		lf.licenseQuery(false);
		lf.setVisible(true);
	}
	
	//window adapter allows for tracking of window events
	protected WindowAdapter makeWindowAdapter()
	{
		return new WindowAdapter()
		{
			@Override
			public void windowOpened(WindowEvent e) //add recently opened window to arraylist
			{
				windows.add(e.getWindow());
			}
			@Override
            public void windowClosing(WindowEvent e) //handle window closing event and remove it from arraylist
            {
				if(e.getWindow().equals(windows.get(0))) //main window
				{
					System.out.println("Fleet window closing.."); 
					
	                v.release();
	                m.release();
	                if(windows.size() > 1)
	                {
	                	lf.release();
	                }
	                
	                windows.remove(e.getWindow());
	                e.getWindow().dispose();
	                
	                System.out.println("Closing Application..");
	                System.exit(0); 
				}
				else //license selector window
				{
					windows.remove(e.getWindow());
					e.getWindow().dispose();
					System.out.println("listFrame window closing..");
					license = lf.getLicense();
					
					if (license != null)
					{
						setFields(license);
					}
					else
					{
						setFields();
					}
				}
            }
		};
	}
	
	//handle button presses
	private class ButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			switch(e.getActionCommand())
			{
				case "Add/Update Entry":
					v.setLicense(licenseField.getText());
					license = v.getLicense();
					
					if(license.equals("N/A"))
					{
						JOptionPane.showMessageDialog(null, "Invalid License Number");
						break;
					}
					
					if(v.searchLicense(license)) //if the license exists do an update instead
					{
						try
						{
							v.setMake(makeField.getText());
							v.setModel(modelField.getText());
							v.setYear(Integer.parseInt(yearField.getText()));
							v.setMileage(Integer.parseInt(mileageField.getText()));
						
							m.setLicense(license);
							m.setLastMileage(Integer.parseInt(lastMileageField.getText()));
							m.setMaintInterval(Integer.parseInt(maintIntervalField.getText()));
							m.setMonthlyMiles(Integer.parseInt(monthlyMilesField.getText()));
							
							v.update();
							m.update();
							
							setFields();
							
							JOptionPane.showMessageDialog(null, "Record updated");
						}
						catch(Exception err)
						{
							JOptionPane.showMessageDialog(null, "One or more invalid fields");
							err.printStackTrace();
						}
					}
					else
					{
						try
						{
							v.setMake(makeField.getText());
							v.setModel(modelField.getText());
							v.setYear(Integer.parseInt(yearField.getText()));
							v.setMileage(Integer.parseInt(mileageField.getText()));
						
							m.setLicense(license);
							m.setLastMileage(Integer.parseInt(lastMileageField.getText()));
							m.setMaintInterval(Integer.parseInt(maintIntervalField.getText()));
							m.setMonthlyMiles(Integer.parseInt(monthlyMilesField.getText()));
							
							v.insert();
							m.insert();
							
							setFields();
							
							JOptionPane.showMessageDialog(null, "Record inserted");
						}
						catch(Exception err)
						{
							JOptionPane.showMessageDialog(null, "One or more invalid fields");
							err.printStackTrace();
						}
					}
					break;
				case "Delete Entry":
					if(v.delete(license) && m.delete(license))
					{
						setFields();
						JOptionPane.showMessageDialog(null, "Record deleted");
					}
					break;
				case "Find Entry by Plate #":
					if (windows.size() < 2)
					{
						launchSelector();
					}
					break;
			}
		}
	}
}
