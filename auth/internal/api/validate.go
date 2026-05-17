package api

import (
	"net/http"
	"strings"

	"auth/pkg/utils"

	"github.com/gin-gonic/gin"
)

type validateRequest struct {
	AccessToken string `json:"access_token"`
}

type validateResponse struct {
	Valid bool `json:"valid"`
}

func Validate(c *gin.Context) {
	accessToken, ok := accessTokenFromRequest(c)
	if !ok {
		utils.LogEndpointError(c, http.StatusOK, "access token was not provided for validation", nil)
		c.JSON(http.StatusOK, validateResponse{Valid: false})
		return
	}

	if err := utils.ValidateAccessToken(accessToken); err != nil {
		utils.LogEndpointError(c, http.StatusOK, "access token validation failed", err)
		c.JSON(http.StatusOK, validateResponse{Valid: false})
		return
	}

	c.JSON(http.StatusOK, validateResponse{Valid: true})
}

func accessTokenFromRequest(c *gin.Context) (string, bool) {
	authHeader := c.GetHeader("Authorization")
	if strings.HasPrefix(authHeader, "Bearer ") {
		token := strings.TrimSpace(strings.TrimPrefix(authHeader, "Bearer "))
		if token != "" {
			return token, true
		}
	}

	var req validateRequest
	if err := c.ShouldBindJSON(&req); err != nil || req.AccessToken == "" {
		return "", false
	}

	return req.AccessToken, true
}
