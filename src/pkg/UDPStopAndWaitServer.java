package pkg;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;

class UDPStopAndWaitServer {
	private static final int BUFFER_SIZE = 1024;
	private static final int PORT = 6789;

	public static void main(String[] args) throws IOException {
		// Create a server socket
		DatagramSocket serverSocket = new DatagramSocket(PORT);

		// Set up byte arrays for sending/receiving data
		byte[] receiveData = new byte[BUFFER_SIZE];
		byte[] dataForSend = new byte[BUFFER_SIZE];
		boolean timedOut = true;

		int diceNumber = 1;
		int right;
		boolean canRerrol = true;
		boolean receivingNick = true;
		ArrayList<ClientData> clients = new ArrayList<>();
		while (true) {
			receivingNick = true;
			while (receivingNick) {

				// Get the received packet
				DatagramPacket received = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(received);

				// Get the message from the packet
				// int message = ByteBuffer.wrap(received.getData()).getInt();
				// get client data
				String nick = new String(receiveData);
				InetAddress IPAddress = received.getAddress();
				int port = received.getPort();
				ClientData client = new ClientData(nick, IPAddress, port);
				if(clients.size()==1 && !clients.get(0).getNick().equalsIgnoreCase(nick)) {
					System.out.println("Unico "+client.toString());
					clients.add(client);
					System.out.println("Todos "+clients);
				}else if(clients.size()==0) {
					clients.add(client);
					//System.out.println("Tamanho dos clientes"+clients.size());
				}
				
				Random random = new Random();
				int chance = random.nextInt(100);

				// 1 in 2 chance of responding to the message
				if (((chance % 2) == 0)) {

					//System.out.println("FROM CLIENT:\t " + client.toString());

					// Convert message to uppercase
					dataForSend = ByteBuffer.allocate(4).putInt(1).array();
					System.out.println("Sending confirmation nick");
					// Send the packet data back to the client
					DatagramPacket packet = new DatagramPacket(dataForSend, dataForSend.length, IPAddress, port);
					serverSocket.send(packet);
					receivingNick = false;

				}
			}

			// Infinite loop to check for connections
			if (clients.size() == 2) {
				
				System.out.println(clients.toString());
				
				//int to byte array
				dataForSend = ByteBuffer.allocate(4).putInt(5).array();
				

				DatagramPacket packet = new DatagramPacket(dataForSend, dataForSend.length,
						clients.get(0).getIPAddress(), clients.get(0).getPort());
				serverSocket.send(packet);
				packet = new DatagramPacket(dataForSend, dataForSend.length, clients.get(1).getIPAddress(),
						clients.get(1).getPort());
				serverSocket.send(packet);

				
				// start the game
				while (true) {
					if (canRerrol) {
						diceNumber = getRandomNumberInRange(1, 6);
						System.out.println("Dice rolled to..." + diceNumber + "\n");

					}
					// Get the received packet
					DatagramPacket received = new DatagramPacket(receiveData, receiveData.length);
					serverSocket.receive(received);

					// Get the message from the packet
					int message = ByteBuffer.wrap(received.getData()).getInt();
					// check the answer
					if (message == diceNumber) {
						// System.out.println( "Congratulations, you guessed it right! \n");
						right = 1;
					} else {
						// System.out.println( "Sorry, you guessed it wrong! \n");
						right = 0;
					}

					Random random = new Random();
					int chance = random.nextInt(100);

					// 1 in 2 chance of responding to the message
					if (((chance % 2) == 0)) {
						canRerrol = true;
						System.out.println("FROM CLIENT: " + message);

						// Get packet's IP and port
						InetAddress IPAddress = received.getAddress();
						int port = received.getPort();

						// Convert message to uppercase
						dataForSend = ByteBuffer.allocate(4).putInt(right).array();

						// Send the packet data back to the client
						packet = new DatagramPacket(dataForSend, dataForSend.length, IPAddress, port);
						serverSocket.send(packet);
					} else {
						System.out.println("Oops, packet with number " + message + " was dropped");
						canRerrol = false;
					}
				}
			}
		}
	}

	private static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
}
