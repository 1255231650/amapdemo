package com.android.amapdemo;

import static com.amap.api.maps.model.MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.MotionEvent;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity implements AMap.OnMapLoadedListener {

    MapView mapview = null;
    AMap mAMap = null;

    private LatLng mStartCenter;
    protected int followStatus = FOLLOW;
    protected static final int FOLLOW = 0;

    /**
     * 自动变为跟随
     */
    protected static final int FOLLOW_AUTO_BACK = 1;

    /**
     * 禁止跟随
     */
    protected static final int FORBID_FOLLOW = 2;

    /**
     * 最小移动地图距离
     */
    public static final int MIN_MOVE_DISTANCE = 25;

    protected RxManager mRxManager;

    private float mStartZoom = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapview = (MapView) findViewById(R.id.map);

        mAMap = mapview.getMap();
        this.mAMap.setCustomMapStylePath(this.getFilesDir() + "/" + "map_style_hyh.data");
        this.mAMap.setOnMapLoadedListener(this);
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        //设置当前定位点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pin0));
        //设置蓝点跟随type
        myLocationStyle.myLocationType(LOCATION_TYPE_FOLLOW_NO_CENTER);
        //设置描点
        myLocationStyle.anchor(0.5f, 1.0f);
        myLocationStyle.strokeColor(android.R.color.transparent);

        myLocationStyle.radiusFillColor(android.R.color.transparent);
        //定位间隔
        myLocationStyle.interval(100);
        this.mAMap.setMyLocationStyle(myLocationStyle);
        //禁止蓝点
        this.mAMap.setMyLocationEnabled(false);
        this.mAMap.setOnMapTouchListener(mMapTouchListener);
        UiSettings uiSettings = this.mAMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(BuildConfig.DEBUG);
        uiSettings.setRotateGesturesEnabled(false);

        mapview.onCreate(savedInstanceState);
    }

    AMap.OnMapTouchListener mMapTouchListener = new AMap.OnMapTouchListener() {
        @Override
        public void onTouch(MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    delayLocation(10);
                    break;
                case MotionEvent.ACTION_DOWN:
                    mStartZoom = mAMap.getCameraPosition().zoom;
                    mStartCenter = mAMap.getCameraPosition().target;
                    followStatus = FORBID_FOLLOW;
                    break;
                default:
                    //do nothing
                    break;
            }
        }
    };

    protected void delayLocation(int time) {
        mRxManager.clear();
        mRxManager.add(Observable.just(1)
                .delay(time, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> {
                    if (followStatus == FORBID_FOLLOW) {
                        followStatus = FOLLOW_AUTO_BACK;
                    } else {
                        followStatus = FOLLOW;
                    }
                }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapview.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapview.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapview.onSaveInstanceState(outState);
    }

    @Override
    public void onMapLoaded() {
        this.mapview.setVisibility(View.VISIBLE);
    }
}