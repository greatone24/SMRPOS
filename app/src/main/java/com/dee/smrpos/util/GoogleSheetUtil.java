package com.dee.smrpos.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.dee.smrpos.R;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GoogleSheetUtil {
    private static final String TAG = "GoogleSheetUtil";
    private static final ExecutorService SHEETS_EXECUTOR = Executors.newSingleThreadExecutor();

    // Pass in a Context or Activity so you can show Toasts
    public static void googleSheetInitialization(Activity activity,
                                                 long checkoutId,
                                                 double total,
                                                 String paymentMethod,
                                                 String date) {
        Context appContext = activity.getApplicationContext();
        SHEETS_EXECUTOR.execute(() -> {
            try (InputStream keyStream = appContext.getResources().openRawResource(R.raw.service_account)) {
                GoogleCredentials credentials = ServiceAccountCredentials.fromStream(keyStream)
                        .createScoped(Collections.singleton("https://www.googleapis.com/auth/spreadsheets"));

                Sheets sheetsService = new Sheets.Builder(
                        new com.google.api.client.http.javanet.NetHttpTransport(),
                        new com.google.api.client.json.gson.GsonFactory(),
                        new HttpCredentialsAdapter(credentials))
                        .setApplicationName("smr-pos")
                        .build();
                String cashier = UserUtil.getUsername(appContext);
                List<List<Object>> values = Collections.singletonList(
                        Arrays.asList(cashier, checkoutId, total, paymentMethod, date)
                );
                ValueRange body = new ValueRange().setValues(values);

                AppendValuesResponse response = sheetsService.spreadsheets().values()
                        .append("1Re7XT-KPf7Qmh5-MdTHSoJ_0Zz4Fr3NeWS2U_Hxs_L0", "Checkout!A:E", body)
                        .setValueInputOption("RAW")
                        .setInsertDataOption("INSERT_ROWS")
                        .execute();

                if (response.getUpdates() == null || response.getUpdates().getUpdatedRows() == null
                        || response.getUpdates().getUpdatedRows() != 1) {
                    throw new IOException("Google Sheets did not confirm the checkout row was written.");
                }

                Log.i(TAG, "Checkout " + checkoutId + " appended to " + response.getUpdates().getUpdatedRange());
                showToast(appContext, "Checkout synced to Google Sheets");

            } catch (IOException e) {
                Log.e(TAG, "Failed to append checkout " + checkoutId + " to Google Sheets", e);
                showToast(appContext, "Checkout saved locally; Google Sheets sync failed: " + e.getMessage());
            } catch (RuntimeException e) {
                Log.e(TAG, "Unexpected error while syncing checkout " + checkoutId, e);
                showToast(appContext, "Checkout saved locally; Google Sheets sync failed: " + e.getMessage());
            }
        });
    }

    private static void showToast(Context context, String message) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show());
    }
}
