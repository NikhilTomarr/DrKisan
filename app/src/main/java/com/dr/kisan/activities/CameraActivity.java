package com.dr.kisan.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dr.kisan.R;
import com.dr.kisan.models.DiseaseClassifier;
import com.dr.kisan.models.DiseaseResult;
import com.dr.kisan.utils.ImageProcessor;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.BuildConfig;

public class CameraActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 1001;
    private static final int REQUEST_GALLERY = 1002;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};

    private PreviewView previewView;
    private FloatingActionButton fabCapture;
    private MaterialButton btnGallery;
    private MaterialButton btnFlash;
    private android.widget.ImageView btnBack;

    private Camera camera;
    private Preview preview;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;

    private DiseaseClassifier diseaseClassifier;
    private boolean isFlashOn = false;
    private android.app.ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        initializeViews();
        initializeClassifier();

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        cameraExecutor = Executors.newSingleThreadExecutor();
        setupClickListeners();
    }

    private void initializeViews() {
        previewView = findViewById(R.id.previewView);
        fabCapture = findViewById(R.id.fabCapture);
        btnGallery = findViewById(R.id.btnGallery);
        btnFlash = findViewById(R.id.btnFlash);
        btnBack = findViewById(R.id.btnBack);
    }



    private void setupClickListeners() {
        fabCapture.setOnClickListener(v -> capturePhoto());
        btnGallery.setOnClickListener(v -> openGallery());
        btnFlash.setOnClickListener(v -> toggleFlash());
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases(cameraProvider);
            } catch (Exception e) {
                Toast.makeText(this, "Camera initialization failed", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases(ProcessCameraProvider cameraProvider) {
        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        preview = new Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build();

        try {
            cameraProvider.unbindAll();
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
        } catch (Exception e) {
            Toast.makeText(this, "Camera binding failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void capturePhoto() {
        if (imageCapture == null) return;

        File photoFile = new File(getExternalFilesDir(null),
                "captured_image_" + System.currentTimeMillis() + ".jpg");

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile)
                .build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        processAndAnalyzeImage(photoFile);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(CameraActivity.this, "Photo capture failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    private void toggleFlash() {
        if (camera != null && camera.getCameraInfo().hasFlashUnit()) {
            isFlashOn = !isFlashOn;
            camera.getCameraControl().enableTorch(isFlashOn);
            btnFlash.setIcon(isFlashOn ?
                    ContextCompat.getDrawable(this, R.drawable.ic_flash_on) :
                    ContextCompat.getDrawable(this, R.drawable.ic_flash_off));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            processImageFromGallery(selectedImageUri);
        }
    }

    private void processImageFromGallery(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            analyzeImage(bitmap);
        } catch (IOException e) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    private void processAndAnalyzeImage(File imageFile) {
        try {
            Bitmap bitmap = ImageProcessor.getBitmapFromFile(imageFile);
            analyzeImage(bitmap);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
        }
    }

    private void analyzeImage(Bitmap bitmap) {
        if (bitmap == null) {
            Toast.makeText(this, "Invalid image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate bitmap properties
        if (bitmap.getWidth() < 100 || bitmap.getHeight() < 100) {
            Toast.makeText(this, "Image too small for analysis", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading indicator
        showLoadingDialog();

        // Run analysis in background thread
        cameraExecutor.execute(() -> {
            try {
                Log.d("CameraActivity", "Starting real model analysis...");
                Log.d("CameraActivity", "Image size: " + bitmap.getWidth() + "x" + bitmap.getHeight());

                // Check if classifier is ready
                if (diseaseClassifier == null || !diseaseClassifier.isModelReady()) {
                    Log.e("CameraActivity", "Disease classifier not ready");
                    runOnUiThread(() -> {
                        hideLoadingDialog();
                        Toast.makeText(CameraActivity.this, "Model not ready. Please wait...", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                // Start analysis timer
                long startTime = System.currentTimeMillis();

                // Process the image with real TensorFlow Lite model
                DiseaseResult result = diseaseClassifier.classifyImage(bitmap);

                long analysisTime = System.currentTimeMillis() - startTime;
                Log.d("CameraActivity", "Analysis completed in " + analysisTime + "ms");

                if (result == null) {
                    Log.e("CameraActivity", "Analysis returned null result");
                    runOnUiThread(() -> {
                        hideLoadingDialog();
                        Toast.makeText(CameraActivity.this, "Analysis failed - no result", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                Log.d("CameraActivity", "Analysis successful: " + result.getDiseaseName() +
                        " (" + result.getConfidencePercentage() + ")");

                // Switch back to main thread for UI updates
                runOnUiThread(() -> {
                    hideLoadingDialog();
                    navigateToResults(result, bitmap);
                });

            } catch (Exception e) {
                Log.e("CameraActivity", "Error during real model analysis", e);
                runOnUiThread(() -> {
                    hideLoadingDialog();
                    String errorMsg = "Analysis failed: " + e.getMessage();
                    Toast.makeText(CameraActivity.this, errorMsg, Toast.LENGTH_LONG).show();

                    // Show detailed error in debug mode
                    if (BuildConfig.DEBUG) {
                        new android.app.AlertDialog.Builder(CameraActivity.this)
                                .setTitle("Debug Error Info")
                                .setMessage("Error: " + e.getClass().getSimpleName() + "\n" + e.getMessage())
                                .setPositiveButton("OK", null)
                                .show();
                    }
                });
            }
        });
    }


    private void initializeClassifier() {
        try {
            diseaseClassifier = new DiseaseClassifier(this);
            if (diseaseClassifier.isModelReady()) {
                Log.d("CameraActivity", "Disease classifier initialized successfully");
            } else {
                Log.e("CameraActivity", "Disease classifier failed to initialize");
                Toast.makeText(this, "Model loading failed", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("CameraActivity", "Error initializing classifier", e);
            Toast.makeText(this, "Failed to load disease detection model", Toast.LENGTH_LONG).show();
        }
    }

    private void navigateToResults(DiseaseResult result, Bitmap bitmap) {
        Intent intent = new Intent(this, DiseaseResultActivity.class);

        // Pass result data
        intent.putExtra("disease_name", result.getDiseaseName());
        intent.putExtra("confidence", result.getConfidence());
        intent.putExtra("plant_species", result.getPlantSpecies());
        intent.putExtra("severity", result.getSeverity());
        intent.putExtra("description", result.getDescription());
        intent.putExtra("treatment", result.getTreatment());
        intent.putExtra("prevention", result.getPrevention());

        // Save and pass bitmap
        String imagePath = ImageProcessor.saveBitmapToFile(this, bitmap);
        intent.putExtra("image_path", imagePath);

        startActivity(intent);
    }

    private void showLoadingDialog() {
        loadingDialog = new android.app.ProgressDialog(this);
        loadingDialog.setMessage("Analyzing image...");
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // FIX: Add super call in onRequestPermissionsResult
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permissions required", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
        if (diseaseClassifier != null) {
            diseaseClassifier.close();
        }
    }
}
