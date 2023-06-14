package com.derucci.deruccimallwebview.webview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.derucci.deruccimallwebview.R;

/**
 * Author: 林雄军
 * Description: Description
 * Date: 2023/6/7
 */
public class MallNavBar extends RelativeLayout {
    private TextView titleView;
    private ImageView closeView;
    private ImageView closeViewWhite;
    private LinearLayout backArrowView;
    private ConstraintLayout navBarBox;

    public MallNavBar(Context context) {
        this(context, null, 0);
    }

    public MallNavBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MallNavBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.component_nav_bar, this);
        initView();
    }

    private void initView() {
        titleView = findViewById(R.id.title);
        closeView = findViewById(R.id.close_view);
        closeViewWhite = findViewById(R.id.close_view_white);
        backArrowView = findViewById(R.id.left_view);
        navBarBox = findViewById(R.id.nav_bar_box);
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void setBackArrowVisibility(int visibility) {
        backArrowView.setVisibility(visibility);
    }

    public void setOnBackArrowClick(Runnable runnable) {
        backArrowView.setOnClickListener((view) -> {
            runnable.run();
        });
    }

    public void setOnCloseClick(Runnable runnable) {
        closeView.setOnClickListener((view) -> {
            runnable.run();
        });
        closeViewWhite.setOnClickListener((view) -> {
            runnable.run();
        });
    }

    public void setBackground(int color) {
        if(color == Color.TRANSPARENT) {
            closeViewWhite.setVisibility(VISIBLE);
            closeView.setVisibility(GONE);
        } else {
            closeViewWhite.setVisibility(GONE);
            closeView.setVisibility(VISIBLE);
        }
        navBarBox.setBackgroundColor(color);
    }

    public void setPaddingTop(int i) {
        setPadding(0, i, 0,0);
    }


}