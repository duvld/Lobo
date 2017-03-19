package com.example.angietong.lobo.UI;

import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.angietong.lobo.R;

public class PostActivity extends Fragment {

    TextView textView;

    public PostActivity()
    {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.activity_post, container, false);
        textView = (TextView)view.findViewById(R.id.textView);
        Bundle bundle = getArguments();
        String message = Integer.toString(bundle.getInt("count"));
        textView.setText("THIS IS THE " +message+ "SWIPE VIEW PAGE");
        return view;
    }
}
