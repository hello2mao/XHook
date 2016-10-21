package com.mhb.xhook.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mhb.xhook.R;
import com.mhb.xhook.base.activities.BaseActivity;
import com.mhb.xhook.ui.ProcessListActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.findViewById(R.id.process_list).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.process_list: {
                Intent intent = new Intent(MainActivity.this, ProcessListActivity.class);
                this.startActivity(intent);
            }

        }
    }
}
