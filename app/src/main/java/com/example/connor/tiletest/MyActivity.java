package com.example.connor.tiletest;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.qozix.tileview.TileView;
import com.qozix.tileview.markers.MarkerEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MyActivity extends Activity {
    private TileView tileView;
    ArrayList<Point> pinList;
    boolean loaded = false;
    private Firebase myFirebaseRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Rusty");
        getActionBar().setIcon(R.drawable.rusty);
        //getActionBar().setBackgroundDrawable(new ColorDrawable(0x000000));
        myFirebaseRef = new Firebase("https://rusty.firebaseio.com/");

        tileView = new TileView(this);
        tileView.setSize( 3000,1862 );
        tileView.setScale(.25);
        tileView.addDetailLevel( 0.25f, "tiles/jackson/250/%col%_%row%.jpg", "samples/jackson.jpg",128,128 );
        tileView.addDetailLevel( 1.0f, "tiles/jackson/1000/%col%_%row%.jpg", "samples/jackson.jpg",128,128 );
        tileView.addDetailLevel( 0.5f, "tiles/jackson/500/%col%_%row%.jpg", "samples/jackson.jpg",128,128 );
        tileView.addDetailLevel( 0.125f, "tiles/jackson/125/%col%_%row%.jpg", "samples/jackson.jpg",128,128 );
        //tileView.setMarkerAnchorPoints( -0.5f, -1.0f );
        tileView.setScaleLimits( 0, 2.5 );
        tileView.setTransitionsEnabled( true );



        //get points
        ArrayList<Point> locs = new ArrayList<Point>();
        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(loaded){
                        return;
                }
                Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                while (iter.hasNext()) {
                    DataSnapshot temp = iter.next();
                    Double timestamp = Double.parseDouble(temp.getName());
                    if(System.currentTimeMillis() - timestamp < 180000) {
                        double x = (Double) temp.child("x").getValue();
                        double y = (Double) temp.child("y").getValue();
                        ImageView markerPow = new ImageView(getApplicationContext());
                        markerPow.setImageResource(R.drawable.powderpin2);
                        markerPow.setTag("Powder");
                        tileView.addMarker(markerPow, x, y, -0.5f, -1.0f);
                        Log.d("From Server x", String.valueOf(y));
                        Log.d("From Server y", String.valueOf(x));
                    }
                }
                loaded = true;
                return;
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


//        tileView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                float myX = motionEvent.getX();
//                float myY = motionEvent.getY();
//                Log.d("X: ", String.valueOf(myX));
//                Log.d("Y: ", String.valueOf(myY));
//                ImageView markerPow = new ImageView(getApplicationContext());
//                markerPow.setImageResource(R.drawable.powderpin);
//                markerPow.setTag("Powder");
//                tileView.addMarker(markerPow, myX, myY, -0.5f, -1.0f);
//                return false;
//            }
//        });
        tileView.addTileViewEventListener(listener);

            setContentView(tileView);
        }
    public void makePowMarker(View view){
        Log.d("Debug", "Bankroll");
    }
    private TileView.TileViewEventListenerImplementation listener = new TileView.TileViewEventListenerImplementation() {
        public void onTap(int x, int y) {
            ImageView markerPow = new ImageView(getApplicationContext());
            markerPow.setImageResource(R.drawable.powderpin2);
            markerPow.setTag("Powder");
            markerPow.setOnClickListener(markerClickListener);
            double uX = tileView.unscale(x);
            double uY = tileView.unscale(y);
            tileView.addMarker(markerPow, uX,uY, -0.5f, -1.0f);

            Firebase point = myFirebaseRef.child(String.valueOf(System.currentTimeMillis()));
            point.child("x").setValue(uX);
            point.child("y").setValue(uY);

            Log.d("X: ", String.valueOf(x));
            Log.d("Y: ", String.valueOf(y));
        }
//        public void onDoubleTap(int x, int y) {
//            Log.d("Debug", "DubDub");
//        }
    };
    private View.OnClickListener markerClickListener = new View.OnClickListener() {

        @Override
        public void onClick( View view ) {

        }
    };
        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
