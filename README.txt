Universal Password Manager
--------------------------
http://upm.sourceforge.net

Copyright (C) 2005-2010 Adrian Smith
Copyright (C) 2011 Lars Pötter

Universal Password Manager is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

Universal Password Manager is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Universal Password Manager; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA


Contents
--------
1. Overview
2. Features
3. Internationalization
4. Roadmap
5. History

1. Overview
   --------
   Universal Password Manager (UPM) allows you to store usernames, passwords, URLs,
   and generic notes in an encrypted database protected by one master password.

   There are several open source password managers available so what makes UPM
   different? It's strongest feature is  it's simplicity - it provides a small
   number of very strong features with no clutter.


2. Features
   --------
   .Small, fast and lean
   .Uses AES for database encryption
   .Written in Java/SWING
   .Fast account searching
   .Streamlined for those who are more comfortable using the keyboard only

3. Internationalization
   --------------------
   UPM supports multiple languages. For each language a separate resource bundle
   file is included. UPM includes resource bundles for
     * English
     * Czech
     * German
     * Italian
     * Spanish
   I'd really appreciate help from anyone willing to provide translations for
   UPM's messages. You can find the English resource bundle (upm.properties) in
   the upm.jar file that ships with UPM (Winzip, 7-Zip, or some other zip tool
   can be used to uncompress the JAR file).

4. Roadmap
   -------
   .Native Linux distributions (RPM, DEB)


5. History
   -------
   25-Oct-2011 : Version 2.0.0
      - removed Database sharing
      - removed adaptations to different Operating Systems
      - improved security
      - changed logging
      - moved code to java 1.6
      - improved database format
      - code cleanup

   01-Feb-2011 : Version 1.6.1
      - Enable CTRL-C on the account details password field

   22-Jun-2010 : Version 1.6
      - Added the ability to export/import from/to a CSV file
      - Added a random password generator
      - Removed the dependency on JCE Unlimited Strength Jurisdiction Policy Files
      - Added support for using HTTPS URLs with database sharing
      - Set focus on the password field on all enter master password dialogs
      - Added Spanish translation (courtesy of Victor Alfonso Pineda)
      - Resize the "Notes" text area when the Account Details dialog is resized

   31-Dec-2006 : Version 1.5
      - Added Czech and German translations (courtesy of Petr Ustohal)
      - Added username and password fields to the HTTP proxy options
      - The HTTP Proxy can be enabled/disabled using a checkbox while still
        retaining the settings for later use
      - In the account information dialog the password is masked by default.
        To view it you untick a checkbox. Thanks to Jelle De Pot for suggesting
        this feature
      - Removed the French localisation (no French translator available)
      - Fixed a problem that caused a NullPointerException on the Mac OS X
        version when exiting the Options dialog after changing the language
      - A few small bugfixes here and there


   27-Nov-2006 : Version 1.4
      - Added support for internationalisation. English and French language bundles
        are included.
      - Added support for editing the account name
      - Double clicking or hitting enter on an account opens the account in
        read-only view mode. Edit it still available on the toolbar, menu bar and
        as a keyboard shortcut.


   23-Sep-2006 : Version 1.3
      The primary purpose of this release is the introduction of AES database encryption.

      Changes
      - Use the BouncyCastle AES provider to encrypt the database (PBEWithSHA256And256BitAES-CBC-BC)
      - Moved the database structural version out of the encrypted portion of the database.
        This means that UPM can use the database version to decide how to decrypt the database.


   09-Sep-2006 : Version 1.2
      This is primarily a bug fix release.

      Bugs Fixed
      [1551461] - Database sync problem on Mac

      Other Changes
      - Tidy up the HTTP proxy code in the HTTPTransport class
      - Don't prompt the user for the password on each database sync
      - Don't bother showing a "success" dialog on each database sync
      - Don't bother asking the user to sync database on application exit
      - Change the status bar message for local databases
      - When user edits and account check if it has changed before marking the database as dirty


   22-Sep-2006 : Version 1.1
      This release has one major new feature, database sharing. This feature allows you to have one database that can be accessed from many machines.


   21-Dec-2005 : Version 1.0
      This is first stable release of UPM. It's a bugfix release only and has no functional differences to version 1.0b1. Databases created with 1.0b1 are fully compatible with 1.0.

      Bugs Fixed
      [1375407] - Possible to change account name to existing account name
      [1375397] - Possible to add duplicate account when account name filtered
      [1375390] - Incorrect items listed after an add when list is filtered
      [1375385] - Error deleting last account in the listview
      [1374280] - Wrong URL on About page


   5-Dec-2005 : Version 1.0b1
      First release.

