package com.example.adas_3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity  {
    public String fb;
    public String result;
    public String answer;
    public String x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText enteredFeedbackText = findViewById(R.id.enterFB);

        Button submitButton = findViewById(R.id.submitBut);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fb = enteredFeedbackText.getText().toString();
                setContentView(R.layout.activity_adas);
                answer = checkFeedback();
                setOutput(answer);


            }

        });

    }

    public String checkFeedback() {
        try{
         answer = new MyAsyncTask().execute().get();
        } catch (Exception e) {
        e.printStackTrace(); }
        return answer;
    }

    public void setOutput(String msg){
        x = msg;
        TextView v = findViewById(R.id.response_2);
        v.setText(x);
    }
    public void feedbackSubmit (){

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    setOutput(result);
                } catch (Exception e) {
                    setOutput("Error");
                }
            }
        };
        t.start();
    }

    private void getValueFromMQTT(final String test) {

        Thread t = new Thread() {

            public void run() {

                final double res;
                final String display_text;

                try {

                    MQTT mqtt = new MQTT();

                    mqtt.setHost("test.mosquitto.org", 1883);
                    BlockingConnection connection = mqtt.blockingConnection();

                    connection.connect();

                    Topic[] topics = {new Topic(test, QoS.AT_LEAST_ONCE)};
                    connection.subscribe(topics);
                    System.out.println("nach connect und subscribe");

                    Message message = connection.receive();
                    System.out.println(message.getTopic());

                    byte[] payload = message.getPayload();

                    // process the message then:
                    message.ack();

                    connection.disconnect();

                    String result = new String(payload);
                    System.out.println(result);


                    if (result !=null && !result.equals("")) {
                        res = Double.parseDouble(result);
                        display_text = "" + res;
                    } else {
                        display_text = "--";
                    }

                    runOnUiThread(new Runnable() {
                        public void run() {
                            TextView a = findViewById(R.id.answ);
                            a.setText(display_text);

                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();

                }

            };

        };
        t.start();
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... arg0) {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("https://felixxx-adas-1.paiza-user-free.cloud/~ubuntu/index.php?fb="+fb);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(
                        urlConnection.getInputStream());
                String result = "";
                if (in != null) {
                    BufferedReader bufferedReader
                            = new BufferedReader(
                            new InputStreamReader(in));
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        result = result + line;
                    }
                    in.close();
                    return result;

                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }

            return null;
        }

    }

}


