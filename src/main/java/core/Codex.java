package core;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * String Encoder and decoder
 */
class Codex
{

    /**
     * Encode using Base64 a given string
     *
     * @param plainText The text to be encoded
     * @return The encoded text
     */
    static String encode(String plainText)
    {
        byte[] encodedBytes = Base64.getEncoder().withoutPadding().encode(plainText.getBytes(StandardCharsets.UTF_8));

        return new String(encodedBytes, Charset.forName("UTF-8"));
    }

    /**
     * Decode using Base64 a given encoded string
     *
     * @param encodedString The encoded string to be decoded
     * @return The decoded String
     */
    static String decode(String encodedString)
    {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString.getBytes());

        return  new String(decodedBytes, Charset.forName("UTF-8"));
    }

    /**
     * Get the SHA256 encoding of a given
     *
     * @param text The String to encode to Sha256
     * @return The encoded string
     */
    static String sha256(String text)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA3-512");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().withoutPadding().encodeToString(hash);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }

        return null;
    }
}
