# Epidemic Simulator

Simulatore di epidemie in un sistema chiuso;
basato sulle specifiche definite nel documento
di progetto di Metodologie di Programmazione dell'anno accademico 2019/2020.

**Indice del Progetto:**

- [Epidemic Simulator](#epidemic-simulator)
    - [Documentazione](#documentazione)
    - [Come iniziare](#come-iniziare)
        - [IDE](#ide)
        - [Dipendenze](#Dipendenze)
        - [Configurazione di avvio](#configurazione-di-avvio)
        - [Creazione JAR](#creazione-jar)
    - [Discussione del progetto](#discussione-del-progetto)
    - [Versioning](#versioning)
    - [Autori](#autori)
    - [License](#license)
    
## Documentazione

La JavaDoc del progetto è disponibile [qui](https://fc-dev.github.io/EpidemicSimulator/)

## Come iniziare

Per ottenere una copia modificabile del progetto si suggerisce di creare una
[fork](https://help.github.com/en/github/getting-started-with-github/fork-a-repo)
del repository e clonarla in locale.

In alternativa è possibile clonare direttamente il repository,
ma in quel caso non si avrà la possibilità di eseguire `git push`
e i commit resteranno in locale.

### IDE

Il progetto è stato utilizzando lo strumento di gestione di progetti
[Apache Maven](https://maven.apache.org/),
perciò qualsiasi IDE Java compatibile con Maven può andare bene.

*(Es: [IntelliJ IDEA](https://www.jetbrains.com/idea/),
[Eclipse](https://www.eclipse.org/downloads/),
[NetBeans](https://netbeans.apache.org/download/))*

### Dipendenze

Il progetto utilizza le seguenti librerie:

|Git|Maven|Utilizzo|
|---|---|---|
|[JSON-java](https://github.com/stleary/JSON-java)|org.json|Salvataggio dati|
|[XChart](https://github.com/knowm/XChart)|org.knowm.xchart|Grafici|
|[JDaze](https://github.com/fc-dev/JDaze)|dev.federicocapece.jdaze|Engine di disegno|
|[reflections](https://github.com/ronmamo/reflections)|org.reflections|Reflection avanzata|

Per ulteriori informazioni sulle versioni utilizzate riferirsi al file di progetto contenente le dipendenze:
[pom.xml](./pom.xml)

Si consiglia di utilizzare Maven per la gestione delle dipendenze quando possibile.

In caso le librerie non siano disponibili tramite maven riferirsi alla documentazione delle singole librerie per le informazioni di installazione.

### Configurazione di avvio

La configurazione di avvio consigliata per il programma consiste nell'eseguire il `main` della classe `SimulatorSettings`.

Sono presenti altri `main` in altre classi per motivi di test, vedere la JavaDoc per ulteriori dettagli.

### Creazione JAR

A causa delle finalità del progetto **non** verranno rilasciate release.

Ciononostante il [pom.xml](./pom.xml) contiene tutte le info necessarie
per generare un file JAR funzionante tramite il comando 
`mvn package`.

### Discussione del Progetto

Il documento di discussione del progetto è disponibile qui: [PROJECT.md](./PROJECT.md)

## Versioning

Come sistema di controllo di versione abbiamo utilizzato [Git](https://git-scm.com/),
sfruttando come hosting [GitHub](https://github.com).

## Autori

- **Federico Capece** - [fc-dev](https://github.com/fc-dev)
- **Paolo Luciano** - [Paoletto123](https://github.com/Paoletto123)
- **Bruno Benedetto Domingo** - [Bruner4](https://github.com/Bruner4)

Per ulteriori dettagli vedere la lista dei
[contributors](https://github.com/fc-dev/EpidemicSimulator/contributors)
che hanno partecipato in questo progetto.

## License

Il progetto è rilasciato sotto licenza MIT - vedere il file [LICENSE.md](LICENSE.md) per i dettagli.
