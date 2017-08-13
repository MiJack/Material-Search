package cn.mijack.persistentsearchdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.mijack.persistentsearchdemo.R;

/**
 * Created by Mr.Yuan on 2017/8/10.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                startActivity(new Intent(this, PersistentSearchActivity.class));
                break;
            case R.id.button2:
                startActivity(new Intent(this, ExpandableSearchActivity.class));
                break;
            case R.id.button3:
                startActivity(new Intent(this, SearchBoxActivity.class));
                break;
        }
    }
}
