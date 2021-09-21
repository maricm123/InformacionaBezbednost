-- Skripta koja se pokrece automatski pri pokretanju aplikacije
-- Baza koja se koristi je H2 in memory baza
-- Gasenjem aplikacije, brisu se svi podaci

-- Obe lozinke su hesovane pomocu BCrypt algoritma


INSERT INTO users(id, email, password, certificate, active, last_password_reset_date) VALUES (1, 'mare@gmail.com', 'mare', './data/mare@gmail.com.cer' ,true, '2017-10-01 21:58:58.508-07');
INSERT INTO users(id, email, password, certificate, active, last_password_reset_date) VALUES (2, 'niko@gmail.com', 'niko', './data/niko@gmail.com.cer' ,true, '2017-10-01 18:57:58.508-07');

INSERT INTO authority(id, name) VALUES (1, 'ROLE_REGULAR');
INSERT INTO authority(id, name) VALUES (2, 'ROLE_ADMIN');

INSERT INTO user_authority(user_id, authority_id) VALUES (1, 1);
INSERT INTO user_authority(user_id, authority_id) VALUES (2, 1);
INSERT INTO user_authority(user_id, authority_id) VALUES (2, 2);
