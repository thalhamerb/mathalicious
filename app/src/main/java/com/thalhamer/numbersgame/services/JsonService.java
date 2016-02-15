package com.thalhamer.numbersgame.services;import android.content.Context;import com.thalhamer.numbersgame.Factory.App;import com.thalhamer.numbersgame.enums.CalcType;import com.thalhamer.numbersgame.enums.GameType;import com.thalhamer.numbersgame.enums.ScoreType;import org.json.JSONException;import org.json.JSONObject;import java.io.BufferedReader;import java.io.File;import java.io.FileInputStream;import java.io.FileOutputStream;import java.io.IOException;import java.io.InputStream;import java.io.InputStreamReader;import java.io.Reader;import java.io.StringWriter;import java.io.Writer;import javax.inject.Inject;import javax.inject.Singleton;/** * performs json related services * <p/> * Created by Brian on 5/9/2015. */@Singletonpublic class JsonService {    @Inject    StatsService statsService;    @Inject    public JsonService() {    }    public String getJsonDataFromResource(String fileName, String resourceType) throws IOException {        int resID = App.getContext().getResources().getIdentifier(fileName, resourceType, App.getContext().getPackageName());        InputStream is = App.getContext().getResources().openRawResource(resID);        return convertStreamToString(is);    }    public String getStringFromInternalFile(File fl) throws IOException {        InputStream is = new FileInputStream(fl);        return convertStreamToString(is);    }    private String convertStreamToString(InputStream is) throws IOException {        Writer writer = new StringWriter();        char[] buffer = new char[1024];        try {            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));            int n;            while ((n = reader.read(buffer)) != -1) {                writer.write(buffer, 0, n);            }        } finally {            is.close();        }        return writer.toString();    }    public void writeJsonToFile(String dataToWrite, String filename) {        FileOutputStream outputStream;        try {            outputStream = App.getContext().openFileOutput(filename, Context.MODE_PRIVATE);            outputStream.write(dataToWrite.getBytes());            outputStream.close();        } catch (Exception e) {            e.printStackTrace();        }    }    public String getGameDescription(JSONObject object) throws JSONException {        CalcType calcType = CalcType.valueOf(object.getString("calcTypeEnum"));        int numToAddUpTo = object.getInt("numToEquateTo");        GameType gameType = GameType.valueOf(object.getString("gameType"));        ScoreType scoreType = ScoreType.valueOf(object.getString("scoreType"));        StringBuilder sb = new StringBuilder();        sb.append(calcType.getCalculation());        sb.append(" to ");        sb.append(numToAddUpTo);        if (scoreType.getGameDescriptionEnd().contains("%s")) {            String gameDesc = String.format(scoreType.getGameDescriptionEnd(), object.getLong("scoreTypeValue"));            sb.append(gameDesc);        } else {            sb.append(scoreType.getGameDescriptionEnd());        }        String gameDescription;        if (GameType.MOVES.equals(gameType)) {            gameDescription = String.format(gameType.getGameDescriptionEnd(), object.getString("numberOfMoves"));        } else if (GameType.TIMED.equals(gameType)) {            if (statsService == null) {                statsService = new StatsService();            }            String timeInMinutes = statsService.getTimeInMinutesFormat(object.getLong("numberOfSeconds") * 1000);            gameDescription = String.format(gameType.getGameDescriptionEnd(), timeInMinutes);        } else {            gameDescription = gameType.getGameDescriptionEnd();        }        sb.append(gameDescription);        return sb.toString();    }}