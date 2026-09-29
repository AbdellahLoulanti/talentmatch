CREATE TABLE tenant (
    id          UUID PRIMARY KEY,
    name        VARCHAR(150) NOT NULL,
    slug        VARCHAR(100) NOT NULL UNIQUE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE app_user (
    id             UUID PRIMARY KEY,
    tenant_id      UUID         NOT NULL REFERENCES tenant (id),
    email          VARCHAR(255) NOT NULL UNIQUE,
    password_hash  VARCHAR(100) NOT NULL,
    role           VARCHAR(30)  NOT NULL CHECK (role IN ('TENANT_ADMIN', 'RECRUITER')),
    active         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_app_user_tenant ON app_user (tenant_id);
