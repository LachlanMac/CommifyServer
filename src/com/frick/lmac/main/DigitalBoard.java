package com.frick.lmac.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*******************************************************************
 * CLASS DigitalBoard [inherits from IOBoard] AUTHOR : Lachlan R McCallum This
 * class represents a virtual Digital Board which can receive and response to
 * commands from a controller.
 ********************************************************************/
public class DigitalBoard extends IOBoard {
	// Number of channels
	private static final int CHANNEL_COUNT = 24;
	// Board Identification variables
	private static final String BOARD_TYPE = "00";
	private static final String VERSION = "090";
	private static final String VERSION_DATE = "04172018";

	private DigitalChannel[] channels;
	private Command inCommand;
	private String tx;

	// Constructor
	public DigitalBoard(int boardID) {
		super(boardID);
		// set to digital board
		boardType = IOBoard.DIGITAL_BOARD_TYPE;
		channels = new DigitalChannel[CHANNEL_COUNT];
		// create channels as type:output
		for (int i = 0; i < channels.length; i++) {
			channels[i] = new DigitalChannel(i, DigitalChannel.TYPE.output);
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
		case 'z':
			// set IO then Return IO
			response = setIO(inCommand.getCommandData());
			break;
		case 'w':
			response = setWatchdogDelay();
			break;
		case 'A':
			response = powerUpClear();
			break;
		case 'b':
			// IGNORE BAUD RATE CHANGES
			break;
		case 'F':
			response = identifyBoard();
			break;
		default:
			Feedback.digitalError("Unknown Command Type [" + cmd + "]");
		}

		return response;
	}

	// [[ COMMANDS ]]
	// returns the command that represents the board identify information
	public String identifyBoard() {
		String boardIdentifier = BOARD_TYPE + VERSION + VERSION_DATE;

		return "A" + boardIdentifier + Util.calculateCheckSum(boardIdentifier.toCharArray()) + "\r";
	}

	// acknowledges a watchdog delay set. Not implemented for virtual boards
	public String setWatchdogDelay() {
		return "A\r";
	}

	// acknowledges a power up clear.
	public String powerUpClear() {
		return "A\r";
	}

	public String setIO(String data) {

		char[] binaryData = Util.getBinaryString(data).toCharArray();

		for (int i = 0; i < binaryData.length; i++) {

			if (channels[i].getType() == DigitalChannel.TYPE.output) {

				if (channels[i].value == false && binaryData[i] == '1') {

					channels[i].updateValue(true);

				} else if (channels[i].value == true && binaryData[i] == '0') {

					channels[i].updateValue(false);
				}

			}
		}

		return ">A" + getIOState() + getDigitalCheckSum(getIOState()) + "\r";
	}

	// [[ HELPER METHODS ]]
	public String getDigitalCheckSum(String hex) {
		return Util.calculateCheckSum(hex.toCharArray());
	}

	// returns the current IO State in hex format
	public String getIOState() {
		char state = '0';
		char[] stateString = new char[CHANNEL_COUNT];

		int index = 0;
		for (int i = channels.length - 1; i >= 0; i--) {

			stateString[index] = channels[i].getState();
			;
			index++;

		}

		return Util.convertToHex(new String(stateString));

	}

	// sets all the digital channel values
	public void setAllValues(int[] channelValue, String[] channelType) {

		for (int i = 0; i < channelValue.length; i++) {
			boolean status;
			DigitalChannel.TYPE type;
			if (channelValue[i] == 0) {
				status = false;
			} else {
				status = true;
			}

			if (channelType[i].equals("OUT")) {
				type = DigitalChannel.TYPE.output;
			} else {
				type = DigitalChannel.TYPE.input;
			}

			channels[i].updateValue(status);
			channels[i].setType(type);

		}

	}

	// [[ NETWORK METHODS ]]
	// Converts the values of the channels into the Commify Protocol
	@Override
	public byte[] getPacketData() {
		byte[] packetData = null;
		char type;
		int val;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < channels.length; i++) {

			if (channels[i].getValue() == false) {
				val = 0;
			} else {
				val = 1;
			}
			if (channels[i].getType() == DigitalChannel.TYPE.input) {
				type = 'I';
			} else {
				type = 'O';
			}

			if (i == 0) {
				sb.append("[" + type + ":" + val);
			} else if (i == (channels.length - 1)) {
				sb.append("-" + type + ":" + val + "]");
			} else {
				sb.append("-" + type + ":" + val);
			}
		}
		return new String(sb).trim().getBytes();
	}

	// Sets all the channels using the Commify Protocol
	@Override
	public byte[] setPacketData(byte[] setData) {

		String[] channelConfig = new String(setData).trim().split("-");

		for (int i = 0; i < CHANNEL_COUNT; i++) {

			String[] channelData = channelConfig[i].trim().split(":");

			String input = channelData[0];
			String value = channelData[1];

			if (input.equals("O")) {

				// if the channel was an input
				if (channels[i].getType() == DigitalChannel.TYPE.input) {
					// make this channel an output
					channels[i].updateValue(false);
					channels[i].setType(DigitalChannel.TYPE.output);
				} else {
					// do nothing, don't set an output
				}
			} else if (input.equals("I")) {

				// if the channel was an output
				if (channels[i].getType() == DigitalChannel.TYPE.output) {
					// change channel type
					channels[i].setType(DigitalChannel.TYPE.input);
				}
				// set value

				if (value.equals("1") && channels[i].getValue() == false) {
					channels[i].updateValue(true);
				} else if (value.equals("0") && channels[i].getValue() == true) {
					channels[i].updateValue(false);
				} else {
					// unchanged
				}

			}

		}
		return "CONFIRED".getBytes();
	}

}
