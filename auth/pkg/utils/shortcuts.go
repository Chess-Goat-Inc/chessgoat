package utils

import (
	"crypto/sha256"
	"encoding/hex"
	"errors"
	"log"
	"time"

	"auth/internal/config"

	"github.com/gin-gonic/gin"
	"github.com/golang-jwt/jwt/v5"
)

const (
	AccessTokenTTL = 15 * time.Minute
	RefreshTokenTTL = 7 * 24 * time.Hour
)

type tokenClaims struct {
	UserID   int    `json:"user_id"`
	Username string `json:"username"`
	Type     string `json:"type"`
	jwt.RegisteredClaims
}

func GenerateAccessToken(userID int, username string) (string, error) {
	now := time.Now()

	return jwt.NewWithClaims(jwt.SigningMethodRS256, tokenClaims{
		UserID:   userID,
		Username: username,
		Type:     "access",
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(now.Add(AccessTokenTTL)),
			IssuedAt:  jwt.NewNumericDate(now),
		},
	}).SignedString(config.AppConfig.Secret)
}

func GenerateTokens(userID int, username string) (string, string, error) {
	now := time.Now()

	accessToken, err := GenerateAccessToken(userID, username)

	if err != nil {
		return "", "", err
	}

	refreshToken, err := jwt.NewWithClaims(jwt.SigningMethodRS256, tokenClaims{
		UserID:   userID,
		Username: username,
		Type:     "refresh",
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(now.Add(RefreshTokenTTL)),
			IssuedAt:  jwt.NewNumericDate(now),
		},
	}).SignedString(config.AppConfig.Secret)

	if err != nil {
		return "", "", err
	}

	return accessToken, refreshToken, nil
}

func ValidateRefreshToken(tokenString string) (int, error) {
	claims, err := validateToken(tokenString, "refresh")
	if err != nil {
		return 0, err
	}

	return claims.UserID, nil
}

func ValidateAccessToken(tokenString string) error {
	_, err := validateToken(tokenString, "access")
	return err
}

func validateToken(tokenString string, tokenType string) (*tokenClaims, error) {
	claims := &tokenClaims{}

	token, err := jwt.ParseWithClaims(tokenString, claims, func(token *jwt.Token) (any, error) {
		if _, ok := token.Method.(*jwt.SigningMethodRSA); !ok {
			return nil, errors.New("unexpected signing method")
		}

		return &config.AppConfig.Secret.PublicKey, nil
	})

	if err != nil {
		return nil, err
	}

	if !token.Valid || claims.Type != tokenType || claims.UserID == 0 || claims.Username == "" {
		return nil, errors.New("invalid token")
	}

	return claims, nil
}

func HashToken(token string) string {
	sum := sha256.Sum256([]byte(token))
	return hex.EncodeToString(sum[:])
}

func LogEndpointError(c *gin.Context, status int, message string, err error) {
	if err != nil {
		log.Printf(
			"auth endpoint error: method=%s path=%s client_ip=%s status=%d message=%q error=%v",
			c.Request.Method,
			c.FullPath(),
			c.ClientIP(),
			status,
			message,
			err,
		)
		return
	}

	log.Printf(
		"auth endpoint error: method=%s path=%s client_ip=%s status=%d message=%q",
		c.Request.Method,
		c.FullPath(),
		c.ClientIP(),
		status,
		message,
	)
}
