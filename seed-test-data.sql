-- ============================================================
-- ASA - Données de test
-- ============================================================
-- Ce script insère des données de test complètes dans la base
-- PostgreSQL, en respectant le schéma final après toutes les
-- migrations Flyway.
-- ============================================================

-- 1. TRAVAILLEURS (workers)
INSERT INTO worker (code, name, email, fullname, address, city, nif, stat) VALUES
('W-P-2024-01', 'lita', 'lita.andria@hei.school', 'Lita Andria', 'Lot IVT 12 Bis', 'Antananarivo', '2000123456', 'STAT001'),
('W-2024-02', 'john', 'john.doe@hei.school', 'John Doe', 'Avenue de l''Indépendance 45', 'Antananarivo', '2000654321', 'STAT002'),
('W-2024-03', 'alice', 'alice.smith@hei.school', 'Alice Smith', 'Rue des Manguiers 78', 'Toamasina', '3000111222', 'STAT003'),
('W-2024-04', 'bob', 'bob.martin@hei.school', 'Bob Martin', 'Boulevard Tananarive 23', 'Antsirabe', '4000333444', 'STAT004'),
('W-2025-05', 'eve', 'eve.dubois@hei.school', 'Eve Dubois', 'Chemin des Rosiers 5', 'Fianarantsoa', '5000555666', 'STAT005'),
('W-2025-06', 'charlie', 'charlie.legrand@hei.school', 'Charlie Legrand', 'Place du Marché 8', 'Mahajanga', '6000777888', 'STAT006'),
('W-101', 'john2', 'john2@hei.school', 'John Second', 'Rue Principale 10', 'Toliara', '7000999000', 'STAT007'),
('W-TEST-2026', 'test', 'hei.amboara.2@gmail.com', 'Test Worker', '456 Rue de Test', 'Antananarivo', 'TESTNIF001', 'TESTSTAT001');

-- 2. PRODUITS
INSERT INTO product (code, name, description) VALUES
('pCode0', 'Développement Web', 'Développement de sites et applications web'),
('care', 'Congés & Maladies', 'Produit care : congés payés, maladies, événements'),
('training', 'Formation', 'Missions de formation interne'),
('maintenance', 'Maintenance', 'Maintenance des systèmes existants');

-- 3. NIVEAUX DE CONTRAT (contract_level)
INSERT INTO contract_level (code, type, monthly_pay, daily_pay) VALUES
('L5P-2026', 'partnerContractor', NULL, 50000),
('L4P-2026', 'partnerContractor', NULL, 100000),
('STD-L1', 'studentContractor', NULL, 25000),
('FTE-L3', 'fullTimeEmployee', 3000000, NULL),
('STD-L2', 'studentContractor', NULL, 35000);

-- 4. MISSIONS
INSERT INTO mission (code, title, description, max_duration_in_days, product_code) VALUES
('mission0-code', 'Développement module facturation', 'Développer le module de facturation en Spring Boot', 30, 'pCode0'),
('mission-web-01', 'Refonte UI Dashboard', 'Refonte de l''interface utilisateur du dashboard', 15, 'pCode0'),
('paidleave', 'Congés Payés', 'Congés annuels payés', 22, 'care'),
('sickleave', 'Maladie', 'Arrêt maladie', 5, 'care'),
('training-java', 'Formation Java 21', 'Session de formation aux nouveautés Java 21', 3, 'training'),
('bugfix-critique', 'Correction bug critique', 'Correction de bugs urgents sur la prod', 5, 'maintenance');

-- 5. EXÉCUTIONS DE MISSIONS (mission_execution)
INSERT INTO mission_execution (id, date, mission_code, worker_code, day_percentage, comment, creation_instant) VALUES
-- Lita Andria : janvier 2025
('me-2025-01-01', '2025-01-01', 'mission0-code', 'W-P-2024-01', 0.5, 'Développement facturation – matin', '2025-01-01 12:00:00+03'),
('me-2025-01-02', '2025-01-01', 'mission-web-01', 'W-P-2024-01', 0.5, 'Refonte dashboard – après-midi', '2025-01-01 12:00:00+03'),
('me-2025-01-03', '2025-01-02', 'mission0-code', 'W-P-2024-01', 1.0, 'Full day : développement facturation', '2025-01-02 12:00:00+03'),
('me-2025-01-04', '2025-01-03', 'paidleave', 'W-P-2024-01', 1.0, 'Congé payé', '2025-01-03 08:00:00+03'),
('me-2025-01-05', '2025-01-06', 'mission0-code', 'W-P-2024-01', 1.0, 'Développement facturation', '2025-01-06 12:00:00+03'),
('me-2025-01-06', '2025-01-07', 'paidleave', 'W-P-2024-01', 1.0, 'Congé payé', '2025-01-07 08:00:00+03'),
-- John Doe : janvier 2025
('me-2025-01-07', '2025-01-01', 'mission-web-01', 'W-2024-02', 1.0, 'Full day : refonte dashboard', '2025-01-01 12:00:00+03'),
('me-2025-01-08', '2025-01-02', 'bugfix-critique', 'W-2024-02', 0.7, 'Correction bug #1234', '2025-01-02 12:00:00+03'),
('me-2025-01-09', '2025-01-02', 'training-java', 'W-2024-02', 0.3, 'Formation Java 21', '2025-01-02 12:00:00+03'),
-- Alice Smith : janvier 2025
('me-2025-01-10', '2025-01-01', 'mission0-code', 'W-2024-03', 1.0, 'Full day : développement facturation', '2025-01-01 12:00:00+03'),
('me-2025-01-11', '2025-01-02', 'mission0-code', 'W-2024-03', 1.0, 'Full day : développement facturation', '2025-01-02 12:00:00+03'),
('me-2025-01-12', '2025-01-03', 'sickleave', 'W-2024-03', 1.0, 'Maladie', '2025-01-03 08:00:00+03'),
-- Bob Martin : janvier 2025
('me-2025-01-13', '2025-01-01', 'mission-web-01', 'W-2024-04', 0.5, 'Dashboard matin', '2025-01-01 12:00:00+03'),
('me-2025-01-14', '2025-01-01', 'training-java', 'W-2024-04', 0.5, 'Formation après-midi', '2025-01-01 12:00:00+03'),
-- Alice Smith : jours supplémentaires (pour atteindre 9 dates, contrat 12j → alerte seuil)
('me-2025-01-15', '2025-01-06', 'mission0-code', 'W-2024-03', 1.0, 'Développement facturation', '2025-01-06 12:00:00+03'),
('me-2025-01-16', '2025-01-07', 'mission0-code', 'W-2024-03', 1.0, 'Développement facturation', '2025-01-07 12:00:00+03'),
('me-2025-01-17', '2025-01-08', 'mission-web-01', 'W-2024-03', 1.0, 'Refonte dashboard', '2025-01-08 12:00:00+03'),
('me-2025-01-18', '2025-01-09', 'mission0-code', 'W-2024-03', 1.0, 'Développement facturation', '2025-01-09 12:00:00+03'),
('me-2025-01-19', '2025-01-10', 'mission0-code', 'W-2024-03', 1.0, 'Développement facturation', '2025-01-10 12:00:00+03'),
('me-2025-01-20', '2025-01-13', 'bugfix-critique', 'W-2024-03', 1.0, 'Correction bug urgente', '2025-01-13 12:00:00+03'),
-- Bob Martin : jours supplémentaires (pour atteindre 9 dates, contrat 12j → alerte seuil)
('me-2025-01-21', '2025-01-02', 'mission0-code', 'W-2024-04', 1.0, 'Développement facturation', '2025-01-02 12:00:00+03'),
('me-2025-01-22', '2025-01-03', 'mission0-code', 'W-2024-04', 1.0, 'Développement facturation', '2025-01-03 12:00:00+03'),
('me-2025-01-23', '2025-01-06', 'mission-web-01', 'W-2024-04', 1.0, 'Refonte dashboard', '2025-01-06 12:00:00+03'),
('me-2025-01-24', '2025-01-07', 'mission0-code', 'W-2024-04', 1.0, 'Développement facturation', '2025-01-07 12:00:00+03'),
('me-2025-01-25', '2025-01-08', 'bugfix-critique', 'W-2024-04', 1.0, 'Correction bug critique', '2025-01-08 12:00:00+03'),
('me-2025-01-26', '2025-01-09', 'mission0-code', 'W-2024-04', 1.0, 'Développement facturation', '2025-01-09 12:00:00+03'),
('me-2025-01-27', '2025-01-10', 'training-java', 'W-2024-04', 1.0, 'Formation Java 21', '2025-01-10 12:00:00+03'),
('me-2025-01-28', '2025-01-13', 'mission0-code', 'W-2024-04', 1.0, 'Développement facturation', '2025-01-13 12:00:00+03'),
-- Lita Andria : février 2025
('me-2025-02-01', '2025-02-03', 'mission0-code', 'W-P-2024-01', 1.0, 'Développement facturation', '2025-02-03 12:00:00+03'),
('me-2025-02-02', '2025-02-04', 'mission0-code', 'W-P-2024-01', 1.0, 'Développement facturation', '2025-02-04 12:00:00+03'),
('me-2025-02-03', '2025-02-05', 'paidleave', 'W-P-2024-01', 1.0, 'Congé payé', '2025-02-05 08:00:00+03'),
('me-2025-02-04', '2025-02-06', 'mission0-code', 'W-P-2024-01', 1.0, 'Développement facturation', '2025-02-06 12:00:00+03'),
-- Lita Andria : juillet 2025
('me-2025-07-01', '2025-07-01', 'mission-web-01', 'W-P-2024-01', 0.6, 'Refonte dashboard', '2025-07-01 12:00:00+03'),
('me-2025-07-02', '2025-07-01', 'training-java', 'W-P-2024-01', 0.4, 'Formation Java', '2025-07-01 12:00:00+03'),
('me-2025-07-03', '2025-07-02', 'mission0-code', 'W-P-2024-01', 1.0, 'Développement facturation', '2025-07-02 12:00:00+03'),
-- Données de test originales conservées
('me_id_0', '2024-01-01', 'mission0-code', 'W-P-2024-01', 0.8, 'comment0', '2024-01-01 12:00:00+03'),
('me_id_1', '2024-01-01', 'mission0-code', 'W-P-2024-01', 0.8, 'comment0', '2024-01-01 12:00:00+03'),
('me_id_2', '2024-01-01', 'mission0-code', 'W-P-2024-01', 0.2, 'comment1', '2024-01-01 12:00:00+03'),
('me_id_3', '2024-07-01', 'mission0-code', 'W-P-2024-01', 0.5, 'comment3', '2024-07-01 12:00:00+03'),
('me_id_4', '2024-07-01', 'mission0-code', 'W-P-2024-01', 0.5, 'comment4', '2024-07-01 12:00:00+03'),
-- Test worker : 79 jours travaillés sur 90 (contrat 90j, 79 déjà utilisés)
('me-test-2026-01-01', '2026-01-01', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 1', '2026-01-01 08:00:00+03'),
('me-test-2026-01-02', '2026-01-02', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 2', '2026-01-02 08:00:00+03'),
('me-test-2026-01-03', '2026-01-03', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 3', '2026-01-03 08:00:00+03'),
('me-test-2026-01-04', '2026-01-04', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 4', '2026-01-04 08:00:00+03'),
('me-test-2026-01-05', '2026-01-05', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 5', '2026-01-05 08:00:00+03'),
('me-test-2026-01-06', '2026-01-06', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 6', '2026-01-06 08:00:00+03'),
('me-test-2026-01-07', '2026-01-07', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 7', '2026-01-07 08:00:00+03'),
('me-test-2026-01-08', '2026-01-08', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 8', '2026-01-08 08:00:00+03'),
('me-test-2026-01-09', '2026-01-09', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 9', '2026-01-09 08:00:00+03'),
('me-test-2026-01-10', '2026-01-10', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 10', '2026-01-10 08:00:00+03'),
('me-test-2026-01-11', '2026-01-11', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 11', '2026-01-11 08:00:00+03'),
('me-test-2026-01-12', '2026-01-12', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 12', '2026-01-12 08:00:00+03'),
('me-test-2026-01-13', '2026-01-13', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 13', '2026-01-13 08:00:00+03'),
('me-test-2026-01-14', '2026-01-14', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 14', '2026-01-14 08:00:00+03'),
('me-test-2026-01-15', '2026-01-15', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 15', '2026-01-15 08:00:00+03'),
('me-test-2026-01-16', '2026-01-16', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 16', '2026-01-16 08:00:00+03'),
('me-test-2026-01-17', '2026-01-17', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 17', '2026-01-17 08:00:00+03'),
('me-test-2026-01-18', '2026-01-18', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 18', '2026-01-18 08:00:00+03'),
('me-test-2026-01-19', '2026-01-19', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 19', '2026-01-19 08:00:00+03'),
('me-test-2026-01-20', '2026-01-20', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 20', '2026-01-20 08:00:00+03'),
('me-test-2026-01-21', '2026-01-21', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 21', '2026-01-21 08:00:00+03'),
('me-test-2026-01-22', '2026-01-22', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 22', '2026-01-22 08:00:00+03'),
('me-test-2026-01-23', '2026-01-23', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 23', '2026-01-23 08:00:00+03'),
('me-test-2026-01-24', '2026-01-24', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 24', '2026-01-24 08:00:00+03'),
('me-test-2026-01-25', '2026-01-25', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 25', '2026-01-25 08:00:00+03'),
('me-test-2026-01-26', '2026-01-26', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 26', '2026-01-26 08:00:00+03'),
('me-test-2026-01-27', '2026-01-27', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 27', '2026-01-27 08:00:00+03'),
('me-test-2026-01-28', '2026-01-28', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 28', '2026-01-28 08:00:00+03'),
('me-test-2026-01-29', '2026-01-29', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 29', '2026-01-29 08:00:00+03'),
('me-test-2026-01-30', '2026-01-30', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 30', '2026-01-30 08:00:00+03'),
('me-test-2026-01-31', '2026-01-31', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 31', '2026-01-31 08:00:00+03'),
('me-test-2026-02-01', '2026-02-01', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 32', '2026-02-01 08:00:00+03'),
('me-test-2026-02-02', '2026-02-02', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 33', '2026-02-02 08:00:00+03'),
('me-test-2026-02-03', '2026-02-03', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 34', '2026-02-03 08:00:00+03'),
('me-test-2026-02-04', '2026-02-04', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 35', '2026-02-04 08:00:00+03'),
('me-test-2026-02-05', '2026-02-05', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 36', '2026-02-05 08:00:00+03'),
('me-test-2026-02-06', '2026-02-06', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 37', '2026-02-06 08:00:00+03'),
('me-test-2026-02-07', '2026-02-07', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 38', '2026-02-07 08:00:00+03'),
('me-test-2026-02-08', '2026-02-08', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 39', '2026-02-08 08:00:00+03'),
('me-test-2026-02-09', '2026-02-09', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 40', '2026-02-09 08:00:00+03'),
('me-test-2026-02-10', '2026-02-10', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 41', '2026-02-10 08:00:00+03'),
('me-test-2026-02-11', '2026-02-11', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 42', '2026-02-11 08:00:00+03'),
('me-test-2026-02-12', '2026-02-12', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 43', '2026-02-12 08:00:00+03'),
('me-test-2026-02-13', '2026-02-13', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 44', '2026-02-13 08:00:00+03'),
('me-test-2026-02-14', '2026-02-14', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 45', '2026-02-14 08:00:00+03'),
('me-test-2026-02-15', '2026-02-15', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 46', '2026-02-15 08:00:00+03'),
('me-test-2026-02-16', '2026-02-16', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 47', '2026-02-16 08:00:00+03'),
('me-test-2026-02-17', '2026-02-17', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 48', '2026-02-17 08:00:00+03'),
('me-test-2026-02-18', '2026-02-18', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 49', '2026-02-18 08:00:00+03'),
('me-test-2026-02-19', '2026-02-19', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 50', '2026-02-19 08:00:00+03'),
('me-test-2026-02-20', '2026-02-20', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 51', '2026-02-20 08:00:00+03'),
('me-test-2026-02-21', '2026-02-21', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 52', '2026-02-21 08:00:00+03'),
('me-test-2026-02-22', '2026-02-22', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 53', '2026-02-22 08:00:00+03'),
('me-test-2026-02-23', '2026-02-23', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 54', '2026-02-23 08:00:00+03'),
('me-test-2026-02-24', '2026-02-24', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 55', '2026-02-24 08:00:00+03'),
('me-test-2026-02-25', '2026-02-25', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 56', '2026-02-25 08:00:00+03'),
('me-test-2026-02-26', '2026-02-26', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 57', '2026-02-26 08:00:00+03'),
('me-test-2026-02-27', '2026-02-27', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 58', '2026-02-27 08:00:00+03'),
('me-test-2026-02-28', '2026-02-28', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 59', '2026-02-28 08:00:00+03'),
('me-test-2026-03-01', '2026-03-01', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 60', '2026-03-01 08:00:00+03'),
('me-test-2026-03-02', '2026-03-02', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 61', '2026-03-02 08:00:00+03'),
('me-test-2026-03-03', '2026-03-03', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 62', '2026-03-03 08:00:00+03'),
('me-test-2026-03-04', '2026-03-04', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 63', '2026-03-04 08:00:00+03'),
('me-test-2026-03-05', '2026-03-05', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 64', '2026-03-05 08:00:00+03'),
('me-test-2026-03-06', '2026-03-06', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 65', '2026-03-06 08:00:00+03'),
('me-test-2026-03-07', '2026-03-07', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 66', '2026-03-07 08:00:00+03'),
('me-test-2026-03-08', '2026-03-08', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 67', '2026-03-08 08:00:00+03'),
('me-test-2026-03-09', '2026-03-09', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 68', '2026-03-09 08:00:00+03'),
('me-test-2026-03-10', '2026-03-10', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 69', '2026-03-10 08:00:00+03'),
('me-test-2026-03-11', '2026-03-11', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 70', '2026-03-11 08:00:00+03'),
('me-test-2026-03-12', '2026-03-12', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 71', '2026-03-12 08:00:00+03'),
('me-test-2026-03-13', '2026-03-13', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 72', '2026-03-13 08:00:00+03'),
('me-test-2026-03-14', '2026-03-14', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 73', '2026-03-14 08:00:00+03'),
('me-test-2026-03-15', '2026-03-15', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 74', '2026-03-15 08:00:00+03'),
('me-test-2026-03-16', '2026-03-16', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 75', '2026-03-16 08:00:00+03'),
('me-test-2026-03-17', '2026-03-17', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 76', '2026-03-17 08:00:00+03'),
('me-test-2026-03-18', '2026-03-18', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 77', '2026-03-18 08:00:00+03'),
('me-test-2026-03-19', '2026-03-19', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 78', '2026-03-19 08:00:00+03'),
('me-test-2026-03-20', '2026-03-20', 'mission0-code', 'W-TEST-2026', 1.0, 'Travail jour 79', '2026-03-20 08:00:00+03');

-- 6. CONTRATS
INSERT INTO contract (id, worker_code, level, entrance_instant, end_instant, job_title, duration_in_days, contract_bucket_key, company) VALUES
('some-id', 'W-P-2024-01', 'L4P-2026', '2025-01-01 08:00:00.000000', NULL, 'Développeur Full Stack', 80, 'contracts/W-P-2024-01/convention.pdf', 'HEI School'),
('contract-02', 'W-2024-02', 'L5P-2026', '2025-01-01 08:00:00.000000', NULL, 'Lead Développeur', 90, 'contracts/W-2024-02/convention.pdf', 'HEI School'),
('contract-03', 'W-2024-03', 'STD-L1', '2025-01-01 08:00:00.000000', NULL, 'Stagiaire Développeur', 12, 'contracts/W-2024-03/convention.pdf', 'HEI School'),
('contract-04', 'W-2024-04', 'STD-L2', '2025-01-01 08:00:00.000000', NULL, 'Junior Développeur', 12, 'contracts/W-2024-04/convention.pdf', 'HEI School'),
('contract-05', 'W-2025-05', 'FTE-L3', '2025-03-01 08:00:00.000000', NULL, 'Ingénieur Full Stack', 365, 'contracts/W-2025-05/convention.pdf', 'HEI School'),
('contract-06', 'W-2025-06', 'L4P-2026', '2025-06-01 08:00:00.000000', NULL, 'Développeur Backend', 80, 'contracts/W-2025-06/convention.pdf', 'HEI School'),
('1234', 'W-101', 'L4P-2026', '2024-01-01 08:00:00.000000', '2024-06-01 08:00:00.000000', 'Ancien Développeur', 80, 'contracts/W-101/convention.pdf', 'HEI School'),
('contract-test', 'W-TEST-2026', 'STD-L2', '2026-01-01 08:00:00.000000', NULL, 'Test Developer', 90, 'contracts/W-TEST-2026/convention.pdf', 'HEI School');

-- 7. FACTURES (invoices)
-- L'auto-increment est géré par le trigger set_autoincrement
-- On laisse autoincrement NULL pour que le trigger le calcule
INSERT INTO invoice (id, worker_code, year_month) VALUES
('inv-2025-01-01', 'W-P-2024-01', '2025-01'),
('inv-2025-02-01', 'W-P-2024-01', '2025-02'),
('inv-2025-03-01', 'W-P-2024-01', '2025-03'),
('inv-2025-04-01', 'W-P-2024-01', '2025-04'),
('inv-2025-05-01', 'W-P-2024-01', '2025-05'),
('inv-2025-01-02', 'W-2024-02', '2025-01'),
('inv-2025-02-02', 'W-2024-02', '2025-02'),
('inv-2025-01-03', 'W-2024-03', '2025-01'),
('inv-2025-01-04', 'W-2024-04', '2025-01'),
('inv-2025-03-05', 'W-2025-05', '2025-03'),
('inv-2025-04-05', 'W-2025-05', '2025-04'),
('inv-2025-06-06', 'W-2025-06', '2025-06'),
-- Factures de test originales
('id1', 'W-P-2024-01', '2025-01'),
('id2', 'W-P-2024-01', '2025-02'),
('id3', 'W-P-2024-01', '2025-03'),
('id4', 'W-P-2024-01', '2025-04');

-- 8. COMPTES BANCAIRES (RIB)
INSERT INTO rib_mg (id, worker_code, banque, agence, compte, cle, iban) VALUES
('rib-01', 'W-P-2024-01', 'BNI Madagascar', 'AG-001', '12345678901', '12', 'MG4600001234567890112'),
('rib-02', 'W-2024-02', 'BOA Madagascar', 'AG-002', '23456789012', '34', 'MG4611112345678901234'),
('rib-03', 'W-2024-03', 'SBM Madagascar', 'AG-003', '34567890123', '56', 'MG4622223456789012356'),
('rib-04', 'W-2024-04', 'BNI Madagascar', 'AG-004', '45678901234', '78', 'MG4633334567890123478'),
('rib-05', 'W-2025-05', 'BOA Madagascar', 'AG-005', '56789012345', '90', 'MG4644445678901234590'),
('rib-06', 'W-2025-06', 'SBM Madagascar', 'AG-006', '67890123456', '11', 'MG4655556789012345611');

-- ============================================================
-- Résumé des données insérées
-- ============================================================
-- Workers    : 7 (dont 2 avec contrats terminés)
-- Produits   : 4 (dont 1 care)
-- Niveaux    : 5 (3 studentContractor, 1 partnerContractor, 1 fullTimeEmployee)
-- Missions   : 6 (dont 2 care: paidleave, sickleave)
-- Exécutions : 27 (dont 5 historiques de 2024)
-- Contrats   : 7 (dont 1 terminé)
-- Factures   : 16 (dont 4 historiques)
-- RIB        : 6
-- ============================================================
