package com.dr.kisan.models;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

public class DiseaseClassifier {
    private static final String TAG = "DiseaseClassifier";
    private static final String MODEL_FILE = "disease_model.tflite";
    private static final String LABELS_FILE = "labels.txt";

    // Model specifications
    private static final int INPUT_SIZE = 224;
    private static final int PIXEL_SIZE = 3;
    private static final int NUM_BYTES_PER_CHANNEL = 4;
    private static final float IMAGE_MEAN = 127.5f;
    private static final float IMAGE_STD = 127.5f;

    private Interpreter interpreter;
    private List<String> labels;
    private ByteBuffer inputBuffer;
    private float[][] outputProbabilities;
    private boolean isModelLoaded = false;
    private int modelOutputSize = 0;

    public DiseaseClassifier(Context context) {
        try {
            Log.d(TAG, "Initializing Disease Classifier...");

            // Load the model
            MappedByteBuffer tfliteModel = loadModelFile(context);
            interpreter = new Interpreter(tfliteModel);

            // Get model output shape dynamically
            int[] outputShape = interpreter.getOutputTensor(0).shape();
            Log.d(TAG, "Model output shape: " + Arrays.toString(outputShape));

            if (outputShape.length >= 2) {
                modelOutputSize = outputShape[1]; // Usually [1, num_classes]
            } else {
                throw new RuntimeException("Unexpected model output shape: " + Arrays.toString(outputShape));
            }

            Log.d(TAG, "Model expects " + modelOutputSize + " output classes");

            // Load labels
            labels = loadLabels(context);
            Log.d(TAG, "Labels file has " + labels.size() + " classes");

            // Check for mismatch
            if (labels.size() != modelOutputSize) {
                Log.w(TAG, "WARNING: Labels count (" + labels.size() + ") != Model output size (" + modelOutputSize + ")");
                Log.w(TAG, "Using model output size for inference");
            }

            // Initialize input buffer
            inputBuffer = ByteBuffer.allocateDirect(
                    1 * INPUT_SIZE * INPUT_SIZE * PIXEL_SIZE * NUM_BYTES_PER_CHANNEL);
            inputBuffer.order(ByteOrder.nativeOrder());

            // Initialize output array with correct model output size
            outputProbabilities = new float[1][modelOutputSize];

            isModelLoaded = true;
            Log.d(TAG, "Model loaded successfully");

        } catch (IOException e) {
            Log.e(TAG, "Error initializing classifier", e);
            isModelLoaded = false;
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error during initialization", e);
            isModelLoaded = false;
        }
    }

    public DiseaseResult classifyImage(Bitmap bitmap) {
        if (!isModelLoaded) {
            throw new RuntimeException("Model not loaded properly");
        }

        try {
            Log.d(TAG, "Starting image classification...");

            // Preprocess the image
            preprocessImage(bitmap);

            // Run inference
            interpreter.run(inputBuffer, outputProbabilities);

            // Process results
            return processResults();

        } catch (Exception e) {
            Log.e(TAG, "Error during classification", e);
            throw new RuntimeException("Classification failed: " + e.getMessage());
        }
    }

    private void preprocessImage(Bitmap bitmap) {
        // Resize bitmap to input size
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true);

        inputBuffer.rewind();

        int[] intValues = new int[INPUT_SIZE * INPUT_SIZE];
        resizedBitmap.getPixels(intValues, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE);

        int pixel = 0;
        for (int i = 0; i < INPUT_SIZE; ++i) {
            for (int j = 0; j < INPUT_SIZE; ++j) {
                final int val = intValues[pixel++];

                float red = ((val >> 16) & 0xFF);
                float green = ((val >> 8) & 0xFF);
                float blue = (val & 0xFF);

                inputBuffer.putFloat((red - IMAGE_MEAN) / IMAGE_STD);
                inputBuffer.putFloat((green - IMAGE_MEAN) / IMAGE_STD);
                inputBuffer.putFloat((blue - IMAGE_MEAN) / IMAGE_STD);
            }
        }

        Log.d(TAG, "Image preprocessing completed");
    }

    private DiseaseResult processResults() {
        float[] probabilities = outputProbabilities[0];

        int maxIndex = 0;
        float maxProbability = probabilities[0];

        for (int i = 1; i < probabilities.length; i++) {
            if (probabilities[i] > maxProbability) {
                maxProbability = probabilities[i];
                maxIndex = i;
            }
        }

        // Get predicted label safely
        String predictedLabel;
        if (maxIndex < labels.size()) {
            predictedLabel = labels.get(maxIndex);
        } else {
            // Fallback if model outputs more classes than labels
            predictedLabel = "Unknown_Disease_Class_" + maxIndex;
            Log.w(TAG, "Model predicted class " + maxIndex + " but only " + labels.size() + " labels available");
        }

        float confidence = maxProbability;

        // Create probability map - only for available labels
        Map<String, Float> allProbabilities = new HashMap<>();
        int maxLabels = Math.min(labels.size(), probabilities.length);

        for (int i = 0; i < maxLabels; i++) {
            allProbabilities.put(labels.get(i), probabilities[i]);
        }

        Log.d(TAG, "Classification result: " + predictedLabel + " with confidence: " + confidence);

        return new DiseaseResult(predictedLabel, confidence, allProbabilities);
    }

    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        try {
            AssetFileDescriptor fileDescriptor = context.getAssets().openFd(MODEL_FILE);
            FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        } catch (IOException e) {
            Log.e(TAG, "Failed to load model file: " + MODEL_FILE, e);
            throw new IOException("Model file not found or corrupted: " + MODEL_FILE);
        }
    }

    private List<String> loadLabels(Context context) throws IOException {
        List<String> labels = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                context.getAssets().open(LABELS_FILE)))) {
            String line;
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                labels.add(line.trim());
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to load labels file: " + LABELS_FILE, e);
            throw new IOException("Labels file not found: " + LABELS_FILE);
        }

        if (labels.isEmpty()) {
            throw new IOException("Labels file is empty");
        }

        Log.d(TAG, "Loaded " + labels.size() + " labels");
        return labels;
    }

    public boolean isModelReady() {
        return isModelLoaded;
    }

    public int getModelOutputSize() {
        return modelOutputSize;
    }

    public int getLabelsCount() {
        return labels != null ? labels.size() : 0;
    }

    public void close() {
        if (interpreter != null) {
            interpreter.close();
            interpreter = null;
        }
        isModelLoaded = false;
        Log.d(TAG, "Disease classifier closed");
    }
}
