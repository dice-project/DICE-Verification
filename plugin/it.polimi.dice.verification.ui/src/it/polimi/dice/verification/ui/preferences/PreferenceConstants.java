package it.polimi.dice.verification.ui.preferences;


public enum PreferenceConstants {
	HOST ("host", "D-VerT Server Host Address:"),
	PORT ("server-port", "D-VerT Server Port Number:");


	private String name;
	private String description;
	
	private PreferenceConstants(String name, String description){
		this.name = name;
		this.description = description;
	}


	public String getName(){
		return this.name;
	}
	
	public String getDescription(){
		return this.description;
	}
	
}

