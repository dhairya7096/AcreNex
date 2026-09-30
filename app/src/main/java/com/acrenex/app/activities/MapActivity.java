package com.acrenex.app.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.acrenex.app.R;
import com.acrenex.app.database.DatabaseManager;
import com.acrenex.app.utils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

import java.util.ArrayList;
import java.util.List;

/** GIS demo: current device location + parcel coordinate/boundary linked to a ULPIN. */
public class MapActivity extends AppCompatActivity implements LocationListener {
    private static final int LOCATION_REQ=4101;
    private MapView map; private LocationManager locationManager; private Marker currentMarker; private String selectedUlpin;

    @Override protected void onCreate(Bundle b){
        super.onCreate(b); if(getSupportActionBar()!=null)getSupportActionBar().hide();
        Configuration.getInstance().setUserAgentValue(getPackageName());
        selectedUlpin=getIntent().getStringExtra("ulpin");
        DatabaseManager db=new DatabaseManager(this);

        FrameLayout root=new FrameLayout(this); root.setBackgroundColor(utils.BG);
        map=new MapView(this); map.setTileSource(TileSourceFactory.MAPNIK); map.setMultiTouchControls(true); map.setBuiltInZoomControls(false); root.addView(map,new FrameLayout.LayoutParams(-1,-1));

        LinearLayout top=new LinearLayout(this); top.setOrientation(LinearLayout.VERTICAL); top.setPadding(utils.dp(this,14),utils.dp(this,14),utils.dp(this,14),0);
        MaterialCardView header=utils.card(this); LinearLayout hb=utils.column(this);
        LinearLayout titleRow=utils.rowContainer(this);
        TextView title=utils.text(this,selectedUlpin==null?"GIS Land Map":"Parcel Map",20,utils.NAVY,true); titleRow.addView(title,new LinearLayout.LayoutParams(0,-2,1)); titleRow.addView(utils.badge(this,selectedUlpin==null?"LIVE MAP":"ULPIN LINKED",true)); hb.addView(titleRow);
        hb.addView(utils.text(this,selectedUlpin==null?"Explore the land map and your current position.":"Selected ULPIN: "+selectedUlpin,11,utils.MUTED,false));
        header.addView(hb); top.addView(header,new LinearLayout.LayoutParams(-1,-2)); root.addView(top,new FrameLayout.LayoutParams(-1,-2));

        LinearLayout bottom=new LinearLayout(this); bottom.setOrientation(LinearLayout.VERTICAL); bottom.setPadding(utils.dp(this,12),0,utils.dp(this,12),utils.dp(this,14));
        MaterialCardView controls=utils.card(this); LinearLayout cb=utils.column(this);
        cb.addView(utils.text(this,"GIS CONTROLS",9,utils.GOLD,true));
        LinearLayout buttons=new LinearLayout(this); buttons.setOrientation(LinearLayout.HORIZONTAL);
        MaterialButton locate=utils.button(this,"My location"); buttons.addView(locate,new LinearLayout.LayoutParams(0,utils.dp(this,48),1));
        MaterialButton parcel=utils.outlineButton(this,"Show ULPIN"); LinearLayout.LayoutParams pp=new LinearLayout.LayoutParams(0,utils.dp(this,48),1);pp.leftMargin=utils.dp(this,8);buttons.addView(parcel,pp);
        cb.addView(buttons); cb.addView(utils.text(this,"Demo parcel geometry is synthetic until an authorised cadastral/GIS layer is connected.",9,utils.MUTED,false),margin(5)); controls.addView(cb); bottom.addView(controls); FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(-1,-2,Gravity.BOTTOM);root.addView(bottom,bp);
        setContentView(root);

        locate.setOnClickListener(v->requestLocation()); parcel.setOnClickListener(v->{if(selectedUlpin!=null)showParcel(selectedUlpin,db);});
        if(selectedUlpin!=null) showParcel(selectedUlpin,db); else {GeoPoint c=new GeoPoint(23.2156,72.6369);map.getController().setZoom(14.5);map.getController().setCenter(c);}
        if(hasLocationPermission())startLocation();
    }
    private void showParcel(String ulpin,DatabaseManager manager){
        android.database.Cursor c=null;try{c=manager.getDatabase().rawQuery("SELECT latitude,longitude,owner_name,district,village FROM parcels WHERE ulpin=?",new String[]{ulpin});if(!c.moveToFirst())return;double lat=c.getDouble(0),lon=c.getDouble(1);GeoPoint center=new GeoPoint(lat,lon);map.getController().setZoom(17.0);map.getController().setCenter(center);Marker m=new Marker(map);m.setPosition(center);m.setTitle("ULPIN: "+ulpin);m.setSnippet(c.getString(2)+" • "+c.getString(4)+", "+c.getString(3));map.getOverlays().add(m);Polygon poly=new Polygon(map);List<GeoPoint> pts=new ArrayList<>();android.database.Cursor v=manager.getDatabase().rawQuery("SELECT latitude,longitude FROM parcel_vertices WHERE ulpin=? ORDER BY vertex_order",new String[]{ulpin});try{while(v.moveToNext())pts.add(new GeoPoint(v.getDouble(0),v.getDouble(1)));}finally{v.close();}if(pts.size()>=3){poly.setPoints(pts);poly.setFillColor(0x44308F45);poly.setStrokeColor(0xff2F8F45);poly.setStrokeWidth(4);map.getOverlays().add(poly);}map.invalidate();}finally{if(c!=null)c.close();}}
    private boolean hasLocationPermission(){return ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED||ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED;}
    private void requestLocation(){if(!hasLocationPermission()){ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_REQ);return;}startLocation();}
    private void startLocation(){locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);try{if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1500,2,this);if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,2500,5,this);Location last=null;if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)last=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);if(last!=null)onLocationChanged(last);}catch(SecurityException ignored){}}
    @Override public void onLocationChanged(@NonNull Location l){GeoPoint p=new GeoPoint(l.getLatitude(),l.getLongitude());if(currentMarker==null){currentMarker=new Marker(map);currentMarker.setTitle("My current location");map.getOverlays().add(currentMarker);}currentMarker.setPosition(p);map.invalidate();}
    @Override public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] results){super.onRequestPermissionsResult(requestCode,permissions,results);if(requestCode==LOCATION_REQ&&hasLocationPermission())startLocation();}
    @Override protected void onPause(){if(locationManager!=null)try{locationManager.removeUpdates(this);}catch(Exception ignored){}if(map!=null)map.onPause();super.onPause();}
    @Override protected void onResume(){super.onResume();if(map!=null)map.onResume();}
    private LinearLayout.LayoutParams margin(int bottom){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.bottomMargin=utils.dp(this,bottom);return p;}
}
