package com.example.kimberjin.viewtest;

import android.content.pm.PackageManager;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity{

    public static final String TAG = "MAINLOG";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setContentView(new AnimateView(this));

        Button videoBtn = findViewById(R.id.vdBtn);

        getAuth();

        videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extractVideo();
            }
        });

    }

    private void extractVideo() {
        Log.i(TAG, "extractVideo() start");
        MediaExtractor mediaExtractor = new MediaExtractor();
        MediaMuxer mediaMuxer = null;

        try {
            Log.e(TAG, Environment.getExternalStorageDirectory().getPath());
            Log.e(TAG, Environment.getExternalStorageDirectory().getAbsolutePath());
            //设置视频源
            mediaExtractor.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/cemabenteng.mp4");
            //轨道索引
            int videoIndex = -1;
            //视频轨道格式信息
            MediaFormat mediaFormat = null;
            //数据源的轨道数（一般有视频，音频，字幕等）
            int trackCount = mediaExtractor.getTrackCount();

            //循环轨道数，找到需要的视频轨
            for (int i = 0; i < trackCount; i++) {
                mediaFormat = mediaExtractor.getTrackFormat(i);
                String mimeType = mediaFormat.getString(MediaFormat.KEY_MIME);
                if (mimeType.startsWith("video/")) {
                    videoIndex = i;
                    break;
                }
            }

            if (mediaFormat == null || videoIndex < 0) {
                return;
            }

            // 最大缓冲区字节数
            int maxInputSize = mediaFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
            // 格式类型
            String mimeType = mediaFormat.getString(MediaFormat.KEY_MIME);
            // 视频的比特率
            int bitRate = mediaFormat.getInteger(MediaFormat.KEY_BIT_RATE);
            // 视频宽度
            int width = mediaFormat.getInteger(MediaFormat.KEY_WIDTH);
            // 视频高度
            int height = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT);
            // 内容持续时间（以微妙为单位）
            long duration = mediaFormat.getLong(MediaFormat.KEY_DURATION);
            // 视频的帧率
            int framerate = mediaFormat.getInteger(MediaFormat.KEY_FRAME_RATE);

            // 视频内容颜色空间
            int colorFormat = -1;
            if (mediaFormat.containsKey(MediaFormat.KEY_COLOR_FORMAT)) {
                colorFormat = mediaFormat.getInteger(MediaFormat.KEY_COLOR_FORMAT);
            }

            // 关键之间的时间间隔
            int iFrameInterval = -1;
            if (mediaFormat.containsKey(MediaFormat.KEY_I_FRAME_INTERVAL)) {
                iFrameInterval = mediaFormat.getInteger(MediaFormat.KEY_I_FRAME_INTERVAL);
            }

            //  视频旋转顺时针角度
            int rotation = -1;
            if (mediaFormat.containsKey(MediaFormat.KEY_ROTATION)) {
                rotation = mediaFormat.getInteger(MediaFormat.KEY_ROTATION);
            }

            // 比特率模式
            int bitRateMode = -1;
            if (mediaFormat.containsKey(MediaFormat.KEY_BITRATE_MODE)) {
                bitRateMode = mediaFormat.getInteger(MediaFormat.KEY_BITRATE_MODE);
            }

            Log.i(TAG, "mimeType: " + mimeType + "\nmaxInputSize: " + maxInputSize + "\nbitRate: "
                + bitRate +  "\nbitRateMode: " + bitRateMode + "\nwidth: " + width + "\nheight: "
                + height + "\nduration: " + duration/1000 + "\nframerate: " + framerate + "\ncolorFormat: "
                + colorFormat + "\niFrameInterval: " + iFrameInterval + "\nrotation: " + rotation);

            //切换视频的轨道
            mediaExtractor.selectTrack(videoIndex);

            String outPath = Environment.getExternalStorageDirectory().getPath() + "/output.mp4";
            mediaMuxer = new MediaMuxer(outPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            //将视频添加到MediaMuxer，并返回新轨道
            int trackIndex = mediaMuxer.addTrack(mediaFormat);
            ByteBuffer byteBuffer = ByteBuffer.allocate(maxInputSize);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

            //开始合成
            mediaMuxer.start();
            while (true) {
                //检索当前编码的样本并将其存储在字节缓冲区中
                int readSampleSize = mediaExtractor.readSampleData(byteBuffer, 0);
                // 如果没有可获取的样本则退出循环
                if (readSampleSize < 0) {
                    mediaExtractor.unselectTrack(videoIndex);
                    break;
                }

                //设置样本编码信息
                bufferInfo.size = readSampleSize;
                bufferInfo.offset = 0;
                bufferInfo.flags = mediaExtractor.getSampleFlags();
                bufferInfo.presentationTimeUs = mediaExtractor.getSampleTime();
                //写入样本数据
                mediaMuxer.writeSampleData(trackIndex, byteBuffer, bufferInfo);
                //推进到下一个样本，类似快进
                mediaExtractor.advance();
            }

            Log.i(TAG, "Finish extract video! Path: " + outPath);
            Toast.makeText(this, "Extract And Muxer Done!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mediaMuxer != null) {
                mediaMuxer.stop();
                mediaMuxer.release();
            }
            mediaExtractor.release();
        }
    }

    private void getAuth() {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(MainActivity.this,
                    "android.permission.READ_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(MainActivity.this,
                        PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
