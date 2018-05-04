package com.frick.lmac.main;

/*******************************************************************
 * CLASS Util
 * AUTHOR : Lachlan R McCallum
 * This class is a collection of helper functions used to convert
 * data to and from different formats
 ********************************************************************/
public class Util {

	// Convert from a hex to a 24 digit binary string
	public static String getBinaryString(String hex) {

		char[] binString = new char[24];

		int dec = Integer.parseInt(hex, 16);

		char[] bin = Integer.toBinaryString(dec).toCharArray();

		int index = 0;
		for (int i = bin.length - 1; i >= 0; i--) {
			binString[index] = bin[i];
			index++;
		}

		int diff = binString.length - bin.length;

		for (int i = binString.length - diff; i < binString.length; i++) {
			binString[i] = '0';
		}
		return new String(binString);

	}

	// Calculates a checksum
	public static String calculateCheckSum(char[] hex) {

		int sum = 0;
		for (int i = 0; i < hex.length; i++) {
			sum = sum + (int) (hex[i]);
		}

		char[] hexVal = Integer.toHexString(sum).toCharArray();
		char[] lastDigits = new char[] { hexVal[hexVal.length - 2], hexVal[hexVal.length - 1] };

		return new String(lastDigits).toUpperCase();

	}

	// converts a numerical string to hex
	public static String convertToHex(String line) {

		int byteLength = 4;
		int bitStartPos = 0, bitPos = 0;
		String hexString = "";
		int sum = 0;

		String bitStream = formatString(line);

		if (bitStream.length() % 4 != 0) {
			int tempCnt = 0;
			int tempBit = bitStream.length() % 4;
			while (tempCnt < (byteLength - tempBit)) {
				bitStream = "0" + bitStream;
				tempCnt++;
			}
		}

		while (bitStartPos < bitStream.length()) {
			while (bitPos < byteLength) {
				sum = (int) (sum + Integer.parseInt("" + bitStream.charAt(bitStream.length() - bitStartPos - 1))
						* Math.pow(2, bitPos));
				bitPos++;
				bitStartPos++;
			}
			if (sum < 10)
				hexString = Integer.toString(sum) + hexString;
			else
				hexString = (char) (sum + 55) + hexString;

			bitPos = 0;
			sum = 0;
		}
		return hexString.toUpperCase();
	}

	// creates a '-' separated string with components that are 3 bits in size
	public static String getBinaryStringAnalog_3Bit(String hex) {
		char[] hexInput = hex.toCharArray();

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < (hexInput.length / 4); i++) {

			char[] hexValue = new char[] { hexInput[i * 4], hexInput[i * 4 + 1], hexInput[i * 4 + 2] };

			int dec = Integer.parseInt(new String(hexValue), 16);

			char[] binaryValue = Integer.toBinaryString(dec).toCharArray();

			sb.append(new String(binaryValue) + "-");

		}

		return sb.toString();

	}

	// creates a '-' separated string with components that are 4 bits in size
	public static String getBinaryStringAnalog_4Bit(String hex) {
		char[] hexInput = hex.toCharArray();

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < (hexInput.length / 4); i++) {

			char[] hexValue = new char[] { hexInput[i * 4], hexInput[i * 4 + 1], hexInput[i * 4 + 2],
					hexInput[i * 4 + 3] };

			int dec = Integer.parseInt(new String(hexValue), 16);

			char[] binaryValue = Integer.toBinaryString(dec).toCharArray();

			sb.append(new String(binaryValue) + "-");

		}

		return sb.toString();

	}

	public static boolean validateChecksum(String hex, String cs) {
		System.out.println("COMPARING : " + hex + " VS " + cs);
		if (calculateCheckSum(hex.toCharArray()).equals(cs)) {
			return true;
		} else {
			return false;
		}

	}

	// gets an analog channel value and parses into the correct hex format
	public static String getAChannel(int value) {

		char[] binaryString = new char[12];
		for (int i = 0; i < binaryString.length; i++) {
			binaryString[i] = '0';
		}

		char[] bin = Integer.toBinaryString(value).toCharArray();

		int diff = binaryString.length - bin.length;

		for (int j = bin.length - 1; j >= 0; j--) {
			binaryString[j + diff] = bin[j];
		}

		int decimal = Integer.parseInt(new String(bin), 2);
		String hexStr = Integer.toString(decimal, 16);

		if (hexStr.length() == 1) {
			return new String("00" + hexStr + "0").toUpperCase();
		} else if (hexStr.length() == 2) {
			return new String("0" + hexStr + "0").toUpperCase();
		} else {
			return new String(hexStr + "0").toUpperCase();
		}
	}

	public static int[] getAnalogOutput(String hexLine) {

		String hex = formatString(hexLine);

		int[] outputs = new int[8];

		char[] hexInput = hex.toCharArray();
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < (hexInput.length / 3); i++) {

			char[] hexValue = new char[] { hexInput[i * 3], hexInput[i * 3 + 1], hexInput[i * 3 + 2] };

			int decimalValue = Integer.parseInt(formatString(new String(hexValue)), 16);

			char[] binaryValue = Integer.toBinaryString(decimalValue).toCharArray();

			sb.append(new String(binaryValue) + "-");

		}

		String[] binaryData = new String(sb).trim().split("-");

		int index = 0;
		for (int j = binaryData.length - 1; j >= 0; j--) {
			outputs[index] = Integer.parseInt(binaryData[j].trim(), 2);
			index++;
		}

		return outputs;

	}

	public static String formatString(String line) {

		String formattedLine = line;

		formattedLine.trim();

		formattedLine = formattedLine.replace("\n", "");
		formattedLine = formattedLine.replace("\r", "");

		return formattedLine;

	}

	public static boolean isCommandValid(char[] cmd, int expectedSize) {
		if (cmd.length == expectedSize) {
			return true;
		} else {
			return false;
		}

	}

	
}
