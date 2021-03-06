/*
 * Universal Password Manager
 * Copyright (C) 2005-2010 Adrian Smith
 *
 * This file is part of Universal Password Manager.
 *
 * Universal Password Manager is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Universal Password Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Universal Password Manager; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com._17od.upm.gui;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._17od.upm.crypto.CryptoException;
import com._17od.upm.crypto.InvalidPasswordException;
import com._17od.upm.database.AccountInformation;
import com._17od.upm.database.AccountsCSVMarshaller;
import com._17od.upm.database.ExportException;
import com._17od.upm.database.ImportException;
import com._17od.upm.database.PasswordDatabase;
import com._17od.upm.database.PasswordDatabasePersistence;
import com._17od.upm.database.ProblemReadingDatabaseFile;
import com._17od.upm.util.Translator;


public class DatabaseActions
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private MainWindow mainWindow;
    private PasswordDatabase database;
    private ArrayList<String> accountNames;
    private PasswordDatabasePersistence dbPers;

    public DatabaseActions(MainWindow mainWindow)
    {
        this.mainWindow = mainWindow;
        accountNames = new ArrayList<String>();
    }

    /**
     * This method asks the user for the name of a new database and then creates
     * it. If the file already exists then the user is asked if they'd like to
     * overwrite it.
     * @throws CryptoException
     * @throws IOException
     */
    public void newDatabase() throws IOException, CryptoException
    {
        log.debug("new Database action");
        File newDatabaseFile = getSaveAsFile(Translator.translate("newPasswordDatabase"));
        if (newDatabaseFile == null)
        {
            log.debug("FileName is null !");
            return;
        }
        char[] password = askUserForPassword(Translator.translate("enterMasterPassword"), true);
        if(null == password)
        {
            log.debug("user canceled dialog");
            return;
        }
        if (newDatabaseFile.exists())
        {
            log.debug("deleting existing Database");
            newDatabaseFile.delete();
        }
        database = new PasswordDatabase(newDatabaseFile);
        dbPers = new PasswordDatabasePersistence(password);
        saveDatabase();
        mainWindow.doOpenDatabaseActions(database.getDatabaseFile().toString());
        accountNames = getAccountNames();
    }

    public void changeMasterPassword() throws IOException, ProblemReadingDatabaseFile,
                                              CryptoException, PasswordDatabaseException
    {
        //The first task is to get the current master password
        boolean passwordCorrect = false;
        boolean okClicked = true;
        do
        {
            char[] password = askUserForPassword(Translator.translate("enterDatabasePassword"), false);
            if (password == null)
            {
                okClicked = false;
            }
            else
            {
                try
                {
                    dbPers.load(database.getDatabaseFile(), password);
                    passwordCorrect = true;
                }
                catch(InvalidPasswordException e)
                {
                    JOptionPane.showMessageDialog(mainWindow, Translator.translate("incorrectPassword"));
                }
            }
        } while (!passwordCorrect && okClicked);
        //If the master password was entered correctly then the next step is to get the new master password
        if (passwordCorrect == true)
        {
            char[] password = askUserForPassword(Translator.translate("enterNewMasterPassword"), true);
            if(null == password)
            {
                // user canceled dialog
                return;
            }
            else
            {
                //If the user clicked OK and the passwords match then change the database password
                this.dbPers.getEncryptionService().initCipher(password);
                saveDatabase();
            }
        }
    }

    public ArrayList<String> getAccountNames()
    {
        ArrayList<AccountInformation> dbAccounts = database.getAccounts();
        ArrayList<String> accountNames = new ArrayList<String>();
        for (int i=0; i<dbAccounts.size(); i++)
        {
            AccountInformation ai = (AccountInformation) dbAccounts.get(i);
            String accountName = (String) ai.getAccountName();
            accountNames.add(accountName);
        }
        return accountNames;
    }

    /**
     * Prompt the user to enter a password
     * @return The password entered by the user or null of this hit escape/cancel
     */
    private char[] askUserForPassword(String message, boolean mustConfirm)
    {
        log.debug("asking used for password");
        char[] password = null;
        final JPasswordField masterPassword = new JPasswordField("");
        boolean passwordsMatch = false;
        do
        {
            //Get a new master password for this database from the user
            JPasswordField confirmedMasterPassword = new JPasswordField("");
            JOptionPane pane;
            if(true == mustConfirm)
            {
                pane = new JOptionPane(new Object[] {message,
                                                    masterPassword,
                                                    Translator.translate("confirmation"),
                                                     confirmedMasterPassword},
                                    JOptionPane.QUESTION_MESSAGE,
                                    JOptionPane.OK_CANCEL_OPTION);
            }
            else
            {
                pane = new JOptionPane(new Object[] {message, masterPassword },
                                    JOptionPane.QUESTION_MESSAGE,
                                    JOptionPane.OK_CANCEL_OPTION);
            }
            JDialog dialog = pane.createDialog(mainWindow, Translator.translate("masterPassword"));

            dialog.addWindowFocusListener(new WindowAdapter()
                                          {
                                              public void windowGainedFocus(WindowEvent e)
                                              {
                                                  masterPassword.requestFocusInWindow();
                                              }
                                          });
            dialog.requestFocusInWindow();
            pane.requestFocusInWindow();
            masterPassword.requestFocusInWindow();
            dialog.setVisible(true);
            if(pane.getValue() != null && pane.getValue().equals(new Integer(JOptionPane.OK_OPTION)))
            {
                if(true == mustConfirm)
                {
                    if(!Arrays.equals(masterPassword.getPassword(), confirmedMasterPassword.getPassword()))
                    {
                        JOptionPane.showMessageDialog(mainWindow, Translator.translate("passwordsDontMatch"));
                    }
                    else
                    {
                        passwordsMatch = true;
                    }
                }
                password = masterPassword.getPassword();
            }
            else
            {
                log.debug("User clicked cancel");
                return null;
            }
        } while ((passwordsMatch == false) && (true == mustConfirm));
        return password;
    }

    public void openDatabase(String databaseFilename) throws IOException, ProblemReadingDatabaseFile, CryptoException
    {
        openDatabase(databaseFilename, null);
    }

    public void openDatabase(String databaseFilename, char[] password) throws IOException, ProblemReadingDatabaseFile, CryptoException
    {
        boolean passwordCorrect = false;
        boolean okClicked = true;
        while (!passwordCorrect && okClicked) {
            // If we weren't given a password then ask the user to enter one
            if(password == null)
            {
                password = askUserForPassword(Translator.translate("enterDatabasePassword"), false);
                if(password == null)
                {
                    okClicked = false;
                }
            }
            else
            {
                okClicked = true;
            }

            if(true == okClicked)
            {
                try
                {
                    dbPers = new PasswordDatabasePersistence();
                    database = dbPers.load(new File(databaseFilename), password);
                    passwordCorrect = true;
                }
                catch(InvalidPasswordException e)
                {
                    JOptionPane.showMessageDialog(mainWindow, Translator.translate("incorrectPassword"));
                    password = null;
                }
            }
        }
        if(passwordCorrect)
        {
            mainWindow.doOpenDatabaseActions(database.getDatabaseFile().toString());
            accountNames = getAccountNames();
        }
    }

    public void openDatabase() throws IOException, ProblemReadingDatabaseFile, CryptoException
    {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(Translator.translate("openDatabase"));
        int returnVal = fc.showOpenDialog(mainWindow);
        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            File databaseFile = fc.getSelectedFile();
            if(databaseFile.exists())
            {
                openDatabase(databaseFile.getAbsolutePath());
                accountNames = getAccountNames();
            }
            else
            {
                JOptionPane.showMessageDialog(mainWindow, Translator.translate("fileDoesntExistWithName", databaseFile.getAbsolutePath()), Translator.translate("fileDoesntExist"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void deleteAccount() throws IOException, CryptoException, ProblemReadingDatabaseFile, PasswordDatabaseException
    {
            SortedListModel listview = (SortedListModel) mainWindow.getAccountsListview().getModel();
            String selectedAccName = (String) mainWindow.getAccountsListview().getSelectedValue();
            int buttonSelected = JOptionPane.showConfirmDialog(mainWindow,
                                                            Translator.translate("askConfirmDeleteAccount", selectedAccName),
                                                            Translator.translate("confirmDeleteAccount"),
                                                            JOptionPane.YES_NO_OPTION);
            if(buttonSelected == JOptionPane.OK_OPTION)
            {
                //Remove the account from the listview, accountNames arraylist & the database
                listview.removeElement(selectedAccName);
                int i = accountNames.indexOf(selectedAccName);
                if(-1 != i)
                {
                    accountNames.remove(i);
                }
                else
                {
                    System.err.println("Could not delete the Account " + selectedAccName + " from accountNames !");
                }
                database.deleteAccount(selectedAccName);
                saveDatabase();
                //[1375385] Call the filter method so that the listview is
                //reinitialised with the remaining matching items
                filter();
            }
    }

    public void addAccount() throws IOException, CryptoException, ProblemReadingDatabaseFile, PasswordDatabaseException
    {
        //Initialise the AccountDialog
        AccountInformation accInfo = new AccountInformation();
        AccountDialog accDialog = new AccountDialog(accInfo, mainWindow, false, accountNames);
        accDialog.pack();
        accDialog.setLocationRelativeTo(mainWindow);
        accDialog.setVisible(true);
        //If the user press OK then save the new account to the database
        if(accDialog.okClicked())
        {
            database.deleteAccount(accInfo.getAccountName());
            database.addAccount(accInfo);
            saveDatabase();
            accountNames = getAccountNames();
            accountNames.add(accInfo.getAccountName());
            //[1375390] Ensure that the listview is properly filtered after an add
            filter();
        }
    }

    public AccountInformation getSelectedAccount()
    {
        String selectedAccName = (String) mainWindow.getAccountsListview().getSelectedValue();
        return database.getAccount(selectedAccName);
    }

    public void viewAccount()
    {
        AccountInformation accInfo = getSelectedAccount();
        AccountDialog accDialog = new AccountDialog(accInfo, mainWindow, true, accountNames);
        accDialog.pack();
        accDialog.setLocationRelativeTo(mainWindow);
        accDialog.setVisible(true);
    }

    public void editAccount() throws ProblemReadingDatabaseFile, IOException, CryptoException, PasswordDatabaseException
    {
        AccountInformation accInfo = getSelectedAccount();
        String selectedAccName = (String) accInfo.getAccountName();
        AccountDialog accDialog = new AccountDialog(accInfo, mainWindow, false, accountNames);
        accDialog.pack();
        accDialog.setLocationRelativeTo(mainWindow);
        accDialog.setVisible(true);
        //If the ok button was clicked then save the account to the database and update the
        //listview with the new account name (if it's changed)
        if (accDialog.okClicked() && accDialog.getAccountChanged())
        {
            accInfo = accDialog.getAccount();
            database.deleteAccount(selectedAccName);
            database.addAccount(accInfo);
            saveDatabase();
            //If the new account name is different to the old account name then update the
            //accountNames array and refilter the listview
            if (!accInfo.getAccountName().equals(selectedAccName))
            {
                int i = accountNames.indexOf(selectedAccName);
                accountNames.remove(i);
                accountNames.add(accInfo.getAccountName());
                //[1375390] Ensure that the listview is properly filtered after an edit
                filter();
            }
        }
    }

    public void filter()
    {
        String filterStr = mainWindow.getSearchField().getText().toLowerCase();
        ArrayList<String> filteredAccountsList = new ArrayList<String>();
        for(int i = 0; i < accountNames.size(); i++)
        {
            String accountName = (String) accountNames.get(i);
            if(filterStr.equals("") || accountName.toLowerCase().indexOf(filterStr) != -1)
            {
                filteredAccountsList.add(accountName);
            }
        }
        mainWindow.populateListview(filteredAccountsList);
        //If there's only one item in the Listview then select it
        if(mainWindow.getAccountsListview().getModel().getSize() == 1)
        {
            mainWindow.getAccountsListview().setSelectedIndex(0);
        }
    }

    public void options()
    {
        OptionsDialog oppDialog = new OptionsDialog(mainWindow);
        oppDialog.pack();
        oppDialog.setLocationRelativeTo(mainWindow);
        oppDialog.setVisible(true);
        if(oppDialog.hasLanguageChanged())
        {
            mainWindow.initialiseControlsWithDefaultLanguage();
            if(database != null)
            {
                setStatusBarText();
            }
        }
    }

    public void showAbout()
    {
        AboutDialog aboutDialog = new AboutDialog(mainWindow);
        aboutDialog.pack();
        aboutDialog.setLocationRelativeTo(mainWindow);
        aboutDialog.setVisible(true);
    }

    public void resetSearch()
    {
        mainWindow.getSearchField().setText("");
    }

    public void exitApplication()
    {
        System.exit(0);
    }

    public void export()
    {
        File exportFile = getSaveAsFile(Translator.translate("exportFile"));
        if (exportFile == null)
        {
            return;
        }
        if (exportFile.exists())
        {
            exportFile.delete();
        }
        AccountsCSVMarshaller marshaller = new AccountsCSVMarshaller();
        try
        {
            marshaller.marshal(this.database.getAccounts(), exportFile);
        }
        catch (ExportException e)
        {
            JOptionPane.showMessageDialog(mainWindow, e.getMessage(), Translator.translate("problemExporting"), JOptionPane.ERROR_MESSAGE);
        }
    }

    public void importAccounts() throws ProblemReadingDatabaseFile, IOException, CryptoException, PasswordDatabaseException
    {
            // Prompt for the file to import
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle(Translator.translate("import"));
            int returnVal = fc.showOpenDialog(mainWindow);
            if(returnVal == JFileChooser.APPROVE_OPTION)
            {
                File csvFile = fc.getSelectedFile();
                // Unmarshall the accounts from the CSV file
                try
                {
                    AccountsCSVMarshaller marshaller = new AccountsCSVMarshaller();
                    ArrayList<AccountInformation> accountsInCSVFile = marshaller.unmarshal(csvFile);
                    ArrayList<AccountInformation> accountsToImport = new ArrayList<AccountInformation>();
                    boolean importCancelled = false;
                    // Add each account to the open database. If the account
                    // already exits the prompt to overwrite
                    for(int i=0; i<accountsInCSVFile.size(); i++)
                    {
                        AccountInformation importedAccount = (AccountInformation) accountsInCSVFile.get(i);
                        if(database.getAccount(importedAccount.getAccountName()) != null)
                        {
                            Object[] options = {"Overwrite Existing", "Keep Existing", "Cancel"};
                            int answer = JOptionPane.showOptionDialog(
                                    mainWindow,
                                    Translator.translate("importExistingQuestion", importedAccount.getAccountName()),
                                    Translator.translate("importExistingTitle"),
                                    JOptionPane.YES_NO_CANCEL_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    options,
                                    options[1]);
                            if(answer == 1)
                            {
                                continue; // If keep existing then continue to the next iteration
                            }
                            else if(answer == 2)
                            {
                                importCancelled = true;
                                break; // Cancel the import
                            }
                        }
                        accountsToImport.add(importedAccount);
                    }
                    if (!importCancelled && accountsToImport.size() > 0)
                    {
                        for(int i=0; i<accountsToImport.size(); i++)
                        {
                            AccountInformation accountToImport = (AccountInformation) accountsToImport.get(i);
                            database.deleteAccount(accountToImport.getAccountName());
                            database.addAccount(accountToImport);
                        }
                        saveDatabase();
                        accountNames = getAccountNames();
                        filter();
                    }
                }
                catch (ImportException e)
                {
                    JOptionPane.showMessageDialog(mainWindow, e.getMessage(), Translator.translate("problemImporting"), JOptionPane.ERROR_MESSAGE);
                }
                catch (IOException e)
                {
                    JOptionPane.showMessageDialog(mainWindow, e.getMessage(), Translator.translate("problemImporting"), JOptionPane.ERROR_MESSAGE);
                }
                catch (CryptoException e)
                {
                    JOptionPane.showMessageDialog(mainWindow, e.getMessage(), Translator.translate("problemImporting"), JOptionPane.ERROR_MESSAGE);
                }
            }
    }

    /**
     * This method prompts the user for the name of a file.
     * If the file exists then it will ask if they want to overwrite (the file isn't overwritten though,
     * that would be done by the calling method)
     * @param title The string title to put on the dialog
     * @return The file to save to or null
     */
    private File getSaveAsFile(String title)
    {
        File selectedFile;
        boolean gotValidFile = false;
        do
        {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle(title);
            int returnVal = fc.showSaveDialog(mainWindow);
            if(returnVal != JFileChooser.APPROVE_OPTION)
            {
                return null;
            }
            selectedFile = fc.getSelectedFile();
            //Warn the user if the database file already exists
            if(selectedFile.exists())
            {
                Object[] options = {"Yes", "No"};
                int i = JOptionPane.showOptionDialog(mainWindow,
                                                     Translator.translate("fileAlreadyExistsWithFileName",
                                                                        selectedFile.getAbsolutePath()) + '\n' +
                                                                          Translator.translate("overwrite"),
                                                     Translator.translate("fileAlreadyExists"),
                                                     JOptionPane.YES_NO_OPTION,
                                                     JOptionPane.QUESTION_MESSAGE,
                                                     null,
                                                     options,
                                                     options[1]);
                if(i == JOptionPane.YES_OPTION)
                {
                    gotValidFile = true;
                }
            }
            else
            {
                gotValidFile = true;
            }
        } while (!gotValidFile);
        return selectedFile;
    }

    private void saveDatabase() throws IOException, CryptoException
    {
        dbPers.save(database);
    }

    private void setStatusBarText()
    {
        String status = null;
        Color color = null;
        status = Translator.translate("localDatabase");
        color = Color.BLACK;
        mainWindow.getStatusBar().setText(status);
        mainWindow.getStatusBar().setForeground(color);
    }
}
