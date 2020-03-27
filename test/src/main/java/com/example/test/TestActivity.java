package com.example.test;

import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.router.Router;
import com.example.router.annotation.Route;

@Route(path = "/test/home", group = "test")
public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        findViewById(R.id.test_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.getInstance().build("/app/home", "app").navigation();
            }
        });
    }
}
