package com.thalhamer.numbersgame.Modules;

/**
 * Created by Brian on 12/25/2015.
 */

import com.thalhamer.numbersgame.Activity.OpeningScreenActivity;
import com.thalhamer.numbersgame.services.popup.GameIntroPopupService;

import dagger.Module;

@Module(injects = {
        OpeningScreenActivity.class,
        GameIntroPopupService.class
},
        library = true
)
public class OpeningScreenActivityModule {
}
