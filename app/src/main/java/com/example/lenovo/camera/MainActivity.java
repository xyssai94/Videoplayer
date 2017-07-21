package com.example.lenovo.camera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String DEFAULT_FILE_PATH  = Environment.getExternalStorageDirectory() + "/Pictures/testvideo.mp4";
    private static final int VIDEO_WIDTH  = 800;
    private static final int VIDEO_HEIGHT = 600;

    private TextView mTipsTextView;
    private VideoServer mVideoServer;

    CameraPreview mPreview;
    FrameLayout preview;
    Button bPath,buttonCaptureVideo,buttonCapturePhoto;
    ImageView mediaPreview;
    private static final String TAG="CameraPreview";
    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
         Environment.DIRECTORY_PICTURES).toString());

    String path=mediaStorageDir.getPath();



    @Override
    protected void onDestroy() {
        mVideoServer.stop();
        super.onDestroy();
    }

    public static String getLocalIpStr(Context context) {
        WifiManager wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return intToIpAddr(wifiInfo.getIpAddress());
    }

    private static String intToIpAddr(int ip) {
        return (ip & 0xff) + "." + ((ip>>8)&0xff) + "." + ((ip>>16)&0xff) + "." + ((ip>>24)&0xff);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTipsTextView = (TextView)findViewById(R.id. textView2);
        mVideoServer = new VideoServer(DEFAULT_FILE_PATH, VIDEO_WIDTH, VIDEO_HEIGHT, VideoServer.DEFAULT_SERVER_PORT);
        mTipsTextView.setText("IP address:\n\n"+getLocalIpStr(this)+":"+VideoServer.DEFAULT_SERVER_PORT);
        try {
            mVideoServer.start();
        }
        catch (IOException e) {
            e.printStackTrace();
            mTipsTextView.setText(e.getMessage());
        }

        mediaPreview = (ImageView) findViewById(R.id.media_preview);
        preview=(FrameLayout) findViewById(R.id.camera_preview);

        //mPreview=new CameraPreview(this,path);
        initCamera();

        bPath=(Button)findViewById(R.id.button_path);
        bPath.setOnClickListener(this);

        buttonCaptureVideo=(Button)findViewById(R.id.button_capture_video);
        buttonCaptureVideo.setOnClickListener(this);

        buttonCapturePhoto=(Button)findViewById(R.id.button_capture_photo);
        buttonCapturePhoto.setOnClickListener(this);

        mediaPreview.setOnClickListener(this);



        //ask user for permission
        requestMultiplePermissions();
    }
    private static final int REQUEST_CODE = 1;
    private void requestMultiplePermissions(){
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA};
        requestPermissions(permissions, REQUEST_CODE);
    }


    private void initCamera() {
        mPreview = new CameraPreview(this,path);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        Log.i("initCamera","initCamera is using");

    }
    public void onPause() {
        super.onPause();
        mPreview = null;
        Log.e("usePause","usePause");

    }

    public void onResume() {
        super.onResume();
        Log.e("useResume","useResume");
        if (mPreview == null) {
            initCamera();
        }
    }


    private void showDialog(View view) {
        EnterPath enterPath= new EnterPath(MainActivity.this,path);
        enterPath.setOnDialogClickListener(new EnterPath.OnDialogClickListener() {
            @Override
            public void returnPath(String path) {
                if(path!=null || !TextUtils.isEmpty(path)){
                    MainActivity.this.path=path;
                    mPreview.setPath(path);
                }
            }
        });
        enterPath.show();


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_path:
                showDialog(view);
                break;
            case R.id.button_capture_photo:
                mPreview.takePicture(mediaPreview);
                break;
            case R.id.button_capture_video:
                if (mPreview.isRecording()) {
                    mPreview.stopRecording(mediaPreview);
                    buttonCaptureVideo.setText("Record");
                } else {
                    if (mPreview.startRecording()) {
                        buttonCaptureVideo.setText("Stop");
                    }
                }
                break;
            case R.id.media_preview:
                Intent intent = new Intent(MainActivity.this, ShowPhotoVideo.class);
                String MediaFileType=mPreview.getOutputMediaFileType();
                Uri MediaFileUri=mPreview.getOutputMediaFileUri();
                if(MediaFileType!=null && MediaFileUri!=null){
                    intent.setDataAndType(MediaFileUri, MediaFileType);
                    startActivityForResult(intent, 0);
                }else{
                    Toast.makeText(getBaseContext(),"Please take a photo or video first",Toast.LENGTH_LONG).show();
                }
                break;


        }

    }
}