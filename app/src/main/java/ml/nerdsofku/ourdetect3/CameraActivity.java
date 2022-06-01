package ml.nerdsofku.ourdetect3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity {

    private CameraView cameraPreview;
    private ImageButton imageButton;
    //private ProgressBar progressBar;
    private RelativeLayout overlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraPreview = findViewById(R.id.camera);
        imageButton = findViewById(R.id.btnClassify);
        overlay = findViewById(R.id.overlay);
        //progressBar = findViewById(R.id.proBar);
        //progressBar.setVisibility(View.GONE);



        cameraPreview.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                //progressBar.setVisibility(View.VISIBLE);
                Bitmap bitmap = null;
                try {
                    bitmap = processImage(cameraKitImage.getBitmap());
                } catch (IOException e) {
                    Log.e("gf","Dsddd");
                    e.printStackTrace();
                }
                //bitmap = Bitmap.createScaledBitmap(bitmap,ClassifyActivity.INPUT_SIZE,ClassifyActivity.INPUT_SIZE,false);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Intent intent = new Intent(CameraActivity.this,ClassifyActivity.class);
                intent.putExtra("img",stream.toByteArray());
                //progressBar.setVisibility(View.GONE);
                startActivity(intent);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraPreview.captureImage();
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Get the preview size
        int previewWidth = cameraPreview.getMeasuredWidth(),
                previewHeight = cameraPreview.getMeasuredHeight();

        // Set the height of the overlay so that it makes the preview a square
        RelativeLayout.LayoutParams overlayParams = (RelativeLayout.LayoutParams) overlay.getLayoutParams();
        overlayParams.height = previewHeight - previewWidth;
        overlay.setLayoutParams(overlayParams);
    }

    private Bitmap processImage(Bitmap bitmap) throws IOException {

        // Load the bitmap from the byte array
        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inPreferredConfig = Bitmap.Config.RGB_8888;
        //Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        // Rotate and crop the image into a square
        int croppedWidth = (width > height) ? height : width;
        int croppedHeight = (width > height) ? height : width;

        Matrix matrix = new Matrix();
        matrix.postRotate(0);
        Bitmap cropped = Bitmap.createBitmap(bitmap, 0, 0, croppedWidth, croppedHeight, matrix, true);
        bitmap.recycle();

        // Scale down to the output size
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(cropped, 224, 224, true);
        cropped.recycle();

        return scaledBitmap;
    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraPreview.start();
    }

    @Override
    protected void onResume() {
        cameraPreview.start();
        super.onResume();
    }

    @Override
    protected void onPause() {
        cameraPreview.stop();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraPreview.stop();
        super.onStop();
    }


}
