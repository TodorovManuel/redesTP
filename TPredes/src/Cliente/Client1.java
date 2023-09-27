/*package TPredes.src.Cliente;

import java.net.*;

public class Client1 {
    public static final String serverIP = "127.0.0.1";
    public static final int serverPort = 12345;
    public static final int clientPort = 54321;
    public static InetAddress clientAddress;

    public static void main(String[] args) {
        try {
            DatagramSocket clientSocket = new DatagramSocket(clientPort);
            clientAddress = InetAddress.getByName(serverIP);

            while (true) {
                String message = "Hola desde Cliente 1";
                byte[] sendData = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, serverPort);
                clientSocket.send(sendPacket);

                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);

                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Respuesta del servidor: " + receivedMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
*/

