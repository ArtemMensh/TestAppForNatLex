package com.example.testappfornatlex;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener {

    final private String WEATHER_KEY = "6056f57916b62096c5342e1c77536a79";
    final private String WEATHER_REQUEST_COORDINATE =
            "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s";
    final private String WEATHER_REQUEST_TOWN =
            "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s";
    private Toolbar toolbar;
    private TextView tv_city, tv_temp;
    private SwitchCompat switchCompat;
    private ListView listView;
    private Data currentData;
    private DataAdapter adapter;
    private boolean CHECK_CELSIUS = true;

    DBHelper dbHelper;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);


        tv_city = findViewById(R.id.id_city);
        tv_temp = findViewById(R.id.id_temperature);

        // Listener for change color in the header
        tv_temp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                ChangeColorHeader();

            }
        });

        switchCompat = findViewById(R.id.id_switch);
        listView = findViewById(R.id.id_list);

        MyLocation.SetUpLocationListener(this);

        List<Data> list_items = LoadInfoOnDataBase();
        adapter = new DataAdapter(this, R.layout.list_view, list_items);

        // Listener for listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // получаем нажатый элемент
                Data item = adapter.getItem(position);
                String request = String.format(WEATHER_REQUEST_TOWN, item.getName(), WEATHER_KEY);
                String content = GetContent(request);
                SetInformation(content, false);
            }
        });

        if (!list_items.isEmpty()) {
            StartFillInData(list_items);
        }

        // Fill in listView
        listView.setAdapter(adapter);

    }

    private void ChangeColorHeader() {
        ConstraintLayout layout = findViewById(R.id.id_mainView);
        double d_temp = Double.valueOf(tv_temp.getText().toString());

        if (switchCompat.isChecked() != CHECK_CELSIUS) {
            d_temp = FromKelvinToCelsius(d_temp);
        }

        if (d_temp < 10) {
            layout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLightBlueForHeader));
        } else if (d_temp > 25) {
            layout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorRedForHeader));
        } else {
            layout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorOrangeForHeader));
        }

    }

    private void StartFillInData(List<Data> list_items) {

        // First element in header
        Data first = list_items.get(0);
        list_items.remove(0);
        adapter.remove(first);

        // Current element in header
        currentData = first;

        // Fill in textViews in header
        tv_city.setText(first.getName());
        tv_temp.setText(first.getTemp());

    }

    private List<Data> LoadInfoOnDataBase() {
        List<Data> list_items = new ArrayList();

        dbHelper = new DBHelper(this);
        SQLiteDatabase sq = dbHelper.getReadableDatabase();

        cursor = sq.query(DBHelper.TABLE_NAME, null, null, null, null, null, "time desc");

        if (cursor.moveToFirst()) {
            do {
                String temp = cursor.getString(cursor.getColumnIndex(DBHelper.TEMPE));
                String name = cursor.getString(cursor.getColumnIndex(DBHelper.NAME));
                String time = cursor.getString(cursor.getColumnIndex(DBHelper.TIME));
                String image = cursor.getString(cursor.getColumnIndex(DBHelper.IMAGE));

                list_items.add(new Data(name, temp, time, image));


            } while (cursor.moveToNext());
        }

        cursor.close();
        dbHelper.close();
        sq.close();

        return list_items;
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
            MyLocation.SetUpLocationListener(this);

            try {
                Location l = MyLocation.imHere;
                String request = String.format(WEATHER_REQUEST_COORDINATE,
                        l.getLatitude(), l.getLongitude(), WEATHER_KEY);
                String content = GetContent(request);
                SetInformation(content, true);

            } catch (Exception e) {
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
        SetInformation(content, false);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    private String GetContent(String request) {
        String content = null;
        GetUrlContentTask g = new GetUrlContentTask();

        try {
            content = g.execute(request).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return content;
    }

    private void SetInformation(String content, boolean location) {
        switch (content) {
            case (""):
                Toast toast = Toast.makeText(getApplicationContext(),
                        "City not found", Toast.LENGTH_SHORT);
                toast.show(); // information
                break;

            case ("No connect"):
                Toast info = Toast.makeText(getApplicationContext(),
                        "Problem with connection to server", Toast.LENGTH_SHORT);
                info.show();
                break;

            default:
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    String name = jsonObject.getString("name");
                    String temp = GetTempFromKel(jsonObject.getJSONObject("main").getString("temp"));

                    tv_city.setText(name);
                    tv_temp.setText(temp + ".0");

                    // Transfer item from header to listView
                    if (currentData != null) {

                        adapter.insert(currentData, 0);
                        adapter.notifyDataSetChanged();
                    }

                    // Create new Date obj for add in database and update current Data obj
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss ");
                    String time = sdf.format(new Date());
                    currentData = new Data(name, temp, time, String.valueOf(location));
                    SaveCityDB(currentData);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void LoadChartActivity(View view) {
        // Find name city
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        View view1 = viewGroup.getChildAt(0);
        TextView text = (TextView) view1;

        String name = text.getText().toString().split(",")[0];

        Intent intent = new Intent(this, Chart.class);
        intent.putExtra("name", name);
        startActivity(intent);

    }

    private void SaveCityDB(Data item) {

        ContentValues cv = new ContentValues();
        dbHelper = new DBHelper(this);
        SQLiteDatabase sq = dbHelper.getWritableDatabase();

        double temp = Integer.valueOf(item.getTemp());
        if (switchCompat.isChecked() != CHECK_CELSIUS) {
            temp = FromKelvinToCelsius(temp);
        }

        cv.put(DBHelper.NAME, item.getName());
        cv.put(DBHelper.TEMPE, temp);
        cv.put(DBHelper.TIME, item.getTime());
        cv.put(DBHelper.IMAGE, item.getImage());

        sq.insert(DBHelper.TABLE_NAME, null, cv);

        sq.close();
        dbHelper.close();

    }

    private String GetTempFromKel(String string) {
        Double kel = Double.valueOf(string);
        String t;
        if (switchCompat.isChecked() == CHECK_CELSIUS) {
            t = String.valueOf(Math.round(kel - 273.15));
        } else {
            t = String.valueOf(Math.round((kel - 273.15) * (9 / 5) + 32));
        }

        return t;
    }

    public void ChangeTemp(View view) {
        String str;
        Double d_temp = Double.valueOf(tv_temp.getText().toString());

        if (switchCompat.isChecked() == CHECK_CELSIUS) {
            str = String.format("%.1f", FromKelvinToCelsius(d_temp)).replace(',', '.');
        } else {
            str = String.format("%.1f", FromCelsiusToFahrenheit(d_temp)).replace(',', '.');

        }
        currentData.setTemp(str);
        tv_temp.setText(str);

        ChangeTempInListView();

    }

    private void ChangeTempInListView() {
        for (int i = 0; i < adapter.getCount(); i++) {
            Data d = adapter.getItem(i);
            double d_temp = Double.valueOf(d.getTemp());
            String str;

            if (switchCompat.isChecked() == CHECK_CELSIUS) {
                str = String.format("%.1f", FromKelvinToCelsius(d_temp)).replace(',', '.');
            } else {
                str = String.format("%.1f", FromCelsiusToFahrenheit(d_temp)).replace(',', '.');

            }

            adapter.getItem(i).setTemp(str);
        }
        adapter.notifyDataSetChanged();
    }

    private double FromKelvinToCelsius(double d_temp) {
        return (d_temp - 32) * (5.0 / 9);
    }

    private double FromCelsiusToFahrenheit(double d_temp) {
        return d_temp * (9.0 / 5) + 32;
    }


}
