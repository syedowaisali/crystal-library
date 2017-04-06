/*
 * Copyright (C) 2013-2014 Dominik Schürmann <dominik@dominikschuermann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.crystal.thirdparty.htmltextview;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;

import com.crystal.R;

import java.io.InputStream;
import java.util.Scanner;

public class HtmlTextView extends JellyBeanSpanFixTextView {

    public static int BULLET_RADIUS = 12;
    public static int BULLET_INDENT = 20;

    public static final String TAG = "HtmlTextView";
    public static final boolean DEBUG = false;

    boolean linkHit;

    private ClickableTableSpan clickableTableSpan;

    private DrawTableLinkSpan drawTableLinkSpan;

    boolean dontConsumeNonUrlClicks = true;
    private boolean removeFromHtmlSpace = true;

    public HtmlTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        HtmlTextView.BULLET_RADIUS = context.getResources().getDimensionPixelSize(R.dimen.bullet_radius);
        HtmlTextView.BULLET_INDENT = context.getResources().getDimensionPixelSize(R.dimen.bullet_indent);
    }

    public HtmlTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        HtmlTextView.BULLET_RADIUS = context.getResources().getDimensionPixelSize(R.dimen.bullet_radius);
        HtmlTextView.BULLET_INDENT = context.getResources().getDimensionPixelSize(R.dimen.bullet_indent);
    }

    public HtmlTextView(Context context) {
        super(context);
        HtmlTextView.BULLET_RADIUS = context.getResources().getDimensionPixelSize(R.dimen.bullet_radius);
        HtmlTextView.BULLET_INDENT = context.getResources().getDimensionPixelSize(R.dimen.bullet_indent);
    }


    public void setHtml(int resId) {
        setHtml(resId, null);
    }


    public void setHtml(String html) {
        setHtml(html, null);
    }


    public void setHtml(int resId, Html.ImageGetter imageGetter) {
        InputStream inputStreamText = getContext().getResources().openRawResource(resId);

        setHtml(convertStreamToString(inputStreamText), imageGetter);
    }


    public void setHtml(String html, Html.ImageGetter imageGetter) {
        final HtmlTagHandler htmlTagHandler = new HtmlTagHandler(getPaint());
        htmlTagHandler.setClickableTableSpan(clickableTableSpan);
        htmlTagHandler.setDrawTableLinkSpan(drawTableLinkSpan);

        html = htmlTagHandler.overrideTags(html);

        if (removeFromHtmlSpace) {
            setText(removeHtmlBottomPadding(Html.fromHtml(html, imageGetter, htmlTagHandler)));
        } else {
            setText(Html.fromHtml(html, imageGetter, htmlTagHandler));
        }

        // make links work
        setMovementMethod(LocalLinkMovementMethod.getInstance());
    }


    public void setRemoveFromHtmlSpace(boolean removeFromHtmlSpace) {
        this.removeFromHtmlSpace = removeFromHtmlSpace;
    }

    public void setClickableTableSpan(ClickableTableSpan clickableTableSpan) {
        this.clickableTableSpan = clickableTableSpan;
    }

    public void setDrawTableLinkSpan(DrawTableLinkSpan drawTableLinkSpan) {
        this.drawTableLinkSpan = drawTableLinkSpan;
    }


    static private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    static private CharSequence removeHtmlBottomPadding(CharSequence text) {
        if (text == null) {
            return null;
        }

        while (text.length() > 0 && text.charAt(text.length() - 1) == '\n') {
            text = text.subSequence(0, text.length() - 1);
        }
        return text;
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        linkHit = false;
        boolean res = super.onTouchEvent(event);

        if (dontConsumeNonUrlClicks) {
            return linkHit;
        }
        return res;
    }*/

}
