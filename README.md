# SoftwareprojektQ4Abgabe

-- Das ist der Backend-Part unserers Softwareprojektes (soweit wie vom Frontend angefordert) ---

- im gleichen Ordner befindet sich die .jar Datei mit .bat Datei zum Ausführen
- die Dateien des Servers befinden sich im "src"-Ordner (sollte die .jar-Datei Probleme bereiten, dann bitte die ServerApp.java Datei verwenden)
- die aktiven Verfasser der Dateien sind in Zeile 1 jeder Datei als Kommentar aufgeführt
- zum Ausführen der Dateien muss sich der Rechner im Schulnetzwerk befinden (oder einen anderen Weg für den Zugriff auf die Datenbank beitzen)


Zum Auführen wird zudem noch die folgende Tabelle benötigt:

CREATE TABLE member_of(

chat_id int NOT NULL,
user_id int NOT NULL,

foreign key(chat_id) references chatrooms(id),
foreign key(user_id) references users(id)

);


Kommunikation zw. Front- und Backend (Sciht des Frontend):


Logindaten eingeben
-> "login username|password"

    Erfolgreich: <- "username"
    Fehlerhaft (PW || NN falsch): <- "1"

Empfangen neuer Nachrichten:
-> "chat <chatID>"
<- "message|timestamp|authorName|message|timestamp|authorName|..."

Empfangen aller Chats (für den startup):
-> "chats"
<- "chats chatID|chatName|chatID|chatName"

Schreiben von Nachrichten:
-> "send chatID|message" : timestamp wird vom Server aufgenommen

    Erfolgreich: <- "send 0"
    Fehlerhaft (illagale Zeichen): <- "send 1"

Ausloggen:
-> "logout"

Empfangen
<- "msg chatID|message|timestamp|authorName"
