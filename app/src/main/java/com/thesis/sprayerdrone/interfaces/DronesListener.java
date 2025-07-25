package com.thesis.sprayerdrone.interfaces;

import com.github.MakMoinee.library.interfaces.DefaultEventListener;
import com.thesis.sprayerdrone.models.Drones;

public interface DronesListener extends DefaultEventListener {
    default void onDeleteListener(Drones drones) {

    }
}
