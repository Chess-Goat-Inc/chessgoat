package config

import (
	"crypto/rsa"
	"os"

	"github.com/golang-jwt/jwt/v5"
)


type Config struct {
	Database struct {
		Url     string
		User     string
		Password string
		Name     string
	}

	Secret *rsa.PrivateKey

}

var AppConfig Config

func Load() {
	AppConfig.Database.Url = os.Getenv("DB_URL")
	AppConfig.Database.User = os.Getenv("DB_USER")
	AppConfig.Database.Password = os.Getenv("DB_PASSWORD")
	AppConfig.Database.Name = os.Getenv("DB_NAME")

	privateKeyBytes, err := os.ReadFile(os.Getenv("SECRET_FILE"))
	if err != nil {
		panic(err)
	}

	private, err := jwt.ParseRSAPrivateKeyFromPEM(privateKeyBytes)
	
	if err != nil {
		panic(err)
	}
	
	AppConfig.Secret = private;

}