package com.kommunity;

/**
 * Created by Nidhi on 4/18/2015.
 */
import android.content.Context;
import android.content.SharedPreferences;

import com.parse.Parse;
import com.parse.ParseObject;
import com.facebook.FacebookSdk;

public class Application  extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        //Parse.initialize(this, "cSsOJ7LrGXXp06GTCLErgrIcbDZO9FatypbVh6jN", "gZrJlTlNjOJ4qsRYYrluqtkEWCbdto0j64PPvT3I");
        Parse.initialize(this, "gIpNJkEksy2Z4OAoEnHYLglRtK1tvRBeqllMUO7v", "m4tFy2KfZXS53avmSlWbGplhAf6D472k5AA17Tx1");
    }
}
