package com.luyucheng.socketchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText inputName;

    private Button sendName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputName = (EditText) findViewById(R.id.input_name);
        sendName = (Button) findViewById(R.id.send_name);
        sendName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputName.getText().toString();
                if (!"".equals(content)) {
                    Intent intent=new Intent(MainActivity.this,ChatRoomActivity.class);
                    intent.putExtra("name",content);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }


}
