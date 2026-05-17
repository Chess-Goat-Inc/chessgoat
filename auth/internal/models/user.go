package models

type User struct {
	UserID       int
	Username     string
	PasswordHash string
	Score        int
}
