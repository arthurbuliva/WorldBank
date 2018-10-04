package core;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static core.LockSmith.CIPHER_ALGORITHM;
import static java.nio.file.Files.readAllBytes;

/**
 * Encrypt and decrypt strings and texts
 */
public class Enigma
{
    private Cipher cipher;
    private LockSmith lockSmith = new LockSmith();

    /**
     * Instantiate the class and encryption algorithm
     *
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws NoSuchProviderException
     */
    public Enigma() throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException
    {
        this.cipher = Cipher.getInstance(CIPHER_ALGORITHM);
    }

    /**
     * Provide a PrivateKey from the encryption algorithm according to the details at
     * https://docs.oracle.com/javase/8/docs/api/java/security/spec/PKCS8EncodedKeySpec.html
     *
     * @param filename The SSH private key file
     * @return a PrivateKey that can be used for data decryption
     * @throws Exception
     */
    public PrivateKey getPrivate(String filename) throws Exception
    {
        byte[] keyBytes = readAllBytes(new File(filename).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(CIPHER_ALGORITHM);
        return kf.generatePrivate(spec);
    }

    /**
     * Provide a PublicKey from the encryption algorithm according to the details at
     * https://docs.oracle.com/javase/8/docs/api/java/security/spec/X509EncodedKeySpec.html
     *
     * @param filename The SSH private key file
     * @return a PublicKey for data encryption
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public PublicKey getPublic(String filename)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
    {
        byte[] keyBytes = readAllBytes(new File(filename).toPath());
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
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public void encryptFile(byte[] input, File output, PrivateKey key)
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
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public void decryptFile(byte[] input, File output, PublicKey key)
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
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws IOException
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
     * @throws UnsupportedEncodingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    public String encryptText(String msg, PrivateKey key)
            throws UnsupportedEncodingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException
    {
        this.cipher.init(Cipher.ENCRYPT_MODE, key);

        return Base64.encodeBase64String(cipher.doFinal(msg.getBytes("UTF-8")));
    }

    /**
     * Decrypt a given cryptic text
     *
     * @param msg The cryptic text that is to be decrypted
     * @param key The PublicKey to be used to decrypt the message
     * @return The decrypted text
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String decryptText(String msg, PublicKey key)
            throws InvalidKeyException, UnsupportedEncodingException,
            IllegalBlockSizeException, BadPaddingException
    {
        this.cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.decodeBase64(msg)), "UTF-8");
    }

    /**
     * Reads a file and extracts the contents into an array of bytes
     *
     * @param file The input file
     * @return The contents of the file in an array of bytes
     * @throws IOException
     */
    public byte[] getFileInBytes(File file) throws IOException
    {
        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        fis.read(bytes);
        fis.close();
        return bytes;
    }
}
