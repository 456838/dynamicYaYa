package com.yy.xiguaplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.xigua.p2p.P2PClass;
import com.xigua.p2p.P2PEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2017/1/5 9:45
 * Time: 9:45
 * Description:
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.);
        P2PClass.getInstance().init();
//        int pid = android.os.Process.myPid();
//        Log.e("tag_so", "pid " + pid);
//        //self也可以改成pid
//        File f1 = new File("/proc/"+pid+"/maps");
//        if (f1.exists() && f1.isFile()) {
//            readFileByLines(f1.getAbsolutePath());
//        } else {
//            Log.e("tag_so", " cannot read so libs " + f1.exists());
//        }
//        Log.e("ping","ping ret:"+ NativeCHelper.ping());
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "ftp://a.gbl.114s.com:20320/0808/28岁未成年2016.HD720P.国语中字.mp4";
                P2PEngine.addDownloadTask(url);
            }
        }).start();
        String hit = "有你便是晴天";
        Log.e("aa",hit);
        Toast.makeText(getApplicationContext(),hit,Toast.LENGTH_SHORT).show();
    }

    public static void readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                Log.e("tag_so", "line " + line + ": " + tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }
}
