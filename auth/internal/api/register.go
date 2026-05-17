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
	"github.com/jackc/pgx/v5/pgconn"
	"golang.org/x/crypto/bcrypt"
)

type registerRequest struct {
	Username string `json:"username"`
	Password string `json:"password"`
}

type registerResponse struct {
	AccessToken  string `json:"access_token"`
	RefreshToken string `json:"refresh_token"`
}

func Register(c *gin.Context) {

	var req registerRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		utils.LogEndpointError(c, http.StatusBadRequest, "invalid register request body", err)
		c.Status(http.StatusBadRequest)
		return
	}

	req.Username = strings.TrimSpace(req.Username)

	if _, err := database.GetUserByUsername(c.Request.Context(), req.Username); err == nil {
		utils.LogEndpointError(c, http.StatusConflict, "register username already exists", nil)
		c.Status(http.StatusConflict)
		return
	} else if !errors.Is(err, pgx.ErrNoRows) {
		utils.LogEndpointError(c, http.StatusInternalServerError, "failed to check existing user during register", err)
		c.Status(http.StatusInternalServerError)
		return
	}

	passwordHash, err := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)
	if err != nil {
		utils.LogEndpointError(c, http.StatusInternalServerError, "failed to hash password during register", err)
		c.Status(http.StatusInternalServerError)
		return
	}

	user, err := database.InsertUser(c.Request.Context(), models.User{
		Username:     req.Username,
		PasswordHash: string(passwordHash),
		Score:        1000,
	})

	if err != nil {
		if isUniqueViolation(err) {
			utils.LogEndpointError(c, http.StatusConflict, "register username unique constraint violation", err)
			c.Status(http.StatusConflict)
			return
		}

		utils.LogEndpointError(c, http.StatusInternalServerError, "failed to insert user during register", err)
		c.Status(http.StatusInternalServerError)
		return
	}

	accessToken, refreshToken, err := utils.GenerateTokens(user.UserID, user.Username)

	if err != nil {
		utils.LogEndpointError(c, http.StatusInternalServerError, "failed to generate tokens during register", err)
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
		utils.LogEndpointError(c, http.StatusInternalServerError, "failed to persist refresh token during register", err)
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

	c.JSON(http.StatusCreated, registerResponse{
		AccessToken:  accessToken,
		RefreshToken: refreshToken,
	})
}


func isUniqueViolation(err error) bool {
	var pgErr *pgconn.PgError
	return errors.As(err, &pgErr) && pgErr.Code == "23505"
}
