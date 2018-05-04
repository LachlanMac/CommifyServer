package com.frick.lmac.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.TooManyListenersException;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;

/*******************************************************************
 * CLASS CommLayer [INTERFACES: Runnable, SerialPortEventListener] AUTHOR :
 * Lachlan R McCallum CommLayer handles the communication between the Quantum
 * Controller and the virtual IO boards, listening for messages and sending IO
 * commands.
 ********************************************************************/

public class CommLayer implements Runnable, SerialPortEventListener {

	private static CommPortIdentifier portID;
	private static final int LOOP_DELAY_TIMER_MS = 50;
	private static final int READ_WRITE_BUFFER_DELAY_NS = 500000;
	private static final int TIME_OUT_COUNTER = 100;
	private SerialPort serialPort;
	private final char CARRIAGE_RETURN = 10;
	// input and output streams
	private InputStream iStream;
	private OutputStream oStream;
	// thread that listens for serial events
	private Thread commThread;
	// Thread safe queue where Received commands are kept and processed
	private BlockingQueue<Command> commandQueue;
	// Holds a reference to the current command being built by the listener
	private Command currentCommand;
	// Manager object
	private BoardManager cm;
	// List of all virtual IO boards
	private Vector<IOBoard> boardList;
	// status boolean for writing to serial port
	static boolean outputBufferEmptyFlag = false;

	// CONSTRUCTOR
	public CommLayer(CommPortIdentifier portID, BoardManager cm) {
		this.cm = cm;
		this.portID = portID;
		this.boardList = cm.getIOBoards();

		currentCommand = new Command();
		commandQueue = new ArrayBlockingQueue<Command>(1024);

	}

	// Starts the Comm Interface
	public void startCommInterface() {
		commThread.start();
	}

	// Sets initial variables and configuration for the commport connection
	public void initializeComms() {
		try {
			serialPort = (SerialPort) portID.open("CommPort", 100000);
			iStream = serialPort.getInputStream();
			oStream = serialPort.getOutputStream();
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
			serialPort.notifyOnOutputEmpty(true);
			serialPort.setSerialPortParams(19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			commThread = new Thread(this);

		} catch (PortInUseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
		}
	}

	// Listens for Serial Events
	@Override
	public void serialEvent(SerialPortEvent event) {

		switch (event.getEventType()) {

		case SerialPortEvent.BI:
			System.out.println("= = Break Interrupt = =");
		case SerialPortEvent.OE:
			System.out.println("= = Overrun Error = =");
		case SerialPortEvent.FE:
			System.out.println("= = Framing Error = =");
		case SerialPortEvent.PE:
			System.out.println("= = Parity Error = =");
		case SerialPortEvent.CD:
			System.out.println("= = Carrier Detect = =");
		case SerialPortEvent.CTS:
			System.out.println("= = Clear To Send = =");
		case SerialPortEvent.DSR:
			System.out.println("= = Data Set Ready = =");
		case SerialPortEvent.RI:
			System.out.println("= = Ring Indicator = =");
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:

			break;

		case SerialPortEvent.DATA_AVAILABLE:
			byte[] readBuffer = new byte[32];

			try {
				while (iStream.available() > 0) {
					int numBytes = iStream.read(readBuffer);
				}

				boolean endOfCommand = false;

				for (int j = 0; j < readBuffer.length; j++) {
					if (readBuffer[j] == CARRIAGE_RETURN) {
						endOfCommand = true;
					}
				}

				if (endOfCommand) {
					currentCommand.addData(new String(readBuffer));
					currentCommand.parseCommand();
					commandQueue.add(currentCommand);
					currentCommand = new Command();
				} else {
					currentCommand.addData(new String(readBuffer));
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			break;
		}
	}

	// Thread Loop
	@Override
	public void run() {
		int timeout = 0;
		try {
			Thread.sleep(1);
			while (true) {

				timeout++;

				if (timeout > TIME_OUT_COUNTER) {
					Feedback.error("Timeout - No Communication detected");
					timeout = 0;
				}
				Thread.sleep(LOOP_DELAY_TIMER_MS);
				if (!commandQueue.isEmpty()) {
					timeout = 0;
					Command c = commandQueue.peek();

					int boardType = c.getType();
					int boardID = c.getBoardID();

					if (boardList.size() == 0) {
						//throw out packet if there is no board to send it to.
						commandQueue.poll();
					}

					for (int i = 0; i < boardList.size(); i++) {

						if ((boardList.get(i).getBoardType() == boardType)
								&& (boardList.get(i).getBoardID() == boardID)) {

							if (c.hasBeenParsed()) {
								// Receive command
								boardList.get(i).rx(c);
								// generate response
								String out = boardList.get(i).tx();
								// buffer delay
								Thread.sleep(0, READ_WRITE_BUFFER_DELAY_NS);
								// write response
								oStream.write(out.getBytes());

								// DEQUEUE
								if (!commandQueue.isEmpty()) {
									commandQueue.poll();
								}
							} else {
								// bad packet
								if (!commandQueue.isEmpty()) {
									commandQueue.poll();
								}
								currentCommand = new Command();
							}

						} else {
							// bad packet
							commandQueue.poll();
						}
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
