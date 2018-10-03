package test;

import core.Enigma;

import java.io.File;
import java.io.FileOutputStream;
import java.security.*;

import static core.LockSmith.PRIVATE_KEY;
import static core.LockSmith.PUBLIC_KEY;

/**
 * @author Arthur Buliva
 */
public class Crypto
{

    public static void main(String[] args) throws Exception {
        Enigma ac = new Enigma();
        PrivateKey privateKey = ac.getPrivate(PRIVATE_KEY);
        PublicKey publicKey = ac.getPublic(PUBLIC_KEY);

        String data = "[{\"field\":\"accountNumber\",\"inputValue\":\"411234567890\",\"validity\":true,\"accountNumberCode\":\"4112\"},{\"field\":\"accountHolderName\",\"inputValue\":\"Arthur Buliva\",\"validity\":true},{\"field\":\"accountHolderAddress\",\"inputValue\":\"Amsterdam, The Netherlands\",\"validity\":true},{\"field\":\"BIC\",\"inputValue\":\"SCBKENLXXXX\",\"warningMessage\":\"Should be derived!\",\"validity\":true},{\"coinId\":\"" +
                "b0x6UUtzak1FaWh5a0c5ajhtdVpKY29zWnQzMTl2ZmtCclFSMndQVmNVNnZaMFo2Lzk3Qko0Qk1Ha3A2aFVUNElmQXdLcG5KcXo3MFgxVzE3WnZsdXc\"}]";

        String encrypted_msg = ac.encryptText(data, privateKey);
        String decrypted_msg = ac.decryptText(encrypted_msg, publicKey);
        System.out.println("Original Message: " + data +
                "\nEncrypted Message: " + encrypted_msg
                + "\nDecrypted Message: " + decrypted_msg);

        File file = new File("KeyPair/text.txt");
        file.getParentFile().mkdirs();
        FileOutputStream fileOutputStream = new FileOutputStream(file);

        fileOutputStream.write(data.getBytes());
        fileOutputStream.close();

        if (new File("KeyPair/text.txt").exists()) {
            ac.encryptFile(ac.getFileInBytes(new File("KeyPair/text.txt")),
                    new File("KeyPair/text_encrypted.txt"),privateKey);
            ac.decryptFile(ac.getFileInBytes(new File("KeyPair/text_encrypted.txt")),
                    new File("KeyPair/text_decrypted.txt"), publicKey);
        } else {
            System.out.println("Create a file text.txt under folder KeyPair");
        }
    }
}