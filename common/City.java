package common;

import java.util.HashSet;

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
}