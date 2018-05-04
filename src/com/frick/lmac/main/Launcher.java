package com.frick.lmac.main;

import javax.comm.CommPortIdentifier;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/*******************************************************************
 * CLASS Launcher AUTHOR : Lachlan R McCallum
 ********************************************************************/

public class Launcher {

	static final boolean TEST_UI = false;
	static final int DEFAULT_SERVER_PORT = 8888;
	static final int DEFAULT_CLIENT_PORT = 8889;
	static CommPortIdentifier portID;
	static String commPort = "/dev/ttyS2";
	static int serverPort = DEFAULT_SERVER_PORT;
	

	public static void main(String[] args) {

		if (args.length == 1)
			serverPort = Integer.parseInt(args[0]);
		if (args.length == 3)
			serverPort = Integer.parseInt(args[0]);

		Feedback.log("Creating Virtual IO Boards");
		BoardManager cm = new BoardManager();
		PacketHandler pb = new PacketHandler(cm);
		Thread server = new CommifyServer(cm, pb, serverPort);
		Feedback.log("Starting Server on port:" + serverPort);
		server.start();
		Feedback.log("Establishing virtual Comms port: " + commPort);
		CommPortFinder finder = new CommPortFinder(commPort);
		Feedback.log("Initializing virtual Comms port");
		CommLayer comms = new CommLayer(finder.getPort(), cm);
		comms.initializeComms();
		Feedback.log("Starting Communication Layer");
		comms.startCommInterface();

	}

}
