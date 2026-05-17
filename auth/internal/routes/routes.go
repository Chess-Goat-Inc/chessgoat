package routes

import (
	"auth/internal/api"

	"github.com/gin-gonic/gin"
)

func Setup() *gin.Engine {
	router := gin.Default()

	{
		group := router.Group("/auth")

		group.POST("/register", api.Register)
		group.POST("/login", api.Login)
		group.POST("/refresh", api.Refresh)
		group.POST("/validate", api.Validate)
	}

	return router
}
