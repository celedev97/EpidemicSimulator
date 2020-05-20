# Epidemic Simulator

Simulatore di epidemie in un sistema chiuso;
basato sulle specifiche definite nel documento
di progetto di Metodologie di Programmazione dell'anno accademico 2019/2020.

**TODO: AGGIUNGERE INDICE DI NAVIGAZIONE!!!**

- [Markdown Navigation](#epidemic-simulator)
    - [Come iniziare](#come-iniziare)
        - [IDE](#ide)
        - [Dipendenze](#dipendenze)
        - [Configurazione di avvio](#configurazione-di-avvio)
        - [Creazione JAR](#creazione-jar)
    - [Discussione del progetto](#discussione-del-progetto)
    - [Versioning](#versioning)
    - [Autori](#autori)
    - [License](#license)
    - [Ringraziamenti](#ringraziamenti)

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

### Collegamento delle strategie

### Implementazione delle strategie

### Interfaccia grafica

#### Settings

#### Simulatore testuale

#### Simulatore grafico

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

## Ringraziamenti

* Un gigantesco grazie all'utente 
[tirz](https://stackoverflow.com/users/4718768/tirz)
per l'idea di usare il `ClassLoader` in congiunzione al `FileSystem` al fine di ottenere tutte le classi in un package.

