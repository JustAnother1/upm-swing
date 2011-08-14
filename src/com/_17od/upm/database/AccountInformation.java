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

import java.io.Serializable;

public class AccountInformation implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String accountName;
    private byte[] userId;
    private char[] password;
    private byte[] url;
    private byte[] notes;

    public AccountInformation()
    {
        accountName = "";
        userId = "".getBytes();
        password[0] = 0;
        url = "".getBytes();
        notes = "".getBytes();
    }

    public AccountInformation(String accountName, byte[] userId, char[] password,
            byte[] url, byte[] notes)
    {
        this.accountName = accountName;
        this.userId = userId;
        this.password = password;
        this.url = url;
        this.notes = notes;
    }

    public String getAccountName()
    {
        return accountName;
    }

    public void setAccountName(String accountName)
    {
        this.accountName = accountName;
    }

    public byte[] getNotes()
    {
        return notes;
    }

    public void setNotes(byte[] notes)
    {
        this.notes = notes;
    }

    public char[] getPassword()
    {
        return password;
    }

    public void setPassword(char[] password)
    {
        this.password = password;
    }

    public byte[] getUrl()
    {
        return url;
    }

    public void setUrl(byte[] url)
    {
        this.url = url;
    }

    public byte[] getUserId()
    {
        return userId;
    }

    public void setUserId(byte[] userId)
    {
        this.userId = userId;
    }

}
