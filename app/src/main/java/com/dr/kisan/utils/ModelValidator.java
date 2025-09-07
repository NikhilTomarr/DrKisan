package com.dr.kisan.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class ModelValidator {
    private static final String TAG = "ModelValidator";

    public static boolean validateModelFiles(Context context) {
        try {
            // Check if assets exist
            String[] assets = context.getAssets().list("");
            boolean modelExists = false;
            boolean labelsExist = false;

            Log.d(TAG, "Available assets: " + Arrays.toString(assets));

            for (String asset : assets) {
                if (asset.equals("disease_model.tflite")) {
                    modelExists = true;
                    Log.d(TAG, "Model file found: " + asset);
                }
                if (asset.equals("labels.txt")) {
                    labelsExist = true;
                    Log.d(TAG, "Labels file found: " + asset);
                }
            }

            if (!modelExists) {
                Log.e(TAG, "disease_model.tflite not found in assets");
                return false;
            }

            if (!labelsExist) {
                Log.e(TAG, "labels.txt not found in assets");
                return false;
            }

            // Validate model structure
            return validateModelStructure(context);

        } catch (IOException e) {
            Log.e(TAG, "Error validating model files", e);
            return false;
        }
    }

    private static boolean validateModelStructure(Context context) {
        AssetFileDescriptor fileDescriptor = null;
        FileInputStream inputStream = null;
        Interpreter interpreter = null;

        try {
            // Load model file
            fileDescriptor = context.getAssets().openFd("disease_model.tflite");
            inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel fileChannel = inputStream.getChannel();

            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();

            MappedByteBuffer modelBuffer = fileChannel.map(
                    FileChannel.MapMode.READ_ONLY,
                    startOffset,
                    declaredLength
            );

            // Create interpreter
            interpreter = new Interpreter(modelBuffer);

            // Get model input/output shapes
            int[] inputShape = interpreter.getInputTensor(0).shape();
            int[] outputShape = interpreter.getOutputTensor(0).shape();

            Log.d(TAG, "Model input shape: " + Arrays.toString(inputShape));
            Log.d(TAG, "Model output shape: " + Arrays.toString(outputShape));

            // Count labels
            int labelCount = countLabels(context);
            Log.d(TAG, "Labels count: " + labelCount);
            Log.d(TAG, "Model output classes: " + outputShape[1]);

            // Check file sizes (basic validation)
            long modelSize = fileDescriptor.getLength();
            Log.d(TAG, "Model size: " + (modelSize / 1024) + " KB");

            if (modelSize < 1000) { // Less than 1KB is suspicious
                Log.w(TAG, "Model file seems too small");
                return false;
            }

            Log.d(TAG, "Model validation successful");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error validating model structure", e);
            return false;
        } finally {
            // Close resources properly
            if (interpreter != null) {
                try {
                    interpreter.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing interpreter", e);
                }
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing input stream", e);
                }
            }

            if (fileDescriptor != null) {
                try {
                    fileDescriptor.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing file descriptor", e);
                }
            }
        }
    }

    private static int countLabels(Context context) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    context.getAssets().open("labels.txt")));

            int count = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    count++;
                }
            }
            return count;

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing reader", e);
                }
            }
        }
    }
}
