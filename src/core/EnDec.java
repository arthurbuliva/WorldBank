package core;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Encoder and decoder
 */
public class EnDec
{
    /**
     * Encode using Base64 a given string
     * @param plainText
     * @return
     */
    public static String encode(String plainText)
    {
        byte[] encodedBytes = Base64.getEncoder().withoutPadding().encode(plainText.getBytes(StandardCharsets.UTF_8));
        String encoded = new String(encodedBytes, Charset.forName("UTF-8"));

        return encoded;
    }

    /**
     * Decode using Base64 a given encoded string
     * @param encodedString
     * @return
     */
    public static String decode(String encodedString)
    {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString.getBytes());
        String decoded = new String(decodedBytes, Charset.forName("UTF-8"));

        return decoded;
    }

    /**
     * Decode using SHA256 a given encoded string
     * @param text
     * @return
     */
    public static String sha256(String text)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA3-512");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            String encoded = Base64.getEncoder().withoutPadding().encodeToString(hash);

            return encoded;
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }

        return null;
    }

}
