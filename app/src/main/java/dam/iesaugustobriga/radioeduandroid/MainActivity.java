package dam.iesaugustobriga.radioeduandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dam.iesaugustobriga.radioeduandroid.common.Constants;
import dam.iesaugustobriga.radioeduandroid.common.SharedPreferencesManager;
import dam.iesaugustobriga.radioeduandroid.data.VolleySingleton;
import dam.iesaugustobriga.radioeduandroid.ui.dashboard.DashboardActivity;
import dam.iesaugustobriga.radioeduandroid.ui.auth.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        VolleySingleton volley = VolleySingleton.getInstance();

        String token = SharedPreferencesManager.getStringValue(Constants.PREF_TOKEN);

        if (token == null) {
            goToLogin();
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.API_URL + "validate-token.php",
                    response -> {
                        Log.i("RESPONSE", response);
                        if (response == null) {
                            goToLogin();
                        } else try {
                            JSONObject user = new JSONObject(response);
                            user.put("bearer_token", token);
                            SharedPreferencesManager.setPreferences(user);
                            goToHome();
                        } catch (JSONException e) {
                            Log.e("ERROR", e.getMessage());
                            goToLogin();
                        }
                    }, error -> {
                        Log.e("ERROR", error.toString());
                        goToLogin();
                    }
            ) {
                @Override
                protected Map<String,String> getParams() {
                    Map<String,String> params = new HashMap<>();
                    params.put("bearer-token", "Bearer " + token);
                    return params;
                }
            };
            stringRequest.setTag(Constants.TAG);
            volley.addToRequestQueue(stringRequest);
        }
    }

    public void goToLogin() {
        SharedPreferencesManager.clearSharedPreferences();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToHome() {
        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

}