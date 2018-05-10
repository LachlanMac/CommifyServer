package com.frick.lmac.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/*******************************************************************
 * CLASS AnalogBoard [inherits from IOBoard] AUTHOR : Lachlan R McCallum This
 * class represents a virtual Analog Board which can receive and response to
 * commands from a controller.
 ********************************************************************/
public class AnalogBoard extends IOBoard {
	// Number of input and output channels
	private static final int INPUT_CHANNEL_COUNT = 24;
	private static final int OUTPUT_CHANNEL_COUNT = 8;
	// Board Identification variables
	private static final String BOARD_TYPE = "01";
	private static final String VERSION = "300";
	private static final String VERSION_DATE = "04172018";

	private AnalogChannel[] channels;
	private Command inCommand;
	private String tx;
	

	// Constructor
	public AnalogBoard(int boardID) {
		super(boardID);
		// current string value to transmit
		tx = "";
		// set to analog board
		boardType = IOBoard.ANALOG_BOARD_TYPE;
		// initialize analog channels
		channels = new AnalogChannel[INPUT_CHANNEL_COUNT + OUTPUT_CHANNEL_COUNT];
		for (int i = 0; i < INPUT_CHANNEL_COUNT; i++) {
			channels[i] = new AnalogChannel(i, AnalogChannel.TYPE.input);
		}
		for (int i = INPUT_CHANNEL_COUNT; i < channels.length; i++) {
			channels[i] = new AnalogChannel(i, AnalogChannel.TYPE.output);
		}

	}

	@Override
	// Method that receives the command request
	public void rx(Command in) {
		hasReceived = true;
		inCommand = in;
		char cmdType = inCommand.getCommandType();
		// generate response
		tx = getResponse(cmdType);
	}

	@Override
	// Method that returns the response
	public String tx() {
		String temp = this.tx;
		tx = "";
		return temp;
	}

	@Override
	public String getResponse(char cmd) {
		String response = "";

		switch (cmd) {
		case 'L':
			response = getInputData();
			break;
		case 'F':
			// return board version
			response = getBoardInformation();
			break;
		case 'A':
			// acknowledge reset
			response = "A\r";
			break;
		case 'B':
			// acknowledge watchdog reset
			response = "A\r";
			break;
		case 's':
			// set board configuration (Channels IN: 1-24 OUT:1-8)
			response = setBoardConfiguration();
			break;
		case 'r':
			// read board configuration (Channels IN: 1-24 OUT:1-8)
			response = getBoardConfiguration();
			break;
		case 'v':
			// get vibration garbo
			response = getVibrationData();
			break;
		case 'z':
			if (inCommand.getCommandData().equals("BADPACKET")) {
				Feedback.analogError("Bad Packet received");
				response = "A\r";
				break;
			}

			response = updateAnalogOutputs();
			break;
		default:
			Feedback.analogError("Unknown Command Type [" + cmd + "]");
		}

		return response;
	}

	// [[ COMMANDS ]]
	//
	// returns the command representing the analog input channel data
	public String getInputData() {

		return "A" + getAnalogInputs() + getAnalogCheckSum(getAnalogInputs()) + "\r";
	}

	// returns the command representing the board identity
	public String getBoardInformation() {
		String boardIdentifier = BOARD_TYPE + VERSION + VERSION_DATE;
		return "A" + boardIdentifier + Util.calculateCheckSum(boardIdentifier.toCharArray()) + "\r";
	}

	// returns the command representing the vibration channels
	public String getVibrationData() {
		// TO:DO
		return "AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF??\r";
	}

	// returns an acknowledgement command after setting a configuration
	public String setBoardConfiguration() {

		return "A\r";
	}

	// returns the command representing the configuration of the board
	public String getBoardConfiguration() {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < channels.length; i++) {
			sb.append(channels[i].getConfiguration());
		}

		String boardConfiguration = new String(sb);

		return "A" + boardConfiguration + getAnalogCheckSum(boardConfiguration) + "\r";
	}

	// updates analog outputs
	public String updateAnalogOutputs() {

		String hex = inCommand.getCommandData();
		int[] values = Util.getAnalogOutput(hex);

		for (int i = 0; i < values.length; i++) {
			channels[i + INPUT_CHANNEL_COUNT].updateValue(values[i]);
		}
		return "A\r";
	}
	// [[ HELPER FUNCTIONS ]]

	// returns the checksum from the hex parameter
	public String getAnalogCheckSum(String hexString) {
		return Util.calculateCheckSum(hexString.toCharArray());
	}

	// returns the Analog INPUTS
	public String getAnalogInputs() {
		StringBuffer sb = new StringBuffer();
		for (int i = INPUT_CHANNEL_COUNT - 1; i >= 0; i--) {
			sb.append(channels[i].getHexValue());
		}
		return new String(sb).trim();
	}

	// sets all the channels
	public void setAllValues(int[] channels) {

		for (int i = 0; i < channels.length; i++) {
			this.channels[i].updateValue(channels[i]);
		}

	}

	// [[ NETWORK METHODS ]]
	// Converts the values of the channels into the Commify Protocol
	@Override
	public byte[] getPacketData() {
		byte[] packetData = null;

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < channels.length; i++) {
			char type;
			AnalogChannel tempChannel = channels[i];

			if (tempChannel.getType() == DigitalChannel.TYPE.input) {
				type = 'I';
			} else {
				type = 'O';
			}

			if (i == 0) {
				sb.append("[" + type + ":" + tempChannel.getValue() + ":" + tempChannel.getConfiguration());
			} else if (i == (channels.length - 1)) {
				sb.append("-" + type + ":" + tempChannel.getValue() + ":" + tempChannel.getConfiguration() + "]");
			} else {
				sb.append("-" + type + ":" + tempChannel.getValue() + ":" + tempChannel.getConfiguration());
			}

		}

		packetData = new String(sb).trim().getBytes();

		return packetData;
	}

	// Sets all the channels using the Commify Protocol
	@Override
	public byte[] setPacketData(byte[] setData) {

		String[] channelConfig = new String(setData).trim().split("-");
		for (int i = 0; i < INPUT_CHANNEL_COUNT; i++) {

			String[] channelData = channelConfig[i].trim().split(":");
			String input = channelData[0];
			String value = channelData[1];
			String config = channelData[2];

			channels[i].updateValue(Integer.parseInt(channelData[1]));

		}

		// updateSliders();
		return "CONFIRMED".getBytes();
	}

}
