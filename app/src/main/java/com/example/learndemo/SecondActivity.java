package com.example.learndemo;

import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.aspectjx.annotations.OnSingleClick;
import com.example.router.Router;
import com.example.router.annotation.Route;

@Route(path = "/app/second", group = "app")
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secend);
        findViewById(R.id.senond_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTestActivity();
            }
        });
    }

    @OnSingleClick(500)
    private void openTestActivity() {
        Router.getInstance().build("/test/home", "test").navigation();
    }


}
