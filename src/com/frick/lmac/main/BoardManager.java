package com.frick.lmac.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

/*******************************************************************
 * CLASS CommifyManager AUTHOR : Lachlan R McCallum This class manages the
 * creation of the IO Boards and their initial set up. Boards can be added with
 * default data or by a custom configuration text file.
 ********************************************************************/
public class BoardManager {
	// list of all boards
	Vector<IOBoard> boards;

	// Constructor
	public BoardManager() {

		boards = new Vector<IOBoard>();

	}

	// save the state of the channels into a text file
	public void saveState() {

		File configFile = new File("/home/frick/config.txt");
		try {
			configFile.createNewFile();

			FileWriter writer = new FileWriter(configFile);

			writer.write("TEST FILE");

			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// load states
	public void loadState() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File("/home/frick/Commify/res/config.txt")));

			String line;

			ArrayList<String> list = new ArrayList<String>();

			while ((line = br.readLine()) != null) {

				String[] type = line.split("=");

				String boardType = type[0].trim();
				String data[] = type[1].trim().split(",");

				String idData[] = data[0].split(":");
				String versionData[] = data[1].split(":");
				String c1Data[] = data[2].split(":");
				String c2Data[] = data[3].split(":");
				String c3Data[] = data[4].split(":");
				String c4Data[] = data[5].split(":");
				String c5Data[] = data[6].split(":");
				String c6Data[] = data[7].split(":");
				String c7Data[] = data[8].split(":");
				String c8Data[] = data[9].split(":");
				String c9Data[] = data[10].split(":");
				String c10Data[] = data[11].split(":");
				String c11Data[] = data[12].split(":");
				String c12Data[] = data[13].split(":");
				String c13Data[] = data[14].split(":");
				String c14Data[] = data[15].split(":");
				String c15Data[] = data[16].split(":");
				String c16Data[] = data[17].split(":");
				String c17Data[] = data[18].split(":");
				String c18Data[] = data[19].split(":");
				String c19Data[] = data[20].split(":");
				String c20Data[] = data[21].split(":");
				String c21Data[] = data[22].split(":");
				String c22Data[] = data[23].split(":");
				String c23Data[] = data[24].split(":");
				String c24Data[] = data[25].split(":");

				if (boardType.equals("ANALOG")) {

					int boardID = Integer.parseInt(idData[1]);

					int channel[] = new int[24];

					channel[0] = Integer.parseInt(c1Data[1]);
					channel[1] = Integer.parseInt(c2Data[1]);
					channel[2] = Integer.parseInt(c3Data[1]);
					channel[3] = Integer.parseInt(c4Data[1]);
					channel[4] = Integer.parseInt(c5Data[1]);
					channel[5] = Integer.parseInt(c6Data[1]);
					channel[6] = Integer.parseInt(c7Data[1]);
					channel[7] = Integer.parseInt(c8Data[1]);
					channel[8] = Integer.parseInt(c9Data[1]);
					channel[9] = Integer.parseInt(c10Data[1]);
					channel[10] = Integer.parseInt(c11Data[1]);
					channel[11] = Integer.parseInt(c12Data[1]);
					channel[12] = Integer.parseInt(c13Data[1]);
					channel[13] = Integer.parseInt(c14Data[1]);
					channel[14] = Integer.parseInt(c15Data[1]);
					channel[15] = Integer.parseInt(c16Data[1]);
					channel[16] = Integer.parseInt(c17Data[1]);
					channel[17] = Integer.parseInt(c18Data[1]);
					channel[18] = Integer.parseInt(c19Data[1]);
					channel[19] = Integer.parseInt(c20Data[1]);
					channel[20] = Integer.parseInt(c21Data[1]);
					channel[21] = Integer.parseInt(c22Data[1]);
					channel[22] = Integer.parseInt(c23Data[1]);
					channel[23] = Integer.parseInt(c24Data[1]);

					AnalogBoard b = new AnalogBoard(boardID);

					b.setAllValues(channel);
					Feedback.analogLog("Successfully registered board " + boardID + " to Commify");

					boards.add(b);

				} else if (boardType.equals("DIGITAL")) {

					int boardID = Integer.parseInt(idData[1]);

					String channelType[] = new String[24];
					int channelValue[] = new int[24];

					channelValue[0] = Integer.parseInt(c1Data[1]);
					channelValue[1] = Integer.parseInt(c2Data[1]);
					channelValue[2] = Integer.parseInt(c3Data[1]);
					channelValue[3] = Integer.parseInt(c4Data[1]);
					channelValue[4] = Integer.parseInt(c5Data[1]);
					channelValue[5] = Integer.parseInt(c6Data[1]);
					channelValue[6] = Integer.parseInt(c7Data[1]);
					channelValue[7] = Integer.parseInt(c8Data[1]);
					channelValue[8] = Integer.parseInt(c9Data[1]);
					channelValue[9] = Integer.parseInt(c10Data[1]);
					channelValue[10] = Integer.parseInt(c11Data[1]);
					channelValue[11] = Integer.parseInt(c12Data[1]);
					channelValue[12] = Integer.parseInt(c13Data[1]);
					channelValue[13] = Integer.parseInt(c14Data[1]);
					channelValue[14] = Integer.parseInt(c15Data[1]);
					channelValue[15] = Integer.parseInt(c16Data[1]);
					channelValue[16] = Integer.parseInt(c17Data[1]);
					channelValue[17] = Integer.parseInt(c18Data[1]);
					channelValue[18] = Integer.parseInt(c19Data[1]);
					channelValue[19] = Integer.parseInt(c20Data[1]);
					channelValue[20] = Integer.parseInt(c21Data[1]);
					channelValue[21] = Integer.parseInt(c22Data[1]);
					channelValue[22] = Integer.parseInt(c23Data[1]);
					channelValue[23] = Integer.parseInt(c24Data[1]);

					channelType[0] = c1Data[2];
					channelType[1] = c2Data[2];
					channelType[2] = c3Data[2];
					channelType[3] = c4Data[2];
					channelType[4] = c5Data[2];
					channelType[5] = c6Data[2];
					channelType[6] = c7Data[2];
					channelType[7] = c8Data[2];
					channelType[8] = c9Data[2];
					channelType[9] = c10Data[2];
					channelType[10] = c11Data[2];
					channelType[11] = c12Data[2];
					channelType[12] = c13Data[2];
					channelType[13] = c14Data[2];
					channelType[14] = c15Data[2];
					channelType[15] = c16Data[2];
					channelType[16] = c17Data[2];
					channelType[17] = c18Data[2];
					channelType[18] = c19Data[2];
					channelType[19] = c20Data[2];
					channelType[20] = c21Data[2];
					channelType[21] = c22Data[2];
					channelType[22] = c23Data[2];
					channelType[23] = c24Data[2];

					DigitalBoard b = new DigitalBoard(boardID);
					b.setAllValues(channelValue, channelType);
					Feedback.digitalLog("Successfully registered board " + boardID + " to Commify");
					boards.add(b);

				} else {
					Feedback.error("Failed to parse Board Configuration");
					break;
				}

			}

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean addBoard(IOBoard board) {
		Feedback.log("Adding Board");
		if (board == null) {
			return false;
		}

		if (boards.contains(board)) {

			return false;
		} else {

			boards.add(board);
			return true;
		}
	}

	public boolean removeBoard(IOBoard board) {

		Feedback.log("Removing Board");
		if (board == null) {
			Feedback.log("Could not remove Board");
			return false;
		}

		if (boards.contains(board)) {
			Feedback.log("Board Removed");
			boards.remove(board);
			return true;
		} else {
			Feedback.log("Could not remove Board");
			return false;
		}

	}

	public void startServices() {
		stopServices();
	}
	
	public void stopServices() {
		boards.clear();
	}

	// [[ GETTERS ]]
	public Vector<IOBoard> getIOBoards() {
		return boards;
	}

}