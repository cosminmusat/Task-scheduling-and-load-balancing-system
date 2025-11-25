Mușat-Mare Cristian-Cosmin
332 CD
Tema2 - APD

Algoritmul Round-Robin este synchronized deoarece exista mai multi generatori de task-uri, astfel introducand
race condition la counter-ul clasei RoundRobin. Algoritmul este synchronized pe TaskGenerator.class pentru a lasa
doar un singur generator de task-uri sa execute algoritmul la un moment dat.

La ceilalti algoritmi generatorii de task-uri nu se influenteaza unii pe altii asa ca nu este nevoie de
sincronizare.

Clasa MyHost contine cateva variable care descriu starea respectivului host, precum workLeft, timeBeforeExecTask
care retine timpul de dinaintea executarii unui task, currentTask care este o referinta la task-ul din coada de
prioritati care se executa la un anumit moment si un semafor binar info care blocheaza accesul dispatcherului la
informatiile de stare ale host-ului pana cand acestea sunt actualizate.

Functia de thread ruleaza pana cand este oprita cu functia shutdown (variablia stop este volatile pentru a fi
folosita cu cea mai recenta valoare a sa). In cazul in care coada nu e goala, se ia primul task din coada si se
actualizeaza variabilele de stare, in timp ce semaforul trece pe 0.
Pentru task-urile preemptibile am folosit un sistem de wait-notify prin care un task peemptibil poate fi trezit de
dispatcher in cazul in care exista un task mai prioritar. Functia checkToNotify se apeleaza pentru fiecare algoritm,
respectiv atunci cand se introduce un element in coada unui host. In cazul in care task-ul preemptibil mai are de
rulat, se actualizeaza timpul ramas pentru acesta si ramane in coada.
Pentru task-urile care nu sunt preemptibile pur si simplu se foloseste sleep.

Functia getQueueSize intoarce numarul de elemente din coada, inclusiv considerandu-l pe cel care ruleaza, deoarece
inca se alfa in coada.

Munca ramasa este calculata prin adunarea cu timpul ramas de executie pentru task-uri la adaugarea acestora in
coada si se reduce dupa tipul task-urilor:
- preemptibil: Se considera timpul de dinainte si dupa executarea unei bucati a task-ului.
- nu este preemptibil: Se scade toata durata acestuia deoarece a fost executat.

Functia getWorkLeft intoarce munca ramasa dupa cazul task-ului curent:
- null: workLeft a fost actualizat si se intoarce doar el.
- nu e null: in acest caz, task-ul ruleaza asa ca vom intoarce workLeft - "cat a rulat pana acum".

Ceilalti algoritmi de planificare sunt implementati conform descrierii.
