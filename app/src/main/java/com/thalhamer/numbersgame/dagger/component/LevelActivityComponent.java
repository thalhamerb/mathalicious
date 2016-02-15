package com.thalhamer.numbersgame.dagger.component;

import com.thalhamer.numbersgame.Activity.LevelActivity;
import com.thalhamer.numbersgame.dagger.module.AppModule;
import com.thalhamer.numbersgame.dagger.module.LevelActivityModule;
import com.thalhamer.numbersgame.viewhelper.MainGamePanel;
import com.thalhamer.numbersgame.viewhelper.MainThread;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Dagger level activity component
 */
@Singleton
@Component(modules = {AppModule.class, LevelActivityModule.class})
public interface LevelActivityComponent {

    void injectLevelActivity(LevelActivity levelActivity);

    void injectMainGamePanel(MainGamePanel mainGamePanel);

    void injectMainThread(MainThread mainThread);
}
