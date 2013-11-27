package common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.JComboBox;
import javax.swing.JPanel;

public enum City {

	MONTREAL("Montreal", 9001), QUEBEC("Quebec", 9002), TORONTO("Toronto", 9003), TORONTOA("Toaronto", 9007);
	public String name;
	public int portUdp;

	private City(String name, int port) {
		this.name = name;
		this.portUdp = port;
	}

	static public boolean isCity(String name) {
		HashSet<String> cities = new HashSet<String>();
		for (City city : City.values())
			cities.add(city.toString());
		return cities.contains(name);
	}
	
	@Override
	public String toString(){
		return String.format("City Name: %s, Port: %d", this.name, this.portUdp);
	}
	
	static public class CitiesComponent extends JPanel implements ActionListener{
		public City selection;
		JComboBox cities;
		public CitiesComponent(){
			super();
			cities=new JComboBox();
			for(City city: City.values()){
				cities.addItem(city);
			}

			cities.addActionListener(this);
			this.add(cities);
			//this.setVisible(true);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			this.selection=(City) cities.getSelectedItem();
			
		}
		
		
	}
}