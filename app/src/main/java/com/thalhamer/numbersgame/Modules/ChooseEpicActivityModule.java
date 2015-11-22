package com.thalhamer.numbersgame.Modules;

import com.thalhamer.numbersgame.Activity.ChooseEpicActivity;

import dagger.Module;

/**
 * module
 * <p/>
 * Created by Brian on 11/15/2015.
 */
@Module(injects = {
        ChooseEpicActivity.class
},
        library = true
)
public class ChooseEpicActivityModule {
}
