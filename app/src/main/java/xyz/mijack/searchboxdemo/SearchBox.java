package xyz.mijack.searchboxdemo;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import at.markushi.ui.ActionView;
import at.markushi.ui.action.Action;
import at.markushi.ui.action.BackAction;
import at.markushi.ui.action.DrawerAction;

/**
 * Created by MiJack on 2015/6/4.
 */
public class SearchBox extends LinearLayout {
    private Action backAction;
    private Action drawerAction;
    private LinearLayout searchBox;
    private ActionView menuView;
    private ActionView closeView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private EditText searchText;
    private boolean fromCloseView;

    public enum State {
        INIT,
        /**
         * @deprecated
         */
        BEFORE_TYPING, TYAPING, SEARCHING, SEARCHED
    }

    State state;
    private RecyclerView.Adapter adapter;
    private SearchBox.OnSearchListener onSearchLisener;
    private RecyclerItemClickListener.OnItemClickListener onResultClickListener;

    public SearchBox(Context context) {
        this(context, null);
    }

    public SearchBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        setState(State.INIT);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.base_search_box, this, true);
        backAction = new BackAction();
        drawerAction = new DrawerAction();
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        searchBox = (LinearLayout) findViewById(R.id.searchbox);
        menuView = (ActionView) findViewById(R.id.actionView);
        closeView = (ActionView) findViewById(R.id.search_close);
        recyclerView = (RecyclerView) findViewById(R.id.searchResult);
        searchText = (EditText) findViewById(R.id.searchText);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        setState(State.INIT);
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), getState().toString(), Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(searchText.getText())) {
                    searchText.setText("");
                    setState(State.TYAPING);
                } else {
                    setState(State.INIT);
                }
            }
        });
        menuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getState() == State.INIT) {
                    setState(State.TYAPING);
                } else if (getState() == State.TYAPING) {
                    setState(State.INIT);
                }
            }
        });
//        searchText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
////                if(getState()==State.BEFORE_TYPING){return;}
////                if (getState() == State.TYAPING) {
//
//                if (!TextUtils.isEmpty(s)) {
//                    setState(State.TYAPING);
//                } else {
//                    setState(State.BEFORE_TYPING);
//                }
//            }
//        });

        searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (fromCloseView) {
                    fromCloseView = false;
                    return;
                }
                switch (getState()) {
                    case INIT:
//                        setState(State.BEFORE_TYPING);
//                        break;
                    case SEARCHED:
                        setState(State.TYAPING);
                        break;
                }
//                Action action = hasFocus ? backAction : drawerAction;
//                if (menuView.getAction().getClass() != action.getClass()) {
//                    menuView.setAction(action, true);
//                }
//                if (hasFocus) {
//                    Toast.makeText(v.getContext(), "Has Focus", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(v.getContext(), "Lost Focus", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Toast.makeText(v.getContext(), "Search", Toast.LENGTH_SHORT).show();
                    if (onSearchLisener == null) {
                        throw new IllegalStateException("必须制定搜索过程，传入OnSearchListener接口");
                    }
                    if (onSearchLisener.onSearch(searchText.getText().toString())) {
                        setState(State.SEARCHING);
                    } else {
                        setState(State.SEARCHED);
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(getContext(), "Back", Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), menuView.getAction().getClass().getSimpleName() + ":" + backAction.getClass().getSimpleName()
                    , Toast.LENGTH_SHORT).show();
            if (menuView.getAction().getClass() == backAction.getClass()) {
                menuView.setAction(drawerAction, true);
                searchText.clearFocus();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    public State getState() {
        return state;
    }

    public void setState(State state) {
        if (this.state == state) {
            return;
        }

        switch (state) {
            case INIT:
                setInitView(this.state, state);
                break;
            case TYAPING:
                setTypingView(this.state, state);
                break;
            case SEARCHING:
                setSearchingView(this.state, state);
                break;
            case SEARCHED:
                setSearchedView(this.state, state);
                break;
        }
        this.state = state;
    }

    private void setInitView(State oldState, State newState) {
        menuView.setVisibility(VISIBLE);
        menuView.setAction(drawerAction, true);
        closeView.setVisibility(INVISIBLE);
        recyclerView.setVisibility(GONE);
        progressBar.setVisibility(INVISIBLE);
        if (oldState != State.INIT) {
            searchText.setText("");
        }
        searchText.clearFocus();
        //隐藏输入框
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
    }

    /**
     * @param oldState
     * @param newState
     * @deprecated
     */
    private void setBeforeTypingView(State oldState, State newState) {
        menuView.setVisibility(VISIBLE);
        menuView.setAction(backAction, true);
        closeView.setVisibility(VISIBLE);
        recyclerView.setVisibility(GONE);
        progressBar.setVisibility(INVISIBLE);
        searchText.setFocusable(true);
        searchText.requestFocus();
//        if (oldState != State.INIT) {
        searchText.setText("");
//        }
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void setTypingView(State oldState, State newState) {
        menuView.setVisibility(VISIBLE);
        menuView.setAction(backAction, true);
        closeView.setVisibility(VISIBLE);
        recyclerView.setVisibility(GONE);
        progressBar.setVisibility(INVISIBLE);
    }

    private void setSearchingView(State oldState, State newState) {
        closeView.setVisibility(VISIBLE);
        menuView.setVisibility(INVISIBLE);
        recyclerView.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
        searchText.clearFocus();
    }

    private void setSearchedView(State oldState, State newState) {
        menuView.setAction(drawerAction, true);
        menuView.setVisibility(VISIBLE);
        closeView.setVisibility(INVISIBLE);
        recyclerView.setVisibility(VISIBLE);
        progressBar.setVisibility(INVISIBLE);
        searchText.clearFocus();
        //隐藏输入框
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
    }


    public void setAdapter(RecyclerView.Adapter adapter) {
        if (adapter == null) {
            throw new IllegalArgumentException("adapter must not be NULL！");
        }
        this.adapter = adapter;
        recyclerView.setAdapter(adapter);
    }

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    public void setOnSearchListener(SearchBox.OnSearchListener listener) {
        this.onSearchLisener = listener;
    }

    public void setOnResultClickListener(RecyclerItemClickListener.OnItemClickListener onResultClickListener) {
        this.onResultClickListener = onResultClickListener;
    }

    public RecyclerItemClickListener.OnItemClickListener getOnResultClickListener() {
        return onResultClickListener;
    }

    public interface OnSearchListener {

        /**
         * @param search
         * @return 是否需要使用到非UI
         */
        boolean onSearch(String search);
    }


}
