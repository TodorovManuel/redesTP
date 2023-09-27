package TPredes.src.Servidor;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Criptografia {
    private static final String RSA = "RSA";
    public static KeyPair generarLlaves() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }
    public static PublicKey stringBase64ToKey(String publicK) throws Exception {
        byte[] encodedPublicKey = Base64.getDecoder().decode(publicK);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(encodedPublicKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
    public static String keyToStringBase64(PublicKey publicK){
        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicK.getEncoded());
        return publicKeyBase64;
    }
    public static byte[] base64ToByte(String base64){
        Base64.Decoder dec = Base64.getDecoder();
        return dec.decode(base64);
    }
    public static String byteTobase64(byte [] mensaje){
        Base64.Encoder enc = Base64.getEncoder();
        String encoded = enc.encodeToString(mensaje);
        return encoded;
    }

    public static byte[] encriptarAsimetrico(byte[] plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);//encripta con la pub.key del otro
        return cipher.doFinal(plainText);
    }
    public static String desencriptarAsimetrico(byte[] cipherText, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);//desencripta con la priv.key propia
        byte[] result= cipher.doFinal(cipherText);
        return new String(result);
    }
    public static byte[] firma (byte[] aux, PrivateKey privateKey) throws Exception{
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);       //firma con la propia priv.key
        return cipher.doFinal(aux);
    }
    public static String firmaDigital(String mensaje, PrivateKey privateKey){
        try{
            int msg2=mensaje.hashCode();
            String msg = Integer.toString(msg2);
            byte[] msgB = msg.getBytes(StandardCharsets.UTF_8);
            msgB=firma(msgB, privateKey);
            return Criptografia.byteTobase64(msgB);
        }catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }
    public static String desencriptarFirma(byte[] cipherText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] result= cipher.doFinal(cipherText);
        return new String(result);
    }

    public static void main(String[] args) throws Exception {
        /*KeyPair keyPair=Criptografia.generarLlaves();
        String a="hola";
        System.out.println(a);
        System.out.println(Criptografia.encriptarAsimetrico(a.getBytes(),keyPair.getPublic()));
        System.out.println(Criptografia.desencriptarAsimetrico(Criptografia.encriptarAsimetrico(a.getBytes(),keyPair.getPublic()), keyPair.getPrivate()));
*/
    }
}
