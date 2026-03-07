package org.demo.oems.utils;

import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
public class TokenGeneratorUtils {
    public String generateShortUUIDToken(int length) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // Ensure the desired length is not more than 32 (max length of UUID without hyphens)
        if (length > 32 || length < 1) {
            throw new IllegalArgumentException("Length must be between 1 and 32");
        }
        return uuid.substring(0, length);
    }

    public String generateStaticToken(long seed, int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random(seed);
        StringBuilder token = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            token.append(characters.charAt(random.nextInt(characters.length())));
        }
        return token.toString();
    }

}
