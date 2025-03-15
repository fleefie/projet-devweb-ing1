# Documentation des endpoints de l'API

Ce fichier contient tous les endpoints de l'API, leurs retours, ainsi que les requettes possibles.
Les endpoints possèdenet égalemet un niveau d'accès nécéssaire, précisé selon 
[la documentation des utilisateurs](/doc/Utilisateurs#Permissions), ainsi que
parfois une connexion.

## /api/auth/login

Permet de se connecter pour pouvoir accéder au site.

| Propriété  | Description |
|------------|-------------|
| Type       | POST        |
| Connecté ? | Non         |
| Permission | Tous        |

Requette:

```json
{
    "usernameOrEmail": "Nom d'utilisateur ou email de l'utilisateur",
    "password": "Mot de passe, non-haché"
}
```

Réponses:

- Succès:

```json
HTTP 200 OK
{
    "accessToken": "Token d'accès, valable pour 24h",
    "tokenType": "Bearer, toujours"
}
```

- Échec:

```
HTTP 401 UNAUTHORIZED
```

## /api/auth/register

Permet de créer un nouvel utilisateur, pas encore activé par un administrateur.

| Propriété  | Description |
|------------|-------------|
| Type       | POST        |
| Connecté ? | Non         |
| Permission | Tous        |

Requette:

```json
{
    "username": "Nom d'utilisateur, utilisé à la connexion",
    "email": "Email de l'utilisateur",
    "password": "Mot de passe",
    "passwordConfirm": "Confirmation du mot de passe",
    "name": "Nom de l'utilisateur"
}
```

Réponses:

- Succès:

> [!NOTE]
> Effet de bord: L'utilisateur a été créé.

```plaintext
HTTP 200 OK
```

- Échec:

> [!NOTE]
> Effet de bord: L'utilisateur n'a pas été créé.

```json
HTTP 400 BAD_REQUEST
{
    "error": ERREUR
}
```

Avec ERREUR membre de l'énumération suivante:

```java
    public enum UserAuthError {
        UserExists,
        UsernameNotAlphanum,
        NameNotAlphanumspace,
        EmailDoesNotMatchRegex,
        PasswordTooShort,
        PasswordOnlyAlphanum,
        PasswordNotAscii,
        PasswordsDoNotMatch,
        EmptyPassword,
        EmptyUsername,
        EmptyName
    }
```
