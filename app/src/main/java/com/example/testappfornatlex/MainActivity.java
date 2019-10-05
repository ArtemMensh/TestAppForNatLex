package com.example.testappfornatlex;

import android.location.Location;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener {

    final private String WEATHER_KEY = "6056f57916b62096c5342e1c77536a79";
    final private String WEATHER_REQUEST_COORDINATE = "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s";
    final private String WEATHER_REQUEST_TOWN = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s";
    private Toolbar toolbar;
    private TextView city, temp;
    private SwitchCompat switchCompat;
    private int PERMISSION_LOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);

        city = findViewById(R.id.id_city);
        temp = findViewById(R.id.id_temperature);
        switchCompat = findViewById(R.id.id_switch);

        PERMISSION_LOCATION = MyLocation.SetUpLocationListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.toString().equals(getResources().getString(R.string.menu_location))) {
            if(PERMISSION_LOCATION == 1) {
                Location l = MyLocation.imHere;
                String request = String.format(WEATHER_REQUEST_COORDINATE, l.getLatitude(), l.getLongitude(), WEATHER_KEY);

                String content = GetContent(request);

                SetInformation(content);
            }
            else{
                Toast info = Toast.makeText(this, "Location off", Toast.LENGTH_LONG);
                info.show();
            }
        }

        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {

        String request = String.format(WEATHER_REQUEST_TOWN, s, WEATHER_KEY);

        String content = GetContent(request);

        SetInformation(content);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {return false;}

    private String GetContent(String request){
        String content = null;
        GetUrlContentTask g = new GetUrlContentTask();
        try {
            content = g.execute(request).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return content;
    }

    private void SetInformation(String content){
        switch (content) {
            case (""):
                Toast toast = Toast.makeText(getApplicationContext(),
                        "City not found", Toast.LENGTH_SHORT);
                toast.show(); // information
                break;
            case ("No connect"):
                Toast info = Toast.makeText(getApplicationContext(), "Problem with connection to server", Toast.LENGTH_SHORT);
                info.show();
                break;
            default:
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    city.setText(jsonObject.getString("name"));
                    SetTemp(jsonObject.getJSONObject("main").getString("temp"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void SetTemp(String string) {
        Double kel = Double.valueOf(string);
        if (switchCompat.isChecked()) {
            temp.setText(String.valueOf(Math.round(kel - 273.15)));
        } else {
            temp.setText(String.valueOf(Math.round((kel - 273.15) * (9 / 5) + 32)));
        }
    }

    public void ChangeTemp(View view) {
        String s = temp.getText().toString();
        Double d_temp = Double.valueOf(s);
        if (switchCompat.isChecked()) {
            temp.setText(String.valueOf(Math.round((d_temp - 32) * (5.0 / 9))));
        } else {
            temp.setText(String.valueOf(Math.round(d_temp * (9.0 / 5) + 32)));
        }
    }
}
