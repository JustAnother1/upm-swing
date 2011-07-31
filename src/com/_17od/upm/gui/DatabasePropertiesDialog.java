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
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import com._17od.upm.database.PasswordDatabase;
import com._17od.upm.util.Translator;

import java.util.Arrays;


public class DatabasePropertiesDialog extends EscapeDialog
{

    private boolean databaseNeedsSaving = false;

    public DatabasePropertiesDialog(final JFrame frame, ArrayList accountNames, final PasswordDatabase database)
    {
        super(frame, Translator.translate("databaseProperties"), true);

        Container container = getContentPane();

        // Create a pane with an empty border for spacing
        Border emptyBorder = BorderFactory.createEmptyBorder(2, 5, 5, 5);
        JPanel emptyBorderPanel = new JPanel();
        emptyBorderPanel.setLayout(new BoxLayout(emptyBorderPanel, BoxLayout.Y_AXIS));
        emptyBorderPanel.setBorder(emptyBorder);
        container.add(emptyBorderPanel);

        // Create a pane with an title etched border
        Border etchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        Border etchedTitleBorder = BorderFactory.createTitledBorder(etchedBorder, ' ' + Translator.translate("remoteLocation") + ' ');
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(etchedTitleBorder);
        emptyBorderPanel.add(mainPanel);

        GridBagConstraints c = new GridBagConstraints();

        // The Remote URL Label row
        JLabel urlLabel = new JLabel(Translator.translate("url"));
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 3, 0, 0);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        mainPanel.add(urlLabel, c);

        // The Remote URL input field row
        final JTextField urlTextField = new JTextField("", 20);
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 3, 3, 3);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        mainPanel.add(urlTextField, c);

        // The Authentication Credentials label row
        JLabel authLabel = new JLabel(Translator.translate("authenticationCredentials"));
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(3, 3, 0, 0);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        mainPanel.add(authLabel, c);

        // The Authentication Credentials input field row
        String[] sAccountNames = new String[accountNames.size() + 1];
        sAccountNames[0] = "";
        System.arraycopy(accountNames.toArray(), 0, sAccountNames, 1, accountNames.size());
        Arrays.sort(sAccountNames);
        final JComboBox auth = new JComboBox(sAccountNames);
        // TODO auth.setSelectedItem(database.getDbOptions().getAuthDBEntry());
        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 3, 3, 3);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(auth, c);

        // Some spacing
        Component verticalSpace = Box.createVerticalGlue();
        c.gridx = 0;
        c.gridy = 4;
        c.weighty = 1;
        mainPanel.add(verticalSpace, c);

        // The buttons row
        JPanel buttonPanel = new JPanel(new FlowLayout());
        emptyBorderPanel.add(buttonPanel);
        JButton okButton = new JButton(Translator.translate("ok"));
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                saveDatabaseOptions(frame, urlTextField.getText().trim(), (String) auth.getSelectedItem(), database);
            }
        });
        buttonPanel.add(okButton);

        JButton cancelButton = new JButton(Translator.translate("cancel"));
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                databaseNeedsSaving = false;
                setVisible(false);
                dispose();
            }
        });
        buttonPanel.add(cancelButton);

    }


    private URL validateURL(String urlString)
    {
        URL url = null;
        try
        {
            url = new URL(urlString);
        }
        catch (MalformedURLException e)
        {
            // If we got here the the URL is invalid
        }
        return url;
    }


    private void saveDatabaseOptions(JFrame parentFrame, String remoteLocation, String authEntry, PasswordDatabase database)
    {
        // Attempt to save the database and then close the window
        try
        {
            setVisible(false);
            dispose();
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(parentFrame, e.getMessage(), Translator.translate("problemSavingDB"), JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean getDatabaseNeedsSaving()
    {
        return databaseNeedsSaving;
    }

}
