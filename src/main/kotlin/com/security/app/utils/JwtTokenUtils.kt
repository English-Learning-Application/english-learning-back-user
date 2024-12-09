package com.security.app.utils

import io.jsonwebtoken.Claims
import org.springframework.stereotype.Component
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import java.util.*
import javax.crypto.spec.SecretKeySpec

@Component
class JwtTokenUtils (
    @Value("\${jwt.secret}") private val secret:  String = ""
)
{
    private val jwtIssuer = "myIssuer"
    private val signingKey: SecretKeySpec
        get() {
            val keyBytes: ByteArray = Base64.getDecoder().decode(secret)
            return SecretKeySpec(keyBytes, 0, keyBytes.size, "HmacSHA256")
        }


    fun generateToken(email: String, expiration: Long): String {
        val expirationTime = Date(System.currentTimeMillis() + expiration)
        return Jwts.builder()
            .setSubject(email)
            .setIssuer(jwtIssuer)
            .setIssuedAt(Date())
            .setExpiration(expirationTime)
            .signWith(signingKey)
            .compact()
    }

    fun getEmailFromToken(token: String): String? {
        val claims: Claims? = validateToken(token)
        return claims?.subject
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
