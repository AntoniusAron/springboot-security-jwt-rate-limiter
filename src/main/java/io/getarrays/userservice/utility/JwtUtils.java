package io.getarrays.userservice.utility;

import com.auth0.jwt.algorithms.Algorithm;

/**
 * @author VM-frescent
 * @version 1.0
 * @since 25/01/2026
 */
public class JwtUtils {
    // Simpan rahasia di satu tempat saja
    public static final String SECRET_KEY = "secret";

    // Kamu juga bisa sekalian menyimpan algoritma di sini agar seragam
    public static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET_KEY.getBytes());
}
