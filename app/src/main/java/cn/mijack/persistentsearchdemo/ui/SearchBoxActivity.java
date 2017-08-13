package cn.mijack.persistentsearchdemo.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import cn.mijack.persistentsearchdemo.ItemClickSupport;
import cn.mijack.persistentsearchdemo.ItemDivider;
import cn.mijack.persistentsearchdemo.R;
import cn.mijack.persistentsearchdemo.view.SearchBox;

public class SearchBoxActivity extends AppCompatActivity {
    private SearchBox searchBox;
    private ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_box);
        searchBox = findViewById(R.id.searchBox);
        arrayAdapter = new ArrayAdapter("item ");
        searchBox.setRecyclerViewAdapter(arrayAdapter);
        ItemClickSupport.addTo(searchBox.getRecyclerView())
                .setOnItemClickListener((recyclerView, position, v) -> {
                    String data = arrayAdapter.getData(position);
                    searchBox.query(data);
                });
        searchBox.getRecyclerView().addItemDecoration(new ItemDivider());
        setTitle("open:" + searchBox.isSearchBoxOpen());
        searchBox.setOnSearchBoxListener(searchBox -> setTitle("open:" + searchBox.isSearchBoxOpen() ));
        searchBox.setOnQueryTextListener(new SearchBox.OnQueryTextListener() {
            @Override
            public void onQueryTextSubmit(CharSequence query) {
                Toast.makeText(SearchBoxActivity.this, "搜索：" + query, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onQueryTextChange(CharSequence newText) {
                arrayAdapter.setItem(newText.toString());
            }
        });
    }

    private static class ArrayAdapter extends RecyclerView.Adapter {
        private String item;

        public ArrayAdapter(String item) {
            this.item = item;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item, parent, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TextView tv = holder.itemView.findViewById(R.id.tv);
            tv.setText(getData(position));
        }

        @NonNull
        private String getData(int position) {
            return item + position;
        }

        @Override
        public int getItemCount() {
            return 5;
        }

        public void setItem(String item) {
            this.item = item;
            this.notifyDataSetChanged();
        }
    }


}
