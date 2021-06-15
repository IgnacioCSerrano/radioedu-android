package dam.iesaugustobriga.radioeduandroid.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import dam.iesaugustobriga.radioeduandroid.common.Constants;
import dam.iesaugustobriga.radioeduandroid.common.MyApp;
import dam.iesaugustobriga.radioeduandroid.models.Centro;

public class VolleySingleton {
    @SuppressLint("StaticFieldLeak") // no hay fuga de memoria porque siempre se pasa por parámetro el contexto de la aplicacción encapsulado en clase MyApp
    private static VolleySingleton instance;
    private RequestQueue queue;
    private final Context ctx;

    private VolleySingleton() {
        ctx = MyApp.getContext();
        queue = getRequestQueue();
    }

    public static synchronized VolleySingleton getInstance() {
        if (instance == null) {
            instance = new VolleySingleton();
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (queue == null) {
            queue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return queue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        /*
            Método setRetryPolicy() soluciona un bug de Volley de envío doble de petición
            cuando esta dura más de lo esperado, lo que produce errores en inserción
            de datos (segunda tupla duplicada)
        */

        req.setRetryPolicy(new DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }

    public void populateStringSpinner(Spinner spinner, String url, String cadena, String placeholder) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        List<CharSequence> list = new ArrayList<>();
                        list.add(response.equals("[]") ? "" : placeholder);
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            list.add(jsonArray.getJSONObject(i).getString(cadena));
                        }
                        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item, list);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                    }
                }, error -> {
                    Log.e("ERROR", error.getMessage());
                    Toast.makeText(ctx, Constants.SERVER_FAIL, Toast.LENGTH_LONG).show();
                }
        );
        stringRequest.setTag(Constants.TAG);
        this.addToRequestQueue(stringRequest);
    }

    public void populateCentroSpinner(Spinner spinner, String url, String placeholder) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        List<Centro> list = new ArrayList<>();
                        list.add(new Centro(0, response.equals("[]") ? "" : placeholder));
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            int codigo = jsonArray.getJSONObject(i).getInt("codigo");
                            String denominacion = jsonArray.getJSONObject(i).getString("denominacion");
                            list.add(new Centro(codigo, denominacion));
                        }
                        ArrayAdapter<Centro> adapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item, list);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                    }
                }, error -> {
                    Log.e("ERROR", error.getMessage());
                    Toast.makeText(ctx, Constants.SERVER_FAIL, Toast.LENGTH_LONG).show();
                }
        );
        stringRequest.setTag(Constants.TAG);
        this.addToRequestQueue(stringRequest);
    }

}
