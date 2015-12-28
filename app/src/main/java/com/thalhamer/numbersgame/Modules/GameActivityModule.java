package com.thalhamer.numbersgame.Modules;

import com.thalhamer.numbersgame.Activity.LevelActivity;
import com.thalhamer.numbersgame.domain.GameDataHolder;
import com.thalhamer.numbersgame.services.GridMappingService;
import com.thalhamer.numbersgame.services.GridService;
import com.thalhamer.numbersgame.services.LevelEndService;
import com.thalhamer.numbersgame.services.LevelInitiateService;
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
        LevelActivity.class,
        GridMappingService.class,
        MotionEventService.class,
        LevelInitiateService.class,
        LevelEndService.class,
        GridService.class,
        StatsService.class,
        TimerService.class,
        GameDataHolder.class,
},
        library = true
)
public class GameActivityModule {
}