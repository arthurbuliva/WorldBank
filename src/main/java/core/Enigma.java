package core;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static core.LockSmith.CIPHER_ALGORITHM;
import static java.nio.file.Files.readAllBytes;

/**
 * Encrypt and decrypt strings and texts
 */
class Enigma
{
    private Cipher cipher;

    /**
     * Instantiate the class and encryption algorithm
     *
     * @throws NoSuchAlgorithmException An exception with the CIPHER_ALGORITHM in place
     * @throws NoSuchPaddingException An exception with the CIPHER_ALGORITHM in place
     * @throws NoSuchProviderException Missing or invalid encryption provider
     */
    Enigma() throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException
    {
        /**
         * Initialize a LockSmith object in order to create any keys that may be missing
         */
        LockSmith lockSmith = new LockSmith();

        this.cipher = Cipher.getInstance(CIPHER_ALGORITHM);
    }

    /**
     * Provide a PrivateKey from the encryption algorithm according to the details at
     * https://docs.oracle.com/javase/8/docs/api/java/security/spec/PKCS8EncodedKeySpec.html
     *
     * @return  a PrivateKey that can be used for data decryption
     * @throws IOException Input/output exception arising from reading the key file
     * @throws InvalidKeySpecException The key file not being a valid key file
     * @throws NoSuchAlgorithmException An exception with the CIPHER_ALGORITHM in place
     */
    PrivateKey getPrivate()
            throws IOException, InvalidKeySpecException, NoSuchAlgorithmException
    {
        // The public key at any one time will be LockSmith.PRIVATE_KEY
        byte[] keyBytes = readAllBytes(new File(LockSmith.PRIVATE_KEY).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(CIPHER_ALGORITHM);
        return kf.generatePrivate(spec);
    }

    /**
     * Provide a PublicKey from the encryption algorithm according to the details at
     * https://docs.oracle.com/javase/8/docs/api/java/security/spec/X509EncodedKeySpec.html
     *
     * @return a PublicKey for data encryption
     * @throws IOException Input/output exception arising from reading the key file
     * @throws NoSuchAlgorithmException An exception with the CIPHER_ALGORITHM in place
     * @throws InvalidKeySpecException The key file not being a valid key file
     */
    PublicKey getPublic()
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
    {
        // The public key at any one time will be LockSmith.PUBLIC_KEY
        byte[] keyBytes = readAllBytes(new File(LockSmith.PUBLIC_KEY).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(CIPHER_ALGORITHM);
        return kf.generatePublic(spec);
    }

    /**
     * Encrypt a file with a given private key
     *
     * @param input  The file to be encrypted
     * @param output The encrypted file that is the output of the encryption process
     * @param key    The PrivateKey that is used for the encryption
     * @throws IOException Input/output exception arising from reading the key file
     * @throws GeneralSecurityException A general exception with the security during encryption
     */
    void encryptFile(byte[] input, File output, PrivateKey key)
            throws IOException, GeneralSecurityException
    {
        this.cipher.init(Cipher.ENCRYPT_MODE, key);
        writeToFile(output, this.cipher.doFinal(input));
    }

    /**
     * Decrypt a file with a given private key
     *
     * @param input  The file to be decrypted
     * @param output The decrypted file that is the output of the decryption process
     * @param key    The PublicKey that is used for the encryption
     * @throws IOException Input/output exception arising from reading the key file
     * @throws GeneralSecurityException A general exception with the security during decryption
     */
    void decryptFile(byte[] input, File output, PublicKey key)
            throws IOException, GeneralSecurityException
    {
        this.cipher.init(Cipher.DECRYPT_MODE, key);
        writeToFile(output, this.cipher.doFinal(input));
    }

    /**
     * Writes a given set of data into a specified file
     *
     * @param output  The file to be written
     * @param toWrite The data into which to write the file
     * @throws IOException Input/output exception arising from reading the key file
     */
    private void writeToFile(File output, byte[] toWrite)
            throws IOException
    {
        FileOutputStream fos = new FileOutputStream(output);
        fos.write(toWrite);
        fos.flush();
        fos.close();
    }

    /**
     * Encrypt a given string of text
     *
     * @param msg The string to be encrypted
     * @param key The PrivateKey to be used to encrypt the text
     * @return The encrypted text
     * @throws IllegalBlockSizeException Exception with the block size of the msg
     * @throws BadPaddingException Exception with the passing size of the msg
     * @throws InvalidKeyException The key file not being a valid key file
     */
    String encryptText(String msg, PrivateKey key)
            throws IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException
    {
        this.cipher.init(Cipher.ENCRYPT_MODE, key);

        return Base64.encodeBase64String(cipher.doFinal(msg.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Decrypt a given cryptic text
     *
     * @param msg The cryptic text that is to be decrypted
     * @param key The PublicKey to be used to decrypt the message
     * @return The decrypted text
     * @throws InvalidKeyException The key file not being a valid key file
     * @throws IllegalBlockSizeException Exception with the block size of the msg
     * @throws BadPaddingException Exception with the padding size of the msg
     */
    String decryptText(String msg, PublicKey key)
            throws InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException
    {
        this.cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.decodeBase64(msg)), StandardCharsets.UTF_8);
    }
}
