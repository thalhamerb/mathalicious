package com.thalhamer.numbersgame.Modules;

import com.thalhamer.numbersgame.Activity.GameActivity;
import com.thalhamer.numbersgame.domain.GameDataHolder;
import com.thalhamer.numbersgame.services.GameEndService;
import com.thalhamer.numbersgame.services.GameInitiateService;
import com.thalhamer.numbersgame.services.GridMappingService;
import com.thalhamer.numbersgame.services.GridService;
import com.thalhamer.numbersgame.services.MotionEventService;
import com.thalhamer.numbersgame.services.StatsService;
import com.thalhamer.numbersgame.services.TimerService;

import dagger.Module;

/**
 * module
 * <p/>
 * Created by Brian on 11/15/2015.
 */
@Module(injects = {
        GameActivity.class,
        GridMappingService.class,
        MotionEventService.class,
        GameInitiateService.class,
        GameEndService.class,
        GridService.class,
        StatsService.class,
        TimerService.class,
        GameDataHolder.class,
},
        library = true
)
public class GameActivityModule {
}