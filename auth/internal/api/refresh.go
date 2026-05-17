package api

import (
	"errors"
	"net/http"
	"time"

	"auth/internal/database"
	"auth/pkg/utils"

	"github.com/gin-gonic/gin"
	"github.com/jackc/pgx/v5"
)

type refreshRequest struct {
	RefreshToken string `json:"refresh_token"`
}

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

	accessToken, err := utils.GenerateAccessToken(user.UserID, user.Username)

	if err != nil {
		utils.LogEndpointError(c, http.StatusInternalServerError, "failed to generate access token during refresh", err)
		c.Status(http.StatusInternalServerError)
		return
	}

	c.JSON(http.StatusOK, refreshResponse{AccessToken: accessToken})
}

func refreshTokenFromRequest(c *gin.Context) (string, bool) {

	if refreshToken, err := c.Cookie("refresh_token"); err == nil && refreshToken != "" {
		return refreshToken, true
	}

	var req refreshRequest

	if err := c.ShouldBindJSON(&req); err != nil || req.RefreshToken == "" {
		return "", false
	}
	return req.RefreshToken, true
}
