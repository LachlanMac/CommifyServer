package com.frick.lmac.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/*******************************************************************
 * CLASS CommifyServer AUTHOR : Lachlan R McCallum
 ********************************************************************/
public class CommifyServer extends Thread {

	private int serverPort, clientPort;
	private boolean running = false;
	DatagramSocket socket;
	BoardManager cm;
	PacketHandler handler;

	public CommifyServer(BoardManager cm, PacketHandler handler, int port) {
		this.cm = cm;
		this.handler = handler;
		this.serverPort = port;

		makeSocket();

	}

	public void makeSocket() {
		running = true;
		try {
			this.socket = new DatagramSocket(serverPort);
		} catch (SocketException e) {
			Feedback.error("Could not bind to port " + serverPort);
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		Feedback.log("Server listening on port " + serverPort);
		byte[] inData;
		byte[] outData;
		DatagramPacket inPacket;
		DatagramPacket outPacket;
		InetAddress sendAddress;
		int sendPort;

		while (running) {
			// initilie new packets
			inData = new byte[256];
			outData = new byte[256];
			inPacket = new DatagramPacket(inData, inData.length);
			try {
				// RECEIVE
				socket.receive(inPacket);

				// parse incoming data
				// BOARD ID AND TYPE REQUEST?

				sendAddress = inPacket.getAddress();
				sendPort = inPacket.getPort();

				// RESPOND

				outPacket = handler.parseRequest(inPacket.getData(), sendPort, sendAddress);

				socket.send(outPacket);

			} catch (IOException e) {
				socket.close();
				e.printStackTrace();
			}

		}
		socket.close();
	}

	public DatagramSocket getSocket() {
		return socket;
	}
}
