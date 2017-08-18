package cn.mijack.persistentsearchdemo.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.mijack.persistentsearchdemo.ItemClickSupport;
import cn.mijack.persistentsearchdemo.ItemDivider;
import cn.mijack.persistentsearchdemo.R;
import cn.mijack.persistentsearchdemo.model.User;
import cn.mijack.persistentsearchdemo.model.UserResponse;
import cn.mijack.persistentsearchdemo.view.SearchBox;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class SearchBoxActivity extends AppCompatActivity {
    private SearchBox searchBox;
    private UserAdapter userAdapter;
    private Retrofit retrofit;
    private GithubApi githubApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_box);
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.github.com").build();
        githubApi = retrofit.create(GithubApi.class);
        searchBox = findViewById(R.id.searchBox);
        userAdapter = new UserAdapter();
        searchBox.setRecyclerViewAdapter(userAdapter);
        ItemClickSupport.addTo(searchBox.getRecyclerView())
                .setOnItemClickListener((recyclerView, position, v) -> {
//                    String data = userAdapter.getData(position);
//                    searchBox.query(data);
                });
        searchBox.getRecyclerView().addItemDecoration(new ItemDivider());
        setTitle("open:" + searchBox.isSearchBoxOpen());
        searchBox.setOnSearchBoxListener(searchBox -> setTitle("open:" + searchBox.isSearchBoxOpen()));
        searchBox.setOnQueryTextListener(new SearchBox.OnQueryTextListener() {
            @Override
            public void onQueryTextSubmit(CharSequence query) {
                Toast.makeText(SearchBoxActivity.this, "搜索：" + query, Toast.LENGTH_SHORT).show();
                githubApi.searchUsers(query.toString())
                        .enqueue(new Callback<UserResponse>() {
                            @Override
                            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                                System.out.println("find users:" + response.body().getTotal_count());
                            }

                            @Override
                            public void onFailure(Call<UserResponse> call, Throwable t) {

                            }
                        });
            }

            @Override
            public void onQueryTextChange(CharSequence newText) {

            }
        });
    }

    interface GithubApi {
        //    https://api.github.com/search/users?q=mijack
        @GET("/search/users")
        Call<UserResponse> searchUsers(@Query("q") String user);
    }

    private static class UserAdapter extends RecyclerView.Adapter {
        private List<User> data = new ArrayList<>();

        public UserAdapter() {
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
            return data.get(position).getLogin();
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public void setData(List<User> data) {
            this.data.clear();
            this.data.addAll(data);
            this.notifyDataSetChanged();
        }

    }


}
