package com.frick.lmac.main;

public class CommPacket {

	public static final String SUCCESS = "Z";
	public static final String FAILURE = "X";
	public static final String GET = "g";
	public static final String SET = "s";
	public static final String ADD = "a";
	public static final String START = "f";
	public static final String DELETE = "d";
	public static final String split = "=";
	public static final String HEART_BEAT = "h";
	
	
	public static final char GET_CODE = 'g';
	public static final char SET_CODE = 's';
	public static final char ADD_CODE = 'a';
	public static final char DELETE_CODE = 'd';
	public static final char START_CODE = 'f';
	public static final char HEART_BEAT_CODE = 'h';
	
	

	public static String getPacketData(int packetID, String cmdType, String cmdStatus, String data) {

		return new String(packetID + split + cmdType + cmdStatus + data);

	}

}
