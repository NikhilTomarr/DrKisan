package com.dr.kisan.models;

import android.util.Log;
import java.util.Map;

public class DiseaseResult {
    private static final String TAG = "DiseaseResult";

    private String diseaseName;
    private float confidence;
    private String plantSpecies;
    private String severity;
    private String description;
    private String treatment;
    private String prevention;
    private Map<String, Float> allProbabilities;
    private boolean isHealthy;

    public DiseaseResult(String diseaseName, float confidence, Map<String, Float> allProbabilities) {
        this.diseaseName = diseaseName;
        this.confidence = confidence;
        this.allProbabilities = allProbabilities;

        // Parse disease information
        parseDiseaseInfo();

        // Set treatment and prevention
        setTreatmentInfo();

        Log.d(TAG, "Disease result created: " + diseaseName + " (" + (confidence * 100) + "%)");
    }

    private void parseDiseaseInfo() {
        if (diseaseName != null && diseaseName.contains("___")) {
            String[] parts = diseaseName.split("___");

            // Extract and format plant species properly
            this.plantSpecies = formatPlantName(parts[0]);

            // Extract disease information
            String diseaseType = parts[1].replace("_", " ");
            this.isHealthy = diseaseType.equalsIgnoreCase("healthy");

            if (isHealthy) {
                this.severity = "Healthy Plant";
                this.description = "The " + this.plantSpecies + " appears to be healthy with no visible disease symptoms detected.";
            } else {
                this.severity = determineSeverity(confidence);
                this.description = "Disease detected in " + this.plantSpecies + ": " + diseaseType + ". " + getDetailedDescription(diseaseType);
            }
        } else {
            // Fallback - try to extract some meaningful info
            this.plantSpecies = extractPlantFromLabel(diseaseName);
            this.severity = "Analysis Complete";
            this.description = "Disease analysis completed for " + this.plantSpecies + ". Please consult with agricultural expert for detailed diagnosis.";
            this.isHealthy = false;
        }
    }

    private String formatPlantName(String plantName) {
        if (plantName == null || plantName.isEmpty()) {
            return "Unknown Plant";
        }

        // Convert common plant names to proper format
        String formatted = plantName.replace("_", " ");

        // Capitalize first letter of each word
        String[] words = formatted.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        return result.toString().trim();
    }

    private String extractPlantFromLabel(String label) {
        if (label == null || label.isEmpty()) {
            return "Unknown Plant";
        }

        // Common plant names mapping
        String lowerLabel = label.toLowerCase();
        if (lowerLabel.contains("tomato")) return "Tomato";
        if (lowerLabel.contains("potato")) return "Potato";
        if (lowerLabel.contains("apple")) return "Apple";
        if (lowerLabel.contains("corn") || lowerLabel.contains("maize")) return "Corn";
        if (lowerLabel.contains("grape")) return "Grape";
        if (lowerLabel.contains("pepper")) return "Bell Pepper";
        if (lowerLabel.contains("strawberry")) return "Strawberry";
        if (lowerLabel.contains("cherry")) return "Cherry";
        if (lowerLabel.contains("peach")) return "Peach";
        if (lowerLabel.contains("orange")) return "Orange";

        return "Plant"; // Generic fallback
    }

    private String determineSeverity(float confidence) {
        if (confidence > 0.90f) {
            return "High Confidence Detection";
        } else if (confidence > 0.75f) {
            return "Medium Confidence Detection";
        } else if (confidence > 0.60f) {
            return "Low Confidence Detection";
        } else {
            return "Uncertain - Expert Consultation Recommended";
        }
    }

    private String getDetailedDescription(String diseaseType) {
        String type = diseaseType.toLowerCase();

        if (type.contains("blight")) {
            return "Blight is a fungal disease that causes leaf spots and can spread rapidly in humid conditions.";
        } else if (type.contains("rust")) {
            return "Rust appears as orange or reddish spots on leaves and is caused by fungal infection.";
        } else if (type.contains("spot")) {
            return "Leaf spots are circular lesions that can be caused by bacteria or fungi.";
        } else if (type.contains("mosaic") || type.contains("virus")) {
            return "Viral disease causing mottled patterns on leaves, often spread by insects.";
        } else if (type.contains("bacterial")) {
            return "Bacterial infection causing leaf spots, wilting, or stem rot.";
        } else if (type.contains("powdery")) {
            return "Powdery mildew appears as white, powdery coating on leaves.";
        } else {
            return "Please consult agricultural extension services for specific treatment recommendations.";
        }
    }

    private void setTreatmentInfo() {
        if (isHealthy) {
            this.treatment = "No treatment required. Continue regular plant care and monitoring.";
            this.prevention = "Maintain good agricultural practices including proper spacing, irrigation, and nutrition.";
            return;
        }

        String diseaseType = diseaseName.toLowerCase();

        // Treatment recommendations based on disease type
        if (diseaseType.contains("blight")) {
            this.treatment = "Apply copper-based fungicide (Copper sulfate 2-3g/L) or Mancozeb (2.5g/L). " +
                    "Remove affected leaves and ensure good air circulation.";
            this.prevention = "Avoid overhead watering, maintain plant spacing, apply preventive fungicide sprays, " +
                    "and practice crop rotation.";
        } else if (diseaseType.contains("rust")) {
            this.treatment = "Apply systemic fungicide containing Propiconazole (1ml/L) or Tebuconazole. " +
                    "Remove infected leaves immediately.";
            this.prevention = "Ensure good air circulation, avoid leaf wetness, and apply preventive treatments " +
                    "during favorable weather conditions.";
        } else if (diseaseType.contains("bacterial")) {
            this.treatment = "Apply copper-based bactericide. Remove infected plant parts and destroy them. " +
                    "Improve drainage and reduce humidity around plants.";
            this.prevention = "Use disease-free seeds, avoid working with wet plants, maintain proper plant spacing, " +
                    "and practice good sanitation.";
        } else if (diseaseType.contains("virus") || diseaseType.contains("mosaic")) {
            this.treatment = "Remove infected plants immediately to prevent spread. Control insect vectors " +
                    "using appropriate insecticides.";
            this.prevention = "Use virus-free planting material, control aphids and other vector insects, " +
                    "maintain weed-free environment.";
        } else if (diseaseType.contains("powdery")) {
            this.treatment = "Apply sulfur-based fungicide or Potassium bicarbonate solution. " +
                    "Improve air circulation around plants.";
            this.prevention = "Avoid overhead watering, maintain proper plant spacing, and ensure good air circulation.";
        } else {
            this.treatment = "Consult with local agricultural extension officer for specific treatment recommendations. " +
                    "Remove affected plant parts and improve growing conditions.";
            this.prevention = "Follow integrated pest management practices and maintain good plant hygiene.";
        }
    }

    // Getters
    public String getDiseaseName() { return diseaseName; }
    public float getConfidence() { return confidence; }
    public String getPlantSpecies() { return plantSpecies; }
    public String getSeverity() { return severity; }
    public String getDescription() { return description; }
    public String getTreatment() { return treatment; }
    public String getPrevention() { return prevention; }
    public Map<String, Float> getAllProbabilities() { return allProbabilities; }
    public boolean isHealthy() { return isHealthy; }

    public String getConfidencePercentage() {
        return String.format("%.1f%%", confidence * 100);
    }
}
