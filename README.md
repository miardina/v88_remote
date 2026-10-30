# V88 Remote (prototype)

Application Android Kotlin minimale pour tester l'émetteur infrarouge du téléphone et envoyer une commande POWER à une TV Box V88.

## Compilation en ligne avec GitHub Actions

1. Décompresser ce projet.
2. Copier **le contenu** du dossier `V88-Remote` dans la racine de ton dépôt GitHub.
3. Vérifier que la branche principale s'appelle `main` ou `master`.
4. Faire un commit/push.
5. Ouvrir l'onglet **Actions** du dépôt.
6. Sélectionner **Build Android APK**. Le workflow démarre automatiquement après un push. Tu peux aussi lancer **Run workflow**.
7. À la fin du workflow, ouvrir l'exécution réussie et télécharger l'artifact `V88-Remote-debug`.
8. Décompresser l'artifact et installer `app-debug.apk` sur le Redmi 10.

## Test

- Autoriser l'installation d'applications provenant du navigateur ou du gestionnaire de fichiers si Android le demande.
- Ouvrir V88 Remote.
- Vérifier que l'application indique que l'émetteur IR est détecté.
- Orienter le haut du Redmi vers la V88 et appuyer une fois sur POWER.

## Important : code infrarouge provisoire

Le prototype utilise une trame NEC standard à 38 kHz avec adresse `0x01` et commande `0x40`. Ces valeurs sont une hypothèse de départ et **ne sont pas confirmées pour chaque V88 4K**. « Signal envoyé » signifie seulement que le téléphone a transmis la trame, pas que la TV Box l'a reconnue.

Si la V88 ne réagit pas, il faudra identifier le protocole et les codes exacts de la télécommande d'origine ou tester d'autres profils compatibles. La fonction POWER peut également mettre la box en veille plutôt que couper complètement son alimentation.

## Prérequis

- Téléphone Android avec émetteur infrarouge matériel.
- La permission `TRANSMIT_IR` est déclarée dans le manifeste.
- Android Studio n'est pas nécessaire pour la compilation GitHub Actions.
