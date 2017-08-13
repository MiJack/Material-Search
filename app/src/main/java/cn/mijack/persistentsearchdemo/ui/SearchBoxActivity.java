package cn.mijack.persistentsearchdemo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

import cn.mijack.persistentsearchdemo.R;
import cn.mijack.persistentsearchdemo.view.SearchBox;

public class SearchBoxActivity extends AppCompatActivity {
    private SearchBox searchBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_box);
        searchBox = findViewById(R.id.searchBox);

    }

    public static class ItemModel<T> {
        private List<T> data;

        private ItemModel(List<T> data) {
            this.data = data;
        }

        public static <T> ItemModel<T> from(List<T> data) {
            return new ItemModel<>(data);
        }

        public T getItem(int position) {
            return data.get(position);
        }

        public int size() {
            return data == null ? 0 : data.size();
        }

        public ItemModel bindGenerate() {
            return this;
        }

        public int getViewType(int position) {
            return 1;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        }

    }
}
