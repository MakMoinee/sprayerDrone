package com.thesis.sprayerdrone.interfaces;

import com.github.MakMoinee.library.interfaces.DefaultBaseListener;

public interface NetworkListener extends DefaultBaseListener {

    @Override
    default void onError(Error error) {

    }
}
