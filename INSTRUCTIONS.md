# Résumé des modifications apportées

## 1. Finalisation et sécurisation de l'authentification
- **Problème identifié :**
    - L'authentification était partiellement implémentée, mais il manquait une validation robuste des tokens JWT et une gestion sécurisée des mots de passe.
- **Solutions mises en place :**
    - Ajout d'un filtre `JwtAuthenticationFilter` pour valider les tokens JWT sur chaque requête.
    - Génération de tokens JWT sécurisés avec les informations `userId` et `clientId` via `JwtUtil`.
    - Sécurisation des mots de passe avec le hashage BCrypt dans `PasswordUtil`.

## 2. Création et sécurisation des routes pour voir son client, ses comptes et ses utilisateurs
- **Problèmes identifiés :**
    - Les routes pour récupérer les informations du client connecté et ses comptes n'étaient pas filtrées par `clientId`.
    - La route pour récupérer les utilisateurs (`GET /api/users`) exposait les mots de passe et n'était pas sécurisée.
- **Solutions mises en place :**
    - Ajout de la route `GET /api/clients/me` dans `ClientController` pour retourner uniquement les informations du client connecté, en filtrant par `clientId` extrait du JWT.
    - Sécurisation des routes `GET /api/accounts` et `GET /api/accounts/{id}` dans `AccountController` pour retourner uniquement les comptes appartenant au client connecté.
    - Création d'un DTO `UserResponse` pour exclure les mots de passe des réponses de la route `GET /api/users` et ajout d'un filtre par `clientId`.

## 3. Permettre à un client de se connecter pour voir ses données
- **Problème identifié :**
    - La connexion était possible, mais les données accessibles n'étaient pas correctement filtrées par client.
- **Solutions mises en place :**
    - Ajout de la validation des tokens JWT pour garantir que seules les données du client connecté sont accessibles.
    - Mise en place d'une gestion centralisée du `clientId` via `JwtUtil` pour extraire cette information du token.

## 4. Modification du nom d'un compte uniquement
- **Problème identifié :**
    - La route `PUT /api/accounts/{id}` permettait de modifier tous les champs d'un compte, y compris le montant.
- **Solutions mises en place :**
    - Création d'une route `PATCH /api/accounts/{id}` dans `AccountController` pour permettre uniquement la modification du champ `name` via un DTO `UpdateAccountNameRequest`.
    - Ajout d'une validation pour s'assurer que le compte appartient au client connecté avant toute modification.

---

## Résumé technique des modifications
- **Authentification :**
    - Ajout du filtre `JwtAuthenticationFilter`.
    - Génération et validation des tokens JWT via `JwtUtil`.
    - Sécurisation des mots de passe avec `PasswordUtil`.

- **Routes sécurisées :**
    - `GET /api/clients/me` : Retourne les informations du client connecté.
    - `GET /api/accounts` et `GET /api/accounts/{id}` : Retourne les comptes filtrés par `clientId`.
    - `GET /api/users` : Retourne les utilisateurs filtrés par `clientId` sans exposer les mots de passe.

- **Modification des comptes :**
    - `PATCH /api/accounts/{id}` : Permet uniquement de modifier le nom d'un compte.

---

## Technologies utilisées
- **Langage :** Java
- **Framework :** Spring Boot
- **Base de données :** SQLite
- **Sécurité :** Spring Security, JWT
- **Gestion des dépendances :** Maven