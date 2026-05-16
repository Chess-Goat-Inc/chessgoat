package models

import "time"

type RefreshToken struct {
	RefreshTokenID   int
	UserID           int
	RefreshTokenHash string
	IsRevoked        bool
	ExpiresAt        time.Time
}
