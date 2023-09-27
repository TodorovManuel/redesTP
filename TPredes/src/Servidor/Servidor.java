package TPredes.src.Servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {
    private static HashSet<Socket> sockets;

    public void enviarMensaje(Socket sc, String msj) throws IOException {
        DataOutputStream out;
        for(Socket s:sockets){
            if(s!=sc){
                out = new DataOutputStream(s.getOutputStream());
                out.writeUTF(msj);
            }
        }
    }

    public static void main(String[] args) {

        ServerSocket servidor = null;
        Socket sc = null;


        //puerto de nuestro servidor
        final int PUERTO = 5000;

        try {
            //Creamos el socket del servidor
            servidor = new ServerSocket(PUERTO);
            System.out.println("Servidor iniciado");
            Servidor.sockets=new HashSet<>();
            while (true) {
                sc = servidor.accept();
                Servidor.sockets.add(sc);
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
                Servidor s=new Servidor();
                DataInputStream in;
                DataOutputStream out;
                //Espero a que un cliente se conecte
                in = new DataInputStream(sc.getInputStream());
                out = new DataOutputStream(sc.getOutputStream());
                Criptografia cripto = new Criptografia();
                KeyPair llaves = cripto.generarLlaves();
                out.writeUTF((Criptografia.keyToStringBase64(llaves.getPublic())));
                String llaveClienteStr = in.readUTF();
                PublicKey llaveCliente = Criptografia.stringBase64ToKey(llaveClienteStr);
                System.out.println(llaveCliente);
                String mensajeR;
                String mensaje;
                String firma;
                byte[] mensaje2;
                while (true) {
                    //Leo el mensaje que me envia
                    mensajeR = in.readUTF();
                    String[] parts = mensajeR.split(":", 2);
                    firma = parts[0];
                    mensaje= parts[1];
                    byte[] firmaByte = Criptografia.base64ToByte(firma);
                    mensaje2 = Criptografia.base64ToByte(mensaje);
                    mensaje = Criptografia.desencriptarAsimetrico(mensaje2, llaves.getPrivate());
                    if(Integer.toString(mensaje.hashCode()).equals(Criptografia.desencriptarFirma(firmaByte, llaveCliente))) {
                        System.out.println(sc.getInetAddress() + ": " + mensaje);
                        //Le envio un mensaje
                        out.writeUTF("Recibido");
                        s.enviarMensaje(sc, mensaje);
                        //Cierro el socket
                        //sc.close();
                        //System.out.println("Cliente desconectado");
                    }
                    else{
                        System.err.println("Error");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}