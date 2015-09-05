package com.oscarsalguero.solartracker.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.WindowManager;

import com.oscarsalguero.solartracker.R;

/**
 * Utilities class.
 * Created by RacZo on 9/4/15.
 */
public class Utils {

    private static final String LOG_TAG = Utils.class.getName();

    public static void showAlertDialog(final Activity activity, final String title, final String message) {
        try {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(title);
                    builder.setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton(activity.getString(R.string.button_ok),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        } catch (WindowManager.BadTokenException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }
}
