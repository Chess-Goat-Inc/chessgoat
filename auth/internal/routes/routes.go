package routes

import (
	"auth/internal/api"
	"auth/internal/config"

	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
)

func Setup() *gin.Engine {
	router := gin.Default()

	corsConfig := cors.DefaultConfig()
	corsConfig.AllowOrigins = config.AppConfig.Routes.CORSAddresses
	corsConfig.AllowMethods = []string{"GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"}
	corsConfig.AllowHeaders = []string{"Origin", "Content-Type", "Authorization"}
	corsConfig.AllowCredentials = true
	router.Use(cors.New(corsConfig))

	{
		group := router.Group("/auth")

		group.POST("/register", api.Register)
		group.POST("/login", api.Login)
		group.POST("/refresh", api.Refresh)
		group.POST("/validate", api.Validate)
	}

	return router
}
