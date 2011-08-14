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
package com._17od.upm.database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com._17od.upm.crypto.CryptoException;
import com._17od.upm.crypto.EncryptionService;
import com._17od.upm.crypto.InvalidPasswordException;

public class PasswordDatabasePersistence
{
    private static final byte DB_VERSION = 3;

    private EncryptionService encryptionService;

    /**
     * Used when we have a password and we want to get an instance of the class
     * so that we can call load(File, char[])
     */
    public PasswordDatabasePersistence()
    {
    }

    /**
     * Used when we want to create a new database with the given password
     * @param password
     * @throws CryptoException
     */
    public PasswordDatabasePersistence(char[] password) throws CryptoException
    {
        encryptionService = new EncryptionService(password);
    }

    public PasswordDatabase load(File databaseFile, char[] password) throws IOException, ProblemReadingDatabaseFile, InvalidPasswordException, CryptoException
    {

        byte[] fullDatabase;
        fullDatabase = readFile(databaseFile);

        // Check the database is a minimum length
        if (fullDatabase.length < EncryptionService.SALT_LENGTH)
        {
            throw new ProblemReadingDatabaseFile("This file doesn't appear to be a UPM password database");
        }

        ByteArrayInputStream is = null;
        GZIPInputStream gis = null;
        ObjectInputStream ois = null;

        // Calculate the positions of each item in the file
        int dbVersionPos      = 0;
        int saltPos           = dbVersionPos + 1;
        int encryptedBytesPos = saltPos + EncryptionService.SALT_LENGTH;

        // Get the database version
        byte dbVersion = fullDatabase[dbVersionPos];

        if(3 == dbVersion)
        {
            byte[] salt = new byte[EncryptionService.SALT_LENGTH];
            System.arraycopy(fullDatabase, saltPos, salt, 0, EncryptionService.SALT_LENGTH);
            int encryptedBytesLength = fullDatabase.length - encryptedBytesPos;
            byte[] encryptedBytes = new byte[encryptedBytesLength];
            System.arraycopy(fullDatabase, encryptedBytesPos, encryptedBytes, 0, encryptedBytesLength);

            //Attempt to decrypt the database information
            encryptionService = new EncryptionService(password, salt);
            byte[] decryptedBytes;
            try {
                decryptedBytes = encryptionService.decrypt(encryptedBytes);
            } catch (CryptoException e) {
                throw new InvalidPasswordException();
            }

            //If we've got here then the database was successfully decrypted
            is = new ByteArrayInputStream(decryptedBytes);
            gis = new GZIPInputStream(is);
            ois = new ObjectInputStream(gis);
        }
        else
        {
            throw new ProblemReadingDatabaseFile("Don't know how to handle database version [" + dbVersion + "]");
        }

        // Read the remainder of the database in now
        HashMap accounts = new HashMap();
        try
        {
            while (true)
            { //keep loading accounts until an EOFException is thrown
                AccountInformation ai = (AccountInformation)ois.readObject();
                accounts.put(ai.getAccountName(), ai);
            }
        }
        catch (EOFException e)
        {
            //just means we hit eof
        }
        catch (ClassNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        is.close();

        PasswordDatabase passwordDatabase = new PasswordDatabase(accounts, databaseFile);

        return passwordDatabase;

    }

    public void save(PasswordDatabase database) throws IOException, CryptoException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        GZIPOutputStream gout = new GZIPOutputStream(os);
        ObjectOutputStream out = new ObjectOutputStream(gout);

        // serialize the accounts
        Iterator it = database.getAccountsHash().values().iterator();
        while (it.hasNext())
        {
            AccountInformation ai = (AccountInformation) it.next();
            out.writeObject(ai);
        }
        out.close();
        byte[] dataToEncrypt = os.toByteArray();

        //Now encrypt the database data
        byte[] encryptedData = encryptionService.encrypt(dataToEncrypt);

        //Write the salt and the encrypted data out to the database file
        FileOutputStream fos = new FileOutputStream(database.getDatabaseFile());
        fos.write(DB_VERSION);
        fos.write(encryptionService.getSalt());
        fos.write(encryptedData);
        fos.close();
    }

    public EncryptionService getEncryptionService()
    {
        return encryptionService;
    }

    private byte[] readFile(File file) throws IOException
    {
        InputStream is = new FileInputStream(file);

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) file.length()];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0)
        {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length)
        {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();

        return bytes;
    }

}
