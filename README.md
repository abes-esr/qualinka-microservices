## Microservices Qualinka

Ces microservices permettent :  
- find-ra-idref : à partir d'un nom et prénom (name et lastname), renvoie les références d'autorités (RA) correspondantes dans la base de données IdRef.
- find-nonlinked-rc-sudoc : à partir d'un nom et prénom (name et lastname), renvoie les références contextuelles (RC) non liées (pas de 70X$3) dans la base de données Sudoc.
- linked_rc_idref_sudoc : à partir d'un identifiant d'une RA (le ppn) de la base IdRef, renvoie les RC liées de la base Sudoc (en 70X$3).
- attrra : à partir d'un identifiant d'une RA (le ppn), renvoie ses attributs
- attrrc : à partir d'un identifiant d'une RC (le ppn + "-" + sa position à partir des zones 6XX), renvoie ses attributs
- api-gateway : sert de point unique d'accès aux autres services. Il s'agit d'une passerelle
- eureka-naming-server : registre des microservices
- spring-cloud-config-server : centralise la configuration des microservices. Les configurations se trouvent sur ce dépôt : https://github.com/abes-esr/spring-cloud-config-server

Ce dépôt git contient le code source de ces microservices ainsi que la configuration docker pour son déploiement.  

Les urls sont les suivantes : 
- https://qualinka.idref.fr/find-ra-idref/api/v2/req?lastName=robert&firstName=val%C3%A9rie
- https://qualinka.idref.fr/find-nonlinked-rc-sudoc/api/v2/req?lastName=robert&firstName=val%C3%A9rie
- https://qualinka.idref.fr/linked-rc-idref-sudoc/api/v2/req?ra_id=076642860
- https://qualinka.idref.fr/attrra/api/v2/req?ra_id=076642860
- https://qualinka.idref.fr/attrrc/api/v2/req?rc_id=019057547-1

Le docker-compose attend un .env contenant la clé de décryptage JASYPT (MASTER_PASSWORD) des variables contenues dans les fichiers de configuration.  

Les microservices s'administrent comme tous containers d'un docker-compose :  

```
cd qualinka-microservices/

# Démarrer les containers : 
sudo docker-compose up -d

# Voir les containers :
sudo docker-compose ps

# Voir les logs d'un container : 
sudo docker logs NomContainer -f --tail=100

# Arrêter les containers : 
sudo docker-compose stop

# Redémarrage des containers : 
sudo docker-compose restart
```

Il n'y a pas de volume monté : pas de données à sauvegarder.  
Les microservices utilisent un moteur de recherche Solr et une base de données Oracle internes à l'Abes.

