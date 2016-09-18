package com.henry.ceo.zxing;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

/**
 * Created by Administrator on 2016/9/18.
 */
public class CameraActivity extends Activity {
    private SurfaceView  surfaceView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        surfaceView = (SurfaceView) findViewById(R.id.capture_preview_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int cameraId = -1;
        int cameraNum = Camera.getNumberOfCameras();
        if (cameraNum == 0){
            Log.i("sysout", "No Camera");
            return;
        }
        int index = 0;
        while (index < cameraNum) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(index, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                break;
            }
            index++;
        }
        cameraId = index;
        Camera camera;
        if (cameraId < cameraNum) {
            camera = Camera.open(cameraId);
        } else {
            Log.i("sysout", "cameraId="+cameraId);
        }

    }
}
