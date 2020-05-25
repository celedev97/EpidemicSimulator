# Epidemic Simulator

Simulatore di epidemie in un sistema chiuso;
basato sulle specifiche definite nel documento
di progetto di Metodologie di Programmazione dell'anno accademico 2019/2020.

**Indice del Progetto:**

- [Markdown Navigation](#epidemic-simulator)
    - [Come iniziare](#come-iniziare)
        - [IDE](#ide)
        - [Dipendenze](#Dipendenze)
        - [Configurazione di avvio](#configurazione-di-avvio)
        - [Creazione JAR](#creazione-jar)
    - [Discussione del progetto](#discussione-del-progetto)
    - [Versioning](#versioning)
    - [Autori](#autori)
    - [License](#license)
    - [Ringraziamenti](#ringraziamenti)
    

##Come iniziare

Come ottenere una copia modificabile del progetto.

###IDE

Il progetto è stato fatto con l'IDE IntelliJ IDEA (di JetBrains),
perciò è consigliato -anche se non indispensabile-
utilizzare IntelliJ IDEA come IDE per la modifica e/o la compilazione del progetto.

IntelliJ IDEA è scaricabile gratuitamente nella sua versione community al seguente link:
[IntelliJ IDEA](https://www.jetbrains.com/idea/)

###Dipendenze

Il progetto utilizza le seguenti librerie:

|Git|Maven|Utilizzo|
|---|---|---|
|[JSON-java](https://github.com/stleary/JSON-java)|org.json|Salvataggio dati|
|[XChart](https://github.com/knowm/XChart)|org.knowm.xchart|Grafici|
|[JDaze](https://github.com/fc-dev/JDaze)|-non disponibile-|Engine di disegno|
|[reflections](https://github.com/ronmamo/reflections)|org.reflections|Reflection avanzata|

Per ulteriori informazioni sulle versioni utilizzate riferirsi al file di progetto contenente le dipendenze:
[EpidemicSimulator.iml](./EpidemicSimulator.iml)

Si consiglia di utilizzare maven per la gestione delle dipendenze quando disponibile;

In caso le librerie non siano disponibili tramite maven riferirsi alla documentazione delle singole librerie per le informazioni di installazione.

### Configurazione di avvio

La configurazione di avvio consigliata per il programma consiste nell'avvio del main della classe `SimulatorSettings`.

### Creazione JAR

A causa delle finalità del progetto **non** verranno rilasciate release.

Ciononostante il progetto è fornito di un file [Manifest.MF](/src/META-INF/MANIFEST.MF) utilizzabile per creare un JAR
per la distribuzione del programma se necessario.

Per i dettagli sul metodo per creare un JAR riferirsi alla procedura specifica per il proprio IDE/OS.

## Discussione del progetto

### Struttura del simulatore
Il simulatore si avvale di una serie di parametri che gli vengono passati durante l'immissione
all'interno del Frame generato dalla classe **SimulatorSettings**, e li utilizza per l'esecuzione
e il calcolo della prosecuzione della malattia giornalmente fino a quando non si 
verificano uno dei possibili **finali** specificati dal requisito:
- Economic Collapse: Risorse Esaurite ("Resources<0").
- All_Healed: Malattia debellata, nel quale **al più una persona** sopravvive.
- All_Dead: La Malattia vince, ovvero **tutta** la popolazione diviene **Nera**.  

Durante l'esecuzione del suo metodo principale "ExecuteDay", il simulatore estrae randomicamente  
solo le persone attualmente abilitate **al movimento** da una lista apposita,
chiamata per l'occasione "notQuarantinedPersons", e ne sperimenta
un incontro (al giorno 1 avremo tutta la popolazione in movimento).
L'incontro tra le due persone può generare dei cambiamenti nello "status" di una delle due, 
laddove l'incontro preveda la presenza di un individuo sano e di un infetto (l'incontro tra due sani non
può far avvenire nulla, discorso analogo tra due infetti), anche a seconda dei parametri inseriti.
Il simulatore, di fatto, userà il livello di sintomaticità immesso per determinare **randomicamente ed
in funzione della percentuale** la probabilità di un individuo sano di cambiare il suo status sanitario e, laddove questo "switch" avvenga,
calcolerà anche il numero **massimo** di giorni entro cui quella persona diverrà infetta ed eventualmente, in funzione della percentuale di sintomaticità e letalità,
anche il numero di giorni entro cui quella persona svilupperà sintomi e/o morirà.
- **NOTA**:
Tali calcoli servono solo a livello logico per poter permettere al simulatore di aggiornare lo status
della malattia sulla popolazione e per permettere alle strategie e alla GUI (che leggono questi dati)
di agire di conseguenza (nessuno, infatti, può modificare tali parametri).

Avvenuto l'incontro il simulatore controllerà tutti gli individui della popolazione 
che soddisfano il requisito di essere vivi **e** infetti allo stesso tempo; per ognuna di esse
ci sarà un contatore di giorni il quale, una volta raggiunto la soglia massima di giorni
calcolata nel passo precedente, cambierà lo status della persona da verde a giallo e poi, eventualmente, da giallo a rosso.

Un altro dei metodi per il check sanitario di cui si avvale il simulatore è "testVirus" (il metodo usato per il tampone in pratica):
qui viene passata una persona in input e il simulatore ne controlla lo stato, se l'individuo è sintomatico non viene neanche effettuato il test, altrimenti il metodo
restituisce True/False a seconda del fatto che quella persona abbia in circolo il virus o meno.

Tutti gli eventuali cambiamenti che vengono effettuati sono registrati da chiamate denominate **callBacks**, nient'altro
che un arrayList di implementazioni dell'interfaccia "SimulatorCallBack" creata per evitare conflitti di gestione parallela,
ovvero per non avere più strategie in esecuzione all'interno di un simulatore: di fatto ciascun array di "chiamate"
è linkato verso una e una sola strategia per volta, tant'è che prima di avvenire il link chiamata->Strategy viene usato
un metodo "removeCallBack()" che pulisce eventualmente una precedente strategia immessa.


Una volta sperimentati gli incontri, aver cambiato lo status delle persone e aver registrato 
i cambiamenti avvenuti nelle "callBack",
il simulatore effettua un ultimo controllo in funzione delle risorse e della popolazione in vita e 
ritorna un eventuale outcome per fermare
l'esecuzione del programma e stampare uno dei finali elencati precedentemente.

 
### Collegamento delle strategie

### Implementazione delle strategie

### Interfaccia grafica

#### Settings

#### Simulatore testuale

#### Simulatore grafico

## Versioning

Come sistema di controllo di versione abbiamo utilizzato [Git](https://git-scm.com/),
sfruttando come hosting [GitHub](https://github.com).

##Autori

- **Federico Capece** - [fc-dev](https://github.com/fc-dev)
- **Paolo Luciano** - [Paoletto123](https://github.com/Paoletto123)
- **Bruno Benedetto Domingo** - [Bruner4](https://github.com/Bruner4)

Per ulteriori dettagli vedere la lista dei
[contributors](https://github.com/your/project/contributors)
che hanno partecipato in questo progetto.

## License

Il progetto è rilasciato sotto licenza MIT - vedere il file [LICENSE.md](LICENSE.md) per i dettagli.

## Ringraziamenti

* Un gigantesco grazie all'utente 
[tirz](https://stackoverflow.com/users/4718768/tirz)
per l'idea di usare il `ClassLoader` in congiunzione al `FileSystem` al fine di ottenere tutte le classi in un package.

