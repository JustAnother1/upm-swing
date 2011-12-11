/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses/>
 *
 */
package com._17od.upm.gui;

import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JOptionPane;

/**
 * @author Lars P&ouml;tter
 * (<a href=mailto:Lars_Poetter@gmx.de>Lars_Poetter@gmx.de</a>)
 */
public class UpmUncaughtExceptionHandler implements UncaughtExceptionHandler
{

    /**
     *
     */
    public UpmUncaughtExceptionHandler()
    {
    }

    /**
     * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
     */
    @Override
    public void uncaughtException(Thread t, Throwable e)
    {
        String ThreadName = "none";
        String ExecptionMessage = "none";
        if(null != t)
        {
            ThreadName = t.getName();
        }
        if(null != e)
        {
            ExecptionMessage = e.getMessage();
        }
        System.err.println("The Thread " + ThreadName + "Threw the Exception " + ExecptionMessage + " !");
        JOptionPane.showMessageDialog(null,
                "The Thread " + ThreadName + "Threw the Exception " + ExecptionMessage + " !",
                "Exception !",
                JOptionPane.ERROR_MESSAGE);
    }

}
