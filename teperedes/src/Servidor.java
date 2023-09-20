package teperedes.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {

    public static void main(String[] args) {

        ServerSocket servidor = null;
        Socket sc = null;


        //puerto de nuestro servidor
        final int PUERTO = 5000;

        try {
            //Creamos el socket del servidor
            servidor = new ServerSocket(PUERTO);
            System.out.println("Servidor iniciado");
            while (true) {
                sc = servidor.accept();
                System.out.println("Cliente conectado");
                Thread clientThread = new Thread(new ClientHandler(sc));
                clientThread.start();
            }


        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    private static class ClientHandler implements Runnable {
        private final Socket sc;
        public ClientHandler( Socket sc) {
            this.sc=sc;
        }
        @Override
        public void run() {
            try {
                DataInputStream in;
                DataOutputStream out;
                //Espero a que un cliente se conecte
                in = new DataInputStream(sc.getInputStream());
                out = new DataOutputStream(sc.getOutputStream());
                Criptografia cripto = new Criptografia();
                KeyPair llaves = cripto.generarLlaves();
                out.writeUTF((Criptografia.keyToStringBase64(llaves.getPublic())));
                String llaveCliente = in.readUTF();
                System.out.println(Criptografia.stringBase64ToKey(llaveCliente));
                while (true) {
                    //Leo el mensaje que me envia
                    String mensaje = in.readUTF();
                    byte[] mensaje2;
                    mensaje2=Criptografia.base64ToByte(mensaje);
                    mensaje=Criptografia.desencriptarAsimetrico(mensaje2,llaves.getPrivate());
                    System.out.println(sc.getInetAddress()+": "+mensaje);
                    //Le envio un mensaje
                    out.writeUTF("Recibido");

                    //Cierro el socket
                    //sc.close();
                    //System.out.println("Cliente desconectado");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}