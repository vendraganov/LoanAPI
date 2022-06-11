INSERT INTO authorities (id, role) VALUES ('a8120a66-3bc8-465c-80ad-0f640fd8db7e', 'ADMIN');
INSERT INTO authorities (id, role) VALUES ('843b9010-651d-461e-93ed-afe6cbedd41c', 'USER');
INSERT INTO accounts (id, email, password, fk_authority) VALUES ('cb4803b2-eaf1-463b-bdda-c4bf1c86c21e', 'admin@bg', '$2a$10$UBfx4TZnp6jUwtnwGAmYteaQVKDea8ct2OdJxC4MD3jQK3jP.KVnq', 'a8120a66-3bc8-465c-80ad-0f640fd8db7e');
INSERT INTO accounts (id, email, password, fk_authority) VALUES ('5dc370fd-ab2a-4153-950a-b6db87094ee0', 'user@bg', '$2a$10$MVhhGWmGFjwuqjfWmg.MaeFCX7AQoDxyqDzPvc76hM3hhmloXwjRu', '843b9010-651d-461e-93ed-afe6cbedd41c');
INSERT INTO users (id, name, fk_account) VALUES ('801b218f-20d4-4721-98c2-3b02f871af91', 'Zhorzh Raychev', 'cb4803b2-eaf1-463b-bdda-c4bf1c86c21e');
INSERT INTO users (id, name, fk_account) VALUES ('34ddef44-4feb-4ed8-9c02-f6929ae82ebf', 'Ventsislav Draganov', '5dc370fd-ab2a-4153-950a-b6db87094ee0');

INSERT INTO loan_types (id, type, months, amount, interest) VALUES ('54411b13-1e8e-4702-ad41-7341e97302b4', 'Personal Loan', 36, 10000, 7.5);
INSERT INTO loan_types (id, type, months, amount, interest) VALUES ('2e01d2b8-308b-42af-a9d7-f04166df77a2', 'Personal Loan', 60, 20000, 6.5);
INSERT INTO loan_types (id, type, months, amount, interest) VALUES ('89d5dae7-9f22-49e6-8353-f56901b4ebcb', 'Mortgage Loan', 120, 100000, 3.5);
INSERT INTO loan_types (id, type, months, amount, interest) VALUES ('b9d30cab-f59d-4b8a-9966-1aae1d63c87b', 'Mortgage Loan', 240, 200000, 3.0);