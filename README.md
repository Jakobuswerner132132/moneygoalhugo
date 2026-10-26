# MoneyGoal HUD

Eine kleine Fabric-Mod fuer Minecraft 26.3 (nur clientseitig). Sie merkt sich dein Geld-Ziel,
fragt automatisch alle 30 Sekunden /money ab und zeigt den Fortschritt in einem frei
verschiebbaren HUD.

Wichtig: Seit Minecraft 26.1 ist das Spiel nicht mehr obfuskiert, und Fabric hat Yarn
aufgegeben. Diese Mod benutzt deshalb die offiziellen Namen des Spiels (z. B.
Minecraft, GuiGraphicsExtractor, Identifier) und braucht keine Mappings mehr.

## Was die Mod macht

* /moneygoal set <Betrag> setzt dein Ziel (bleibt gespeichert).
* Alle 30 Sekunden laeuft automatisch /money im Chat - ohne dass du tippst.
* Die Server-Antwort wird gelesen und der Betrag im HUD aktualisiert.
* Das HUD zeigt Kontostand, Ziel, Prozentbalken und den fehlenden Betrag.
* Mit der rechten Umschalttaste (aenderbar) oeffnest du den Bearbeiten-Modus: ziehen,
  mit dem Mausrad skalieren, ESC speichert die Position.

## Voraussetzungen

* Java 25 (Minecraft 26.3 verlangt mindestens Java 25)
* IntelliJ IDEA Community Edition (kostenlos) oder ein Terminal
* Gradle 9.8.2 wird vom mitgelieferten Wrapper automatisch heruntergeladen.
  Gradle muss nicht separat installiert sein. Loom 1.19.3 verlangt mindestens Gradle 9.8.
* Die Versionen in gradle.properties sind auf 26.3 eingestellt:
  Minecraft 26.3, Fabric Loader 0.20.1, Fabric API 0.165.0+26.3, Loom 1.19.3

## Bauen

1. Projekt in einen neuen Ordner entpacken (nicht mit dem alten Projekt vermischen).
2. Ordner in IntelliJ IDEA oeffnen. Unter Settings -> Build, Execution, Deployment ->
   Build Tools -> Gradle die Distribution auf "Wrapper" und Gradle JVM auf Java 25 stellen.
   Danach "Reload All Gradle Projects" im Gradle-Panel anklicken.
3. Der Wrapper laedt Gradle 9.8.2 automatisch. Danach rechts im Gradle-Panel:
   Tasks -> build -> build doppelklicken.
   Die fertige Datei liegt in build/libs/ - nimm die moneygoal-1.0.0.jar
   (nicht die mit "-sources").

Alternativ im entpackten Projektordner auf der Kommandozeile:

Windows (PowerShell):

    .\gradlew.bat build

Linux / macOS:

    sh gradlew build

Nicht "gradle build" verwenden: Das wuerde eine eventuell veraltete lokale
Gradle-Installation statt des mitgelieferten Wrappers starten.

## Installieren

1. Fabric Loader fuer Minecraft 26.3 installieren (Installer von fabricmc.net).
2. Fabric API 0.165.0+26.3 in den mods-Ordner legen.
3. Deine gebaute moneygoal-1.0.0.jar in denselben mods-Ordner kopieren.
4. Mit dem Fabric-Profil starten, dann /moneygoal set 25000 eingeben.

## Befehle

    /moneygoal set <Betrag>              Ziel setzen
    /moneygoal balance <Betrag>          Kontostand manuell setzen
    /moneygoal interval <Sekunden>       Abfrage-Intervall (5-600)
    /moneygoal check                     sofort /money abfragen
    /moneygoal edit                      HUD verschieben und skalieren
    /moneygoal show | hide               HUD ein- oder ausblenden
    /moneygoal reset                     Ziel loeschen
    /moneygoal                           Status anzeigen

## Konfiguration

Alles landet in config/moneygoal.json. Wichtig sind die Felder "patterns" (Suchmuster
fuer den Betrag) und "keywords". Wenn dein Server den Betrag anders schreibt, passe
"patterns" an - der erste Treffer gewinnt. Ohne passendes Muster nimmt die Mod die
groesste Zahl aus einer Nachricht, die eines der "keywords" enthaelt.

## Wenn etwas nicht klappt

* Betrag wird nicht erkannt: /moneygoal balance <Betrag> nutzen und "patterns" anpassen.
* Baufehler: Schick mir die rote Meldung aus IntelliJ, dann passe ich den Code an.
  Ich kann die Mod hier nicht selbst kompilieren, deshalb ist der Code auf die
  dokumentierten APIs von Minecraft 26.3 und Fabric API 0.165.0 geschrieben.

## Hinweis

Die Mod schickt automatisch Befehle an den Server. Manche Server sehen das nicht gern -
frag im Zweifel vorher das Serverteam. Mit /moneygoal interval 120 wird die Abfrage
seltener. Dieses Projekt ist nicht mit Mojang, Microsoft oder einem Server verbunden.
