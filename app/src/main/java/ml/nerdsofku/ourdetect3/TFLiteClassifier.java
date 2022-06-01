package ml.nerdsofku.ourdetect3;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class TFLiteClassifier implements Classifier{

    private int inputSize;
    private static AssetManager assetManager;
    private Interpreter interpreter;
    private List<String> labels;
    private final int MAX_OUTPUT = 4;


    static Classifier create(AssetManager assetManager,String modelPath, String labelPath,int inputSize){
        TFLiteClassifier tfLiteClassifier = new TFLiteClassifier();
        tfLiteClassifier.assetManager = assetManager;
        tfLiteClassifier.interpreter = new Interpreter(loadModelFile(modelPath),new Interpreter.Options());
        tfLiteClassifier.labels = loadLabels(labelPath);
        tfLiteClassifier.inputSize = inputSize;
        return tfLiteClassifier;
    }

    private static List<String> loadLabels(String labelPath) {
        List<String> list = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(labelPath))));
            String line;
            while ((line=bufferedReader.readLine())!=null){
                list.add(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static MappedByteBuffer loadModelFile(String modelPath) {
        MappedByteBuffer mappedByteBuffer = null;
        try {
            //Log.e("dsd",modelPath);
            //Log.e("fwf", Arrays.toString(assetManager.list("")));
            //AssetFileDescriptor assetFileDescriptor  = assetManager.openFd(modelPath);
            //Log.e("dsd",assetFileDescriptor.toString());
            //FileInputStream fis = new FileInputStream(assetFileDescriptor.getFileDescriptor());

            FileChannel fileChannel = new RandomAccessFile(new File(modelPath),"r").getChannel();

            //FileChannel fileChannel = fis.getChannel();
            mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY,0,fileChannel.size());
        } catch (IOException ex) {
            Log.e("HEHE",ex.getClass().getSimpleName());

        }

        return mappedByteBuffer;

    }

    @Override
    public List<Recognition> recognizeImage(Bitmap bitmap) {
        ByteBuffer byteBuffer = toByteBuffer(bitmap);
        byte[][] res = new byte[1][labels.size()];
        interpreter.run(byteBuffer,res);
        return getSortedResult(res);
    }

    private List<Recognition> getSortedResult(byte[][] res) {
        PriorityQueue<Recognition> priorityQueue = new PriorityQueue<>(MAX_OUTPUT, new Comparator<Recognition>() {
            @Override
            public int compare(Recognition r0, Recognition r1) {
                return Float.compare(r0.getConfidence(),r1.getConfidence());
            }
        });
        for (int i = 0; i < labels.size(); ++i) {
            priorityQueue.add(
                    new Recognition("" + i, labels.size() > i ? labels.get(i) : "unknown", (float) res[0][i],res[0][i]));
        }

        List<Recognition> recognitionList = new ArrayList<>();
        int recNum = Math.min(priorityQueue.size(),MAX_OUTPUT);
        for(int i=0;i<recNum;i++){
            recognitionList.add(priorityQueue.poll());
        }
        return recognitionList;
    }

    private ByteBuffer toByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer;
        byteBuffer = ByteBuffer.allocateDirect(inputSize*inputSize*3); //each color has 3 byte in RGB color space
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] iValues = new int[inputSize*inputSize];
        bitmap.getPixels(iValues,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
        int pixel = 0;
        for(int i=0;i<inputSize;i++){
            for(int j=0;j<inputSize;j++){
                final int val = iValues[pixel++];
                byteBuffer.put((byte) ((val>>16) & 0xFF)); //R value
                byteBuffer.put((byte) ((val>>8) & 0xFF)); //G value
                byteBuffer.put((byte) (val & 0xFF)); //B value
            }
        }
        return byteBuffer;
    }

    @Override
    public void close() {
        interpreter.close();
    }
}
