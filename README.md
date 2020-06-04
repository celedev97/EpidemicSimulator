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
        - [Struttura del simulatore](#struttura-del-simulatore)
        - [Implementazione delle strategie](#implementazione-delle-strategie)
        - [Interfaccia grafica](#interfaccia-grafica)
            - [Settings](#settings)
            - [Simulatore testuale](#simulatore-testuale)
            - [Simulatore grafico](#simulatore-grafico)
    - [Versioning](#versioning)
    - [Autori](#autori)
    - [License](#license)
    

## Come iniziare

Come ottenere una copia modificabile del progetto.

### IDE

Il progetto è stato fatto con l'IDE IntelliJ IDEA (di JetBrains),
perciò è consigliato -anche se non indispensabile-
utilizzare IntelliJ IDEA come IDE per la modifica e/o la compilazione del progetto.

IntelliJ IDEA è scaricabile gratuitamente nella sua versione community al seguente link:
[IntelliJ IDEA](https://www.jetbrains.com/idea/)

### Dipendenze

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


### Implementazione delle strategie
Nel progetto abbiamo implementato e testato diverse strategie, cercando di strutturarle in maniera 
diversificata rispetto all'altra.
Ogni strategia è dotata di una propria specializzazione in un determinato settore di
gestione della popolazione in funzione dei dati che, giorno per giorno, vengono segnalati dal simulatore
sul decorso e l'evoluzione della malattia.

Ogni strategia ha lo scopo di riconoscere (ed eventualmente modificare) lo status della persona mediante
tamponi e in funzione del suo compito e della sua struttura:

- **No Strategy**: la strategia più banale, ma anche la più "Economic-Friendly": sostanzialmente
si lascia andare la malattia per il suo normale decorso senza testare o bloccare nessuno a seguito
di un criterio imposto. Gli unici costi che si sosterebbero sarebbero quelli per mettere in cura
un sintomatico presso un ospedale e sperare che "l'immunità di gregge" possa permettere alla 
popolazione di "gestire" da soli il virus nel modo migliore possibile.

- **Pecentage Lockdown and Stop Spread**:Una strategia che ha il semplice compito di 
effettuare il lockdown di una certa percentuale di persone immesse da tastiera al
primo rosso/sintomatico che si presenta;ma che in
più consente di effettuare anche dei controlli preventivi sulla popolazione lasciata in
movimento laddove il numero di sintomatici dovesse salire troppo,e **solo se** la percentuale
di risorse attualmente disponibile è superiore a una certa soglia(*nel nostro
caso del 35%*)tale che possa permettere
un minimo al simulatore,dopo i controlli dei tamponi,di progredire senza collassi economici.

- **ContactTracingLightTest:** Da quando viene trovato il primo sintomatico, scorre tutta la popolazione in vita,
e per ogni sintomatico trovato analizza la sua lista degli incontri nei precedenti "developSyntomsMaxDay" giorni.
Di questi, effettua il tampone ad una percentuale data da "testPercentage",
se positivi vengono messi in quarantena fin quando non si è sicuri abbiano debellato la malattia
(o fino a quando non sviluppino sintomi). Il resto invece viene messo in quarantena preventiva senza controllo del tampone.

- **StopEpidemyOnFirstRed:** La strategia più "aggressiva" di tutte, come suggerisce il nome. Non appena
il simulatore abilita il flag "FirstRed" (quindi non appena la prima persona presenterà i sintomi), la strategia
entrerà in azione sottoponendo tutta la popolazione ad un lockdown preventivo che dia il tempo a tutti i
*potenziali* infetti di, eventualmente, presentare sintomi o di avere il virus in circolo e rilevabile.
Scaduto l'intervallo di "lockdown" si prosegue con un tampone a tutti coloro che ancora non hanno
presentato dei sintomi, rimettendo così in libertà tutti e solo gli individui sani.

- **Medium Controlled Lockdown:** Questa Strategia è stata pensata per effettuare dei 
Lockdown preventivi,e contenuti,in funzione del numero dei sintomatici attualmente
rilevati dal simulatore.
Al momento del lancio del simulatore la strategia richiederà un certo parametro da parte
dell'utente,dal nome di **"Percentual of Stop"**,come suggerisce il nome stesso,tale parametro serve
a indicare quanto tempo far aspettare,in termini di percentuale di sintomatici,
la strategia prima di farla entrare in azione.
Quando la soglia di limite viene superata,la strategia provvederà a calcolare una certa 
percentuale di persone che andranno controllate,sul numero totale di individui che in quel
preciso momento hanno la facoltà di movimento(*quindi Verdi e/o Gialli*).
Tale percentuale viene fornita dall'utente e indica in termini di percentuale
la dimensione di "default" di ogni blocco di persone in movimento su cui effettuare il controllo,
a tale valore vengono sommate ulteriori "n" persone da estrarre(*dove il parametro "n" corrisponde al numero di 
rossi rilevati dal simulatore*).
Per ognuna delle persone che ho,randomicamente,estratto ne effettuo un tampone e se risultano
essere positive,non solo si blocca l'individuo in questione,**ma anche tutte le altre persone
che quest'ultimo ha incontrato nei giorni precedenti**,poichè potrebbero essere dei "potenziali"
infetti,tali persone vengono inserite in un'apposita struttura dati:**check**,e attenderanno
il periodo minimo di incubazione prima di essere testati da un tampone ed,eventualmente,
rilasciati.
Se la strategia durante il corso della sua esecuzione dovesse notare che le risorse 
scendono al di sotto una certa soglia(*nel nostro caso del 45%*),viene effettuato in ogni
caso il controllo sulle persone contenute in **check**,anche se l'intervallo di incubazione
non viene raggiunto,rilasciando i sani rilevati e tenendo fermi eventuali asintomatici,abilitando
infine un **flag booleano** che non faccia più attivare la strategia,e lasci andare ormai il tutto per il
suo normale decorso.
Seguendo questa formula riusciamo sempre a effettuare lockdown più eterogenei e regolati
in funzione del numero effettivo di sintomatici che riscontriamo nel decorso dell'epidemia,ed
eventualmente riusciamo anche ad avere una sorta  di "safe-mode" laddove il tutto stia 
richiedendo uno sforzo eccessivo,in termini di risorse.

### Interfaccia grafica

#### Settings
Avviando il programma, compare subito l'interfaccia grafica in cui inserire tutti i parametri
necessari alla simulazione e la strategia da utilizzare (compreso l'uso di nessuna strategia).
L'inserimento dei parametri è regolato tramite l'uso di Spinner, in questo modo viene limitata
l'immissione di soli caratteri numerici, ponendo anche i vincoli di intervalli a numeri interi
(o a una cifra decimale nel caso del numero di incontri **V**). È presente anche il controllo dei
valori minimi e massimi regolato tramite le Annotazioni.  
Per la scelta della strategia viene usato
invece un ComboBox e le relative strategie vengono estrapolate tramite Reflection, in modo che
ad ogni cambio o aggiunta di strategia cambi anche la voce all'interno del ComboBox. Nelle strategie
che prevedono anche un parametro da inserire, comparirà anche uno Spinner per la sua immissione.
![](./gui.jpg)

#### Simulatore testuale
La variante testuale del simulatore non è altro che il classico output testuale con formattazione, sfondo
e colori adeguati. Viene stampato il resoconto dettagliato giorno per giorno della popolazione (sani, infetti, sintomatici, guariti e deceduti)
e la variazione del fattore R0. Alla fine della simulazione, viene riportato il relativo outcome (tutti guariti,
collasso economico o tutti morti) e il riepilogo dei parametri iniziali.

#### Simulatore grafico
Per la variante grafica del simulatore, sono stati usati i tool messi a disposizione da awt e swing,
facendo uso dei layout più comuni come il BoxLayout e il GridBagLayout. L'interfaccia è sostanzialmente
costituita da 2 blocchi fondamentali: la parte centrale, in cui sono visibili graficamente e in tempo
reale gli "incontri" fra le persone (contrassegnate con i loro rispettivi colori), e la parte superiore, dove 
sono stati collocati i dati relativi al simulatore aggiornati giorno dopo giorno sotto forma di barre e di grafici,
i parametri iniziali inseriti nel simulatore e uno slider per settare la velocità della simulazione.

## Versioning

Come sistema di controllo di versione abbiamo utilizzato [Git](https://git-scm.com/),
sfruttando come hosting [GitHub](https://github.com).

## Autori

- **Federico Capece** - [fc-dev](https://github.com/fc-dev)
- **Paolo Luciano** - [Paoletto123](https://github.com/Paoletto123)
- **Bruno Benedetto Domingo** - [Bruner4](https://github.com/Bruner4)

Per ulteriori dettagli vedere la lista dei
[contributors](https://github.com/your/project/contributors)
che hanno partecipato in questo progetto.

## License

Il progetto è rilasciato sotto licenza MIT - vedere il file [LICENSE.md](LICENSE.md) per i dettagli.
