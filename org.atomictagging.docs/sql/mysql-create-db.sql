-- Create database
CREATE DATABASE atomictagging;

-- Crate user
GRANT ALL PRIVILEGES ON atomictagging.* TO 'at'@'localhost' IDENTIFIED BY 'atomictagging';
