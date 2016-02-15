package com.thalhamer.numbersgame.dagger.component;

import com.thalhamer.numbersgame.Activity.ChooseLevelActivity;
import com.thalhamer.numbersgame.dagger.module.AppModule;
import com.thalhamer.numbersgame.dagger.module.ChooseLevelActivityModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Dagger choose level activity component
 */
@Singleton
@Component(modules = {AppModule.class, ChooseLevelActivityModule.class})
public interface ChooseLevelActivityComponent {

    void injectChooseLevelActivity(ChooseLevelActivity chooseLevelActivity);
}
