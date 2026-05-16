package app

import (
	"auth/internal/config"
	"auth/internal/database"
	"auth/internal/routes"
)

func Run() {
	config.Load()
	router := routes.Setup()

	if err := database.Setup(); err != nil {
		panic(err)
	}

	router.Run("0.0.0.0:8080")
}
