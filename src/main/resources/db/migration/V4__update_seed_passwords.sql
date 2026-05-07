-- =====================================================
-- Migration: V4__update_seed_passwords.sql
-- Description: Atualiza hashes de senha seed para refletir senhas documentadas
-- Author: Levi Lunique
-- =====================================================

-- BCrypt hashes gerados com custo 10:
-- Admin@123 -> $2y$10$Ggt9gc1Ed0JJ.o0EZdMwQ.uQfZKc6.8e2rM9YREEs7T6inhBwlB6O
-- Dono@123  -> $2y$10$xbws/7enXtrkje8FA4ArXO6nFDh63XENQ1W0J5xlqiBwVxF94kNpC
-- Cliente@123 -> $2y$10$nY5.zll3XtnHq8PdyHWJAu3HGPPcDitbs133jeHCoIXH/cynjy5tu

UPDATE usuario
SET senha = '$2y$10$Ggt9gc1Ed0JJ.o0EZdMwQ.uQfZKc6.8e2rM9YREEs7T6inhBwlB6O'
WHERE email = 'admin@restauranthub.com';

UPDATE usuario
SET senha = '$2y$10$xbws/7enXtrkje8FA4ArXO6nFDh63XENQ1W0J5xlqiBwVxF94kNpC'
WHERE tipo_usuario = 'DONO_RESTAURANTE';

UPDATE usuario
SET senha = '$2y$10$nY5.zll3XtnHq8PdyHWJAu3HGPPcDitbs133jeHCoIXH/cynjy5tu'
WHERE tipo_usuario = 'CLIENTE';
