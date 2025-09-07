package com.dr.kisan.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.dr.kisan.models.Treatment;
import java.util.ArrayList;
import java.util.List;

public class TreatmentDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "treatments.db";
    private static final int DATABASE_VERSION = 1;

    // Treatment table
    private static final String TABLE_TREATMENTS = "treatments";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DISEASE = "disease";
    private static final String COLUMN_PESTICIDE = "pesticide";
    private static final String COLUMN_DOSAGE = "dosage";
    private static final String COLUMN_METHOD = "application_method";
    private static final String COLUMN_PRECAUTIONS = "precautions";
    private static final String COLUMN_ORGANIC = "is_organic";

    public TreatmentDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TREATMENTS_TABLE = "CREATE TABLE " + TABLE_TREATMENTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DISEASE + " TEXT NOT NULL,"
                + COLUMN_PESTICIDE + " TEXT NOT NULL,"
                + COLUMN_DOSAGE + " TEXT NOT NULL,"
                + COLUMN_METHOD + " TEXT NOT NULL,"
                + COLUMN_PRECAUTIONS + " TEXT,"
                + COLUMN_ORGANIC + " INTEGER DEFAULT 0"
                + ")";
        db.execSQL(CREATE_TREATMENTS_TABLE);

        // Insert sample data
        insertSampleTreatments(db);
    }

    private void insertSampleTreatments(SQLiteDatabase db) {
        // Sample treatments for common diseases
        String[] insertStatements = {
                "INSERT INTO treatments VALUES (null, 'Tomato_Late_blight', 'Copper Sulfate', '2-3 grams/liter', 'Foliar spray', 'Wear protective gear', 1)",
                "INSERT INTO treatments VALUES (null, 'Potato_Early_blight', 'Mancozeb', '2.5 grams/liter', 'Foliar spray', 'Avoid during flowering', 0)",
                "INSERT INTO treatments VALUES (null, 'Apple_Apple_scab', 'Captan', '2 grams/liter', 'Spray application', 'Do not spray during rain', 0)",
                "INSERT INTO treatments VALUES (null, 'Corn_Gray_leaf_spot', 'Propiconazole', '1 ml/liter', 'Foliar application', 'Use during early growth', 0)",
                "INSERT INTO treatments VALUES (null, 'Grape_Black_rot', 'Bordeaux mixture', '10 grams/liter', 'Spray treatment', 'Apply before fruit set', 1)"
        };

        for (String statement : insertStatements) {
            db.execSQL(statement);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TREATMENTS);
        onCreate(db);
    }

    public List<Treatment> getTreatmentsForDisease(String diseaseName) {
        List<Treatment> treatments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_TREATMENTS + " WHERE " + COLUMN_DISEASE + " LIKE ?";
        String[] selectionArgs = {"%" + diseaseName + "%"};

        android.database.Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                Treatment treatment = new Treatment(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getInt(6) == 1
                );
                treatments.add(treatment);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return treatments;
    }
}
