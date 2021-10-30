/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.autobroadcast;

import racecontrol.eventbus.Event;

/**
 *
 * A processor that calculates a rating for an entry.
 *
 * @author Leonard
 */
public interface RatingProcessor {

    public void onEvent(Event e);

    public Entry calculateRating(Entry entry);
}
