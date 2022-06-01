package ml.nerdsofku.ourdetect3;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!new File(getFilesDir() + "/" + "done_blank").exists()) {
            try {
                for (String f : getAssets().list("models")) {
                    new File(getFilesDir() + "/" + "done_blank").mkdir();
                    Log.i("firstFolder", "FF created");
                    new FileOutputStream(new File(getFilesDir() + "/" + f)).write(IOUtils.toByteArray(getAssets().open("models/" + f)));
                }
            } catch (Exception ex) {

            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(MainActivity.this, CameraActivity.class));
                finish();
            }
        }, 1000);
    }
}
