package TPredes.src.Cliente;


import TPredes.src.Servidor.Criptografia;
import TPredes.src.Servidor.Servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Cliente2 {
    public static void main(String[] args) {

        //Host del servidorf
        final String HOST = "localhost";
        //Puerto del servidoRCITO
        final int PUERTO = 5000;
        DataInputStream in;
        DataOutputStream out;
        String mensaje="";
        try {
            //Creo el socket para conectarme con el cliente
            Socket sc = new Socket(HOST, PUERTO);

            in = new DataInputStream(sc.getInputStream());
            out = new DataOutputStream(sc.getOutputStream());
            Criptografia cripto = new Criptografia();
            KeyPair llaves= cripto.generarLlaves();
            //Envio un mensaje al cliente
            Scanner s = new Scanner(System.in);
            out.writeUTF(Criptografia.keyToStringBase64(llaves.getPublic()));
            System.out.println(llaves.getPublic());
            String llaveServer = in.readUTF();
            String msj="";
            try {
                Thread clientThread2 = new Thread(new Cliente2.ClientHandler3(sc, llaves));
                clientThread2.start();
            }
            catch (Exception e){
                e.printStackTrace();
                e.getMessage();
            }

            while(true) {
                msj=s.nextLine();
                out.writeUTF(Criptografia.byteTobase64(Criptografia.encriptarAsimetrico(msj.getBytes(), Criptografia.stringBase64ToKey(llaveServer))));
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static class ClientHandler3 implements Runnable {
        private final Socket sc;
        private KeyPair llaves;
        public ClientHandler3( Socket sc, KeyPair llaves) {
            this.sc=sc;
            this.llaves=llaves;
        }
        @Override
        public void run() {
            try {
                DataInputStream in;
                DataOutputStream out;
                in = new DataInputStream(sc.getInputStream());
                Criptografia cripto = new Criptografia();
                while (true) {
                    String mensaje = in.readUTF();
                   /* byte[] mensaje2;
                    mensaje2=Criptografia.base64ToByte(mensaje);
                    mensaje=Criptografia.desencriptarAsimetrico(mensaje2,llaves.getPrivate());*/
                    System.out.println(sc.getInetAddress()+": "+mensaje);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

