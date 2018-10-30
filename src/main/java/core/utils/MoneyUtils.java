package core.utils;

import exceptions.StorageEncodingException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

/**
 * String Encoder and decoder
 */
public class MoneyUtils
{
    /**
     * Encode using Base64 a given string
     *
     * @param plainText The text to be encoded
     * @return The encoded text
     */
    public static String encode(String plainText) throws StorageEncodingException
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
    public static String decode(String encodedString)
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
    public static String sha256(String text)
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

    /**
     * Check if an input value is a trivial entity such as 123456 or ABCDEF
     *
     * @param input The value to be checked
     * @return true if value is trivial, false otherwise
     */
    public static boolean isTrivial(String input)
    {
        if (input.isEmpty())
        {
            return true;
        }

        Set<Integer> differences = new HashSet<>();

        char[] inputArray = input.toCharArray();

        for (int i = 0; i < inputArray.length - 1; i++)
        {
            int x = Character.getNumericValue(inputArray[i]);
            int y = Character.getNumericValue(inputArray[i + 1]);

            differences.add(x - y);
        }

        return differences.size() == 1;
    }
}
