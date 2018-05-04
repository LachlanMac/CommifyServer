package com.frick.lmac.main;

/*******************************************************************
 * CLASS Feedback AUTHOR : Lachlan R McCallum This class is a simple
 * implementation of logging for testing purposes.
 ********************************************************************/
public class Feedback {

	public static boolean logging = true;
	public static boolean digitalFeedback = true;
	public static boolean analogFeedback = true;
	public static boolean networkFeedback = true;
	public static boolean generalFeedback = true;

	public static void digitalError(String errorMsg) {
		if (!logging)
			return;
		if (digitalFeedback) {
			System.out.println("Digital Error:[" + errorMsg + "]");
		}
	}

	public static void analogError(String errorMsg) {
		if (!logging)
			return;
		if (analogFeedback) {
			System.out.println("Analog Error:[" + errorMsg + "]");
		}
	}

	public static void error(String errorMsg) {
		if (!logging)
			return;
		if (generalFeedback) {
			System.out.println("Error:[" + errorMsg + "]");
		}
	}

	public static void log(String logMsg) {
		if (!logging)
			return;
		if (generalFeedback) {
			System.out.println("Commify:[" + logMsg + "]");
		}

	}

	public static void analogLog(String logMsg) {
		if (!logging)
			return;
		if (analogFeedback) {
			System.out.println("Analog Log:[" + logMsg + "]");
		}
	}

	public static void digitalLog(String logMsg) {
		if (!logging)
			return;
		if (digitalFeedback) {
			System.out.println("Digital Log:[" + logMsg + "]");
		}
	}

	public static void networkLog(String logMsg) {
		if (!logging)
			return;
		if (networkFeedback) {
			System.out.println("Network Log:[" + logMsg + "]");
		}
	}

	public static void networkError(String logMsg) {
		if (!logging)
			return;
		if (networkFeedback) {
			System.out.println("Network Error:[" + logMsg + "]");
		}
	}

}
