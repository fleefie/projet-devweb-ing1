# Documentation des endpoints de l'API

Ce fichier contient tous les endpoints de l'API, leurs retours, ainsi que les requêtes possibles.
Les endpoints possèdent également un niveau d'accès nécessaire, précisé selon 
[la documentation des utilisateurs](/doc/Utilisateurs#Permissions), ainsi que
parfois une connexion.

## Authentication

### /api/auth/login

Permet de se connecter pour pouvoir accéder au site.

| Propriété  | Description                             |
|------------|-----------------------------------------|
| Type       | POST                                    |
| Connecté ? | Non                                     |
| Permission | Tous                                    |

Requête:

```json
{
    "usernameOrEmail": "Nom d'utilisateur ou email de l'utilisateur",
    "password": "Mot de passe, non-haché"
}
```

### /api/auth/register

Permet de créer un nouvel utilisateur, pas encore activé par un administrateur.

| Propriété  | Description                             |
|------------|-----------------------------------------|
| Type       | POST                                    |
| Connecté ? | Non                                     |
| Permission | Tous                                    |

Requête:

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

| Propriété  | Description                             |
|------------|-----------------------------------------|
| Type       | POST                                    |
| Connecté ? | Oui                                     |
| Permission | ADMIN                                   |

Requête:

```json
{
    "username": "Nom d'utilisateur utilisé lors de la connexion"
}
```

### /api/users/delete-user

Permet à un administrateur de supprimer un utilisateur existant.

| Propriété  | Description                             |
|------------|-----------------------------------------|
| Type       | POST                                    |
| Connecté ? | Oui                                     |
| Permission | ADMIN                                   |

Requête:

```json
{
    "username": "Nom d'utilisateur à supprimer"
}
```

### /api/users/remove-role

Permet à un administrateur de retirer un rôle existant à un utilisateur.

| Propriété  | Description                             |
|------------|-----------------------------------------|
| Type       | POST                                    |
| Connecté ? | Oui                                     |
| Permission | ADMIN                                   |

Requête:

```json
{
    "username": "Nom d'utilisateur",
    "role": "Rôle à retirer"
}
```

### /api/users/add-role

Permet à un administrateur d’ajouter un rôle à un utilisateur.

| Propriété  | Description                             |
|------------|-----------------------------------------|
| Type       | POST                                    |
| Connecté ? | Oui                                     |
| Permission | ADMIN                                   |

Requête:

```json
{
    "username": "Nom d'utilisateur",
    "role": "Rôle à ajouter"
}
```

### /api/users/search-users

Permet à un utilisateur d’effectuer une recherche sur les utilisateurs par nom.

| Propriété  | Description                             |
|------------|-----------------------------------------|
| Type       | POST                                    |
| Connecté ? | Oui                                     |
| Permission | USER                                    |

Requête:

```json
{
    "username": "Critère de recherche par nom"
}
```

### /api/users/search-users-admin

Permet à un administrateur d’effectuer une recherche sur tous les utilisateurs, avec toutes les informations.

| Propriété  | Description                             |
|------------|-----------------------------------------|
| Type       | POST                                    |
| Connecté ? | Oui                                     |
| Permission | ADMIN                                   |

Requête:

```json
{
    "username": "Critère de recherche par nom"
}
```

### /api/users/edit-score

Permet à un administrateur de modifier le score d’un utilisateur.

| Propriété  | Description                             |
|------------|-----------------------------------------|
| Type       | POST                                    |
| Connecté ? | Oui                                     |
| Permission | ADMIN                                   |

Requête:

```json
{
    "username": "Nom d'utilisateur",
    "integer": "Valeur à ajouter ou à retrancher du score"
}
```

## Gestion des erreurs

### /api/error/400

Renvoie la page d'erreur pour les requêtes mal formées.

| Propriété  | Description                             |
|------------|-----------------------------------------|
| Type       | GET                                     |
| Connecté ? | Non                                     |
| Permission | Tous                                    |

Aucune requête JSON requise.

### /api/error/401

Renvoie la page d'erreur pour une tentative d'accès non autorisée.

| Propriété  | Description                             |
|------------|-----------------------------------------|
| Type       | GET                                     |
| Connecté ? | Non (peut provenir d'un manque de droits) |
| Permission | Tous                                    |

Aucune requête JSON requise.

### /api/error/403

Renvoie la page d'erreur pour un accès interdit.

| Propriété  | Description                             |
|------------|-----------------------------------------|
| Type       | GET                                     |
| Connecté ? | Oui (droit insuffisant)                 |
| Permission | Tous                                    |

Aucune requête JSON requise.

### /api/error/404

Renvoie la page d'erreur pour une ressource non trouvée.

| Propriété  | Description                             |
|------------|-----------------------------------------|
| Type       | GET                                     |
| Connecté ? | Non                                     |
| Permission | Tous                                    |

Aucune requête JSON requise.

### /api/error/500

Renvoie la page d'erreur pour une erreur interne du serveur.

| Propriété  | Description                             |
|------------|-----------------------------------------|
| Type       | GET                                     |
| Connecté ? | Non                                     |
| Permission | Tous                                    |

Aucune requête JSON requise.
