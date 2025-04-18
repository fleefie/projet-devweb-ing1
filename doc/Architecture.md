# Architecture de l'application

Ce projet est constitué de trois composants:

- Le backend, géré par Spring
- L'API gérée par Spring
- Le frontend, géré par React

Le système de build de ce projet et Maven, avec l'aide de Vite pour React.

# Frontend

Le frontend est contenu dans [frontend](/frontend). Il s'agit d'un projet Vite 
+ React standard. Lors de la [compilation](/README.md#Compilation%20Et%20Lancement),
les fichiers générés par Vite sont ajoutés dans un JAR, dans le répertoire
`CLASSPATH:/static/`. Ce répertoire est le répertoire par défaut des resources
statiques pour Spring.

# Backend

Le backend est contenu dans [application](/application). Il s'agit d'un projet
Spring-Boot standard, également. Il utilise le frontend comme une dépendance,
ce qui permet de séparer une reconstruction du frontend sans toucher au backend.

> [!note] 
> Le backend peut être recompilé sans retranspiler le frontend. Mais l'inverse
> n'est pas le cas. Dans le futur, il faudra séparer les deux, voire même
> séparer le backend de l'API au niveau de la compilation.
>
> Si les temps de compilations ne sont pas un problème, cela peut être évité.
> Quoi qu'il en soit, je n'ai pas l'utilitée imédiate de le faire, donc tant pis :wink:

Le backend possède un composant majeur, 
[NotFoundIndexFilter](/application/src/main/java/fr/cytech/projetdevwebbackend/NotFoundIndexFilter.java).
Cette classe est un filtre qui agit comme un controlleur. React est un framework
pour construire des applications à une seule page (SPA). Or, une SPA nécéssite
d'utiliser l'URL comme un moyen d'enrengistrer son état (pour faire simple). Cela
veut dire qu'il est nécéssaire que notre frontend n'ait qu'une seule page (index.html).
Le filtre permet alors de, une fois par requête, transformer toute requête pour
du contenu HTML en redirection vers cette page. Il permet également de ne pas transformer
les requêtes vers l'API et vers du contenu non-HTML, comme du JS ou des images.

# API

L'API permet de communiquer avec le backend. Voir [la documentation de l'API](/doc/API.md).
Elle retourne soit HTTP 200 OK pour une bonne requette, soit HTTP 400 BAD\_REQUEST
pour une mauvaise. Dans les deux cas, elle peut ou non renvoyer un objet JSON
contennant le résultat de la requette.
