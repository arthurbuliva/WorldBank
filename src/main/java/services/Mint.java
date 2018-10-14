package services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point of the web service
 */
@SpringBootApplication
public class Mint
{
    public static void main(String[] args)
    {
        SpringApplication.run(
                Mint.class, args
        );
    }
}
