package com.bytedance.videoplayer;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class VedioPlayerActivity extends AppCompatActivity {
    private SurfaceView surfaceView;
    private MediaPlayer player;
    private SurfaceHolder holder;
    private  boolean isStop = false;
    private SeekBar seekBar;
    TextView textView;
    private String MEDIA_PROGRESS = "progress";
    private Handler handler = new Handler();
    private Runnable updateseekbar;
    private final String TAG = "MEDIA PLAYER";
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vedio_player);
        //setTitle("Vedio Player");

        surfaceView = findViewById(R.id.surfaceview);
        player = new MediaPlayer();

        initsurfaceview();
        initseekbar();
        initbutton();

        Log.e("logERROR", savedInstanceState==null?"true":"false");
        System.out.println("savedInstance is :" + savedInstanceState==null?"true" :"false");


        if(savedInstanceState !=null){
            int progress = savedInstanceState.getInt(MEDIA_PROGRESS);
            seekBar.setProgress(progress);
            //player.start();
            player.seekTo(progress);
            //isStop = false;
            Log.e("come here", "onCreate: "+progress );
        }

        updateseekbar = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(player.getCurrentPosition());
                handler.postDelayed(updateseekbar, 100);
            }
        };
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mplayer) {
                mplayer.start();
                handler.post(updateseekbar);
                //mplayer.setLooping(true);
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updateseekbar);
        player.pause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        handler.post(updateseekbar);
        player.start();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
        Log.d("destroy", "onDestroy() called");
    }


    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        System.out.println("INSIDE HERE");
        outState.putInt(MEDIA_PROGRESS,player.getCurrentPosition());
        Log.e("save instance", "onSaveInstanceState: "+outState );

    }

    private class PlayerCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            player.setDisplay(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }

    private class SeekBarThread implements Runnable {

        @Override
        public void run() {
            while (player != null ) {
                // 将SeekBar位置设置到当前播放位置
                if(isStop == false) {
                    seekBar.setProgress(player.getCurrentPosition());
                    try {
                        // 每100毫秒更新一次位置
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void initsurfaceview(){
        try{
            //player = MediaPlayer.create(this,R.raw.yuminhong);
            player.setDataSource(getResources().openRawResourceFd(R.raw.v2));
            holder = surfaceView.getHolder();
            holder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    player.setDisplay(surfaceHolder);
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

                }
            });
            player.prepare();
//            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mediaPlayer) {
//                    player.start();
//                    player.setLooping(false);
//                }
//            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void initseekbar(){
        seekBar = findViewById(R.id.seekbar);
        textView = findViewById(R.id.description);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //player.seekTo(i);
                if(b){
                    player.pause();
                    player.seekTo(i);
                    player.start();
                }
                textView.setText("当前进度:" + (int)i * 100/(int)player.getDuration() + "%");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isStop = true;

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // player.seekTo(seekBar.getProgress());
                isStop = false;

            }
        });
        seekBar.setMax(player.getDuration());
    }

    public void initbutton(){
        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStop = false;
                player.start();
                handler.post(updateseekbar);

            }
        });

        findViewById(R.id.pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.pause();
                handler.removeCallbacks(updateseekbar);
                isStop = true;
            }
        });


    }

}


