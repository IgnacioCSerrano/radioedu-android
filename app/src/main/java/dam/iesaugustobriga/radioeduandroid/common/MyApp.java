package dam.iesaugustobriga.radioeduandroid.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;

import dam.iesaugustobriga.radioeduandroid.R;

public class MyApp extends Application {

     /*
        Esta clase permite devolver el contexto de la aplicación desde cualquier punto de la misma.
        Para conseguir que objeto MyApp esté viculado a la creación de la aplicación es necesario
        indicarlo en el elemente application de AndroidManifest.xml mediante el atributo
        android:name=".common.MyApp"
    */

    private static MyApp instance;

    public static Context getContext() {
        return instance; // objeto de clase Application en sí mismo es un contexto de la aplicación (hereda de Context al igual que clase Activity)
    }

    @Override
    public void onCreate() { // método solo se ejecuta una vez al abrir aplicación (patrón Singleton)
        super.onCreate();
        instance = this;
    }

    public static void buildAlertDialogClose(Activity activity, Intent intent, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Sí", (dialog, which) -> {
            if (intent != null) {
                instance.startActivity(intent);
            }
            activity.finish();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        setAlertDialogStyle(dialog, activity);
        dialog.show();
    }

    public static void setAlertDialogStyle(AlertDialog dialog, Activity activity) {
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(activity.getResources().getColor(R.color.white, activity.getTheme()));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary, activity.getTheme()));

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(activity.getResources().getColor(R.color.black, activity.getTheme()));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setBackgroundColor(activity.getResources().getColor(R.color.colorAccent, activity.getTheme()));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(40, 0, 0, 0);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setLayoutParams(params);
        });
    }

    public static void showSnackbar(Context ctx, View parentView, String message, int duration) {
        Snackbar snack = Snackbar.make(ctx, parentView, message, duration);
        View view = snack.getView();
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (view.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams) params).gravity = Gravity.TOP;
        } else if (view.getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {
            ((CoordinatorLayout.LayoutParams) params).gravity = Gravity.TOP;
//            ((CoordinatorLayout.LayoutParams) params).topMargin = 180;
        }
        view.setLayoutParams(params);
        snack.show();
    }

}
