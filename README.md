Installation de la lib C NetCDF :

https://www.unidata.ucar.edu/software/thredds/current/netcdf-java/reference/netcdf4Clibrary.html

Installation de Maven : 

- Pour Ubuntu https://doc.ubuntu-fr.org/maven

- sinon, lien de t�l�chargement https://maven.apache.org/download.cgi
puis suivre les instructions https://maven.apache.org/install.html


Compilation :

Se placer dans le dossier t�l�charg� (au niveau du dossier "src") et ex�cuter

mvn package

Le fichier JSONtoNetCDF-0.0.1-SNAPSHOT.jar devrait se trouver dans "target"

Ex�cution : 

java -jar JSONtoNetCDF-0.0.1-SNAPSHOT.jar "nom du fichier JSON" "nom du fichier NetCDF � g�n�rer"


