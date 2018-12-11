package pkg;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;

class Server {

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
        boolean playing1 = false;
        boolean playing2 = false;
        int[] points = new int[2];
        points[0] = 0;
        points[1] = 0;
        int check = 0;
        ArrayList<ClientData> clients = new ArrayList<>();
        System.out.println("Starting Server");
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
                System.out.println(nick);
                if (clients.size() == 1 && !clients.get(0).getNick().equalsIgnoreCase(nick)) {
                    clients.add(client);
                    System.out.println("Todos " + clients);
                } else if (clients.size() == 0) {

                    clients.add(client);
                    System.out.println("Todos " + clients);
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
                    if (clients.size() == 2) {
                        receivingNick = false;
                    }

                }
            }

            //Infinite loop to check for connections
            while (true) {

                if (clients.size() == 2) {
                    playing1 = true;
                    playing2 = true;

                    System.out.println(clients.toString());

                    //int to byte array
                    dataForSend = ByteBuffer.allocate(4).putInt(5).array();

                    //call first player
                    DatagramPacket packet = new DatagramPacket(dataForSend, dataForSend.length,
                            clients.get(0).getIPAddress(), clients.get(0).getPort());
                    serverSocket.send(packet);

                    // start the game with the first player
                    while (playing1) {
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
                            check++;
                            checkGame(points, check);
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
                            String answer = right + "#" + points[0] + "@" + points[1];
                            // Get packet's IP and port
                            InetAddress IPAddress = received.getAddress();
                            int port = received.getPort();

                            // Convert message to uppercase
                            dataForSend = answer.getBytes();

                            // Send the packet data back to the client
                            packet = new DatagramPacket(dataForSend, dataForSend.length, IPAddress, port);
                            serverSocket.send(packet);
                            playing1 = false;

                        } else {
                            System.out.println("Oops, packet with number " + message + " was dropped");
                            canRerrol = false;

                        }
                    }
                    System.out.println("Calling the second player");
                    canRerrol = false;
                    //call the second player
                    dataForSend = ByteBuffer.allocate(4).putInt(5).array();

                    //call second player
                    packet = new DatagramPacket(dataForSend, dataForSend.length,
                            clients.get(1).getIPAddress(), clients.get(1).getPort());
                    serverSocket.send(packet);
                    //start the game for the second player
                    playing2 = true;

                    while (playing2) {
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
                            check += 2;
                            checkGame(points, check);

                        } else {
                            // System.out.println( "Sorry, you guessed it wrong! \n");
                            right = 0;
                           
                        }

                        Random random = new Random();
                        int chance = random.nextInt(100);

                        // 1 in 2 chance of responding to the message
                        if (((chance % 2) == 0)) {
                            String answer = right + "#" + points[0] + "@" + points[1];
                            canRerrol = true;
                            System.out.println("FROM CLIENT: " + message);

                            // Get packet's IP and port
                            InetAddress IPAddress = received.getAddress();
                            int port = received.getPort();

                            // Convert message to uppercase
                            dataForSend = answer.getBytes();

                            // Send the packet data back to the client
                            packet = new DatagramPacket(dataForSend, dataForSend.length, IPAddress, port);
                            serverSocket.send(packet);
                            playing2 = false;

                        } else {
                            System.out.println("Oops, packet with number " + message + " was dropped");
                            canRerrol = false;

                        }

                    }

                    check = 0;
                    //send the score    

                } else {
                    playing1 = false;
                    playing2 = false;
                }

            }
        }
    }

    static void checkGame(int[] points, int check) {

        switch (check) {
            case 1:
                points[0]++;
                break;
            case 2:
                points[1]++;
                break;
            case 3:
                points[0]++;
                points[1]++;
            default:
                System.out.println("Nobody guessed right!");
                break;

        }

    }

//    private static void game(boolean canRerrol, int diceNumber, DatagramPacket receiveData,
//            DatagramSocket serverSocket, byte[] dataForSend ) throws Exeption {
//        while (true) {
//            if (canRerrol) {
//                diceNumber = getRandomNumberInRange(1, 6);
//                System.out.println("Dice rolled to..." + diceNumber + "\n");
//
//            }
//            // Get the received packet
//            DatagramPacket received = new DatagramPacket(receiveData, receiveData.length);
//            serverSocket.receive(received);
//
//            // Get the message from the packet
//            int message = ByteBuffer.wrap(received.getData()).getInt();
//            // check the answer
//            if (message == diceNumber) {
//                // System.out.println( "Congratulations, you guessed it right! \n");
//                right = 1;
//            } else {
//                // System.out.println( "Sorry, you guessed it wrong! \n");
//                right = 0;
//            }
//
//            Random random = new Random();
//            int chance = random.nextInt(100);
//
//            // 1 in 2 chance of responding to the message
//            if (((chance % 2) == 0)) {
//                canRerrol = true;
//                System.out.println("FROM CLIENT: " + message);
//
//                // Get packet's IP and port
//                InetAddress IPAddress = received.getAddress();
//                int port = received.getPort();
//
//                // Convert message to uppercase
//                dataForSend = ByteBuffer.allocate(4).putInt(right).array();
//
//                // Send the packet data back to the client
//                packet = new DatagramPacket(dataForSend, dataForSend.length, IPAddress, port);
//                serverSocket.send(packet);
//            } else {
//                System.out.println("Oops, packet with number " + message + " was dropped");
//                canRerrol = false;
//            }
//        }
//    }
    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
