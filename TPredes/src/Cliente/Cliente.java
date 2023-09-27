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
import java.security.PublicKey;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Cliente {
    public static void main(String[] args) {
        //Host del servidor
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
            String aux = Criptografia.keyToStringBase64(llaves.getPublic());
            out.writeUTF(aux);
            //out.writeUTF(Criptografia.firmaDigital(aux, llaves.getPrivate()));
            System.out.println(llaves.getPublic());
            String llaveServerStr = in.readUTF();
            PublicKey llaveServer = Criptografia.stringBase64ToKey(llaveServerStr);
            String msj="";
            try {
                Thread clientThread2 = new Thread(new Cliente.ClientHandler2(sc, llaves));
                clientThread2.start();
            }
            catch (Exception e){
                e.printStackTrace();
                e.getMessage();
            }
            while(true) {
                msj=s.nextLine();
                String aux2=msj;
                out.writeUTF(Criptografia.firmaDigital(aux2, llaves.getPrivate())+":"+Criptografia.byteTobase64(Criptografia.encriptarAsimetrico(msj.getBytes(), llaveServer)));
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
    private static class ClientHandler2 implements Runnable {
        private final Socket sc;
        private KeyPair llaves;
        public ClientHandler2( Socket sc, KeyPair llaves) {
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

