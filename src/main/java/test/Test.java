package test;

import core.SupportMatrix;
import org.apache.commons.codec.binary.Hex;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class Test
{
    public static void main(String[] args) throws Exception
    {
        File input = new File("C:\\Users\\arthu\\OneDrive\\Canon\\Jerida-Taji.gif");

        BufferedImage buffImg = ImageIO.read(input);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(buffImg, "PNG", outputStream);
        byte[] data = outputStream.toByteArray();

        System.out.println("Start MD5 Digest");
        MessageDigest md = MessageDigest.getInstance("SHA3-512");
        md.update(data);
        byte[] hash = md.digest();

        System.out.println(Hex.encodeHexString(hash));
    }
}
