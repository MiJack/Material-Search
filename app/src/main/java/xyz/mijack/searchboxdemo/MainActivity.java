package xyz.mijack.searchboxdemo;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.ActionView;
import at.markushi.ui.action.Action;
import at.markushi.ui.action.BackAction;
import at.markushi.ui.action.DrawerAction;

public class MainActivity extends AppCompatActivity implements SearchListener {


    private Action backAction;
    private Action drawerAction;

    /**
     * @By User
     */
    public static class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchHolder> {
        List<String> strings;

        public List<String> getStrings() {
            return strings;
        }

        public void setStrings(List<String> strings) {
            this.strings = strings;
            this.notifyDataSetChanged();
        }

        @Override
        public SearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new SearchHolder(view);
        }

        @Override
        public void onBindViewHolder(SearchHolder holder, int position) {
            holder.view.setText(strings.get(position));
        }

        @Override
        public int getItemCount() {
            return strings == null ? 0 : strings.size();
        }

        public static class SearchHolder extends RecyclerView.ViewHolder {
            TextView view;

            public SearchHolder(View itemView) {
                super(itemView);
                view = (TextView) itemView;
            }
        }
    }

    private SearchAdapter adapter;

    /**
     * @By User
     */
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
                progressBar.setVisibility(View.INVISIBLE);
                adapter.setStrings(strings);
                menuView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }.execute(search);
        return true;
    }

    private ActionView menuView;
    private ActionView closeView;
    private ProgressBar progressBar;
    private EditText searchText;
    private LinearLayout searchBox;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        backAction = new BackAction();
        drawerAction = new DrawerAction();
        searchBox = (LinearLayout) findViewById(R.id.searchbox);
        menuView = (ActionView) findViewById(R.id.actionView);
        closeView = (ActionView) findViewById(R.id.search_close);
        recyclerView = (RecyclerView) findViewById(R.id.searchResult);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new SearchAdapter();
        /**
         * @By User
         */
        recyclerView.setAdapter(adapter);
        /**
         * @By User
         */
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(view.getContext(), adapter.getStrings().get(position), Toast.LENGTH_SHORT).show();
                recyclerView.setVisibility(View.GONE);
                searchText.setText("");
                adapter.setStrings(null);
                closeView.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }

            @Override
            public void onFling(View view, int position) {

            }
        }));
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(searchText.getText())) {
                    searchText.setText("");
                    recyclerView.setVisibility(View.GONE);
                    adapter.setStrings(null);
                    menuView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    searchText.clearFocus();
                    closeView.setVisibility(View.GONE);
                    menuView.setAction(drawerAction, true);
                    //隐藏输入框
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
                }
            }
        });
        menuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuView.getAction().getClass().equals(drawerAction.getClass())) {
                    searchText.setFocusable(true);
                    searchText.requestFocus();
                    menuView.setAction(backAction, true);
                } else {
                    menuView.setAction(drawerAction, true);
                    searchText.clearFocus();
                }
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        searchText = (EditText) findViewById(R.id.searchText);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s))
                    closeView.setVisibility(View.VISIBLE);
            }
        });

        searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            /**
             * @param v
             * @param hasFocus
             */
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Action action = hasFocus ? backAction : drawerAction;
                if (menuView.getAction().getClass() != action.getClass()) {
                    menuView.setAction(action, true);
                }
                if (hasFocus) {
                    Toast.makeText(v.getContext(), "Has Focus", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "Lost Focus", Toast.LENGTH_SHORT).show();
                }
            }
        });
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Toast.makeText(v.getContext(), "Search", Toast.LENGTH_SHORT).show();
                    if (onSearch(searchText.getText().toString())) {
                        progressBar.setVisibility(View.VISIBLE);
                        menuView.setVisibility(View.INVISIBLE);
                    } else {
                        menuView.setAction(backAction, true);
                        progressBar.setVisibility(View.GONE);
                        menuView.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(this, "Back", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, menuView.getAction().getClass().getSimpleName() + ":" + backAction.getClass().getSimpleName()
                    , Toast.LENGTH_SHORT).show();
            if (menuView.getAction().getClass() == backAction.getClass()) {
                menuView.setAction(drawerAction, true);
                searchText.clearFocus();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
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
            startActivity(new Intent(this, TestActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
