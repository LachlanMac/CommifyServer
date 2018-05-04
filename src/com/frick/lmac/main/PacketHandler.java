package com.frick.lmac.main;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Vector;

/*******************************************************************
 * CLASS PacketHandler AUTHOR : Lachlan R McCallum This class builds a packet to
 * be sent over the network and parses incoming packet requests
 ********************************************************************/
public class PacketHandler {

	BoardManager manager;
	DatagramPacket outPacket;
	Vector<IOBoard> boardList;

	// DEFAULT CONSTRUCTOR
	public PacketHandler(BoardManager manager) {
		this.manager = manager;
		boardList = manager.getIOBoards();

	}

	// This method parses a request based on the Commify CommPacket protocol.
	// Current commands are GET, SET, ADD, DELETE and START
	public DatagramPacket parseRequest(byte[] inData, int port, InetAddress address) {
		// initialize byte data
		byte[] returnData = new byte[1];
		// split incoming data
		String[] data = new String(inData).trim().split("=");
		// get packetID ( Currently not used )
		int pID = Integer.parseInt(data[0]);
		// get Command Code
		String cmd = data[1].trim();
		char cmdChar = cmd.charAt(0);
		// get Board ID and Type ( ex. 01 = Digital Board with ID 0 )
		int boardID = Character.getNumericValue(cmd.charAt(1));
		int boardType = Character.getNumericValue(cmd.charAt(2));
		// get Board from board ID and Type
		IOBoard b = getBoard(boardID, boardType);
		// the data of the packet
		String packetData = data[2];

		// Parses based on command recieved
		switch (cmdChar) {
		case CommPacket.GET_CODE:
			returnData = getBoardData(b);
			break;
		case CommPacket.SET_CODE:
			returnData = setBoardData(b, packetData.getBytes());
			break;
		case CommPacket.DELETE_CODE:
			returnData = removeBoard(boardID, boardType, packetData);
			break;
		case CommPacket.ADD_CODE:
			returnData = addBoard(boardID, boardType, packetData);
			break;
		case CommPacket.START_CODE:
			returnData = startService();
			break;

		default:
			Feedback.networkError("Unknown Packet Code");
			break;
		}

		return new DatagramPacket(returnData, returnData.length, address, port);

	}

	// adds a board to Commify Server
	public byte[] addBoard(int boardID, int boardType, String data) {
		IOBoard b = null;
		String returnData;
		// Creates board
		if (boardType == 1) {
			b = new DigitalBoard(boardID);
		} else if (boardType == 2) {
			b = new AnalogBoard(boardID);
		}
		// Adds Board to Manager
		boolean wasAdded = manager.addBoard(b);
		// Send Response
		if (wasAdded) {
			returnData = CommPacket.ADD + CommPacket.SUCCESS + boardID + b.getBoardType() + "=added successfully";
		} else {
			Feedback.networkError("Failed to add IOBoard");
			returnData = CommPacket.ADD + CommPacket.FAILURE + boardID + b.getBoardType() + "=Error adding board";
		}

		return returnData.getBytes();
	}

	// removes a board from Commify Server
	public byte[] removeBoard(int boardID, int boardType, String data) {
		String returnData;
		// gets and removes Board
		boolean wasRemoved = manager.removeBoard(getBoard(boardID, boardType));
		// Send Response
		if (wasRemoved) {
			returnData = CommPacket.DELETE + CommPacket.SUCCESS + boardID + boardType + "=removed successfully";
		} else {
			Feedback.networkError("Failed to remove IOBoard");
			returnData = CommPacket.DELETE + CommPacket.FAILURE + boardID + boardType + "=error removing board";
		}

		return returnData.getBytes();
	}

	// Gets the data associated with the board
	public byte[] getBoardData(IOBoard b) {
		String returnData;
		if (b == null) {
			returnData = new String(CommPacket.GET + CommPacket.FAILURE + "=error getting board data");
		} else {
			returnData = CommPacket.GET + CommPacket.SUCCESS + "=" + new String(b.getPacketData());
		}
		return returnData.getBytes();

	}

	// sends data from the board
	public byte[] setBoardData(IOBoard b, byte[] setData) {

		String returnData;

		if (b == null) {
			returnData = CommPacket.SET + CommPacket.FAILURE + "=error setting board data";
		} else {

			returnData = CommPacket.SET + CommPacket.SUCCESS + "=" + new String(b.setPacketData(setData));
		}
		return returnData.getBytes();

	}

	// returns the current Board by ID and Type
	public IOBoard getBoard(int boardID, int boardType) {

		IOBoard b = null;
		for (int i = 0; i < boardList.size(); i++) {

			if (boardList.get(i).getBoardID() == boardID && boardList.get(i).getBoardType() == boardType) {
				b = boardList.get(i);
			}
		}
		return b;
	}

	// start services
	public byte[] startService() {

		Feedback.log("Starting Services");
		manager.startServices();
		return new String(CommPacket.START + "00=start").getBytes();

	}

}
