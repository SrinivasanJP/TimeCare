package com.srini.timecare.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.srini.timecare.R;


public class AddScheduleFragment extends Fragment {

    private WebView vCalederHolder;
    private ProgressBar vProgressBar;
    private Toolbar vToolbar;
    public AddScheduleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_schedule, container, false);

        vToolbar = view.findViewById(R.id.toolbar);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(vToolbar);
        activity.getSupportActionBar().setTitle("SCHEDULE");
        vProgressBar = view.findViewById(R.id.calenderProgress);
        vProgressBar.setVisibility(View.VISIBLE);
        vProgressBar.setMax(100);
        vCalederHolder = view.findViewById(R.id.calenderHolder);
        vCalederHolder.loadUrl("https://calendar.google.com/calendar/u/0/gp?hl=en-GB");
        vCalederHolder.setWebViewClient(new WebViewClient());
        WebSettings webSettings = vCalederHolder.getSettings();
        webSettings.setJavaScriptEnabled(true);
        vCalederHolder.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress==100) vProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }
        });




        return view;
    }

}