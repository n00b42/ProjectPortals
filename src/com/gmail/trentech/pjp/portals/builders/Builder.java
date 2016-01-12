package com.gmail.trentech.pjp.portals.builders;

public abstract class Builder {

	public final String destination;
	
	public Builder(String destination){
		this.destination = destination;
	}

	public Builder(){
		destination = null;
	}
}
