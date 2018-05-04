package com.frick.lmac.main;

/*******************************************************************
 * CLASS IOChannel [SUPERCLASS]
 * AUTHOR : Lachlan R McCallum
 * This class represents an IO Channel on an IO Board.  Can 
 * be implemented as analog or digital
 ********************************************************************/
public abstract class IOChannel {
	// Data structure to represent the type of a channel
	protected enum TYPE {
		input, output
	};

	// Each channel has a type (input or output) and channel ID
	protected TYPE type;
	protected int channelID;

	// Default super constructor
	public IOChannel(int channelID, TYPE type) {
		this.channelID = channelID;
		this.type = type;
	}

	// set initial channel state
	public abstract void setDefaults();

	// [[ GETTERS ]]
	public int getChannelID() {
		return channelID;
	}

	public TYPE getType() {
		return type;
	}

	// [[ SETTERS ]]
	public void setType(TYPE type) {
		this.type = type;
	}

}
