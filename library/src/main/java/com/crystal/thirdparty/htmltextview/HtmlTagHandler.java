/*
 * Copyright (C) 2013-2015 Dominik Schürmann <dominik@dominikschuermann.de>
 * Copyright (C) 2013-2015 Juha Kuitunen
 * Copyright (C) 2013 Mohammed Lakkadshaw
 * Copyright (C) 2007 The Android Open Source Project
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

import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.AlignmentSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;

import org.xml.sax.XMLReader;

import java.util.Stack;


public class HtmlTagHandler implements Html.TagHandler {

    public static final String UNORDERED_LIST = "HTML_TEXTVIEW_ESCAPED_UL_TAG";
    public static final String ORDERED_LIST = "HTML_TEXTVIEW_ESCAPED_OL_TAG";
    public static final String LIST_ITEM = "HTML_TEXTVIEW_ESCAPED_LI_TAG";
    private final TextPaint mTextPaint;

    int tableTagLevel = 0;

    private int indent = 20;
    private int listItemIndent;
    private CMBulletSpan bullet;
    private ClickableTableSpan clickableTableSpan;
    private DrawTableLinkSpan drawTableLinkSpan;

    public HtmlTagHandler(TextPaint textPaint) {
        mTextPaint = textPaint;
        indent = HtmlTextView.BULLET_INDENT;
        listItemIndent = indent * 2;
        bullet = new CMBulletSpan(indent);
    }


    String overrideTags(String html){

        if (html == null) return null;

        html = html.replace("<ul", "<" + UNORDERED_LIST);
        html = html.replace("</ul>", "</" + UNORDERED_LIST + ">");
        html = html.replace("<ol", "<" + ORDERED_LIST);
        html = html.replace("</ol>", "</" + ORDERED_LIST + ">");
        html = html.replace("<li", "<" + LIST_ITEM);
        html = html.replace("</li>", "</" + LIST_ITEM + ">");

        return html;
    }


    Stack<String> lists = new Stack<>();

    Stack<Integer> olNextIndex = new Stack<>();

    StringBuilder tableHtmlBuilder = new StringBuilder();



    private static class Ul {
    }

    private static class Ol {
    }

    private static class Code {
    }

    private static class Center {
    }

    private static class Strike {
    }

    private static class Table {
    }

    private static class Tr {
    }

    private static class Th {
    }

    private static class Td {
    }

    @Override
    public void handleTag(final boolean opening, final String tag, Editable output, final XMLReader xmlReader) {
        if (opening) {
            // opening tag
            if (HtmlTextView.DEBUG) {
                Log.d(HtmlTextView.TAG, "opening, output: " + output.toString());
            }

            if (tag.equalsIgnoreCase(UNORDERED_LIST)) {
                lists.push(tag);
            } else if (tag.equalsIgnoreCase(ORDERED_LIST)) {
                lists.push(tag);
                olNextIndex.push(1);
            } else if (tag.equalsIgnoreCase(LIST_ITEM)) {
                if (output.length() > 0 && output.charAt(output.length() - 1) != '\n') {
                    output.append("\n");
                }
                if (!lists.isEmpty()) {
                    String parentList = lists.peek();
                    if (parentList.equalsIgnoreCase(ORDERED_LIST)) {
                        start(output, new Ol());
                        olNextIndex.push(olNextIndex.pop() + 1);
                    } else if (parentList.equalsIgnoreCase(UNORDERED_LIST)) {
                        start(output, new Ul());
                    }
                }
            } else if (tag.equalsIgnoreCase("code")) {
                start(output, new Code());
            } else if (tag.equalsIgnoreCase("center")) {
                start(output, new Center());
            } else if (tag.equalsIgnoreCase("s") || tag.equalsIgnoreCase("strike")) {
                start(output, new Strike());
            } else if (tag.equalsIgnoreCase("table")) {
                start(output, new Table());
                if (tableTagLevel == 0) {
                    tableHtmlBuilder = new StringBuilder();
                    // We need some text for the table to be replaced by the span because
                    // the other tags will remove their text when their text is extracted
                    output.append("table placeholder");
                }

                tableTagLevel++;
            } else if (tag.equalsIgnoreCase("tr")) {
                start(output, new Tr());
            } else if (tag.equalsIgnoreCase("th")) {
                start(output, new Th());
            } else if (tag.equalsIgnoreCase("td")) {
                start(output, new Td());
            }
        } else {
            // closing tag
            if (HtmlTextView.DEBUG) {
                Log.d(HtmlTextView.TAG, "closing, output: " + output.toString());
            }

            if (tag.equalsIgnoreCase(UNORDERED_LIST)) {
                lists.pop();
            } else if (tag.equalsIgnoreCase(ORDERED_LIST)) {
                lists.pop();
                olNextIndex.pop();
            } else if (tag.equalsIgnoreCase(LIST_ITEM)) {
                if (!lists.isEmpty()) {
                    if (lists.peek().equalsIgnoreCase(UNORDERED_LIST)) {
                        if (output.length() > 0 && output.charAt(output.length() - 1) != '\n') {
                            output.append("\n");
                        }
                        // Nested BulletSpans increases distance between bullet and text, so we must prevent it.
                        int bulletMargin = indent;
                        if (lists.size() > 1) {
                            bulletMargin = indent - bullet.getLeadingMargin(true);
                            if (lists.size() > 2) {
                                // This get's more complicated when we add a LeadingMarginSpan into the same line:
                                // we have also counter it's effect to BulletSpan
                                bulletMargin -= (lists.size() - 2) * listItemIndent;
                            }
                        }
                        CMBulletSpan newBullet = new CMBulletSpan(bulletMargin);
                        end(output, Ul.class, false,
                                new LeadingMarginSpan.Standard(listItemIndent * (lists.size() - 1)),
                                newBullet);
                    } else if (lists.peek().equalsIgnoreCase(ORDERED_LIST)) {
                        if (output.length() > 0 && output.charAt(output.length() - 1) != '\n') {
                            output.append("\n");
                        }
                        int numberMargin = listItemIndent * (lists.size() - 1);
                        if (lists.size() > 2) {
                            // Same as in ordered lists: counter the effect of nested Spans
                            numberMargin -= (lists.size() - 2) * listItemIndent;
                        }
                        NumberSpan numberSpan = new NumberSpan(mTextPaint, olNextIndex.lastElement() - 1);
                        end(output, Ol.class, false,
                                new LeadingMarginSpan.Standard(numberMargin),
                                numberSpan);
                    }
                }
            } else if (tag.equalsIgnoreCase("code")) {
                end(output, Code.class, false, new TypefaceSpan("monospace"));
            } else if (tag.equalsIgnoreCase("center")) {
                end(output, Center.class, true, new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER));
            } else if (tag.equalsIgnoreCase("s") || tag.equalsIgnoreCase("strike")) {
                end(output, Strike.class, false, new StrikethroughSpan());
            } else if (tag.equalsIgnoreCase("table")) {
                tableTagLevel--;

                // When we're back at the root-level table
                if (tableTagLevel == 0) {
                    final String tableHtml = tableHtmlBuilder.toString();

                    ClickableTableSpan myClickableTableSpan = null;
                    if (clickableTableSpan != null) {
                        myClickableTableSpan = clickableTableSpan.newInstance();
                        myClickableTableSpan.setTableHtml(tableHtml);
                    }

                    DrawTableLinkSpan myDrawTableLinkSpan = null;
                    if (drawTableLinkSpan != null) {
                        myDrawTableLinkSpan = drawTableLinkSpan.newInstance();
                    }

                    end(output, Table.class, false, myDrawTableLinkSpan, myClickableTableSpan);
                } else {
                    end(output, Table.class, false);
                }
            } else if (tag.equalsIgnoreCase("tr")) {
                end(output, Tr.class, false);
            } else if (tag.equalsIgnoreCase("th")) {
                end(output, Th.class, false);
            } else if (tag.equalsIgnoreCase("td")) {
                end(output, Td.class, false);
            }
        }

        storeTableTags(opening, tag);
    }


    private void storeTableTags(boolean opening, String tag) {
        if (tableTagLevel > 0 || tag.equalsIgnoreCase("table")) {
            tableHtmlBuilder.append("<");
            if (!opening) {
                tableHtmlBuilder.append("/");
            }
            tableHtmlBuilder
                    .append(tag.toLowerCase())
                    .append(">");
        }
    }


    private void start(Editable output, Object mark) {
        int len = output.length();
        output.setSpan(mark, len, len, Spannable.SPAN_MARK_MARK);

        if (HtmlTextView.DEBUG) {
            Log.d(HtmlTextView.TAG, "len: " + len);
        }
    }


    private void end(Editable output, Class kind, boolean paragraphStyle, Object... replaces) {
        Object obj = getLast(output, kind);
        // start of the tag
        int where = output.getSpanStart(obj);
        // end of the tag
        int len = output.length();

        // If we're in a table, then we need to store the raw HTML for later
        if (tableTagLevel > 0) {
            final CharSequence extractedSpanText = extractSpanText(output, kind);
            tableHtmlBuilder.append(extractedSpanText);
        }

        output.removeSpan(obj);

        if (where != len) {
            int thisLen = len;
            // paragraph styles like AlignmentSpan need to end with a new line!
            if (paragraphStyle) {
                output.append("\n");
                thisLen++;
            }
            for (Object replace : replaces) {
                output.setSpan(replace, where, thisLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            if (HtmlTextView.DEBUG) {
                Log.d(HtmlTextView.TAG, "where: " + where);
                Log.d(HtmlTextView.TAG, "thisLen: " + thisLen);
            }
        }
    }


    private CharSequence extractSpanText(Editable output, Class kind) {
        final Object obj = getLast(output, kind);
        // start of the tag
        final int where = output.getSpanStart(obj);
        // end of the tag
        final int len = output.length();

        final CharSequence extractedSpanText = output.subSequence(where, len);
        output.delete(where, len);
        return extractedSpanText;
    }


    private static Object getLast(Editable text, Class kind) {
        Object[] objs = text.getSpans(0, text.length(), kind);
        if (objs.length == 0) {
            return null;
        } else {
            for (int i = objs.length; i > 0; i--) {
                if (text.getSpanFlags(objs[i - 1]) == Spannable.SPAN_MARK_MARK) {
                    return objs[i - 1];
                }
            }
            return null;
        }
    }

    public void setClickableTableSpan(ClickableTableSpan clickableTableSpan) {
        this.clickableTableSpan = clickableTableSpan;
    }

    public void setDrawTableLinkSpan(DrawTableLinkSpan drawTableLinkSpan) {
        this.drawTableLinkSpan = drawTableLinkSpan;
    }
} 
