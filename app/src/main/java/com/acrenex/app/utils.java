package com.acrenex.app;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

/** Shared premium UI system for AcreNex. All programmatic screens use this helper. */
public class utils {
    public static final int NAVY = Color.rgb(11,59,46);
    public static final int BLUE = Color.rgb(23,107,90);
    public static final int GREEN = Color.rgb(47,143,69);
    public static final int GOLD = Color.rgb(212,167,44);
    public static final int WARNING = Color.rgb(201,139,36);
    public static final int TEXT = Color.rgb(23,55,45);
    public static final int MUTED = Color.rgb(110,126,119);
    public static final int BG = Color.rgb(245,246,241);
    public static final int BORDER = Color.rgb(220,228,223);

    public static int dp(Context c,int v){return Math.round(v*c.getResources().getDisplayMetrics().density);}

    /**
     * Creates a ScrollView whose child remains attached to it. Call setScreenContentView()
     * instead of setContentView(root). This removes the historical Android parent-attachment crash.
     */
    public static LinearLayout screen(Context c,String title,String subtitle){
        ScrollView scroll=new ScrollView(c);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setBackgroundColor(BG);

        LinearLayout root=new LinearLayout(c);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(c,16),dp(c,16),dp(c,16),dp(c,34));
        root.setBackgroundColor(BG);
        scroll.addView(root,new ScrollView.LayoutParams(-1,-2));

        MaterialCardView hero=new MaterialCardView(c);
        hero.setRadius(dp(c,26));
        hero.setCardElevation(dp(c,1));
        hero.setStrokeWidth(dp(c,1));
        hero.setStrokeColor(Color.rgb(45,121,93));
        hero.setBackgroundResource(R.drawable.bg_brand_hero);
        hero.setCardBackgroundColor(NAVY);

        LinearLayout heroRow=new LinearLayout(c);
        heroRow.setOrientation(LinearLayout.HORIZONTAL);
        heroRow.setGravity(Gravity.CENTER_VERTICAL);
        heroRow.setPadding(dp(c,15),dp(c,14),dp(c,15),dp(c,14));

        ImageView mark=new ImageView(c);
        mark.setImageResource(R.drawable.ic_acrenex_mark);
        mark.setContentDescription("AcreNex company mark");
        mark.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        GradientDrawable circle=new GradientDrawable();
        circle.setColor(Color.WHITE); circle.setCornerRadius(dp(c,16));
        mark.setBackground(circle); mark.setPadding(dp(c,5),dp(c,5),dp(c,5),dp(c,5));
        heroRow.addView(mark,new LinearLayout.LayoutParams(dp(c,56),dp(c,56)));

        LinearLayout heading=new LinearLayout(c);
        heading.setOrientation(LinearLayout.VERTICAL);
        heading.setPadding(dp(c,14),0,0,0);
        TextView brand=text(c,"ACRENEX  •  LAND INTELLIGENCE",9,Color.rgb(231,198,91),true);
        heading.addView(brand,new LinearLayout.LayoutParams(-1,-2));
        TextView t=text(c,title,23,Color.WHITE,true);
        t.setMaxLines(2); heading.addView(t,new LinearLayout.LayoutParams(-1,-2));
        heroRow.addView(heading,new LinearLayout.LayoutParams(0,-2,1));
        hero.addView(heroRow);
        root.addView(hero,new LinearLayout.LayoutParams(-1,-2));

        if(subtitle!=null&&!subtitle.trim().isEmpty()){
            TextView s=text(c,subtitle,13,MUTED,false);
            s.setPadding(dp(c,3),dp(c,11),dp(c,3),dp(c,12));
            root.addView(s,new LinearLayout.LayoutParams(-1,-2));
        }
        return root;
    }

    /** Safely attaches the ScrollView created by screen(). */
    public static void setScreenContentView(AppCompatActivity activity,LinearLayout root){
        if(activity==null||root==null)return;
        ViewParent parent=root.getParent();
        if(parent instanceof ViewGroup){
            ViewGroup top=(ViewGroup)parent;
            if(top.getParent()!=null && top.getParent() instanceof ViewGroup){
                ((ViewGroup)top.getParent()).removeView(top);
            }
            activity.setContentView(top);
        }else{
            activity.setContentView(root);
        }
    }

    public static TextView text(Context c,String value,int size,int color,boolean bold){
        TextView v=new TextView(c); v.setText(value); v.setTextSize(size); v.setTextColor(color);
        v.setTypeface(Typeface.DEFAULT,bold?Typeface.BOLD:Typeface.NORMAL);
        v.setIncludeFontPadding(true); v.setPadding(0,dp(c,2),0,dp(c,2)); return v;
    }
    public static TextView section(Context c,String value){
        TextView v=text(c,value.toUpperCase(),10,MUTED,true); v.setLetterSpacing(.12f);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.topMargin=dp(c,8); p.bottomMargin=dp(c,7); v.setLayoutParams(p); return v;
    }
    public static MaterialButton button(Context c,String label){
        MaterialButton b=new MaterialButton(c); b.setText(label); b.setTextSize(13); b.setTextColor(Color.WHITE); b.setAllCaps(false);
        b.setCornerRadius(dp(c,14)); b.setBackgroundTintList(ColorStateList.valueOf(BLUE)); b.setMinHeight(dp(c,48));
        b.setRippleColor(ColorStateList.valueOf(Color.rgb(55,135,113))); return b;
    }
    public static MaterialButton outlineButton(Context c,String label){
        MaterialButton b=new MaterialButton(c); b.setText(label); b.setTextSize(13); b.setAllCaps(false); b.setCornerRadius(dp(c,14));
        b.setTextColor(BLUE); b.setStrokeColor(ColorStateList.valueOf(BLUE)); b.setStrokeWidth(dp(c,1));
        b.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE)); return b;
    }
    public static MaterialCardView card(Context c){
        MaterialCardView card=new MaterialCardView(c); card.setCardBackgroundColor(Color.WHITE); card.setRadius(dp(c,20));
        card.setCardElevation(dp(c,2)); card.setUseCompatPadding(true); card.setStrokeColor(BORDER); card.setStrokeWidth(dp(c,1)); return card;
    }
    public static void addCard(LinearLayout root,View content,Context c){MaterialCardView card=card(c); card.addView(content); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.bottomMargin=dp(c,12);root.addView(card,p);}
    public static LinearLayout column(Context c){LinearLayout l=new LinearLayout(c);l.setOrientation(LinearLayout.VERTICAL);l.setPadding(dp(c,18),dp(c,15),dp(c,18),dp(c,16));return l;}
    public static LinearLayout rowContainer(Context c){LinearLayout l=new LinearLayout(c);l.setOrientation(LinearLayout.HORIZONTAL);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
    public static void row(LinearLayout l,Context c,String label,String value){LinearLayout r=rowContainer(c);TextView a=text(c,label,12,MUTED,false);TextView b=text(c,value,13,TEXT,true);b.setGravity(Gravity.END);r.addView(a,new LinearLayout.LayoutParams(0,-2,1));r.addView(b,new LinearLayout.LayoutParams(0,-2,1));LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,-2);rp.bottomMargin=dp(c,4);l.addView(r,rp);}
    public static TextView badge(Context c,String value,boolean positive){TextView b=text(c,value,10,positive?GREEN:Color.rgb(178,113,26),true);GradientDrawable g=new GradientDrawable();g.setColor(positive?Color.rgb(231,246,235):Color.rgb(255,245,222));g.setCornerRadius(dp(c,18));b.setBackground(g);b.setPadding(dp(c,10),dp(c,5),dp(c,10),dp(c,5));return b;}
}
