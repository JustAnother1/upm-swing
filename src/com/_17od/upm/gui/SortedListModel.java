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

import java.text.Collator;
import java.util.Comparator;
import java.util.TreeSet;
import javax.swing.AbstractListModel;


public class SortedListModel extends AbstractListModel
{
    private static final long serialVersionUID = 1L;
    private TreeSet<String> model;

    public SortedListModel()
    {
        model = new TreeSet<String>(new Comparator<String>()
        {
            public int compare(String str1, String str2)
            {
                Collator collator = Collator.getInstance();
                int result = collator.compare(str1, str2);
                return result;
            }
        });
    }

    public int getSize()
    {
        return model.size();
    }

    public String getElementAt(int index)
    {
        return (String) model.toArray()[index];
    }

    public void addElement(String element)
    {
        if (model.add(element))
        {
            fireContentsChanged(this, 0, getSize());
        }
    }

    public void clear()
    {
        model.clear();
        fireContentsChanged(this, 0, getSize());
    }

    public boolean contains(String element)
    {
        return model.contains(element);
    }

    public boolean removeElement(String element)
    {
        boolean removed = model.remove(element);
        if(removed)
        {
            fireContentsChanged(this, 0, getSize());
        }
        return removed;
    }

}
