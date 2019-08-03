package com.example.sreekar.acremote;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    TextView temp;
    int tempV = 74;
    int tempS = tempV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temp = (TextView) findViewById(R.id.textView2);
        temp.setText(String.valueOf(tempV));

        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://192.168.0.14:3000/api/getDeviceState";
        //String url = "http://api.wunderground.com/api/517398b8f4f5fc52/conditions/q/CA/San_Francisco.json";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        if(response == "on"){
                            temp.setText("68");
                        }
                        else{
                            temp.setText(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                temp.setText("That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        String url2 ="http://192.168.0.14:3000/api/postTemp";
        final StringRequest stringSend = new StringRequest(Request.Method.POST, url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.d("response", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("response", "That didn't work!");
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                if(tempS==0){
                    params.put("temp", "off");
                }
                else if(tempS==1){
                    params.put("temp", "on");
                }
                else{
                    params.put("temp", Integer.toString(tempS));
                }
                return params;
            }
        };

        Button up = (Button) findViewById(R.id.up_button);
        up.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tempV++;
                if(tempV>76){
                    tempV = 76;
                }
                temp.setText(String.valueOf(tempV));
                tempS = tempV;
                queue.add(stringSend);
            }
        });

        Button down = (Button) findViewById(R.id.down_button);
        down.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tempV--;
                if(tempV<60){
                    tempV = 60;
                }
                temp.setText(String.valueOf(tempV));
                tempS = tempV;
                queue.add(stringSend);

            }
        });

        Button power = (Button) findViewById(R.id.power_button);
        power.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(temp.getText()=="off"){
                    tempV = 68;
                    temp.setText(String.valueOf(tempV));
                    tempS = 1;
                    queue.add(stringSend);
                }
                else {
                    temp.setText("off");
                    tempS = 0;
                    queue.add(stringSend);
                }

            }
        });
    }
}
