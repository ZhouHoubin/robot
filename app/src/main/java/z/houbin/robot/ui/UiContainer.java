package z.houbin.robot.ui;

import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import z.houbin.robot.R;

public class UiContainer {
    private LinearLayout mRoot;
    private Handler handler = new Handler();

    public UiContainer(LinearLayout mRoot) {
        this.mRoot = mRoot;
    }

    public void left(String text) {
        View left = View.inflate(mRoot.getContext(), R.layout.item_left, null);
        TextView leftText = left.findViewById(R.id.text);
        leftText.setText(text);
        addChild(left);
    }

    public void right(String text) {
        View right = View.inflate(mRoot.getContext(), R.layout.item_right, null);
        TextView rightText = right.findViewById(R.id.text);
        rightText.setText(text);
        addChild(right);
    }

    private void addChild(final View view) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mRoot.addView(view);
            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollToBottom();
            }
        }, 1000);
    }

    private void scrollToBottom(){
        ScrollView scroll = (ScrollView) mRoot.getParent();
        scroll.fullScroll(ScrollView.FOCUS_DOWN);
    }
}
