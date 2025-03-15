# Documentation des endpoints de l'API

Ce fichier contient tous les endpoints de l'API, leurs retours, ainsi que les requettes possibles.
Les endpoints possèdenet égalemet un niveau d'accès nécéssaire, précisé selon 
[la documentation des utilisateurs](/doc/Utilisateurs#Permissions), ainsi que
parfois une connexion.

## Authentication

### /api/auth/login

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

### /api/auth/register

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

## Administration

### /api/users/accept-user

Permet à un administrateur d'accepter l'inscription d'un utilisateur.

| Propriété  | Description |
|------------|-------------|
| Type       | POST        |
| Connecté ? | Oui         |
| Permission | ADMI?       |

Requette:

```json
{
    "username": "Nom d'utilisteur utilisé lors de la connexion"
}
```
