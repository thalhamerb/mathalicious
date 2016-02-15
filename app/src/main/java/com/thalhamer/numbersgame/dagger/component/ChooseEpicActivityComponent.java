package com.thalhamer.numbersgame.dagger.component;

import com.thalhamer.numbersgame.Activity.ChooseEpicActivity;
import com.thalhamer.numbersgame.dagger.module.AppModule;
import com.thalhamer.numbersgame.dagger.module.ChooseEpicActivityModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Dagger choose epic activity component
 */
@Singleton
@Component(modules = {AppModule.class, ChooseEpicActivityModule.class})
public interface ChooseEpicActivityComponent {

    void injectEpicActivity(ChooseEpicActivity chooseEpicActivity);
}
