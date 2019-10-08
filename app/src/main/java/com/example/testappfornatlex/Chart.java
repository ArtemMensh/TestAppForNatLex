package com.example.testappfornatlex;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.anychart.anychart.AnyChart;
import com.anychart.anychart.AnyChartView;
import com.anychart.anychart.Cartesian;
import com.anychart.anychart.DataEntry;
import com.anychart.anychart.ValueDataEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Chart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        // Load info town
        String town = Objects.requireNonNull(getIntent().getExtras()).getString("name");
        ArrayList<Data> list_items = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase sq = dbHelper.getReadableDatabase();

        Cursor cursor = sq.query(DBHelper.TABLE_NAME, null, "name = ?", new String[]{town}, null, null, null);

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

        // Create chart
        Cartesian line = AnyChart.line();
        List<DataEntry> list_data_entry = new ArrayList<>();

        for (Data item : list_items) {
            list_data_entry.add(new ValueDataEntry(item.getTime(), Double.valueOf(item.getTemp())));
        }

        line.setData(list_data_entry);

        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        anyChartView.setChart(line);
    }
}
