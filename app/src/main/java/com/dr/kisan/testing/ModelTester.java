package com.dr.kisan.testing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.dr.kisan.models.DiseaseClassifier;
import com.dr.kisan.models.DiseaseResult;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ModelTester {
    private Context context;
    private DiseaseClassifier classifier;

    public ModelTester(Context context) {
        this.context = context;
        this.classifier = new DiseaseClassifier(context);
    }

    public void runAccuracyTests() {
        List<TestCase> testCases = createTestCases();
        int correctPredictions = 0;
        int totalTests = testCases.size();

        for (TestCase testCase : testCases) {
            try {
                InputStream inputStream = context.getAssets().open("test_images/" + testCase.imagePath);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                DiseaseResult result = classifier.classifyImage(bitmap);

                if (result.getDiseaseName().toLowerCase().contains(testCase.expectedDisease.toLowerCase())) {
                    correctPredictions++;
                    System.out.println("✓ Correct: " + testCase.imagePath + " -> " + result.getDiseaseName());
                } else {
                    System.out.println("✗ Incorrect: " + testCase.imagePath + " -> Expected: " +
                            testCase.expectedDisease + ", Got: " + result.getDiseaseName());
                }

                inputStream.close();
            } catch (IOException e) {
                System.out.println("Error loading test image: " + testCase.imagePath);
            }
        }

        double accuracy = (double) correctPredictions / totalTests * 100;
        System.out.println("\nTest Results:");
        System.out.println("Correct Predictions: " + correctPredictions + "/" + totalTests);
        System.out.println("Accuracy: " + String.format("%.2f", accuracy) + "%");
    }

    private List<TestCase> createTestCases() {
        List<TestCase> testCases = new ArrayList<>();

        // Add test cases for various diseases
        testCases.add(new TestCase("tomato_blight_1.jpg", "tomato_late_blight"));
        testCases.add(new TestCase("potato_healthy_1.jpg", "healthy"));
        testCases.add(new TestCase("apple_scab_1.jpg", "apple_scab"));
        testCases.add(new TestCase("corn_rust_1.jpg", "corn_rust"));

        return testCases;
    }

    private static class TestCase {
        String imagePath;
        String expectedDisease;

        TestCase(String imagePath, String expectedDisease) {
            this.imagePath = imagePath;
            this.expectedDisease = expectedDisease;
        }
    }
}
