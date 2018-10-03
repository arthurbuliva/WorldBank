package core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class LockSmith
{

    private KeyPairGenerator keyGen;
    private KeyPair pair;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public static final String PRIVATE_KEY = "keys/private.key";
    public static final String PUBLIC_KEY = "keys/public.key";
    public static final String CIPHER_ALGORITHM = "RSA";
    public static final int KEY_SIZE = 9999;

    public LockSmith() throws NoSuchAlgorithmException, NoSuchProviderException
    {
        this.keyGen = KeyPairGenerator.getInstance(CIPHER_ALGORITHM);
        this.keyGen.initialize(KEY_SIZE);

        File privateKeyFile = new File(PRIVATE_KEY);
        File publicKeyFile = new File(PUBLIC_KEY);

        if (!publicKeyFile.exists() || !privateKeyFile.exists())
        {
            publicKeyFile.getParentFile().mkdirs();
            privateKeyFile.getParentFile().mkdirs();

            try
            {
                createKeys();
                writeToFile(PUBLIC_KEY, getPublicKey().getEncoded());
                writeToFile(PRIVATE_KEY, getPrivateKey().getEncoded());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public void createKeys()
    {
        System.out.println("Generating first time encryption keys. This will take a while...");
        // TODO: Once the keys are generated, deleting them causes a decryption error. Fix this

        this.pair = this.keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    public PrivateKey getPrivateKey()
    {
        return this.privateKey;
    }

    public PublicKey getPublicKey()
    {
        return this.publicKey;
    }

    public void writeToFile(String path, byte[] key) throws IOException
    {
        File f = new File(path);
        f.getParentFile().mkdirs();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();
    }
}
