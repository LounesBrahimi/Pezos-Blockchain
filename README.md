# Pezos-Blockchain

## Sujet :

Pezos® est une blockchain gouvernée par un dictateur. Son algorithme de consensus est une preuve
d’autorité : seul le dictateur a le droit de produire des blocs. En revanche, le dictateur n’est pas le meilleur
développeur et les blocs qu’il produit contiennent systématiquement des erreurs. Le dictateur s’en remet
à ses fidèles utilisateurs pour déceler les erreurs. En récompense, le dictateur a prévu un mécanisme de
gratification. Ainsi, pour chaque erreur trouvée, 1 pez (la monnaie de Pezos®) sera créé et versé aux
utilisateurs ayant trouvé l’erreur. À chaque type d’erreur, on associe un type de pez spécifique : si l’on ne
trouve qu’un seul type d’erreur, on ne disposera seulement que de ce type de pez.


Le dictateur n’a pas une machine puissante. Ainsi, il a décidé que les blocs ne pouvaient être créés que
toutes les 10 minutes. Dans ce laps de temps, les utilisateurs peuvent vérifier la validité du dernier bloc
produit et injecter une opération de correction. Une fois cette opération injectée (et valide), le dictateur va
produire son prochain bloc en l’incluant et en appliquant la gratification. Chaque bloc produit est final et
toute opération de correction reçue ne sera valide que pour le dernier bloc produit : si une opération arrive
trop tard, elle sera ignorée par le réseau.


Le but de ce projet est donc d’implanter un client de correction capable de se connecter au serveur
Pezos®, écoutant les nouveaux blocs et détectant les erreurs pour proposer des corrections afin d’obtenir le
maximum de pez.

