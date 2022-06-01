package ml.nerdsofku.ourdetect3;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ClassifyActivity extends AppCompatActivity {

    public static final String LABEL_PATH = "labels_mobilenet_quant_v1_224.txt";
    public static final String MODEL_PATH = "mobilenet_v1_1.0_224_quant.tflite";
    //public static final boolean QANT = true;
    public static final int INPUT_SIZE = 224;

    private ImageView imageView;
    private TextView name, confidence, ccHead,cc1,cc2,cc3;
    private Bitmap bitmap;
    private Classifier classifier;
    private List<Classifier.Recognition> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify);

        imageView = findViewById(R.id.imageToView);
        name = findViewById(R.id.itemName);
        confidence = findViewById(R.id.confidence);
        ccHead = findViewById(R.id.closeConItem);
        cc1 = findViewById(R.id.conItem1);
        cc2 = findViewById(R.id.conItem2);
        cc3 = findViewById(R.id.conItem3);
        bitmap = generateBMP();
        makeClassifier();
        result = classifier.recognizeImage(bitmap);
        printResults();
    }

    private void printResults() {
        int size = result.size();
        name.setText(result.get(0).getTitle());
        confidence.setText(result.get(0).getConfidenceString());
        hideCloseItems();
        //showCloseItems();
        cc1.setText(result.get(1).getTitle() +" : "+ result.get(1).getConfidenceString());
        cc2.setText(result.get(2).getTitle() +" : "+ result.get(2).getConfidenceString());
        cc3.setText(result.get(3).getTitle() +" : "+ result.get(3).getConfidenceString());
        imageView.setImageBitmap(bitmap);
    }

    private void hideCloseItems() {
        ccHead.setVisibility(View.INVISIBLE);
        cc1.setVisibility(View.INVISIBLE);
        cc2.setVisibility(View.INVISIBLE);
        cc3.setVisibility(View.INVISIBLE);
    }

    /*private void showCloseItems() {
        ccHead.setVisibility(View.VISIBLE);
        cc1.setVisibility(View.VISIBLE);
        cc2.setVisibility(View.VISIBLE);
        cc3.setVisibility(View.VISIBLE);
    }*/

    private void makeClassifier() {
        classifier = TFLiteClassifier.create(getResources().getAssets(),getFilesDir()+"/"+MODEL_PATH,getFilesDir()+"/"+LABEL_PATH,INPUT_SIZE);
    }

    private Bitmap generateBMP() {
        byte[] bytes = getIntent().getExtras().getByteArray("img");
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
