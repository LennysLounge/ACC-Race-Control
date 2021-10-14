/*
 * Copyright (c) 2021 Leonard Sch?ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.lpui.table;

/**
 *
 * A listener for table model changes.
 *
 * @author Leonard
 */
public interface TableModelChangedListener {

    /**
     * Gets called when an entry has been added to the model.
     *
     * @param index the index the entry was added at.
     */
    public void onEntryAdded(int index);
}
