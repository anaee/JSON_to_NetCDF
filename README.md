Installation de la lib C NetCDF :

https://www.unidata.ucar.edu/software/thredds/current/netcdf-java/reference/netcdf4Clibrary.html

Installation de Maven : 

- Pour Ubuntu https://doc.ubuntu-fr.org/maven

- sinon, lien de téléchargement https://maven.apache.org/download.cgi
puis suivre les instructions https://maven.apache.org/install.html


Compilation :

Se placer dans le dossier téléchargé (au niveau du dossier "src") et exécuter

mvn package

Le fichier JSONtoNetCDF-0.0.1-SNAPSHOT.jar devrait se trouver dans "target"

Exécution : 

java -jar JSONtoNetCDF-0.0.1-SNAPSHOT.jar "nom du fichier JSON" "nom du fichier NetCDF à générer"


