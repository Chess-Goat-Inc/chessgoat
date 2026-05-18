package com.chessgoat.gameservice.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import jakarta.annotation.PostConstruct
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*

@Service
class JwtService {

    private lateinit var algorithm: Algorithm

    @PostConstruct
    fun init() {
        val publicKey = loadPublicKey()

        algorithm = Algorithm.RSA256(
                publicKey,
                null
        )
    }

    fun verifyToken(token: String): JwtPayload {
        val verifier = JWT.require(algorithm).build()

        val decoded = verifier.verify(token)

        val userId = decoded.getClaim("user_id").asInt()
        val username = decoded.getClaim("username").asString()

        return JwtPayload(
            userId = userId,
            username = username
        )
    }

    private fun loadPublicKey(): RSAPublicKey {
        val resource = ClassPathResource("keys/jwt_public.pem")

        val keyBytes =
            resource.inputStream
                .bufferedReader()
                .use { it.readText() }
                .replace(
                    "-----BEGIN PUBLIC KEY-----",
                    ""
                )
                .replace(
                    "-----END PUBLIC KEY-----",
                    ""
                )
                .replace("\\s".toRegex(), "")

        val decoded = Base64.getDecoder().decode(keyBytes)

        val spec = X509EncodedKeySpec(decoded)

        val keyFactory = KeyFactory.getInstance("RSA")

        return keyFactory.generatePublic(spec) as RSAPublicKey
    }
}