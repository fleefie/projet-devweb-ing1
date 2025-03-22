# Documentation des endpoints de l'API, avec leurs réponses

Ce fichier recense les endpoints de l'API, leurs requêtes et leurs réponses possibles. Les niveaux d'accès nécessaires sont décrits selon 
[la documentation des utilisateurs](/doc/Utilisateurs#Permissions).

Si une requête contient un champ non valide (ex: champ nul, mot de passe trop court...)
une erreur de type 400 sera renvoyée, de la forme:

```JSON
{
    "fieldErrors": {
        "option": "erreur spécifique",
        ...
    },
    "message": "The request contains invalid data"
}
```

Par exemple:

```JSON
{
    // Register
    "fieldError": {
        "password": "password too short",
        "username": "username must not be null"
    },
    "message": "The request contains invalid data"
}
```

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

Réponses:
- 200 OK: Retourne un objet contenant le token JWT, par exemple:  
  ```json
  {
      "accessToken": "<token>",
      "tokenType": "Bearer"
  }
  ```
- 401 Unauthorized: Retourne un objet d'erreur sous la forme:  
  ```json
  {
      "message": "Description de l'erreur"
  }
  ```

---

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

Réponses:
- 200 OK:  
  ```json
  {
      "message": "User registered successfully"
  }
  ```
- 400 Bad Request:  
  ```json
  {
      "message": "Passwords do not match"
  }
  ```
  ou 
  ```json
  {
      "message": "Un message d'erreur spécifique (ex: compte déjà existant)"
  }
  ```

---

## Administration

### /api/users/accept-user

Permet à un administrateur d'accepter l'inscription d'un utilisateur (activation du compte).

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

Réponses:
- 200 OK:  
  ```json
  {
      "message": "User accepted successfully"
  }
  ```
- 400 Bad Request:  
  ```json
  {
      "message": "Description de l'erreur"
  }
  ```

---

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

Réponses:
- 200 OK:  
  ```json
  {
      "message": "User deleted successfully"
  }
  ```
- 400 Bad Request:  
  ```json
  {
      "message": "Description de l'erreur (ex: compte administrateur non supprimable, user introuvable...)"
  }
  ```

---

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

Réponses:
- 200 OK:  
  ```json
  {
      "message": "User edited successfully"
  }
  ```
- 400 Bad Request:  
  ```json
  {
      "message": "Description de l'erreur (ex: impossible de modifier le compte admin, rôle non existant...)"
  }
  ```

---

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

Réponses:
- 200 OK:  
  ```json
  {
      "message": "User edited successfully"
  }
  ```
- 400 Bad Request:  
  ```json
  {
      "message": "Description de l'erreur (ex: impossible de modifier le compte admin, rôle déjà attribué...)"
  }
  ```

---

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

Réponses:
- 200 OK: Retourne la liste des utilisateurs correspondants au critère de recherche. Par exemple:  
  ```json
  [
      {
          "username": "user001",
          "name": "Nom complet éventuel"
      },
      {
          "username": "user002",
          "name": "Autre utilisateur"
      }
  ]
  ```
- 400 Bad Request:  
  ```json
  {
      "message": "Description de l'erreur (ex: problème de requête...)"
  }
  ```

---

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

Réponses:
- 200 OK: Retourne la liste complète des utilisateurs, par exemple:
  ```json
  [
      {
          "username": "user001",
          "email": "test@example.com",
          "roles": ["USER", "ADMIN"],
          "score": 42
          // etc...
      },
      {
          "username": "user002",
          "email": "autre@example.com",
          "roles": ["USER"],
          "score": 10
      }
  ]
  ```
- 400 Bad Request:  
  ```json
  {
      "message": "Description de l'erreur"
  }
  ```

---

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

Réponses:
- 200 OK:  
  ```json
  {
      "message": "User edited successfully"
  }
  ```
- 400 Bad Request:  
  ```json
  {
      "message": "Description de l'erreur"
  }
  ```

---

## Gestion des erreurs

### /api/error/400

Renvoie la page d'erreur pour les requêtes mal formées.

| Propriété  | Description                             |
|------------|-----------------------------------------|
| Type       | GET                                     |
| Connecté ? | Non                                     |
| Permission | Tous                                    |

Aucune requête JSON requise.  
Réponse:  
```json
{
    "message": "Bad Request"
}
```

---

### /api/error/401

Renvoie la page d'erreur pour une tentative d'accès non autorisée.

| Propriété  | Description                                              |
|------------|----------------------------------------------------------|
| Type       | GET                                                      |
| Connecté ? | Non (peut provenir d'un manque de droits)               |
| Permission | Tous                                                     |

Aucune requête JSON requise.  
Réponse:  
```json
{
    "message": "Unauthorized"
}
```

---

### /api/error/403

Renvoie la page d'erreur pour un accès interdit.

| Propriété  | Description                      |
|------------|----------------------------------|
| Type       | GET                              |
| Connecté ? | Oui (droit insuffisant)          |
| Permission | Tous                             |

Aucune requête JSON requise.  
Réponse:  
```json
{
    "message": "Forbidden"
}
```

---

### /api/error/404

Renvoie la page d'erreur pour une ressource non trouvée.

| Propriété  | Description                             |
|------------|-----------------------------------------|
| Type       | GET                                     |
| Connecté ? | Non                                     |
| Permission | Tous                                    |

Aucune requête JSON requise.  
Réponse:  
```json
{
    "message": "Not Found"
}
```

---

### /api/error/500

Renvoie la page d'erreur pour une erreur interne du serveur.

| Propriété  | Description                             |
|------------|-----------------------------------------|
| Type       | GET                                     |
| Connecté ? | Non                                     |
| Permission | Tous                                    |

Aucune requête JSON requise.  
Réponse:
```json
{
    "message": "Internal Server Error"
}
```
