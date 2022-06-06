package com.eggplant.qiezisocial.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eggplant.qiezisocial.R;
import com.eggplant.qiezisocial.entry.CommentEntry;
import com.eggplant.qiezisocial.utils.ScreenUtil;
import com.eggplant.qiezisocial.utils.UrlUtils;
import com.eggplant.qiezisocial.widget.spannable.CircleMovementMethod;
import com.eggplant.qiezisocial.widget.spannable.SpannableClickable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yiwei on 16/7/9.
 */
public class CommentListView extends LinearLayout {
    private int itemNameColor;
    private int itemContentColor;
    private int itemSelectorColor;
    private int itemReplyColor;
    private String diaryUid = "";
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private List<CommentEntry> mDatas;
    private LayoutInflater layoutInflater;
    private TextView commentTv;
    private boolean showLoadMore;
    private boolean loadMoreOpen = false;
    private int maxShowLine = 7;
    private int dp2 = ScreenUtil.dip2px(getContext(), 2);
    private int dp3 = ScreenUtil.dip2px(getContext(), 3);

    public void setShowLoadMore(boolean showLoadMore) {
        this.showLoadMore = showLoadMore;
    }

    public boolean isShowLoadMore() {
        return showLoadMore;
    }

    public void setDiaryUid(String diaryUid) {
        this.diaryUid = diaryUid;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public OnItemLongClickListener getOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setDatas(List<CommentEntry> datas) {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        mDatas = datas;
        notifyDataSetChanged();
    }

    public List<CommentEntry> getDatas() {
        return mDatas;
    }

    public CommentListView(Context context) {
        super(context);
    }

    public CommentListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
    }

    public CommentListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    protected void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.PraiseListView, 0, 0);
        try {
            //textview的默认颜色
            itemNameColor = typedArray.getColor(R.styleable.PraiseListView_item_color_name, getResources().getColor(R.color.praise_item_default));
            itemContentColor = typedArray.getColor(R.styleable.PraiseListView_item_color_content, getResources().getColor(R.color.praise_item_default));
            itemReplyColor = typedArray.getColor(R.styleable.PraiseListView_item_color_reply, getResources().getColor(R.color.praise_item_default));
            itemSelectorColor = typedArray.getColor(R.styleable.PraiseListView_item_selector_color, getResources().getColor(R.color.praise_item_selector_default));
        } finally {
            typedArray.recycle();
        }
    }

    public void notifyDataSetChanged() {
        removeAllViews();
        if (mDatas == null || mDatas.size() == 0) {
            return;
        }
        int dataSize = mDatas.size();
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (showLoadMore && dataSize > maxShowLine) {
            if (!loadMoreOpen) {
                for (int i = 0; i < maxShowLine; i++) {
                    final int index = i;
                    View view = getView(index);
                    if (view == null) {
                        throw new NullPointerException("listview item layout is null, please check getView()...");
                    }
                    addView(view, index, layoutParams);
                }

                View openView = getOpenView();
                addView(openView);
            } else {
                for (int i = 0; i < mDatas.size(); i++) {
                    final int index = i;
                    View view = getView(index);
                    if (view == null) {
                        throw new NullPointerException("listview item layout is null, please check getView()...");
                    }
                    addView(view, index, layoutParams);
                }
                View closeView = getCloseView();
                addView(closeView);
            }

        } else {
            for (int i = 0; i < mDatas.size(); i++) {
                final int index = i;
                View view = getView(index);
                if (view == null) {
                    throw new NullPointerException("listview item layout is null, please check getView()...");
                }
                addView(view, index, layoutParams);
            }
        }


    }

    private View getOpenView() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        QzTextView loadMoreView = new QzTextView(getContext());
        loadMoreView.setTextSize(9);
        loadMoreView.setPadding(dp3, dp2, dp3, dp2);
        loadMoreView.setCompoundDrawablePadding(dp2);
        loadMoreView.setTextColor(ContextCompat.getColor(getContext(), R.color.loadmore_color));
        loadMoreView.setText("展开");
        loadMoreView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_comm_open, 0);
        loadMoreView.setLayoutParams(layoutParams);
        loadMoreView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreOpen = true;
                notifyDataSetChanged();
            }
        });
        return loadMoreView;
    }

    private View getCloseView() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        QzTextView clolseView = new QzTextView(getContext());
        clolseView.setTextSize(9);
        clolseView.setPadding(dp3, dp2, dp3, dp2);
        clolseView.setCompoundDrawablePadding(dp2);
        clolseView.setTextColor(ContextCompat.getColor(getContext(), R.color.loadmore_color));
        clolseView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_comm_close, 0);
        clolseView.setText("收起");
        clolseView.setLayoutParams(layoutParams);
        clolseView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreOpen = false;
                notifyDataSetChanged();
            }
        });
        return clolseView;
    }

    //    public void setCommentTvColor(int color){
//        if (commentTv!=null){
//            commentTv.setTextColor(color);
//        }
//    }
    private static final String TAG = "CommentListView";

    private View getView(final int position) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(getContext());
        }
        View convertView = layoutInflater.inflate(R.layout.item_comment, null, false);

        commentTv = (TextView) convertView.findViewById(R.id.commentTv);
        commentTv.setTextColor(itemContentColor);
        final CircleMovementMethod circleMovementMethod = new CircleMovementMethod(itemSelectorColor, itemSelectorColor);

        final CommentEntry bean = mDatas.get(position);
        String name = bean.userinfor.nick;
        int id = Integer.parseInt(bean.uid);
        int answId = Integer.parseInt(bean.toid);
        String toReplyName = "";
        Log.i("commentlistview", " toid:" + bean.toid + "  diaryid:" + diaryUid);
        if (answId > 0 && answId != id && !TextUtils.equals(bean.toid, diaryUid)) {
            toReplyName = bean.touserinfor.nick;
        }

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(setClickableSpan(name, id, true));

        if (!TextUtils.isEmpty(toReplyName)) {
            builder.append(setColorSpan(" 回复 "));
            builder.append(setClickableSpan(toReplyName, answId, true));
        }
        builder.append(": ");
        //转换表情字符
        String contentBodyStr = bean.text;


        CharSequence text = contentBodyStr;
        String rexgString = "\\[qzxs\\d+\\]";
        Pattern pattern = Pattern.compile(rexgString);
        Matcher matcher = pattern.matcher(text);


        int length = builder.length();
        builder.append(UrlUtils.formatUrlString(contentBodyStr));

        while (matcher.find()) {
            String group = matcher.group();
            int textImg = getTextImg(group);
            textImg = textImg == 0 ? R.drawable.emoji1 : textImg;
            builder.setSpan(
                    new ImageSpan(getContext(), getSpanBitmap(textImg)), length + matcher.start(), length + matcher
                            .end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        commentTv.setText(builder);

        commentTv.setMovementMethod(circleMovementMethod);
        commentTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (circleMovementMethod.isPassToTv()) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position);
                    }
                }
            }
        });
        commentTv.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (circleMovementMethod.isPassToTv()) {
                    if (onItemLongClickListener != null) {
                        onItemLongClickListener.onItemLongClick(position);
                    }
                    return true;
                }
                return false;
            }
        });

        return convertView;
    }

    private Bitmap getSpanBitmap(int result) {
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeResource(getResources(), result);
        Bitmap newBit = Bitmap.createScaledBitmap(bitmap, ScreenUtil.dip2px(getContext(), 25), ScreenUtil.dip2px(getContext(), 25), true);
//        BitmapFactory.Options options=new BitmapFactory.Options();
//        options.inSampleSize=1;
//        options.inJustDecodeBounds = false;
//        bitmap=BitmapFactory.decodeResource(getResources(),result,options);
        return newBit;
    }

    private int getTextImg(String url) {
        int result = 0;
        String replace = url.replace("[qzxs", "");
//        Log.e(TAG, "getTextImg: "+replace );
        String replace1 = replace.replace("]", "");
//        Log.e(TAG, "getTextImg: "+replace1 );
        int index = Integer.parseInt(replace1);
        for (int i = 0; i < 140; i++) {
            if (index == i) {
                result = images[i];
            }
        }
        return result;
    }

    private static final int[] images = {R.drawable.emoji1, R.drawable.emoji2, R.drawable.emoji3, R.drawable.emoji4, R.drawable.emoji5, R.drawable.emoji6, R.drawable.emoji7, R.drawable.emoji8, R.drawable.emoji9
            , R.drawable.emoji10, R.drawable.emoji11, R.drawable.emoji12, R.drawable.emoji13, R.drawable.emoji14, R.drawable.emoji15, R.drawable.emoji16, R.drawable.emoji17, R.drawable.emoji18, R.drawable.emoji19, R.drawable.emoji20
            , R.drawable.emoji21, R.drawable.emoji22, R.drawable.emoji23, R.drawable.emoji24, R.drawable.emoji25, R.drawable.emoji26, R.drawable.emoji27, R.drawable.emoji28, R.drawable.emoji29, R.drawable.emoji30, R.drawable.emoji31, R.drawable.emoji31
            , R.drawable.emoji32, R.drawable.emoji33, R.drawable.emoji34, R.drawable.emoji35, R.drawable.emoji36, R.drawable.emoji37, R.drawable.emoji38, R.drawable.emoji39, R.drawable.emoji40, R.drawable.emoji41, R.drawable.emoji42, R.drawable.emoji43
            , R.drawable.emoji44, R.drawable.emoji45, R.drawable.emoji46, R.drawable.emoji47, R.drawable.emoji48, R.drawable.emoji49, R.drawable.emoji50, R.drawable.emoji51, R.drawable.emoji52, R.drawable.emoji53, R.drawable.emoji54, R.drawable.emoji55
            , R.drawable.emoji56, R.drawable.emoji57, R.drawable.emoji58, R.drawable.emoji59, R.drawable.emoji60, R.drawable.emoji61, R.drawable.emoji62, R.drawable.emoji63, R.drawable.emoji64, R.drawable.emoji65, R.drawable.emoji66, R.drawable.emoji67
            , R.drawable.emoji68, R.drawable.emoji69, R.drawable.emoji70, R.drawable.emoji71, R.drawable.emoji72, R.drawable.emoji73, R.drawable.emoji74, R.drawable.emoji75, R.drawable.emoji76, R.drawable.emoji77, R.drawable.emoji78, R.drawable.emoji79
            , R.drawable.emoji80, R.drawable.emoji81, R.drawable.emoji82, R.drawable.emoji83, R.drawable.emoji84, R.drawable.emoji85, R.drawable.emoji86, R.drawable.emoji87, R.drawable.emoji89, R.drawable.emoji90, R.drawable.emoji91, R.drawable.emoji92
            , R.drawable.emoji93, R.drawable.emoji94, R.drawable.emoji95, R.drawable.emoji96, R.drawable.emoji97, R.drawable.emoji98, R.drawable.emoji99, R.drawable.emoji100, R.drawable.emoji101, R.drawable.emoji102, R.drawable.emoji103, R.drawable.emoji104
            , R.drawable.emoji105, R.drawable.emoji106, R.drawable.emoji107, R.drawable.emoji108, R.drawable.emoji109, R.drawable.emoji110, R.drawable.emoji111, R.drawable.emoji112, R.drawable.emoji113, R.drawable.emoji114, R.drawable.emoji115, R.drawable.emoji116
            , R.drawable.emoji117, R.drawable.emoji118, R.drawable.emoji119, R.drawable.emoji120, R.drawable.emoji121, R.drawable.emoji122, R.drawable.emoji123, R.drawable.emoji124, R.drawable.emoji125, R.drawable.emoji126, R.drawable.emoji127, R.drawable.emoji128
            , R.drawable.emoji129, R.drawable.emoji130, R.drawable.emoji131, R.drawable.emoji132, R.drawable.emoji133, R.drawable.emoji134, R.drawable.emoji135, R.drawable.emoji136, R.drawable.emoji137, R.drawable.emoji138, R.drawable.emoji139, R.drawable.emoji140};

    @NonNull
    private SpannableString setClickableSpan(final String textStr, final int id, boolean isName) {
        int itemColor = isName ? itemNameColor : itemContentColor;
        SpannableString subjectSpanText = new SpannableString(textStr);
        subjectSpanText.setSpan(new SpannableClickable(itemColor) {
                                    @Override
                                    public void onClick(View widget) {
//                                        WrapIntent intent = new WrapIntent(getContext(), WrapIntent.INNER_URI_QIEZI_SPACE + "://" + id);
//                                        if(intent.valid){
//                                            getContext().startActivity(intent);
//                                        }
                                    }
                                }, 0, subjectSpanText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return subjectSpanText;
    }

    private SpannableString setColorSpan(final String textStr) {
        // 正确的写法
        SpannableString result = new SpannableString(textStr);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(itemReplyColor);
        result.setSpan(colorSpan, 0, textStr.length() , Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return result;
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

}
