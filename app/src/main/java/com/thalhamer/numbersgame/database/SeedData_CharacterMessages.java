package com.thalhamer.numbersgame.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.Lists;
import com.thalhamer.numbersgame.enums.Character;
import com.thalhamer.numbersgame.enums.MessageLocation;
import com.thalhamer.numbersgame.enums.MessageType;

import java.util.List;

/**
 * seed data
 * <p/>
 * Created by Brian on 12/19/2015.
 */
public class SeedData_CharacterMessages {

    public static void clearTableAndSeedData(SQLiteDatabase db) {
        db.execSQL("drop table if exists character_message");
    }

    public static void populateTableAndSeedData(SQLiteDatabase db) {
        db.execSQL("create table character_message (_id integer primary key, character text," +
                " message_type text, message text, message_location text)");
        characterMessagesInserts(db);
    }

    private static void characterMessagesInserts(SQLiteDatabase db) {
        List<String> einsteinPosPhrases = Lists.newArrayList();
        einsteinPosPhrases.add("Nice score! They should make you person of the century.");
        einsteinPosPhrases.add("Your fingers must have been moving at the speed of light!");
        einsteinPosPhrases.add("Win one more game and I'll tell you the secret to my hair...and maybe time travel.");
        einsteinPosPhrases.add("Fantastic...like my hair.");
        einsteinPosPhrases.add("That score is so high, you should get a novel prize.");
        einsteinPosPhrases.add("You are doing so well.  We should patent your talent.");
        einsteinPosPhrases.add("That score is so massive, light bends around it.");
        einsteinPosPhrases.add("Ohh yeaaa! I think you are ready for the multiply levels.");
        einsteinPosPhrases.add("I need help with a new theory. You up for the challenge?");
        einsteinPosPhrases.add("You must be a magician because that was 'mathical'.");
        einsteinPosPhrases.add("That was the bomb diggity!  Did I say that right?  I wasn't born this century.");
        einsteinPosPhrases.add("I better put some sunblock on before all of those stars gives me a sunburn.");
        einsteinPosPhrases.add("If there was an Olympic event for this game you would win gold.");

        List<String> einsteinNegPhrases = Lists.newArrayList();
        einsteinNegPhrases.add("Neeeiiiinnnnn!! (Translation from German: Noooooo!!).");
        einsteinNegPhrases.add("Lets just let this low score be sucked into the black hole.");
        einsteinNegPhrases.add("Here is a hint. Run really fast while playing a timed game and the timer will slow down.");
        einsteinNegPhrases.add("You'll do better next time!  You think I figured out the theory of mass-energy in one try?");
        einsteinNegPhrases.add("My magic eight ball says 'try again'.");
        einsteinNegPhrases.add("That's ok. You will get it next time!");
        einsteinNegPhrases.add("That star looks lonely.  It needs a friend or two.");
        einsteinNegPhrases.add("With practice comes greatness.");
        einsteinNegPhrases.add("A person who never made a mistake never tried anything new.");
        einsteinNegPhrases.add("Intellectual growth should commence at birth and cease only at death.");

        List<String> newtonPosPhrases = Lists.newArrayList();
        newtonPosPhrases.add("Keep that momentum going, or as call it my first law of motion.");
        newtonPosPhrases.add("That score is so big, it has its own gravity.");
        newtonPosPhrases.add("I'm impressed by your math skills, and I invented calculus.");
        newtonPosPhrases.add("You've earned yourself a cookie.  I prefer Fig Newtons.");
        newtonPosPhrases.add("You plus this game equals awesome!");
        newtonPosPhrases.add("Keep doing this well and I'm going to have to make the next game add to one hundred in order to challenge you!");
        newtonPosPhrases.add("Nice score.  Next time you see Einstein, tell him my hair is better.");
        newtonPosPhrases.add("You put that game to rest! I call that 'The Newton's Cradle'.");
        newtonPosPhrases.add("Another level bites the dust.  Oh yeah!");
        newtonPosPhrases.add("You should do the chicken dance to celebrate that score!");
        newtonPosPhrases.add("I thought of a nickname for you, 'The Human Calculator'.");
        newtonPosPhrases.add("There's the shot...swish.  Nothing but net!");
        newtonPosPhrases.add("You are a math rockstar!");

        List<String> newtonNegPhrases = Lists.newArrayList();
        newtonNegPhrases.add("You'll do better next time!");
        newtonNegPhrases.add("Try again!  Thomas Edison made 1,000 unsuccessful attempts at inventing the light bulb before he got it right.");
        newtonNegPhrases.add("I wish I had this game in the 1600's.  It would have kept me busy while staying inside to avoid the Bubonic Plague.");
        newtonNegPhrases.add("You need some inspiration? Sitting under a tree and letting an apple fall on my head worked for me.");
        newtonNegPhrases.add("In history I am portrayed as a genius.  The truth is my powers are ordinary.  Only my hard work brought me to success.");
        newtonNegPhrases.add("You got the talent.  You just need the practice.");
        newtonNegPhrases.add("Keep trying!  You'll get it.");
        newtonNegPhrases.add("Constrant dripping hollows out a stone.");
        newtonNegPhrases.add("Nothing is impossible.  The word itself says \"I'm possible\"");
        newtonNegPhrases.add("Keep trying!  Failures increase the probability of success.");
        newtonNegPhrases.add("Play again.  You got this!");
        newtonNegPhrases.add("There is no such thing as instant success.");

        List<String> galileoPosPhrases = Lists.newArrayList();
        galileoPosPhrases.add("That score is astronomical!");
        galileoPosPhrases.add("I don't even need my telescope to see all of those stars!");
        galileoPosPhrases.add("Nice!  I think I have another star in this beard somewhere.");
        galileoPosPhrases.add("Wow. Enough said...");
        galileoPosPhrases.add("Look at all of those stars!");
        galileoPosPhrases.add("Pro status.");

        List<String> galileoNegPhrases = Lists.newArrayList();
        galileoNegPhrases.add("You think you got it bad. I was exiled for saying the sun revolves around the earth...still waiting for an apology.");

        performCharacterMessageInserts(db, com.thalhamer.numbersgame.enums.Character.EINSTEIN, MessageType.POSITIVE, MessageLocation.AFTER_GAME, einsteinPosPhrases);
        performCharacterMessageInserts(db, com.thalhamer.numbersgame.enums.Character.EINSTEIN, MessageType.NEGATIVE, MessageLocation.AFTER_GAME, einsteinNegPhrases);

        performCharacterMessageInserts(db, Character.NEWTON, MessageType.POSITIVE, MessageLocation.AFTER_GAME, newtonPosPhrases);
        performCharacterMessageInserts(db, Character.NEWTON, MessageType.NEGATIVE, MessageLocation.AFTER_GAME, newtonNegPhrases);

        performCharacterMessageInserts(db, Character.GALILEO, MessageType.POSITIVE, MessageLocation.AFTER_GAME, galileoPosPhrases);
        performCharacterMessageInserts(db, Character.GALILEO, MessageType.NEGATIVE, MessageLocation.AFTER_GAME, galileoNegPhrases);

        List<String> normalDuringGamePhrases = Lists.newArrayList();
        normalDuringGamePhrases.add("Awesome!");
        normalDuringGamePhrases.add("Phenomenal!");
        normalDuringGamePhrases.add("Get another one of those!");
        normalDuringGamePhrases.add("Mathalicious!");
        performCharacterMessageInserts(db, null, MessageType.NORMAL, MessageLocation.DURING_GAME, normalDuringGamePhrases);

        List<String> posDuringGamePhrases = Lists.newArrayList();
        posDuringGamePhrases.add("You calculating whizz!");
        posDuringGamePhrases.add("I didn't see that one.");
        performCharacterMessageInserts(db, null, MessageType.POSITIVE, MessageLocation.DURING_GAME, posDuringGamePhrases);
    }

    public static void performCharacterMessageInserts(SQLiteDatabase db, Character character, MessageType messageType,
                                                      MessageLocation messageLocation, List<String> phrases) {
        for (String phrase : phrases) {
            ContentValues insertValues = new ContentValues();
            if (character != null) {
                insertValues.put(DatabaseContract.CharacterMessage.CHARACTER, character.toString());
            }
            insertValues.put(DatabaseContract.CharacterMessage.MESSAGE_TYPE, messageType.toString());
            insertValues.put(DatabaseContract.CharacterMessage.MESSAGE_LOCATION, messageLocation.toString());
            insertValues.put(DatabaseContract.CharacterMessage.MESSAGE, phrase);
            db.insert(DatabaseContract.CharacterMessage.TABLE_NAME, null, insertValues);
        }
    }
}
