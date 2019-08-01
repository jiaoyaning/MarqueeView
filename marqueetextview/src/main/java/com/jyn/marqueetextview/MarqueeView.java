package com.jyn.marqueetextview;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jiao on 2019/7/31.
 */
public class MarqueeView extends HorizontalScrollView {

    private TextView mTextView;
    private TextView mGhostTextView;

    private CharSequence mText;
    private int measureText;

    private int mOffset = 0;
    private int mGhostOffset = 0;

    /**
     * 间隔
     */
    private int spacing = 100;

    /**
     * 移动速度
     */
    private int speed = 1;

    private ValueAnimator valueAnimator;

    public MarqueeView(Context context) {
        this(context, null);
    }

    public MarqueeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout();
        initAnim();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (measureText > w) {
            startAnim();
        }
    }

    private void initLayout() {
        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        relativeLayout.setLayoutParams(layoutParams);
        addView(relativeLayout);

        mTextView = createTextView();
        mGhostTextView = createTextView();

        relativeLayout.addView(mTextView);
        relativeLayout.addView(mGhostTextView);

    }

    private void initAnim() {
        valueAnimator = ValueAnimator.ofFloat(0, measureText);
        valueAnimator.addUpdateListener(animatorUpdateListener);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setText(CharSequence text) {
        this.mText = fromHtml(text.toString());
        buildMsgText(mTextView);
        buildMsgText(mGhostTextView);
        measureText = (int) mTextView.getPaint().measureText(mText, 0, mText.length());
        mOffset = 0;
        mGhostOffset = measureText + spacing;
        mGhostTextView.setX(mGhostOffset);
    }

    private TextView createTextView() {
        TextView textView = new TextView(getContext());
        textView.setPadding(0, 0, 0, 0);
        textView.setSingleLine();
        textView.setTextColor(Color.parseColor("#FFFFFF"));
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER_VERTICAL);//水平居中
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        return textView;
    }

    public void startAnim() {
        valueAnimator.setDuration((long) measureText);
        valueAnimator.start();
    }

    public void stopAnim() {
        valueAnimator.clone();
    }

    ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mOffset -= speed;
            mGhostOffset -= speed;
            if (mOffset + measureText < 0) {
                mOffset = mGhostOffset + measureText + spacing;
            }
            if (mGhostOffset + measureText < 0) {
                mGhostOffset = mOffset + measureText + spacing;
            }
            invalidate();
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mTextView == null || mGhostTextView == null) {
            return;
        }
        mTextView.setX(mOffset);
        mGhostTextView.setX(mGhostOffset);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return true;
    }

    private String xmlMsgToStr(String xmlMsg) {
        String PATTEN_STR1 = "<span id=\"title\">.+?</span>|<span id=\"name\">.+?</span>|<p>|</p>|<TextFlow .+?>|</TextFlow>";
        String titelStr = "";
        String nameStr = "";
        InputStream inStream = new ByteArrayInputStream(xmlMsg.getBytes());
        XmlPullParser pullParser = Xml.newPullParser();
        try {
            pullParser.setInput(inStream, "UTF-8");
            int event = pullParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:
                        if ("span".equals(pullParser.getName())) {
                            if (pullParser.getAttributeValue(0).equals("title")) {
                                titelStr = pullParser.nextText() + " ";
                            } else if (pullParser.getAttributeValue(0).equals("name")) {
                                nameStr = pullParser.nextText();
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:

                        break;
                }
                event = pullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Pattern pattern = Pattern.compile(PATTEN_STR1);
        Matcher matcher = pattern.matcher(xmlMsg);
        while (matcher.find()) {
            xmlMsg = xmlMsg.replace(matcher.group(), "");
        }
        return titelStr + nameStr + xmlMsg;
    }

    private Spanned fromHtml(String source) {
        return Html.fromHtml(xmlMsgToStr(source));
    }

    private void buildMsgText(TextView textView) {
        textView.setText(mText);
        CharSequence str = textView.getText();

        if (str instanceof Spannable) {
            int end = str.length();
            Spannable sp = (Spannable) str;
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);  //找出text中的a标签
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
            spannableStringBuilder.clearSpans();
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFF")), 0, mText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFB00")), mText.length(), titelStr.length() + nameStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            for (URLSpan url : urls) {
                String jumpUrl = url.getURL();
                MarqueeLayoutUrlClickSpan myURLSpan = new MarqueeLayoutUrlClickSpan(jumpUrl);
                spannableStringBuilder.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            textView.setText(spannableStringBuilder);
        }
    }

    private class MarqueeLayoutUrlClickSpan extends ClickableSpan {
        private String url;

        public MarqueeLayoutUrlClickSpan(String url) {
            this.url = url;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            //设置超链接字体颜色
            ds.linkColor = Color.parseColor("#FFFFFF");
            super.updateDrawState(ds);
            ds.setUnderlineText(true);
        }

        @Override
        public void onClick(View widget) {

        }
    }
}
