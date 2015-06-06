package xyz.mijack.searchboxdemo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by MiJack on 2015/6/4.
 */
public class SampleAdapter extends RecyclerView.Adapter<SampleAdapter.SampleHolder> {
    List<String> stringList;


    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    public List<String> getStringList() {
        return stringList;
    }

    @Override
    public SampleHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new SampleHolder(view);
    }

    @Override
    public void onBindViewHolder(SampleHolder holder, int position) {
        holder.tv.setText(stringList.get(position));
    }

    @Override
    public int getItemCount() {
        return stringList == null ? 0 : stringList.size();
    }

    public static class SampleHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public SampleHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.content);
        }
    }
}
