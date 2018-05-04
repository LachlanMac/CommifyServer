package com.frick.lmac.main;

/*******************************************************************
 * CLASS AnalogChannel [inherits from IOChannel]
 * AUTHOR : Lachlan R McCallum
 * This class represents an Analog Channel on an analog board.  This
 * channel stores a value, type and configuration
 ********************************************************************/

public class AnalogChannel extends IOChannel {
	// numerical value of the channel (0 - 4095)
	private int value;
	// configuration type
	private char configurationType;

	// Constructor without a specified configuration type
	public AnalogChannel(int channelID, TYPE type) {
		super(channelID, type);
		setDefaults();
	}

	// Constructor with a specified configuration type
	public AnalogChannel(int channelID, TYPE type, char configurationType) {
		super(channelID, type);
		this.configurationType = configurationType;
	}

	// updates a channels value
	public void updateValue(int value) {
		this.value = value;
	}

	@Override
	public void setDefaults() {
		this.value = 0;

		// set default configuration types
		if (this.type == TYPE.output) {
			this.configurationType = '2';
		} else {
			this.configurationType = '1';
		}
	}

	// [[ SETTERS ]]

	public void setConfiguration(char configuration) {
		this.configurationType = configuration;

	}

	// set a value of a channel
	public void setValue(int value) {
		if (this.type != TYPE.output) {
			this.value = value;
		} else {
			Feedback.analogError("Cannot set an Output");
		}
	}

	// [[ GETTERS ]]
	// return the channel value in hex format
	public String getHexValue() {
		return Util.getAChannel(value);
	}

	// return the value in decimal format
	public int getValue() {
		return value;
	}

	// returns the char representation of a channel configuration
	public char getConfiguration() {
		return configurationType;
	}

	// return string representation of channel configuration
	public String getChannelConfiguration() {
		String channelConfig = " ";

		switch (this.configurationType) {

		case '1':
			channelConfig = "0 - 5 volt";
			break;

		case '2':
			channelConfig = "0 - 10 volt";
			break;

		case '3':
			channelConfig = "4 - 20 ma";
			break;

		case '4':
			channelConfig = "ICTD";
			break;

		case '6':
			channelConfig = "RTD";
			break;

		case 'A':
			channelConfig = "RPM Speed/Frequency";
			break;

		default:
			Feedback.analogError("Unrecognized channel configuration");
		}

		return channelConfig;
	}

}
