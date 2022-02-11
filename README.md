## Microservices Qualinka

Ce dépôt git contient le code source des microservices ci-dessous, ainsi que la configuration docker pour son déploiement.

### Description des microservices

#### find-ra-idref 
A partir d'un nom et prénom (name et lastname), renvoie les références d'autorités (RA) correspondantes dans la base de données IdRef. Un fichier de requêtes Solr peut être précisé (file)
  - Url : https://qualinka.idref.fr/find-ra-idref/api/v2/req?lastName=robert&firstName=val%C3%A9rie
  - Url avec fichier de requête : https://qualinka.idref.fr/find-ra-idref/api/v2/req?lastName=robert&firstName=val%C3%A9rie&file=findra_light
  - Debug : https://qualinka.idref.fr/find-ra-idref/api/v2/debug/req?lastName=robert&firstName=val%C3%A9rie

#### find-nonlinked-rc-sudoc 
A partir d'un nom et prénom (name et lastname), renvoie les références contextuelles (RC) non liées (pas de 70X$3) dans la base de données Sudoc. Un fichier de requêtes Solr peut être précisé (file)
 - Url : https://qualinka.idref.fr/find-nonlinked-rc-sudoc/api/v2/req?lastName=robert&firstName=val%C3%A9rie
 - Url avec fichier de requête : https://qualinka.idref.fr/find-nonlinked-rc-sudoc/api/v2/req?lastName=robert&firstName=val%C3%A9rie&file=findrc_light
 - Debug : https://qualinka.idref.fr/find-nonlinked-rc-sudoc/api/v2/debug/req?lastName=robert&firstName=val%C3%A9rie

#### linked_rc_idref_sudoc 
A partir d'un identifiant d'une RA (le ppn) de la base IdRef, renvoie les RC liées de la base Sudoc (en 70X$3).
 - Url : https://qualinka.idref.fr/linked-rc-idref-sudoc/api/v2/req?ra_id=076642860

#### attrra
A partir d'un identifiant d'une RA (le ppn), renvoie ses attributs
 - Url : https://qualinka.idref.fr/attrra/api/v2/req?ra_id=076642860 

#### attrrc 
A partir d'un identifiant d'une RC (le ppn + "-" + sa position à partir des zones 6XX), renvoie ses attributs
 - Url : https://qualinka.idref.fr/attrrc/api/v2/req?rc_id=019057547-1

Pour ces 5 microservices : par défaut, le format de réponse est JSON, mais il est possible d'ajouter &format=xml pour obtenir la réponse en XML (sauf pour le mode debug).  
Les fichiers de requêtes qu'on peut préciser pour find-ra-idref et find-nonlinked-rc-sudoc doivent se trouver sur ce dépôt : https://github.com/abes-esr/qualinka-findws-requests

#### api-gateway 
Sert de point unique d'accès aux autres services. Il s'agit d'une passerelle

#### eureka-naming-server 
Registre des microservices

#### spring-cloud-config-server 
Centralise la configuration des microservices. Les configurations se trouvent sur ce dépôt : https://github.com/abes-esr/spring-cloud-config-server

### Administration

Le docker-compose attend un .env contenant la clé de décryptage JASYPT (MASTER_PASSWORD) des variables contenues dans les fichiers de configuration.  

Les microservices s'administrent comme tout conteneur d'un docker-compose :  

```
cd qualinka-microservices/

# Démarrer les conteneurs : 
sudo docker-compose up -d

# Voir les conteneurs :
sudo docker-compose ps

# Voir les logs d'un conteneur : 
sudo docker logs NomConteneur -f --tail=100

# Arrêter les conteneurs : 
sudo docker-compose stop

# Redémarrage des conteneurs : 
sudo docker-compose restart
```

Il n'y a pas de volume monté : pas de données à sauvegarder.  
Les microservices utilisent un moteur de recherche Solr et une base de données Oracle internes à l'Abes.

