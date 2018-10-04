package core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;

class LockSmith
{

    private KeyPairGenerator keyGen;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public static final String PRIVATE_KEY = "keys/private.key";
    public static final String PUBLIC_KEY = "keys/public.key";
    static final String CIPHER_ALGORITHM = "RSA";

    /**
     * Instantiate the LockSmith
     *
     * @throws NoSuchAlgorithmException Missing Cipher algorithm
     */
    LockSmith() throws NoSuchAlgorithmException
    {
        int KEY_SIZE = 9999;

        this.keyGen = KeyPairGenerator.getInstance(CIPHER_ALGORITHM);
        this.keyGen.initialize(KEY_SIZE);

        File privateKeyFile = new File(PRIVATE_KEY);
        File publicKeyFile = new File(PUBLIC_KEY);

        if (!publicKeyFile.exists() || !privateKeyFile.exists())
        {
            /*
             * Create the necessary file directory structures to hold the keys
             */
            publicKeyFile.getParentFile().mkdirs();
            privateKeyFile.getParentFile().mkdirs();

            try
            {
                createKeys();
                writeToFile(PUBLIC_KEY, getPublicKey().getEncoded());
                writeToFile(PRIVATE_KEY, getPrivateKey().getEncoded());
            }
            catch (IOException ex)
            {
                ex.printStackTrace();

                /*
                 * We would not be able to encrypt the data. There is no need to proceed
                 */
                System.exit(1);
            }
        }
    }

    /**
     * Create the private and public keys, storing them into local files
     */
    private void createKeys()
    {
        System.out.println("Generating first time encryption keys. This will take a while...");
        // TODO: Once the keys are generated, deleting them causes a decryption error. Fix this

        KeyPair pair = this.keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    /**
     * Extracts the private key from the key pair
     *
     * @return The private key
     */
    private PrivateKey getPrivateKey()
    {
        return this.privateKey;
    }

    /**
     * Extracts the public key from the key pair
     *
     * @return The public key
     */
    private PublicKey getPublicKey()
    {
        return this.publicKey;
    }

    /**
     * Write data to file
     * @param path The file into which the data is to be written
     * @param data The data to be written
     * @throws IOException Exception during the file writing operation
     */
    private void writeToFile(String path, byte[] data) throws IOException
    {
        File f = new File(path);
        f.getParentFile().mkdirs();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(data);
        fos.flush();
        fos.close();
    }
}
