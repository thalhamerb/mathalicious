package com.thalhamer.numbersgame.Modules;

import com.thalhamer.numbersgame.Activity.ChooseLevelActivity;

import dagger.Module;

/**
 * module
 * <p/>
 * Created by Brian on 11/15/2015.
 */
@Module(injects = {
        ChooseLevelActivity.class
},
        library = true
)
public class ChooseLevelActivityModule {
}
