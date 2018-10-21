package core;

import com.google.gson.Gson;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

public class CoinDisplay
{
    private Enigma enigma;

    public CoinDisplay() throws
            NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException
    {
        /*
         Initialize the encryption/decryption class
         */
        enigma = new Enigma();
    }

    /**
     * Retrieves a coin from the store and displays its value
     *
     * @param coinId The id of the coin to be retrieved
     * @return A decrypted string of the values
     */
    public String showCoin(String coinId)
    {
        Store store = new Store();

        if (coinId.isEmpty())
        {
            return new Gson().toJson(new ArrayList());
        }

        try
        {
            return enigma.decryptText((String) store.displayCoin(coinId), enigma.getPublic());
        }
        catch (NullPointerException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException
                | IOException | NoSuchAlgorithmException | InvalidKeySpecException e)
        {
            e.printStackTrace();
            
            return new Gson().toJson(new ArrayList());
        }
    }
}
