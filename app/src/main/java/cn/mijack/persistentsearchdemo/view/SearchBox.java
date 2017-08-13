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

import cn.mijack.persistentsearchdemo.ItemClickSupport;
import cn.mijack.persistentsearchdemo.ItemDivider;
import cn.mijack.persistentsearchdemo.R;
import cn.mijack.persistentsearchdemo.ui.SearchBoxActivity;

/**
 * Created by Mr.Yuan on 2017/8/11.
 */

public class SearchBox extends CardView {

    public static final int STATE_INIT = 0;
    public static final int STATE_FOCUS = 1;
    public static final int STATE_SHOW_HISTORY = 2;
    public static final int STATE_SHOW_SUGGUEST = 3;
    public static final int STATE_DISPLAY = 4;
    private int state = STATE_INIT;
    private ImageView imageView;
    private DrawerArrowDrawable drawable;
    private EditText editText;
    private RecyclerView recyclerView;
    private ImageView voiceView;
    private ImageView closeView;
    private ProgressBar progressBar;
    private View layout;
    private boolean closed = true;
    private RecyclerView.Adapter suggestionAdapter;

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
//        recyclerView.setAdapter(arrayAdapter);
        recyclerView.addItemDecoration(new ItemDivider());
        closeView.setOnClickListener(view -> closeSearchView());
//        ItemClickSupport.addTo(recyclerView)
//                .setOnItemClickListener((recyclerView, position, view) -> {
//                });
//        editText.setOnFocusChangeListener((view, b) -> {
//            if (state == STATE_SHOW_SUGGUEST) {
//                showSuggest();
//                return;
//            }
//            if (state == STATE_DISPLAY) {
//                return;
//            }
//            if (b) {
//                openSearchView();
//            } else {
//                closeSearchView();
//            }
//        });
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                System.out.println("onTextChanged:" + charSequence);
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//        editText.setOnEditorActionListener((textView, i, keyEvent) -> {
//            doSearch(textView.getText().toString());
//            return true;
//        });
//        imageView.setOnClickListener(view -> {
//            if (drawable.getProgress() != 0 && drawable.getProgress() != 1) {
//                return;
//            }
//            if (closed) {
//                openSearchView();
//            } else {
//                closeSearchView();
//            }
//        });
    }

    private void doSearch(String search) {
        Toast.makeText(getContext(), "search:" + search, Toast.LENGTH_SHORT).show();
    }

    private void showSuggest() {

    }

    private void doDisplay(String data) {
        state = STATE_DISPLAY;
        Toast.makeText(getContext(), "doDisplay", Toast.LENGTH_SHORT).show();
        voiceView.setVisibility(View.GONE);
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

    private void openSearchView() {
        state = STATE_FOCUS;
        editText.requestFocus();
        voiceView.setVisibility(View.GONE);
        closeView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        ValueAnimator progressAnimator = ValueAnimator.ofFloat(drawable.getProgress(), 1);
        progressAnimator.addUpdateListener(animator -> {
            Float animatedValue = (Float) animator.getAnimatedValue();
            drawable.setProgress(animatedValue);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) recyclerView.getLayoutParams();
            recyclerView.setVisibility(View.VISIBLE);
            lp.topMargin = (int) (layout.getHeight() + (animatedValue - 1) * recyclerView.getHeight());
            System.out.println("top:" + lp.topMargin);
            recyclerView.setLayoutParams(lp);
        });
        progressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                drawable.setVerticalMirror(drawable.getProgress() == 1);
//                PersistentSearchActivity.this.closed = false;
            }
        });
        progressAnimator.start();
    }

    private void closeSearchView() {
        state = STATE_INIT;
        voiceView.setVisibility(View.VISIBLE);
        closeView.setVisibility(View.GONE);
        clearFocus();
        editText.setText(null);
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
                            recyclerView.setVisibility(View.VISIBLE);
                            lp.topMargin = (int) (layout.getHeight() + (animatedValue - 1) * recyclerView.getHeight());
                            System.out.println("top:" + lp.topMargin);
                            recyclerView.setLayoutParams(lp);
                        });
        progressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                drawable.setVerticalMirror(false);
//                PersistentSearchActivity.this.closed = true;
            }
        });
        progressAnimator.start();
    }

    public void clearFocus() {
        editText.clearFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive(editText)) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }
}
