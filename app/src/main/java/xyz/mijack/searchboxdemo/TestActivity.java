package xyz.mijack.searchboxdemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity implements SearchBox.OnSearchListener, RecyclerItemClickListener.OnItemClickListener {
    private SearchBox searchBox;
    SampleAdapter adapter;




    /**
     * 交给User做的事：
     * 1、设置RecyclerView#Adapter
     * 2、搜索过程
     * 3、RecyclerView的事件处理
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        searchBox = (SearchBox) findViewById(R.id.searchbox);
        adapter = new SampleAdapter();
        searchBox.setAdapter(adapter);
        searchBox.setOnResultClickListener(this);
        searchBox.setOnSearchListener(this);

    }

    @Override
    public boolean onSearch(String search) {

        new AsyncTask<String, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(String... params) {
                List<String> strings = new ArrayList<String>();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < 10; ) {
                    i++;
                    strings.add("结果" + i + ":\t" + params[0]);
                }
                return strings;
            }

            @Override
            protected void onPostExecute(List<String> strings) {
                searchBox.setState(SearchBox.State.SEARCHED);
                adapter.setStringList(strings);
            }
        }.execute(search);
        return true;
    }

    @Override
    public void onItemClick(View view, int position) {
        searchBox.setState(SearchBox.State.INIT);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onFling(View view, int position) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
