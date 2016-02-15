package com.thalhamer.numbersgame.dagger.component;

import com.thalhamer.numbersgame.Activity.OpeningScreenActivity;
import com.thalhamer.numbersgame.dagger.module.AppModule;
import com.thalhamer.numbersgame.dagger.module.OpeningScreenActivityModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Dagger opening screen activity component
 */
@Singleton
@Component(modules = {AppModule.class, OpeningScreenActivityModule.class})
public interface OpeningScreenActivityComponent {

    void injectOpeningScreenActivity(OpeningScreenActivity openingScreenActivity);
}
