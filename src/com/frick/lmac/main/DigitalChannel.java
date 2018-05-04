package com.frick.lmac.main;

/*******************************************************************
 * CLASS DigitalChannel [inherits from IOChannel]
 * AUTHOR : Lachlan R McCallum
 * This class represents a Digital Channel on a digital board.  This
 * channel stores a value and type
 ********************************************************************/
public class DigitalChannel extends IOChannel {
	// represents the on or off stae of the channel
	boolean value;

	// Constructor that sets initial value when not specified
	public DigitalChannel(int channelID, TYPE type) {
		super(channelID, type);
		setDefaults();
	}

	// Constructor that sets the initial value to passed parameter
	public DigitalChannel(int channelID, TYPE type, boolean value) {
		super(channelID, type);
		this.value = value;
	}

	// returns the state of the channel
	public boolean getValue() {
		return value;
	}

	// sets the state of the channel
	public void setValue(boolean value) {
		if (this.type != TYPE.output) {
			this.value = value;
		} else {
			Feedback.digitalError("Cannot set an Output");
		}
	}

	// updates the state of the channel (DEBUG)
	public void updateValue(boolean value) {
		this.value = value;
	}

	// returns the state in a character format for Binary string
	public char getState() {
		if (!this.value) {
			return '0';
		} else {
			return '1';
		}
	}

	@Override
	public void setDefaults() {
		this.value = false;
	}
}
