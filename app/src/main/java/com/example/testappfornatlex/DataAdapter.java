package com.example.testappfornatlex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class DataAdapter extends ArrayAdapter<Data> {

    private LayoutInflater inflater;
    private int layout;
    private List<Data> data;

    public DataAdapter(Context context, int resource, List<Data> list) {
        super(context, resource, list);
        data = list;
        layout = resource;
        inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convert, ViewGroup parent) {
        View view = inflater.inflate(this.layout, parent, false);

        TextView name = (TextView) view.findViewById(R.id.id_list_name);
        TextView time = (TextView) view.findViewById(R.id.id_list_time);
        ImageView imageView = (ImageView) view.findViewById(R.id.id_list_img);

        Data items = data.get(position);

        name.setText(items.getName() + ", " + items.getTemp());
        time.setText(items.getTime());

        if(items.getImage().equals("true")){
            // set image in image view
            imageView.setBackgroundResource(R.drawable.ic_statistics);
        }



        return view;
    }

}
