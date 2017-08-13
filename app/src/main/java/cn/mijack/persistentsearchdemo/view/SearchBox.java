package cn.mijack.persistentsearchdemo.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import cn.mijack.persistentsearchdemo.R;

/**
 * @author Yuan Yujie
 * @since 2017/8/11.
 */
public class SearchBox extends CardView {

    public static final int STATE_INIT = 0;
    public static final int STATE_FOCUS = 1;
    public static final int STATE_DISPLAY = 2;
    private int state = STATE_INIT;
    private ImageView imageView;
    private DrawerArrowDrawable drawable;
    private EditText editText;
    private RecyclerView recyclerView;
    private ImageView voiceView;
    private ImageView closeView;
    private ProgressBar progressBar;
    private View layout;
    private boolean isSearchBoxOpen = false;
    private RecyclerView.Adapter recyclerViewAdapter;
    private OnSearchBoxStateChangeListener onSearchBoxListener;
    private OnQueryTextListener onQueryTextListener;
    private OnVoiceActiveListener onVoiceActiveListener;

    public SearchBox(Context context) {
        this(context, null);
    }

    public SearchBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.base_search_box, this, true);
        drawable = new DrawerArrowDrawable(context);
        imageView = v.findViewById(R.id.imageView);
        voiceView = v.findViewById(R.id.ic_voice);
        voiceView.setOnClickListener(voiceView -> {
            if (onVoiceActiveListener != null) {
                onVoiceActiveListener.onVoiceActive(this);
            }
        });
        progressBar = v.findViewById(R.id.progressBar);
        closeView = v.findViewById(R.id.ic_close);
        layout = v.findViewById(R.id.layout);
        imageView.setImageDrawable(drawable);
        drawable.setProgress(0f);
        layout.setOnClickListener(view -> {
        });
        drawable.setColor(Color.parseColor("#FF737373"));
        editText = findViewById(R.id.editText);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        closeView.setOnClickListener(view -> {
            if (state == STATE_DISPLAY) {
                openSearchView();
            } else {
                closeSearchView();
            }
        });
        editText.setOnFocusChangeListener((view, b) -> {
            if (b) {
                openSearchView();
            } else if (state != STATE_DISPLAY) {
                closeSearchView();
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                showRecyclerView();
                if (onQueryTextListener != null) {
                    onQueryTextListener.onQueryTextChange(charSequence);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (onQueryTextListener != null) {
                onQueryTextListener.onQueryTextSubmit(textView.getText());
            }
            return true;
        });
        imageView.setOnClickListener(view -> {
            if (drawable.getProgress() != 0 && drawable.getProgress() != 1) {
                return;
            }
            if (state == STATE_DISPLAY || !isSearchBoxOpen()) {
                openSearchView();
            } else {
                closeSearchView();
            }
        });
    }

    private void showRecyclerView() {
        if (recyclerView.getVisibility() != VISIBLE) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) recyclerView.getLayoutParams();
            lp.topMargin = layout.getHeight();
            valueAnimator.addUpdateListener(animator -> {
                Float animatedValue = (Float) animator.getAnimatedValue();
                int recyclerViewHeight = recyclerView.getHeight();
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) recyclerView.getLayoutParams();
                layoutParams.topMargin = layout.getHeight() + (int) (recyclerViewHeight * (animatedValue - 1));
                recyclerView.setLayoutParams(layoutParams);
            });
            valueAnimator.start();
        }
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void hideRecyclerView() {
        recyclerView.setVisibility(View.INVISIBLE);
    }

    public void query(CharSequence data) {
        state = STATE_DISPLAY;
        Toast.makeText(getContext(), "query", Toast.LENGTH_SHORT).show();
        hideVoiceIcon();
        closeView.setVisibility(View.VISIBLE);
        clearFocus();
        editText.setText(data);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(drawable.getProgress(), 0);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) recyclerView.getLayoutParams();
        lp.topMargin = (int) (layout.getHeight() + recyclerView.getHeight() * drawable.getProgress());
        valueAnimator.addUpdateListener(animator -> {
            Float animatedValue = (Float) animator.getAnimatedValue();
            int recyclerViewHeight = recyclerView.getHeight();

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) recyclerView.getLayoutParams();
            layoutParams.topMargin = layout.getHeight() + (int) (recyclerViewHeight * (animatedValue - 1));
            recyclerView.setLayoutParams(layoutParams);
        });
        valueAnimator.start();
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                drawable.setVerticalMirror(false);
            }
        });
    }

    public void openSearchView() {
        isSearchBoxOpen = true;
        state = STATE_FOCUS;
        editText.requestFocus();
        hideVoiceIcon();
        closeView.setVisibility(View.VISIBLE);
        hideRecyclerView();
        ValueAnimator progressAnimator = ValueAnimator.ofFloat(drawable.getProgress(), 1);
        progressAnimator.addUpdateListener(animator -> {
            Float animatedValue = (Float) animator.getAnimatedValue();
            drawable.setProgress(animatedValue);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) recyclerView.getLayoutParams();
            showRecyclerView();
            lp.topMargin = (int) (layout.getHeight() + (animatedValue - 1) * recyclerView.getHeight());
            recyclerView.setLayoutParams(lp);
        });
        progressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                drawable.setVerticalMirror(drawable.getProgress() == 1);
            }
        });
        progressAnimator.start();
        if (onSearchBoxListener != null) {
            onSearchBoxListener.onSearchStateChange(this);
        }
    }

    public void closeSearchView() {
        isSearchBoxOpen = false;
        state = STATE_INIT;
        showVoiceIcon();
        closeView.setVisibility(View.GONE);
        clearFocus();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) recyclerView.getLayoutParams();
        drawable.setVerticalMirror(drawable.getProgress() == 1f);
        ValueAnimator progressAnimator = ValueAnimator.ofFloat(drawable.getProgress(), 0);
        progressAnimator.addUpdateListener(
                params.topMargin == layout.getHeight() - recyclerView.getHeight() ?
                        animator -> {
                            Float animatedValue = (Float) animator.getAnimatedValue();
                            drawable.setProgress(animatedValue);
                        } :
                        animator -> {
                            Float animatedValue = (Float) animator.getAnimatedValue();
                            drawable.setProgress(animatedValue);
                            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) recyclerView.getLayoutParams();
                            showRecyclerView();
                            lp.topMargin = (int) (layout.getHeight() + (animatedValue - 1) * recyclerView.getHeight());
                            System.out.println("top:" + lp.topMargin);
                            recyclerView.setLayoutParams(lp);
                        });
        progressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                drawable.setVerticalMirror(false);
                editText.setText(null);
            }
        });
        progressAnimator.start();
        if (onSearchBoxListener != null) {
            onSearchBoxListener.onSearchStateChange(this);
        }
    }

    public void requestEditTextFocus() {
        requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    public void clearFocus() {
        editText.clearFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive(editText)) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    public void setRecyclerViewAdapter(RecyclerView.Adapter recyclerViewAdapter) {
        this.recyclerViewAdapter = recyclerViewAdapter;
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    public RecyclerView.Adapter getRecyclerViewAdapter() {
        return recyclerViewAdapter;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public boolean isSearchBoxOpen() {
        return isSearchBoxOpen;
    }

    public void setSearchBoxOpen(boolean toOpen) {
        if (toOpen) {
            openSearchView();
        } else {
            closeSearchView();
        }
    }

    public void toggleSearchBox() {
        if (isSearchBoxOpen()) {
            closeSearchView();
        } else {
            openSearchView();
        }
    }

    public void hideVoiceIcon() {
        voiceView.setVisibility(GONE);
    }

    public void showVoiceIcon() {
        voiceView.setVisibility(VISIBLE);
    }

    public void setOnQueryTextListener(OnQueryTextListener onQueryTextListener) {
        this.onQueryTextListener = onQueryTextListener;
    }

    public void setOnSearchBoxListener(OnSearchBoxStateChangeListener onSearchBoxListener) {
        this.onSearchBoxListener = onSearchBoxListener;
    }

    public CharSequence getQueryText() {
        return editText.getText();
    }

    public interface OnSearchBoxStateChangeListener {
        void onSearchStateChange(SearchBox searchBox);
    }

    public interface OnQueryTextListener {

        void onQueryTextSubmit(CharSequence query);

        void onQueryTextChange(CharSequence newText);
    }

    public interface OnVoiceActiveListener {
        void onVoiceActive(SearchBox searchBox);
    }
}
