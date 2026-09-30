package com.acrenex.app.ai;

import android.content.Context;
import android.net.Uri;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class OCRManager {

    public interface OCRCallback {
        void onSuccess(String text);
        void onError(String message);
    }

    private final Context context;
    private final TextRecognizer recognizer;

    public OCRManager(Context context) {

        this.context =
                context.getApplicationContext();

        recognizer =
                TextRecognition.getClient(
                        TextRecognizerOptions.DEFAULT_OPTIONS
                );
    }

    public void extractText(
            Uri imageUri,
            OCRCallback callback
    ) {

        try {

            InputImage image =
                    InputImage.fromFilePath(
                            context,
                            imageUri
                    );

            recognizer
                    .process(image)
                    .addOnSuccessListener(
                            text ->
                                    callback.onSuccess(
                                            text.getText()
                                    )
                    )
                    .addOnFailureListener(
                            error ->
                                    callback.onError(
                                            error.getMessage()
                                    )
                    );

        } catch (Exception e) {

            callback.onError(
                    "Unable to process document"
            );
        }
    }

    public String extractField(
            String text,
            String fieldName
    ) {

        if (text == null ||
                fieldName == null) {

            return "";
        }

        String[] lines =
                text.split("\\r?\\n");

        for (String line : lines) {

            if (line
                    .toLowerCase()
                    .contains(
                            fieldName.toLowerCase()
                    )) {

                String[] parts =
                        line.split(
                                "[:=-]",
                                2
                        );

                if (parts.length == 2) {

                    return parts[1].trim();
                }
            }
        }

        return "";
    }

    public void close() {

        recognizer.close();
    }
}