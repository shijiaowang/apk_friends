package com.friends.lmm.friends.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.friends.lmm.friends.R;
import com.friends.lmm.friends.bean.Post;
import com.friends.lmm.friends.utils.CustomUrlSpan;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tbsplus.tbs.tencent.com.tbsplus.TbsPlus;

public class MyAdapter extends BaseAdapter {
    private List<Post> list;
    private Context context;

    public MyAdapter(Context context, List<Post> list) {
        this.context = context;
        this.list = list;
    }

    public void addPost(List<Post> list) {
        this.list = list;

    }

    @Override
    public int getCount() {
        if (list == null)
            return 0;
        else
            return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_post, null);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.post_username);
            holder.time = (TextView) view.findViewById(R.id.post_time);
            holder.fabulous = (TextView) view.findViewById(R.id.item_fabulous);
            holder.comment = (TextView) view.findViewById(R.id.item_comment);
            holder.content = (TextView) view.findViewById(R.id.post_content);
            holder.icon = (ImageView) view.findViewById(R.id.headIcon);
            holder.post_share = (ImageView) view.findViewById(R.id.post_share);
            holder.nineGrid = (NineGridView) view.findViewById(R.id.post_nineGrid);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Picasso.with(context).load(list.get(i).getUserIcon()).into(holder.icon);
        String mycontent = list.get(i).getContent();
        if (mycontent == null || mycontent.length() <= 0) {
            holder.content.setVisibility(View.GONE);
        } else {
            holder.content.setVisibility(View.VISIBLE);
            holder.content.setText(mycontent);
            interceptHyperLink(holder.content);
        }
        holder.name.setText(list.get(i).getUserName());
        holder.time.setText(list.get(i).getTime());
        holder.fabulous.setText(list.get(i).getPraise() + "");
        holder.comment.setText(list.get(i).getComment() + "");
        if (list.get(i).isHaveIcon()) {//判断是否有图片
            ArrayList<ImageInfo> imageInfo = new ArrayList<>();
            for (int j = 0; j < list.get(i).getHeadImgUrl().size(); j++) {
                ImageInfo info = new ImageInfo();
                info.setThumbnailUrl(list.get(i).getHeadImgUrl().get(j));
                info.setBigImageUrl(list.get(i).getHeadImgUrl().get(j));
                imageInfo.add(info);
            }
            holder.nineGrid.setAdapter(new NineGridViewClickAdapter(context, imageInfo));
        } else {
            holder.nineGrid.setVisibility(View.GONE);
        }
        holder.post_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(Intent.ACTION_SEND);
//                intent.setType("text/*");
//                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
//                intent.putExtra(Intent.EXTRA_TEXT,"www.baidu.com");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(Intent.createChooser(intent, "分享"));
                String url = "www.baidu.com";
                TbsPlus.openUrl(context, url, TbsPlus.eTBSPLUS_SCREENDIR.eTBSPLUS_SCREENDIR_SENSOR);
            }
        });
        return view;
    }
    /**
     * 拦截超链接
     * @param tv
     */
    private void interceptHyperLink(TextView tv) {
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence text = tv.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable spannable = (Spannable) tv.getText();
            URLSpan[] urlSpans = spannable.getSpans(0, end, URLSpan.class);
            if (urlSpans.length == 0) {
                return;
            }

            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
            // 循环遍历并拦截 所有http://开头的链接
            for (URLSpan uri : urlSpans) {
                String url = uri.getURL();
                if (url.indexOf("http://") == 0||url.indexOf("https://") == 0) {
                    CustomUrlSpan customUrlSpan = new CustomUrlSpan(context,url);
                    spannableStringBuilder.setSpan(customUrlSpan, spannable.getSpanStart(uri),
                            spannable.getSpanEnd(uri), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
            tv.setText(spannableStringBuilder);
        }
    }


    private class ViewHolder {
        private TextView name;
        private TextView time;
        private TextView content;
        private ImageView icon;
        private ImageView post_share;//分享
        private TextView fabulous;//赞
        private TextView comment; //评论
        private com.lzy.ninegrid.NineGridView nineGrid;
    }
}
