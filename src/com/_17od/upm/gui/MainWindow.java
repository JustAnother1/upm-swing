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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com._17od.upm.crypto.CryptoException;
import com._17od.upm.database.AccountInformation;
import com._17od.upm.database.ProblemReadingDatabaseFile;
import com._17od.upm.util.Preferences;
import com._17od.upm.util.Translator;
import com._17od.upm.util.Util;

/**
 * This is the main application entry class
 */
public class MainWindow extends JFrame implements ActionListener
{
    private static final long serialVersionUID = 1L;
    private static final String applicationName = "Universal Password Manager";
    public static final String NEW_DATABASE_TXT = "newDatabaseMenuItem";
    public static final String OPEN_DATABASE_TXT = "openDatabaseMenuItem";
    public static final String CHANGE_MASTER_PASSWORD_TXT = "changeMasterPasswordMenuItem";
    public static final String ADD_ACCOUNT_TXT = "addAccountMenuItem";
    public static final String EDIT_ACCOUNT_TXT = "editAccountMenuItem";
    public static final String DELETE_ACCOUNT_TXT = "deleteAccountMenuItem";
    public static final String VIEW_ACCOUNT_TXT = "viewAccountMenuItem";
    public static final String COPY_USERNAME_TXT = "copyUsernameMenuItem";
    public static final String COPY_PASSWORD_TXT = "copyPasswordMenuItem";
    public static final String OPTIONS_TXT = "optionsMenuItem";
    public static final String ABOUT_TXT = "aboutMenuItem";
    public static final String RESET_SEARCH_TXT = "resetSearchMenuItem";
    public static final String EXIT_TXT = "exitMenuItem";
    public static final String EXPORT_TXT = "exportMenuItem";
    public static final String IMPORT_TXT = "importMenuItem";

    private JButton addAccountButton;
    private JButton editAccountButton;
    private JButton deleteAccountButton;
    private JButton copyUsernameButton;
    private JButton copyPasswordButton;
    private JButton optionsButton;
    private JTextField searchField;
    private JButton resetSearchButton;
    private JLabel searchIcon;
    private JMenu databaseMenu;
    private JMenuItem newDatabaseMenuItem;
    private JMenuItem openDatabaseMenuItem;
    private JMenuItem changeMasterPasswordMenuItem;
    private JMenuItem exitMenuItem;
    private JMenu helpMenu;
    private JMenuItem aboutMenuItem;
    private JMenu accountMenu;
    private JMenuItem addAccountMenuItem;
    private JMenuItem editAccountMenuItem;
    private JMenuItem deleteAccountMenuItem;
    private JMenuItem viewAccountMenuItem;
    private JMenuItem copyUsernameMenuItem;
    private JMenuItem copyPasswordMenuItem;
    private JMenuItem exportMenuItem;
    private JMenuItem importMenuItem;
    private JList accountsListview;
    private JLabel statusBar = new JLabel(" ");
    private DatabaseActions dbActions;

    public MainWindow(String title)
    {
        super(title);
        try
        {
            Preferences.load();
        }
        catch(FileNotFoundException e1)
        {
            Util.errorHandler(e1);
        }
        catch(IOException e1)
        {
            Util.errorHandler(e1);
        }
        Translator.initialise();
        setIconImage(Util.loadImage("upm.gif").getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dbActions = new DatabaseActions(this);
        //Set up the content pane.
        addComponentsToPane();
        //Display the window.
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        //Load the startup database if it's configured
        String db = Preferences.get(Preferences.DB_TO_LOAD_ON_STARTUP);
        if(db != null && !db.equals(""))
        {
            File dbFile = new File(db);
            if(!dbFile.exists())
            {
                Util.errorHandler(new Exception(Translator.translate("dbDoesNotExist", db)));
            }
            else
            {
                try
                {
                    dbActions.openDatabase(db);
                }
                catch(IOException e)
                {
                    Util.errorHandler(e);
                }
                catch(ProblemReadingDatabaseFile e)
                {
                    Util.errorHandler(e);
                }
                catch(CryptoException e)
                {
                    Util.errorHandler(e);
                }
            }
        }

        // Give the search field focus
        // I'm using requestFocusInWindow() rather than requestFocus()
        // because the javadocs recommend it
        searchField.requestFocusInWindow();
    }

    public static void main(String[] args)
    {
        // This is the start of the Programm.
        // So first things first
        // add a new Uncaught Exception Handler
        Thread.setDefaultUncaughtExceptionHandler(new UpmUncaughtExceptionHandler());

        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                //Use the System look and feel
                try
                {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
                catch(ClassNotFoundException e)
                {
                    Util.errorHandler(e);
                }
                catch(InstantiationException e)
                {
                    Util.errorHandler(e);
                }
                catch(IllegalAccessException e)
                {
                    Util.errorHandler(e);
                }
                catch(UnsupportedLookAndFeelException e)
                {
                    Util.errorHandler(e);
                }
                Double jvmVersion = new Double(System.getProperty("java.specification.version"));
                if (jvmVersion.doubleValue() < 1.6)
                {
                    JOptionPane.showMessageDialog(null,
                                                  Translator.translate("requireJava16"),
                                                  Translator.translate("problem"),
                                                  JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
                else
                {
                    new MainWindow(applicationName);
                }
            }
        });
    }

    private void addComponentsToPane()
    {
        //Ensure the layout manager is a BorderLayout
        if (!(getContentPane().getLayout() instanceof GridBagLayout))
        {
                getContentPane().setLayout(new GridBagLayout());
        }
        //Create the menubar
        setJMenuBar(createMenuBar());
        GridBagConstraints c = new GridBagConstraints();
        //The toolbar Row
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets = new Insets(0, 0, 0, 0);
        c.weightx = 0;
        c.weighty = 0;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        Component toolbar = createToolBar();
        getContentPane().add(toolbar, c);
        //Keep the frame background color consistent
        getContentPane().setBackground(toolbar.getBackground());
        //The seperator Row
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 0, 0, 0);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        getContentPane().add(new JSeparator(), c);
        //The search field row
        searchIcon = new JLabel(Util.loadImage("search.gif"));
        searchIcon.setDisabledIcon(Util.loadImage("search_d.gif"));
        searchIcon.setEnabled(false);
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(5, 1, 5, 1);
        c.weightx = 0;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        getContentPane().add(searchIcon, c);
        searchField = new JTextField(15);
        searchField.setEnabled(false);
        searchField.setMinimumSize(searchField.getPreferredSize());
        searchField.getDocument().addDocumentListener(new DocumentListener()
        {
            public void changedUpdate(DocumentEvent e)
            {
                //This method never seems to be called
                System.out.println("Never did happen !");
            }
            public void insertUpdate(DocumentEvent e)
            {
                dbActions.filter();
            }
            public void removeUpdate(DocumentEvent e)
            {
                dbActions.filter();
            }
        });
        searchField.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    dbActions.resetSearch();
                }
                else if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    //If the user hits the enter key in the search field and there's only one item
                    //in the listview then open that item (this code assumes that the one item in
                    //the listview has already been selected. this is done automatically in the
                    //DatabaseActions.filter() method)
                    if (accountsListview.getModel().getSize() == 1)
                    {
                        viewAccountMenuItem.doClick();
                    }
                }
            }
        });
        c.gridx = 1;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(5, 1, 5, 1);
        c.weightx = 0;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        getContentPane().add(searchField, c);
        resetSearchButton = new JButton(Util.loadImage("stop.gif"));
        resetSearchButton.setDisabledIcon(Util.loadImage("stop_d.gif"));
        resetSearchButton.setEnabled(false);
        resetSearchButton.setToolTipText(Translator.translate(RESET_SEARCH_TXT));
        resetSearchButton.setActionCommand(RESET_SEARCH_TXT);
        resetSearchButton.addActionListener(this);
        resetSearchButton.setBorder(BorderFactory.createEmptyBorder());
        resetSearchButton.setFocusable(false);
        c.gridx = 2;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(5, 1, 5, 1);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        getContentPane().add(resetSearchButton, c);
        //The accounts listview row
        accountsListview = new JList();
        accountsListview.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountsListview.setSelectedIndex(0);
        accountsListview.setVisibleRowCount(10);
        accountsListview.setModel(new SortedListModel());
        JScrollPane accountsScrollList = new JScrollPane(accountsListview, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        accountsListview.addFocusListener(new FocusAdapter()
        {
            public void focusGained(FocusEvent e)
            {
             //If the listview gets focus, there is one ore more items in the listview and there is nothing
             //already selected, then select the first item in the list
                if (accountsListview.getModel().getSize() > 0 && accountsListview.getSelectedIndex() == -1)
                {
                    accountsListview.setSelectionInterval(0, 0);
                }
            }
        });
        accountsListview.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                setButtonState();
            }
        });
        accountsListview.addMouseListener(new MouseAdapter()
        {
           public void mouseClicked(MouseEvent e)
           {
               if (e.getClickCount() == 2)
               {
                   viewAccountMenuItem.doClick();
               }
           }
        });
        accountsListview.addKeyListener(new KeyAdapter()
        {
            public void keyReleased(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    viewAccountMenuItem.doClick();
                }
            }
        });
        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(0, 1, 1, 1);
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.BOTH;
        getContentPane().add(accountsScrollList, c);
        // Add the statusbar
        c.gridx = 0;
        c.gridy = 4;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(0, 1, 1, 1);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        getContentPane().add(statusBar, c);
    }

    private JToolBar createToolBar()
    {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setRollover(true);
        // The "Add Account" button
        addAccountButton = new JButton();
        addAccountButton.setToolTipText(Translator.translate(ADD_ACCOUNT_TXT));
        addAccountButton.setIcon(Util.loadImage("add_account.gif"));
        addAccountButton.setDisabledIcon(Util.loadImage("add_account_d.gif"));;
        addAccountButton.addActionListener(this);
        addAccountButton.setEnabled(false);
        addAccountButton.setActionCommand(ADD_ACCOUNT_TXT);
        toolbar.add(addAccountButton);
        // The "Edit Account" button
        editAccountButton = new JButton();
        editAccountButton.setToolTipText(Translator.translate(EDIT_ACCOUNT_TXT));
        editAccountButton.setIcon(Util.loadImage("edit_account.gif"));
        editAccountButton.setDisabledIcon(Util.loadImage("edit_account_d.gif"));;
        editAccountButton.addActionListener(this);
        editAccountButton.setEnabled(false);
        editAccountButton.setActionCommand(EDIT_ACCOUNT_TXT);
        toolbar.add(editAccountButton);
        // The "Delete Account" button
        deleteAccountButton = new JButton();
        deleteAccountButton.setToolTipText(Translator.translate(DELETE_ACCOUNT_TXT));
        deleteAccountButton.setIcon(Util.loadImage("delete_account.gif"));
        deleteAccountButton.setDisabledIcon(Util.loadImage("delete_account_d.gif"));;
        deleteAccountButton.addActionListener(this);
        deleteAccountButton.setEnabled(false);
        deleteAccountButton.setActionCommand(DELETE_ACCOUNT_TXT);
        toolbar.add(deleteAccountButton);
        toolbar.addSeparator();
        // The "Copy Username" button
        copyUsernameButton = new JButton();
        copyUsernameButton.setToolTipText(Translator.translate(COPY_USERNAME_TXT));
        copyUsernameButton.setIcon(Util.loadImage("copy_username.gif"));
        copyUsernameButton.setDisabledIcon(Util.loadImage("copy_username_d.gif"));;
        copyUsernameButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                copyUsernameToClipboard();
            }
        });
        copyUsernameButton.setEnabled(false);
        toolbar.add(copyUsernameButton);
        // The "Copy Password" button
        copyPasswordButton = new JButton();
        copyPasswordButton.setToolTipText(Translator.translate(COPY_PASSWORD_TXT));
        copyPasswordButton.setIcon(Util.loadImage("copy_password.gif"));
        copyPasswordButton.setDisabledIcon(Util.loadImage("copy_password_d.gif"));;
        copyPasswordButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                copyPasswordToClipboard();
            }
        });
        copyPasswordButton.setEnabled(false);
        toolbar.add(copyPasswordButton);
        toolbar.addSeparator();
        // The "Option" button
        optionsButton = new JButton();
        optionsButton.setToolTipText(Translator.translate(OPTIONS_TXT));
        optionsButton.setIcon(Util.loadImage("options.gif"));
        optionsButton.setDisabledIcon(Util.loadImage("options_d.gif"));;
        optionsButton.addActionListener(this);
        optionsButton.setEnabled(true);
        optionsButton.setActionCommand(OPTIONS_TXT);
        toolbar.add(optionsButton);
        return toolbar;
    }

    private JMenuBar createMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        databaseMenu = new JMenu(Translator.translate("databaseMenu"));
        databaseMenu.setMnemonic(KeyEvent.VK_D);
        menuBar.add(databaseMenu);
        newDatabaseMenuItem = new JMenuItem(Translator.translate(NEW_DATABASE_TXT), KeyEvent.VK_N);
        newDatabaseMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        databaseMenu.add(newDatabaseMenuItem);
        newDatabaseMenuItem.addActionListener(this);
        newDatabaseMenuItem.setActionCommand(NEW_DATABASE_TXT);
        openDatabaseMenuItem = new JMenuItem(Translator.translate(OPEN_DATABASE_TXT), KeyEvent.VK_O);
        openDatabaseMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        databaseMenu.add(openDatabaseMenuItem);
        openDatabaseMenuItem.addActionListener(this);
        openDatabaseMenuItem.setActionCommand(OPEN_DATABASE_TXT);
        databaseMenu.addSeparator();
        changeMasterPasswordMenuItem = new JMenuItem(Translator.translate(CHANGE_MASTER_PASSWORD_TXT), KeyEvent.VK_G);
        changeMasterPasswordMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        databaseMenu.add(changeMasterPasswordMenuItem);
        changeMasterPasswordMenuItem.addActionListener(this);
        changeMasterPasswordMenuItem.setEnabled(false);
        changeMasterPasswordMenuItem.setActionCommand(CHANGE_MASTER_PASSWORD_TXT);
        databaseMenu.addSeparator();
        exportMenuItem = new JMenuItem(Translator.translate(EXPORT_TXT));
        databaseMenu.add(exportMenuItem);
        exportMenuItem.addActionListener(this);
        exportMenuItem.setEnabled(false);
        exportMenuItem.setActionCommand(EXPORT_TXT);
        importMenuItem = new JMenuItem(Translator.translate(IMPORT_TXT));
        databaseMenu.add(importMenuItem);
        importMenuItem.addActionListener(this);
        importMenuItem.setEnabled(false);
        importMenuItem.setActionCommand(IMPORT_TXT);
        accountMenu = new JMenu(Translator.translate("accountMenu"));
        accountMenu.setMnemonic(KeyEvent.VK_A);
        menuBar.add(accountMenu);
        addAccountMenuItem = new JMenuItem(Translator.translate(ADD_ACCOUNT_TXT), KeyEvent.VK_A);
        addAccountMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        accountMenu.add(addAccountMenuItem);
        addAccountMenuItem.addActionListener(this);
        addAccountMenuItem.setEnabled(false);
        addAccountMenuItem.setActionCommand(ADD_ACCOUNT_TXT);
        editAccountMenuItem = new JMenuItem(Translator.translate(EDIT_ACCOUNT_TXT), KeyEvent.VK_E);
        editAccountMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        accountMenu.add(editAccountMenuItem);
        editAccountMenuItem.addActionListener(this);
        editAccountMenuItem.setEnabled(false);
        editAccountMenuItem.setActionCommand(EDIT_ACCOUNT_TXT);
        deleteAccountMenuItem = new JMenuItem(Translator.translate(DELETE_ACCOUNT_TXT), KeyEvent.VK_D);
        deleteAccountMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        accountMenu.add(deleteAccountMenuItem);
        deleteAccountMenuItem.addActionListener(this);
        deleteAccountMenuItem.setEnabled(false);
        deleteAccountMenuItem.setActionCommand(DELETE_ACCOUNT_TXT);
        viewAccountMenuItem = new JMenuItem(Translator.translate(VIEW_ACCOUNT_TXT), KeyEvent.VK_V);
        viewAccountMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        accountMenu.add(viewAccountMenuItem);
        viewAccountMenuItem.addActionListener(this);
        viewAccountMenuItem.setEnabled(false);
        viewAccountMenuItem.setActionCommand(VIEW_ACCOUNT_TXT);
        copyUsernameMenuItem = new JMenuItem(Translator.translate(COPY_USERNAME_TXT), KeyEvent.VK_U);
        copyUsernameMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        accountMenu.add(copyUsernameMenuItem);
        copyUsernameMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copyUsernameToClipboard();
            }
        });
        copyUsernameMenuItem.setEnabled(false);
        copyUsernameMenuItem.setActionCommand(COPY_USERNAME_TXT);
        copyPasswordMenuItem = new JMenuItem(Translator.translate(COPY_PASSWORD_TXT), KeyEvent.VK_P);
        copyPasswordMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        accountMenu.add(copyPasswordMenuItem);
        copyPasswordMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                copyPasswordToClipboard();
            }
        });
        copyPasswordMenuItem.setEnabled(false);
        copyPasswordMenuItem.setActionCommand(COPY_PASSWORD_TXT);
        exitMenuItem = new JMenuItem(Translator.translate(EXIT_TXT), KeyEvent.VK_X);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        exitMenuItem.addActionListener(this);
        exitMenuItem.setActionCommand(EXIT_TXT);
        aboutMenuItem = new JMenuItem(Translator.translate(ABOUT_TXT), KeyEvent.VK_A);
        aboutMenuItem.addActionListener(this);
        aboutMenuItem.setActionCommand(ABOUT_TXT);
        databaseMenu.addSeparator();
        databaseMenu.add(exitMenuItem);
        helpMenu = new JMenu(Translator.translate("helpMenu"));
        helpMenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(helpMenu);
        helpMenu.add(aboutMenuItem);
        return menuBar;
    }

    public JList getAccountsListview()
    {
        return accountsListview;
    }

    private void copyUsernameToClipboard()
    {
        AccountInformation accInfo = dbActions.getSelectedAccount();
        copyToClipboard(new String(accInfo.getUserId()));
    }

    private void copyPasswordToClipboard()
    {
        AccountInformation accInfo = dbActions.getSelectedAccount();
        copyToClipboard(new String(accInfo.getPassword()));
    }

    private void copyToClipboard(String s)
    {
        StringSelection stringSelection = new StringSelection(s);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, stringSelection);
    }

    public JTextField getSearchField()
    {
        return searchField;
    }

    public void actionPerformed(ActionEvent event)
    {
        if (event.getActionCommand() == MainWindow.NEW_DATABASE_TXT)
        {
            try
            {
                dbActions.newDatabase();
            }
            catch(IOException e)
            {
                Util.errorHandler(e);
            }
            catch(CryptoException e)
            {
                Util.errorHandler(e);
            }
        }
        else if (event.getActionCommand() == MainWindow.OPEN_DATABASE_TXT)
        {
            try
            {
                dbActions.openDatabase();
            }
            catch(IOException e)
            {
                Util.errorHandler(e);
            }
            catch(ProblemReadingDatabaseFile e)
            {
                Util.errorHandler(e);
            }
            catch(CryptoException e)
            {
                Util.errorHandler(e);
            }
        }
        else if (event.getActionCommand() == MainWindow.ADD_ACCOUNT_TXT)
        {
            try
            {
                dbActions.addAccount();
            }
            catch(IOException e)
            {
                Util.errorHandler(e);
            }
            catch(CryptoException e)
            {
                Util.errorHandler(e);
            }
            catch(ProblemReadingDatabaseFile e)
            {
                Util.errorHandler(e);
            }
            catch(PasswordDatabaseException e)
            {
                Util.errorHandler(e);
            }
        }
        else if (event.getActionCommand() == MainWindow.EDIT_ACCOUNT_TXT)
        {
            try
            {
                dbActions.editAccount();
            }
            catch(ProblemReadingDatabaseFile e)
            {
                Util.errorHandler(e);
            }
            catch(IOException e)
            {
                Util.errorHandler(e);
            }
            catch(CryptoException e)
            {
                Util.errorHandler(e);
            }
            catch(PasswordDatabaseException e)
            {
                Util.errorHandler(e);
            }
        }
        else if (event.getActionCommand() == MainWindow.DELETE_ACCOUNT_TXT)
        {
            try
            {
                dbActions.deleteAccount();
            }
            catch(IOException e)
            {
                Util.errorHandler(e);
            }
            catch(CryptoException e)
            {
                Util.errorHandler(e);
            }
            catch(ProblemReadingDatabaseFile e)
            {
                Util.errorHandler(e);
            }
            catch(PasswordDatabaseException e)
            {
                Util.errorHandler(e);
            }
        }
        else if (event.getActionCommand() == MainWindow.VIEW_ACCOUNT_TXT)
        {
            dbActions.viewAccount();
        }
        else if (event.getActionCommand() == MainWindow.OPTIONS_TXT)
        {
            dbActions.options();
        }
        else if (event.getActionCommand() == MainWindow.ABOUT_TXT)
        {
            dbActions.showAbout();
        }
        else if (event.getActionCommand() == MainWindow.RESET_SEARCH_TXT)
        {
            dbActions.resetSearch();
        }
        else if (event.getActionCommand() == MainWindow.CHANGE_MASTER_PASSWORD_TXT)
        {
            try
            {
                dbActions.changeMasterPassword();
            }
            catch(IOException e)
            {
                Util.errorHandler(e);
            }
            catch(ProblemReadingDatabaseFile e)
            {
                Util.errorHandler(e);
            }
            catch(CryptoException e)
            {
                Util.errorHandler(e);
            }
            catch(PasswordDatabaseException e)
            {
                Util.errorHandler(e);
            }
        }
        else if (event.getActionCommand() == MainWindow.EXIT_TXT)
        {
            dbActions.exitApplication();
        }
        else if (event.getActionCommand() == MainWindow.EXPORT_TXT)
        {
            dbActions.export();
        }
        else if (event.getActionCommand() == MainWindow.IMPORT_TXT)
        {
            try
            {
                dbActions.importAccounts();
            }
            catch(ProblemReadingDatabaseFile e)
            {
                Util.errorHandler(e);
            }
            catch(IOException e)
            {
                Util.errorHandler(e);
            }
            catch(CryptoException e)
            {
                Util.errorHandler(e);
            }
            catch(PasswordDatabaseException e)
            {
                Util.errorHandler(e);
            }
        }
    }

    public JLabel getStatusBar()
    {
        return statusBar;
    }

    /**
     * Initialize all the menus, buttons, etc to take account of the language selected by the user
     */
    public void initialiseControlsWithDefaultLanguage()
    {
        databaseMenu.setText(Translator.translate("databaseMenu"));
        newDatabaseMenuItem.setText(Translator.translate(NEW_DATABASE_TXT));
        openDatabaseMenuItem.setText(Translator.translate(OPEN_DATABASE_TXT));
        changeMasterPasswordMenuItem.setText(Translator.translate(CHANGE_MASTER_PASSWORD_TXT));
        accountMenu.setText(Translator.translate("accountMenu"));
        addAccountMenuItem.setText(Translator.translate(ADD_ACCOUNT_TXT));
        editAccountMenuItem.setText(Translator.translate(EDIT_ACCOUNT_TXT));
        deleteAccountMenuItem.setText(Translator.translate(DELETE_ACCOUNT_TXT));
        viewAccountMenuItem.setText(Translator.translate(VIEW_ACCOUNT_TXT));
        copyUsernameMenuItem.setText(Translator.translate(COPY_USERNAME_TXT));
        copyPasswordMenuItem.setText(Translator.translate(COPY_PASSWORD_TXT));
        exitMenuItem.setText(Translator.translate(EXIT_TXT));
        aboutMenuItem.setText(Translator.translate(ABOUT_TXT));
        exportMenuItem.setText(Translator.translate(EXPORT_TXT));
        importMenuItem.setText(Translator.translate(IMPORT_TXT));
        addAccountButton.setToolTipText(Translator.translate(ADD_ACCOUNT_TXT));
        editAccountButton.setToolTipText(Translator.translate(EDIT_ACCOUNT_TXT));
        deleteAccountButton.setToolTipText(Translator.translate(DELETE_ACCOUNT_TXT));
        copyUsernameButton.setToolTipText(Translator.translate(COPY_USERNAME_TXT));
        copyPasswordButton.setToolTipText(Translator.translate(COPY_PASSWORD_TXT));
        optionsButton.setToolTipText(Translator.translate(OPTIONS_TXT));
        optionsButton.setToolTipText(Translator.translate(OPTIONS_TXT));
        resetSearchButton.setToolTipText(Translator.translate(RESET_SEARCH_TXT));
    }

    private void setButtonState()
    {
        Boolean state_enbabled;
        if(getAccountsListview().getSelectedValue() == null)
        {
            state_enbabled = false;
        }
        else
        {
            state_enbabled = true;
        }
        editAccountButton.setEnabled(state_enbabled);
        copyUsernameButton.setEnabled(state_enbabled);
        copyPasswordButton.setEnabled(state_enbabled);
        deleteAccountButton.setEnabled(state_enbabled);
        editAccountMenuItem.setEnabled(state_enbabled);
        copyUsernameMenuItem.setEnabled(state_enbabled);
        copyPasswordMenuItem.setEnabled(state_enbabled);
        deleteAccountMenuItem.setEnabled(state_enbabled);
        viewAccountMenuItem.setEnabled(state_enbabled);
    }

    public void populateListview(ArrayList<String> accountNames)
    {
        SortedListModel listview = (SortedListModel) getAccountsListview().getModel();
        listview.clear();
        getAccountsListview().clearSelection();
        for(int i=0; i<accountNames.size(); i++)
        {
            listview.addElement(accountNames.get(i));
        }
        setButtonState();
    }

    public void doOpenDatabaseActions(String dataBaseFileName)
    {
        addAccountButton.setEnabled(true);
        addAccountMenuItem.setEnabled(true);
        getSearchField().setEnabled(true);
        getSearchField().setText("");
        searchIcon.setEnabled(true);
        resetSearchButton.setEnabled(true);
        changeMasterPasswordMenuItem.setEnabled(true);
        exportMenuItem.setEnabled(true);
        importMenuItem.setEnabled(true);
        setTitle(dataBaseFileName + " - " + applicationName);
        populateListview(dbActions.getAccountNames());
    }
}
