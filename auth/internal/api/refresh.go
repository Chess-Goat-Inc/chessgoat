package api

import (
	"errors"
	"net/http"
	"time"

	"auth/internal/database"
	"auth/internal/models"
	"auth/pkg/utils"

	"github.com/gin-gonic/gin"
	"github.com/jackc/pgx/v5"
)

type refreshResponse struct {
	AccessToken string `json:"access_token"`
}

func Refresh(c *gin.Context) {

	refreshToken, ok := refreshTokenFromRequest(c)

	if !ok {
		utils.LogEndpointError(c, http.StatusUnauthorized, "refresh token was not provided", nil)
		c.Status(http.StatusUnauthorized)
		return
	}

	userID, err := utils.ValidateRefreshToken(refreshToken)

	if err != nil {
		utils.LogEndpointError(c, http.StatusUnauthorized, "refresh token validation failed", err)
		c.Status(http.StatusUnauthorized)
		return
	}

	storedToken, err := database.GetRefreshTokenByHash(c.Request.Context(), utils.HashToken(refreshToken))

	if err != nil {
		if errors.Is(err, pgx.ErrNoRows) {
			utils.LogEndpointError(c, http.StatusUnauthorized, "refresh token was not found in database", err)
			c.Status(http.StatusUnauthorized)
			return
		}

		utils.LogEndpointError(c, http.StatusInternalServerError, "failed to load refresh token from database", err)
		c.Status(http.StatusInternalServerError)
		return
	}

	if storedToken.UserID != userID || storedToken.IsRevoked || time.Now().After(storedToken.ExpiresAt) {
		utils.LogEndpointError(c, http.StatusUnauthorized, "refresh token is revoked, expired, or belongs to a different user", nil)
		c.Status(http.StatusUnauthorized)
		return
	}

	user, err := database.GetUserByID(c.Request.Context(), userID)
	if err != nil {
		utils.LogEndpointError(c, http.StatusInternalServerError, "failed to load user during refresh", err)
		c.Status(http.StatusInternalServerError)
		return
	}

	accessToken, newRefreshToken, err := utils.GenerateTokens(user.UserID, user.Username)

	if err != nil {
		utils.LogEndpointError(c, http.StatusInternalServerError, "failed to generate tokens during refresh", err)
		c.Status(http.StatusInternalServerError)
		return
	}

	if err := database.RevokeRefreshTokenByID(c.Request.Context(), storedToken.RefreshTokenID); err != nil {
		utils.LogEndpointError(c, http.StatusInternalServerError, "failed to revoke old refresh token during refresh", err)
		c.Status(http.StatusInternalServerError)
		return
	}

	_, err = database.InsertRefreshToken(c.Request.Context(), models.RefreshToken{
		UserID:           user.UserID,
		RefreshTokenHash: utils.HashToken(newRefreshToken),
		IsRevoked:        false,
		ExpiresAt:        time.Now().Add(utils.RefreshTokenTTL),
	})

	if err != nil {
		utils.LogEndpointError(c, http.StatusInternalServerError, "failed to persist new refresh token during refresh", err)
		c.Status(http.StatusInternalServerError)
		return
	}

	c.SetCookie(
		"refresh_token",
		newRefreshToken,
		int(utils.RefreshTokenTTL.Seconds()),
		"/",
		"",
		false,
		true,
	)

	c.JSON(http.StatusOK, refreshResponse{AccessToken: accessToken})
}

func refreshTokenFromRequest(c *gin.Context) (string, bool) {

	if refreshToken, err := c.Cookie("refresh_token"); err == nil && refreshToken != "" {
		return refreshToken, true
	}

	return "", false
}
