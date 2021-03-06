package com.example.cst438_01_wk01hw02solo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LandingActivity extends AppCompatActivity {

    public static String ACTIVITY_LABEL = "LANDING_ACTIVITY_COM_EXAMPLE";
    private TextView textViewResult;
    private int uid;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        textViewResult = findViewById(R.id.text_view_result);
        TextView textViewWelcomeMSG = findViewById(R.id.welcome_msg);

        //retrieve username and id from MainActivity
        Bundle extras = getIntent().getExtras();
        uid = extras.getInt("EXTRA_UID");
        String un = extras.getString("EXTRA_UN");

        //display "personalized" welcome msg
        textViewWelcomeMSG.setText("Welcome " + un + ", your user id is " + uid + " and these are your posts.");

        //begin api fetch
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<List<Post>> call = jsonPlaceHolderApi.getPosts();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (!response.isSuccessful()) { //if failed response display code in text view
                    textViewResult.setText("Code: " + response.code());
                    return;
                }

                List<Post> posts = response.body(); //if successful response loop and append relevant posts to text view
                for (Post p : posts) {
                    if (uid == p.getUserId()) {
                        String content = "";
                        content += "Post ID: " + p.getId() + "\n";
                        content += "Title: " + p.getTitle() + "\n";
                        content += "Body: " + p.getBody() + "\n\n";
                        textViewResult.append(content);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                textViewResult.setText(t.getMessage()); //display error on failure
            }
        });

    }

    //intent switcher
    public static Intent intentFactory(Context context, int id, String username) {
        Intent intent = new Intent(context, LandingActivity.class);
        Bundle extras = new Bundle();
        extras.putInt("EXTRA_UID", id); //for passing userId
        extras.putString("EXTRA_UN", username); // for passing username
        intent.putExtras(extras);
        return intent;
    }
}