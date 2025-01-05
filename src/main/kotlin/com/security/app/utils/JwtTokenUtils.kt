package com.security.app.utils

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.spec.SecretKeySpec

@Component
class JwtTokenUtils
{
    private val secret: String = System.getenv("JWT_SECRET")

    private val jwtIssuer : String = System.getenv("JWT_ISSUER")
    private val signingKey: SecretKeySpec
        get() {
            val keyBytes: ByteArray = Base64.getDecoder().decode(secret)
            return SecretKeySpec(keyBytes, 0, keyBytes.size, "HmacSHA256")
        }


    fun generateToken(userId: UUID, expiration: Long): String {
        val expirationTime = Date(System.currentTimeMillis() + expiration)
        return Jwts.builder()
            .setSubject(userId.toString())
            .setIssuer(jwtIssuer)
            .setIssuedAt(Date())
            .setExpiration(expirationTime)
            .signWith(signingKey)
            .compact()
    }

    fun getUserId(token: String): String? {
        val claims: Claims? = validateToken(token)
        return claims?.subject
    }

    fun isTokenStillValid(token: String): Boolean {
        val claims: Claims? = validateToken(token)
        return claims?.expiration?.after(Date()) ?: false
    }


    private fun validateToken(token: String): Claims? {
        try {
            return Jwts.parser()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (ex: Exception) {
            println("Exception: ${ex.message}")
        }
        return null
    }
}
