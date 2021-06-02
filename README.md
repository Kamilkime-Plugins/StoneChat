# StoneChat
Podstawowe zarządzanie chatem w jednej komendzie. Bezpłatna i w pełni otwartoźródłowa alternatywa dla płatnych pluginów jak BMChatPremium

----------------------------------------------------
### :desktop_computer: Instalacja
Plugin przeznaczony jest dla wersji MC od 1.8 do 1.16.5. Działa z dowolnym silnikiem opartym o BukkitAPI — CraftBukkit, Spigot, Paper, Tuinity itp. Najnowszy .jar z pluginem można zawsze znaleźć [tu](https://github.com/Kamilkime/StoneChat/releases).

----------------------------------------------------
### :gear: Konfiguracja pluginu
W pliku config.yml znajdują się ustawienia przechowywania danych i czasu autozapisu. Dostępne są dwa sposoby przechowywania danych: **MySQL** i **SQLite**.

Zapis SQLite jest zalecany, ponieważ jest w pełni lokalny — nie wymaga posiadania dedykowanej bazy czy instalacji żadnego dodatkowego oprogramowania. W jego konfiguracji należy podać jedynie plik, w którym przechowywana będzie baza — najlepiej zostawić to ustawione domyślnie.

Zapis MySQL zalecany jest dla osób, które posiadają już bazę MySQL lub podobną (np. MariaDB) i chcą używać danych o wykopanym kamieniu poza samym serwerem. W jego konfiguracji należy podać dane do połączenia z bazą, wymuszanie użycia SSL przy połączeniu (należy je wyłączyć, jeśli baza nie obsługuje SSL) i liczbę zarezerwowanych połączeń (zalecane min. 2).

W przypadku obu sposobów zapisu — należy podać także nazwę tabeli, w której przechowywane będą dane graczy.

Konfiguracja takich rzeczy jak wymóg wykopanego kamienia, uruchomienie chatu czy slowmode — odbywa się przy użyciu komend, opisanych w sekcji [Komendy](#keyboard-komendy).

----------------------------------------------------
### :speech_balloon: Konfiguracja wiadomości
W pliku messages.yml znajdują się wszystkie wiadomości. Mogą one przyjmować dwie formy:</br></br>

**Pojedyncza linia wiadomości**
```yaml
noPermission: "&cBrak uprawnień"
```

 </br>

**Lista linii wiadomości**
```yaml
chatCleared:
- ""
- "&7Chat został wyczyszczony przez &a{PLAYER}"
- ""
```

</br>Każda sekcja może być pojedynczą linią lub listą — plugin automatycznie wykrywa typ sekcji i dopasowuje wysyłanie wiadomości, można więc utworzyć na przykład taką sekcję:
```yaml
chatCleared: "&7Chat został wyczyszczony przez &a{PLAYER}"
```

----------------------------------------------------
### :keyboard: Komendy
W pluginie jest jedna komenda — **/stonechat**, z aliasami **/chat** i **/c**. Wszystkie funkcje dostępne są z użyciem odpowiednich argumentów:
Komenda | Opis komendy | Uprawnienie
------- | ---- | -----------
/stonechat help | Wyświetla pomoc pluginu | stonechat.help
/stonechat on | Włącza chat | stonechat.on
/stonechat off | Wyłącza chat | stonechat.off
/stonechat toggle | Przełącza uruchomienie chatu | stonechat.toggle
/stonechat clear | Czyści chat | stonechat.clear
/stonechat slow <czas_w_sekundach> | Uruchamia slowmode na chacie, podanie **0** wyłącza slowmode | stonechat.slow
/stonechat stone <liczba> | Zmienia wymóg wykopanego kamienia, podanie **0** wyłącza wymóg | stonechat.stone
/stonechat reload | Ponownie ładuje konfigurację wiadomości i autozapisu | stonechat.reload

----------------------------------------------------
### :no_entry: Uprawnienia
Poza uprawnieniami do komend — dostępne są dodatkowe uprawnienia zbiorcze oraz uprawnienia do omijania ograniczeń:
Uprawnienie | Opis uprawnienia
----------- | ----------------
stonechat.* | Zawiera w sobie wszystkie uprawnienia pluginu
stonechat.bypass.* | Zawiera w sobie wszystkie uprawnienia od omijania ograniczeń
stonechat.bypass.chatoff | Pozwala na pisanie mimo wyłączonego chatu
stonechat.bypass.stone | Pozwala na pisanie mimo niewykopania odpowiedniej liczby kamieni
stonechat.bypass.slowmode | Pozwala na pisanie z pominięciem slowmode

----------------------------------------------------
### :memo: Błędy i propozycje
Wszelkie błędy, propozycje dodania czegoś nowego czy jakiekolwiek pytania — powinny być zadawane [tu](https://github.com/Kamilkime/StoneChat/issues). Jeśli nie posiadasz konta na GitHub'ie, możesz także napisać do mnie na Discordzie — **Kamilkime#9792**.

----------------------------------------------------
### :unlock: Licencja
Plugin rozpowszechniany jest na licencji [Apache License 2.0](https://choosealicense.com/licenses/apache-2.0/), która pozwala na dalszą modyfikację kodu (również w celach komercyjnych), pod warunkiem zachowania oryginalnych informacji o licencji i wskazaniu zmian w oryginalnym kodzie.