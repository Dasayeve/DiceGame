package pkg;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.Scanner;

import org.omg.CORBA.TIMEOUT;

class UDPStopAndWaitClient {
	private static final int BUFFER_SIZE = 1024;
	private static final int PORT = 6789;
	private static final String HOSTNAME = "localhost";
	// variables
	private static String nick;
	static boolean sending = true;
	boolean invalidNumber = true;
	static Scanner input = new Scanner(System.in);

	public static void main(String args[]) throws Exception {
		// Create a socket

		DatagramSocket socket = new DatagramSocket();
		socket.setSoTimeout(1000);
		// aux variables

		// The message we're going to send converted to bytes
		// Integer sequenceNumber = BASE_SEQUENCE_NUMBER;
		Integer number = 1;
		System.out.println("WELCOME TO THE UDP DICE SIMULATOR \n");
		System.out.print("Enter your nick");
		nick = input.nextLine();
		boolean timedOut = true;

		// initialize and wait
		while (timedOut) {

			// Create a byte array for sending and receiving data
			byte[] sendData = new byte[BUFFER_SIZE];
			byte[] receiveData = new byte[BUFFER_SIZE];

			// Get the IP address of the server
			InetAddress IPAddress = InetAddress.getByName(HOSTNAME);

			System.out.println("Sending nick" + nick);
			// Get byte data for message
			sendData = nick.getBytes();

			try {
				// Send the UDP Packet to the server
				DatagramPacket packet = new DatagramPacket(sendData, sendData.length, IPAddress, PORT);
				socket.send(packet);

				// Receive the server's packet
				DatagramPacket received = new DatagramPacket(receiveData, receiveData.length);
				socket.receive(received);

				// If we receive an ack, stop the while loop
				System.out.println("Player " + nick + " registered.");
				timedOut = false;
			} catch (SocketTimeoutException exception) {
				// If we don't get an ack, prepare to resend sequence number
				System.out.println("Timeout (Nick" + nick);

			}

		}

		// will receive the canstart value, -1 for not ready
                //5 for ready
		timedOut = true;
		int canStart = -1;
		while (timedOut) {
			try {
				byte[] startByte = new byte[BUFFER_SIZE];
				DatagramPacket received = new DatagramPacket(startByte, startByte.length);
				socket.receive(received);
				canStart = ByteBuffer.wrap(received.getData()).getInt();
				// If we receive an ack, stop the while loop
				timedOut = false;
			} catch (SocketTimeoutException exception) {
				// If we don't get an ack, prepare to resend sequence number
				System.out.println("Timeout (Confirmation server to client " + canStart + ")");

			}
		}

		System.out.print("CanStart Value: " + canStart);
		if (canStart == 5) {

			while (sending) {

				System.out.println("Choose a number between 1 to 6");
				number = input.nextInt();
				timedOut = true;
				while (timedOut) {

					// Create a byte array for sending and receiving data
					byte[] sendData = new byte[BUFFER_SIZE];
					byte[] receiveData = new byte[BUFFER_SIZE];

					// Get the IP address of the server
					InetAddress IPAddress = InetAddress.getByName(HOSTNAME);

					System.out.println("Sending Packet (Number " + number + ")");
					// Get byte data for message
					sendData = ByteBuffer.allocate(4).putInt(number).array();

					try {
						// Send the UDP Packet to the server
						DatagramPacket packet = new DatagramPacket(sendData, sendData.length, IPAddress, PORT);
						socket.send(packet);

						// Receive the server's packet
						DatagramPacket received = new DatagramPacket(receiveData, receiveData.length);
						socket.receive(received);

						// Get the message from the server's packet
						int returnMessage = ByteBuffer.wrap(received.getData()).getInt();

						System.out.println("FROM SERVER:" + returnMessage);
						if (returnMessage == 1) {
							System.out.println("Congratulations, you guessed it right! \n");
						} else {
							System.out.println("Sorry, you guessed it wrong! \n");
						}

						// If we receive an ack, stop the while loop
						timedOut = false;
					} catch (SocketTimeoutException exception) {
						// If we don't get an ack, prepare to resend sequence number
						System.out.println("Timeout (Sequence Number " + number + ")");

					}

				}
				System.out.println("Play again? \n 1 - Play \n 0 - Do not play");
				number = input.nextInt();
				if (number == 0) {
					sending = false;
				}
			}

			socket.close();
		}
	}
}
