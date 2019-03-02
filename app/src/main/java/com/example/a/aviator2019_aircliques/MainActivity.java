package com.example.a.aviator2019_aircliques;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private TextView seatNumber;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    Bitmap photo;
    String return_str;
    private String seat_number_str = "";

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }


    }

    public void assignSeat(View view) {
        MyAsyncTasks myAsyncTasks = new MyAsyncTasks();

        myAsyncTasks.execute();
    }


    public class MyAsyncTasks extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display a progress dialog for good user experiance
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        protected String doInBackground(String... params) {
            String current;

//            scanFile(outputFile);
//                record.setEnabled(true);
//                stop.setEnabled(false);
//                play.setEnabled(true);

            String server_IP = "http://192.168.0.135:5000/";
            String sendAudio = "sendAudio/";
            String sendAudio_URL = server_IP + sendAudio;

            try {
                current = doFileUpload();


//                return seat_number_str;
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            Log.e("Debug", "do in background " + current);
            return seat_number_str;

        }

        @Override
        protected void onPostExecute(String current) {
            progressDialog.dismiss();




//            speakWords(current);
//            chatbot_textview.append("\nInsideout: " + current + "\n");

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.imageView = (ImageView)this.findViewById(R.id.imageView1);
        Button photoButton = (Button) this.findViewById(R.id.button1);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);

            }
        });
    }

    public String doFileUpload() throws JSONException {
//        bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
//        Canvas canvas = new Canvas(bitmap);

//        v.draw(canvas);

        //Preparing payload for signature prediction via image
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        Log.v("tag", encoded);

        JSONObject predictSignature_payload = new JSONObject();
        JSONObject image = new JSONObject();
        JSONObject imageBytes = new JSONObject();
        imageBytes.put("imageBytes", encoded);
        image.put("image", imageBytes);
        predictSignature_payload.put("payload", image);

        //Preparing payload for test time via username



        String getSeatNumber_URL = "http://192.168.0.134:5000/getSeatNumber";
        HashMap headers = new HashMap();
        headers.put("Content-Type", "application/json");
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest signaturePrediction = new JsonObjectRequest(
                Request.Method.POST,
                getSeatNumber_URL,
                predictSignature_payload,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Rest Response", response.toString());
                        seat_number_str = "";
                        try {
                            seat_number_str = response.getString("seat_number");
                            Log.v("seat_number", seat_number_str);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(), seat_number_str, Toast.LENGTH_SHORT).show();
                        seatNumber = (TextView) findViewById(R.id.seatNumber);
                        String currentStr = "You are assigned to seat: " + seat_number_str;
                        seatNumber.setText(currentStr);


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Rest Response", error.toString());
                    }
                }
        );
        requestQueue.add(signaturePrediction);

        return seat_number_str;
    }



    public void signUp(View view) {
        Intent collectData = new Intent(MainActivity.this, collectData.class);
        collectData.putExtra("key", "test");
        MainActivity.this.startActivity(collectData);


    }
}


