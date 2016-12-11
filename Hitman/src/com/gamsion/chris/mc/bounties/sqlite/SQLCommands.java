package com.gamsion.chris.mc.bounties.sqlite;

public enum SQLCommands {
	createTable(String.format("CREATE %s ")),
	;
	
	
	String command;
	
	
	SQLCommands(String command){
		this.command = command;
	}
}
