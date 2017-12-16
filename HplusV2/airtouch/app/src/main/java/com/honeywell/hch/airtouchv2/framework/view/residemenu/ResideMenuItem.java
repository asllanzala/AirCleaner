package com.honeywell.hch.airtouchv2.framework.view.residemenu;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;


/**
 * Created by Jin Qian on 1/22/2015.
 */
public class ResideMenuItem extends LinearLayout {
    /**
     * menu item layout
     */
    private RelativeLayout mMenuLayout;
    /**
     * menu item icon
     */
    private ImageView iv_icon;
    private ImageView iv_arrow;
    /**
     * menu item title
     */
    private TextView tv_title;
    private TextView tv_mobile;
    ColorStateList cslMenu = (ColorStateList) getResources().getColorStateList(R.color.menu_text);
    ColorStateList cslGrey = (ColorStateList) getResources().getColorStateList(R.color.login_hint_text);

    public ResideMenuItem(Context context) {
        super(context);
        initViews(context);
    }

    public ResideMenuItem(Context context, String title) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (title.equals("Line")) {
            inflater.inflate(R.layout.residemenu_line, this);
        }

    }

    public ResideMenuItem(Context context, int icon, String title) {
        super(context);

        ColorStateList csl1 = (ColorStateList) getResources().getColorStateList(R.color.edit_city_text);
        ColorStateList csl2 = (ColorStateList) getResources().getColorStateList(R.color.white);

        if (title.equals("Nick" + "  ")) {
            initNickViews(context);
        } else {
            initViews(context);
        }

        iv_icon.setImageResource(icon);
        tv_title.setText(title);

        if (title.equals("Nick" + "  ")) {
            tv_title.setText("");
            tv_title.setTextSize(24);
            tv_mobile.setTextSize(12);
            iv_icon.setPadding(0, 75, 0, 75);
            tv_title.setPadding(0, 75, 0, 0);
        } else if (title.equals(context.getString(R.string.places_care) + "  ")) {
            mMenuLayout.setBackgroundColor(getResources().getColor(R.color.title_bar_bg));
            iv_icon.setVisibility(View.VISIBLE);
            iv_arrow.setVisibility(View.VISIBLE);
            iv_arrow.setImageResource(R.drawable.menu_arrow_down);
            tv_title.setTextColor(csl1);
//            iv_icon.setPadding(0, 50, 0, 0);
//            tv_title.setPadding(0, 50, 0, 0);
        } else if (title.equals(context.getString(R.string.add_device) + "  ")) {
            tv_title.setTextColor(csl1);
            iv_icon.setVisibility(View.VISIBLE);
            iv_arrow.setVisibility(View.VISIBLE);
            iv_arrow.setImageResource(R.drawable.menu_arrow_right);
            mMenuLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.residemenu_item_selector2));
        } else if (title.equals(context.getString(R.string.user_guide) + "  ")) {
            tv_title.setTextColor(csl1);
            iv_icon.setVisibility(View.VISIBLE);
            iv_arrow.setVisibility(View.VISIBLE);
            iv_arrow.setImageResource(R.drawable.menu_arrow_right);
            mMenuLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.residemenu_item_selector2));
        } else if (title.equals(context.getString(R.string.change_password) + "  ")) {
            tv_title.setTextColor(csl1);
            iv_arrow.setVisibility(View.VISIBLE);
            iv_arrow.setImageResource(R.drawable.menu_arrow_right);
            mMenuLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.residemenu_item_selector2));
        } else if (title.equals(context.getString(R.string.log_out) + "  ")) {
            tv_title.setTextColor(csl1);
        } else if (title.equals("")) {
            iv_icon.setVisibility(View.INVISIBLE);
        } else {
            // user home information
            iv_icon.setVisibility(View.INVISIBLE);
            tv_title.setTextSize(16);
            tv_title.setTextColor(csl2);
            tv_title.setPadding(0, 10, 0, 10);
        }

    }

    private void initViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.residemenu_item, this);
        mMenuLayout = (RelativeLayout) findViewById(R.id.residemenu_item_layout);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        iv_arrow = (ImageView) findViewById(R.id.iv_arrow);
    }

    private void initNickViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.residemenu_nick_item, this);
        mMenuLayout = (RelativeLayout) findViewById(R.id.residemenu_item_layout);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_mobile = (TextView) findViewById(R.id.tv_mobile);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
//        ColorStateList csl = (ColorStateList) getResources().getColorStateList(R.color.menu_home_text);
//        tv_mobile.setTextColor(csl);
        tv_mobile.setText(AuthorizeApp.shareInstance().getMobilePhone());
    }

    /**
     * set the icon color;
     *
     * @param icon
     */
    public void setIcon(int icon) {
        iv_icon.setImageResource(icon);
    }

    /**
     * set the title with resource
     * ;
     *
     * @param title
     */
    public void setTitle(int title) {
        tv_title.setText(title);
    }

    /**
     * set the title with string;
     *
     * @param title
     */
    public void setTitle(String title) {
        tv_title.setText(title);
    }

    public void setMobile(String mobile) {
        tv_mobile.setText(mobile);
    }

    public TextView getTv_title() {
        return tv_title;
    }

    public ImageView getIv_icon() {
        return iv_icon;
    }

}
