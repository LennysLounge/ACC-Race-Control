/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.livetiming.racedistance;

import base.screen.eventbus.Event;
import base.screen.eventbus.EventBus;
import base.screen.eventbus.EventListener;
import base.screen.networking.data.RealtimeInfo;
import base.screen.networking.events.RealtimeCarUpdate;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author Leonard
 */
public class RaceDistance
        implements EventListener {
    
    private static final Logger LOG = Logger.getLogger(RaceDistance.class.getName());

    /**
     * A map of car ids to their calculated spline positions.
     */
    private Map<Integer, Float> raceDistances = new HashMap<>();

    public RaceDistance() {
        EventBus.register(this);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeCarUpdate) {
            RealtimeInfo car = ((RealtimeCarUpdate) e).getInfo();
            if (!raceDistances.containsKey(car.getCarId())) {
                raceDistances.put(car.getCarId(), car.getSplinePosition() + car.getLaps());
            }

            //Normalise values
            float raceDistance = raceDistances.get(car.getCarId());
            float prevSplinePosition = raceDistance % 1;
            float nowSplinePosition = car.getSplinePosition() % 1;
  
            float positionDifference = nowSplinePosition - prevSplinePosition;
            if(Math.abs(positionDifference) > 0.5f){
                positionDifference -= Math.signum(positionDifference);
            }
            float newPosition = raceDistances.get(car.getCarId()) + positionDifference;
            //update the race distance
            raceDistances.put(car.getCarId(), newPosition);
            EventBus.publish(new RaceDistanceEvent(car, newPosition));
        }
    }

    public void remove() {
        EventBus.unregister(this);
    }

}
