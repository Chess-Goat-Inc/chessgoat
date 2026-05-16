package database

import (
	"context"
	"fmt"
	"strings"
	"time"

	"auth/internal/config"
	"auth/internal/models"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
)

var DB *pgxpool.Pool

func Setup() error {
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	dbURL := normalizeDatabaseURL(config.AppConfig.Database.Url)
	pool, err := pgxpool.New(ctx, dbURL)
	if err != nil {
		return fmt.Errorf("create postgres pool: %w", err)
	}

	if err := pool.Ping(ctx); err != nil {
		pool.Close()
		return fmt.Errorf("ping postgres: %w", err)
	}

	DB = pool
	return nil
}

func InsertUser(ctx context.Context, user models.User) (models.User, error) {
	query := `
		INSERT INTO users (username, password_hash, score)
		VALUES ($1, $2, $3)
		RETURNING user_id, username, password_hash, score
	`

	var created models.User
	if err := DB.QueryRow(ctx, query, user.Username, user.PasswordHash, user.Score).Scan(
		&created.UserID,
		&created.Username,
		&created.PasswordHash,
		&created.Score,
	); err != nil {
		return models.User{}, fmt.Errorf("insert user: %w", err)
	}

	return created, nil
}

func GetUserByID(ctx context.Context, userID int) (models.User, error) {
	query := `
		SELECT user_id, username, password_hash, score
		FROM users
		WHERE user_id = $1
	`

	return scanUser(DB.QueryRow(ctx, query, userID))
}

func GetUserByUsername(ctx context.Context, username string) (models.User, error) {
	query := `
		SELECT user_id, username, password_hash, score
		FROM users
		WHERE username = $1
	`

	return scanUser(DB.QueryRow(ctx, query, username))
}

func InsertRefreshToken(ctx context.Context, refreshToken models.RefreshToken) (models.RefreshToken, error) {
	query := `
		INSERT INTO refresh_tokens (user_id, refresh_token_hash, is_revoked, expires_at)
		VALUES ($1, $2, $3, $4)
		RETURNING refresh_token_id, user_id, refresh_token_hash, is_revoked, expires_at
	`

	var created models.RefreshToken
	if err := DB.QueryRow(
		ctx,
		query,
		refreshToken.UserID,
		refreshToken.RefreshTokenHash,
		refreshToken.IsRevoked,
		refreshToken.ExpiresAt,
	).Scan(
		&created.RefreshTokenID,
		&created.UserID,
		&created.RefreshTokenHash,
		&created.IsRevoked,
		&created.ExpiresAt,
	); err != nil {
		return models.RefreshToken{}, fmt.Errorf("insert refresh token: %w", err)
	}

	return created, nil
}

func GetRefreshTokenByID(ctx context.Context, refreshTokenID int) (models.RefreshToken, error) {
	query := `
		SELECT refresh_token_id, user_id, refresh_token_hash, is_revoked, expires_at
		FROM refresh_tokens
		WHERE refresh_token_id = $1
	`

	return scanRefreshToken(DB.QueryRow(ctx, query, refreshTokenID))
}

func GetRefreshTokenByHash(ctx context.Context, refreshTokenHash string) (models.RefreshToken, error) {
	query := `
		SELECT refresh_token_id, user_id, refresh_token_hash, is_revoked, expires_at
		FROM refresh_tokens
		WHERE refresh_token_hash = $1
	`

	return scanRefreshToken(DB.QueryRow(ctx, query, refreshTokenHash))
}

func GetRefreshTokensByUserID(ctx context.Context, userID int) ([]models.RefreshToken, error) {
	query := `
		SELECT refresh_token_id, user_id, refresh_token_hash, is_revoked, expires_at
		FROM refresh_tokens
		WHERE user_id = $1
		ORDER BY refresh_token_id
	`

	rows, err := DB.Query(ctx, query, userID)
	if err != nil {
		return nil, fmt.Errorf("get refresh tokens by user id: %w", err)
	}
	defer rows.Close()

	refreshTokens := make([]models.RefreshToken, 0)
	for rows.Next() {
		var refreshToken models.RefreshToken
		if err := rows.Scan(
			&refreshToken.RefreshTokenID,
			&refreshToken.UserID,
			&refreshToken.RefreshTokenHash,
			&refreshToken.IsRevoked,
			&refreshToken.ExpiresAt,
		); err != nil {
			return nil, fmt.Errorf("scan refresh token: %w", err)
		}

		refreshTokens = append(refreshTokens, refreshToken)
	}

	if err := rows.Err(); err != nil {
		return nil, fmt.Errorf("iterate refresh tokens: %w", err)
	}

	return refreshTokens, nil
}

func scanUser(row pgx.Row) (models.User, error) {
	var user models.User
	if err := row.Scan(&user.UserID, &user.Username, &user.PasswordHash, &user.Score); err != nil {
		return models.User{}, fmt.Errorf("scan user: %w", err)
	}

	return user, nil
}

func scanRefreshToken(row pgx.Row) (models.RefreshToken, error) {
	var refreshToken models.RefreshToken
	if err := row.Scan(
		&refreshToken.RefreshTokenID,
		&refreshToken.UserID,
		&refreshToken.RefreshTokenHash,
		&refreshToken.IsRevoked,
		&refreshToken.ExpiresAt,
	); err != nil {
		return models.RefreshToken{}, fmt.Errorf("scan refresh token: %w", err)
	}

	return refreshToken, nil
}

func normalizeDatabaseURL(dbURL string) string {
	return strings.Replace(dbURL, "postgresql+asyncpg://", "postgresql://", 1)
}
