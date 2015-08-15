    package com.kommunity;

    import android.content.Intent;
    import android.support.v7.app.ActionBarActivity;
    import android.os.Bundle;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.view.LayoutInflater;
    import android.app.Activity;
    import android.widget.LinearLayout;
    import android.view.*;
    import android.app.AlertDialog;
    import android.widget.TextView;

    import com.parse.FindCallback;
    import com.parse.ParseException;
    import com.parse.ParseGeoPoint;
    import com.parse.ParseObject;
    import com.parse.ParseQuery;

    import java.util.Collections;


    public class dashboard extends ActionBarActivity {


        private ParseGeoPoint dummylocation;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_dashboard);

            dummylocation = new ParseGeoPoint(43.071436, -89.407043);
            ParseQuery<ParseObject> query = ParseQuery.getQuery("sosObject");
            query.whereNear("location", dummylocation);
            query.setLimit(10);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(java.util.List<ParseObject> objects,ParseException e) {
                    if (e == null) {
                        for(ParseObject obj : objects){
                            dummylocation = (ParseGeoPoint)obj.get("location");
                            createTile(obj.getString("sub"), obj.getString("desc"), String.valueOf(dummylocation.getLatitude()), String.valueOf(dummylocation.getLongitude()));
                        }
                    } else {
                        return;
                    }
                }
            });
        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu items for use in the action bar
            getMenuInflater().inflate(R.menu.menu_dashboard,menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            switch(item.getItemId()) {
                case R.id.action_add:
                    startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                    // Add the functionality to switch
                    // to the google maps page
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }
        public boolean createTile(String header, String content, String lat, String lon){
            LayoutInflater inflate = getLayoutInflater();
            LinearLayout tileContainer = (LinearLayout)findViewById(R.id.tileContainer);
            View v = inflate.inflate(R.layout.tiles, null);
            TextView head = (TextView) v.findViewById(R.id.header);
            TextView cont = (TextView) v.findViewById(R.id.content);
            TextView lt = (TextView) v.findViewById(R.id.lat);
            TextView lo = (TextView) v.findViewById(R.id.lon);
            head.append(header);
            cont.append(content);
            lt.append(lat);
            lo.append(lon);
            tileContainer.addView(v);
            return false;
        }

        public void goToMap(View v){
            TextView tempLat = (TextView) v.findViewById(R.id.lat) ;
            storage.latitude = Double.valueOf(String.valueOf(tempLat.getText()));

            TextView tempLon = (TextView) v.findViewById(R.id.lon) ;
            storage.longitude = Double.valueOf(String.valueOf(tempLon.getText()));

            TextView tempHeader = (TextView) v.findViewById(R.id.header) ;
            storage.header = String.valueOf(tempHeader.getText());

            TextView tempCont = (TextView) v.findViewById(R.id.content) ;
            storage.content = String.valueOf(tempCont.getText());

            startActivity(new Intent(getApplicationContext(), TitleTilePopUp.class));
        }
    }

