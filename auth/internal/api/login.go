package api

import (
	"errors"
	"net/http"
	"strings"
	"time"

	"auth/internal/database"
	"auth/internal/models"
	"auth/pkg/utils"

	"github.com/gin-gonic/gin"
	"github.com/jackc/pgx/v5"
	"golang.org/x/crypto/bcrypt"
)

func Login(c *gin.Context) {

	var req registerRequest
	
	if err := c.ShouldBindJSON(&req); err != nil {
		utils.LogEndpointError(c, http.StatusBadRequest, "invalid login request body", err)
		c.Status(http.StatusBadRequest)
		return
	}

	req.Username = strings.TrimSpace(req.Username)

	user, err := database.GetUserByUsername(c.Request.Context(), req.Username)
	if err != nil {
		if errors.Is(err, pgx.ErrNoRows) {
			utils.LogEndpointError(c, http.StatusUnauthorized, "login user not found", err)
			c.Status(http.StatusUnauthorized)
			return
		}

		utils.LogEndpointError(c, http.StatusInternalServerError, "failed to load user during login", err)
		c.Status(http.StatusInternalServerError)
		return
	}

	if err := bcrypt.CompareHashAndPassword([]byte(user.PasswordHash), []byte(req.Password)); err != nil {
		utils.LogEndpointError(c, http.StatusUnauthorized, "login password mismatch", err)
		c.Status(http.StatusUnauthorized)
		return
	}

	accessToken, refreshToken, err := utils.GenerateTokens(user.UserID, user.Username)
	if err != nil {
		utils.LogEndpointError(c, http.StatusInternalServerError, "failed to generate tokens during login", err)
		c.Status(http.StatusInternalServerError)
		return
	}

	_, err = database.InsertRefreshToken(c.Request.Context(), models.RefreshToken{
		UserID:           user.UserID,
		RefreshTokenHash: utils.HashToken(refreshToken),
		IsRevoked:        false,
		ExpiresAt:        time.Now().Add(utils.RefreshTokenTTL),
	})

	if err != nil {
		utils.LogEndpointError(c, http.StatusInternalServerError, "failed to persist refresh token during login", err)
		c.Status(http.StatusInternalServerError)
		return
	}

	c.SetCookie(
		"refresh_token",
		refreshToken,
		int(utils.RefreshTokenTTL.Seconds()),
		"/",
		"",
		false,
		true,
	)

	c.JSON(http.StatusOK, registerResponse{
		AccessToken:  accessToken,
		RefreshToken: refreshToken,
	})
}
