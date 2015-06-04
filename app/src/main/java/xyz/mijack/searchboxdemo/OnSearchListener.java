package xyz.mijack.searchboxdemo;

/**
 * Created by MiJack on 2015/6/3.
 */
public interface OnSearchListener {

    /**
     * @param search
     * @return 是否需要使用到非UI
     */
    boolean onSearch(String search);
}

