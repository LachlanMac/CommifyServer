package com.frick.lmac.main;

/*******************************************************************
 * CLASS Command AUTHOR : Lachlan R McCallum This class represents the read and
 * write data of a command sent to the virtual IO boards. Builds data string
 * through a StringBuilder call and parses upon completion
 ********************************************************************/
public class Command {
	// global identifiers
	private static final char DIGITAL_COMMAND = '1';
	private static final char ANALOG_COMMAND = '2';
	private static final int DIGITAL_DATA_LENGTH = 6;

	private boolean hasParsed = false;
	// class variables
	private String cmdData, csData;
	private char cmdType, type, boardID;

	private StringBuffer sb;

	// CONSTRUVTOR
	public Command() {
		// initialize variables
		sb = new StringBuffer();

		type = 999;
		boardID = 999;
		cmdData = "";
		csData = "";
		cmdType = 'x';

	}

	// appends data to the string buffer as it comes in from the IO Reader
	public void addData(String data) {
		String trimmed = data.trim();
		sb.append(trimmed);
	}

	// Parses the received command
	public void parseCommand() {

		String cmd = new String(sb);
		String cmdString = cmd.trim();
		if (cmdString.length() <= 1) {
			// initial packet, throw away
			return;
		}
		// if digital command
		if (cmdString.charAt(1) == DIGITAL_COMMAND) {

			this.boardID = cmdString.charAt(2);
			this.type = cmdString.charAt(1);
			this.cmdType = cmdString.charAt(3);

			char[] cmdArray = cmdString.toCharArray();

			// Replace the Command character to split the string on it
			cmdArray[3] = '-';

			String[] data = Util.formatString(new String(cmdArray)).split("-");

			parseDigitalCommand(this.type, this.boardID, this.cmdType, data[1]);

		}
		// if analog command
		if (cmdString.charAt(1) == ANALOG_COMMAND) {

			this.boardID = cmdString.charAt(2);
			this.type = cmdString.charAt(1);
			// convert String to char Array for manipulation
			char[] cmdArray = cmdString.toCharArray();
			// replace the command character to splitting delimiter
			cmdArray[2] = '-';

			String[] data = Util.formatString(new String(cmdArray)).split("-");

			parseAnalogCommand(type, boardID, data[1]);

		}
	}

	// parse digital commands
	public void parseDigitalCommand(char boardType, char boardID, char cmdType, String command) {

		char cType = cmdType;
		String tmpCommand = command;

		switch (cType) {

		case 'A':
			// power up clear ex. >10AA2[CR]
			this.cmdData = "";
			this.csData = tmpCommand;

			break;
		case 'b':
			// baud rate change (6 chars, decimal) ex. >10b019200EF[CR]
			this.cmdData = tmpCommand.substring(0, DIGITAL_DATA_LENGTH);
			this.csData = tmpCommand.substring(tmpCommand.length() - 2, tmpCommand.length());
			break;
		case 'w':
			// watch dog delay Decimal seconds * 100 [4 ASCII characters ex >10w03E8B8[CR]]
			this.cmdData = tmpCommand.substring(0, 4);
			this.csData = tmpCommand.substring(tmpCommand.length() - 2, tmpCommand.length());

			break;
		case 'F':
			// Identify Board - ex. >10FA7[CR]
			this.cmdData = "";
			this.csData = tmpCommand;

			break;
		case 'z':
			// set-return IO config
			this.cmdData = tmpCommand.substring(0, DIGITAL_DATA_LENGTH);
			this.csData = tmpCommand.substring(tmpCommand.length() - 2, tmpCommand.length());

			break;
		default:

			break;

		}
		// Signals the command as being parsed
		hasParsed = true;

	}

	// parse analog commands
	public void parseAnalogCommand(char boardType, char boardID, String unformattedCommand) {
		String testCommandString = new String(sb);
		// Special Logic to account for commands with multiple characters
		String command = Util.formatString(unformattedCommand);

		char[] commandArray = command.toCharArray();
		char commandType = commandArray[0];
		this.cmdType = commandType;
		String commandString = command.substring(1, command.length()).trim();

		switch (commandType) {

		case 'A':
			// power up clear
			this.csData = command;
			this.cmdData = "";

			break;

		case 'B':
			// baud rate change ex. >20b019200[Checksum][CR]
			this.cmdData = commandString.substring(0, 6);
			this.csData = command.substring(command.length() - 2, command.length());

			break;
		case 'L':
			// read Analog IO
			this.cmdData = commandString.substring(0, 6);
			this.csData = command.substring(command.length() - 2, command.length());

			break;
		case 'F':
			// get version
			this.cmdData = "";
			this.csData = commandString;

			break;
		case 'r':
			// read board configuration
			this.cmdData = "";
			this.csData = commandString;

			break;

		case 's':
			// set board configuration ex >20s44444111111111191111111133333333[CS][CR]
			// outputs only type 2 and 3
			this.cmdData = commandString.substring(0, 24);
			this.csData = command.substring(command.length() - 2, command.length());

			break;
		case 'v':
			// return all vibration data
			this.cmdData = "";
			this.csData = commandString;

			break;
		case 'z':
			// special output analog command
			if (commandString.length() == 26) {
				this.cmdData = commandString.substring(0, 24).trim();
				this.csData = command.substring(command.length() - 2, command.length()).trim();
			} else if (commandString.length() == 14) {
				this.cmdData = commandString.substring(0, 12).trim();
				this.csData = command.substring(command.length() - 2, command.length()).trim();
			} else {
				cmdData = "BADPACKET";
				csData = "00";

				break;
			}
			

			break;
		default:

			break;
		}
		// signals the command as being parsed
		hasParsed = true;

	}

	// [[ GETTERS ]]
	public int getType() {
		return Character.getNumericValue(this.type);
	}

	public int getBoardID() {
		return Character.getNumericValue(this.boardID);
	}

	public char getCommandType() {
		return cmdType;
	}

	public String getCommandData() {
		return cmdData;
	}

	public boolean hasBeenParsed() {
		return hasParsed;
	}

}
