package com.jwetherell.augmented_reality.ui.objects;

import java.text.BreakIterator;
import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;


/**
 * This class extends PaintableObject to draw text with a box surrounding it.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PaintableBoxedText extends PaintableObject {

    private float width = 0, height = 0;
    private float areaWidth = 0, areaHeight = 0;
    private ArrayList<CharSequence> lineList = null;
    private float lineHeight = 0;
    private float maxLineWidth = 0;
    private float pad = 0;

    private CharSequence txt = null;
    private float fontSize = 12;
    private int borderColor = Color.rgb(255, 255, 255);
    private int backgroundColor = Color.argb(160, 0, 0, 0);
    private int textColor = Color.rgb(255, 255, 255);

    public PaintableBoxedText(CharSequence txtInit, float fontSizeInit, float maxWidth) {
        this(txtInit, fontSizeInit, maxWidth, Color.rgb(255, 255, 255), Color.argb(128, 0, 0, 0), Color.rgb(255, 255, 255));
    }

    public PaintableBoxedText(CharSequence txtInit, float fontSizeInit, float maxWidth, int borderColor, int bgColor, int textColor) {
        set(txtInit, fontSizeInit, maxWidth, borderColor, bgColor, textColor);
    }

    /**
     * Set this objects parameters. This should be used instead of creating new
     * objects.
     * 
     * @param txtInit
     *            CharSequence to paint.
     * @param fontSizeInit
     *            Font size to use.
     * @param maxWidth
     *            max width of the text.
     * @param borderColor
     *            Color of the border.
     * @param bgColor
     *            Background color of the surrounding box.
     * @param textColor
     *            Color of the text.
     * @throws NullPointerException
     *             if String param is NULL.
     */
    public void set(CharSequence txtInit, float fontSizeInit, float maxWidth, int borderColor, int bgColor, int textColor) {
        if (txtInit == null) throw new NullPointerException();

        this.borderColor = borderColor;
        this.backgroundColor = bgColor;
        this.textColor = textColor;
        this.pad = getTextAsc();

        set(txtInit, fontSizeInit, maxWidth);
    }

    /**
     * Set this objects parameters. This should be used instead of creating new
     * objects. Note: This uses previously set or default values for border
     * color, background color, and text color.
     * 
     * @param txtInit
     *            CharSequence to paint.
     * @param fontSizeInit
     *            Font size to use.
     * @param maxWidth
     *            max width of the text.
     * @throws NullPointerException
     *             if String param is NULL.
     */
    public void set(CharSequence txtInit, float fontSizeInit, float maxWidth) {
        if (txtInit == null) throw new NullPointerException();

        try {
            prepTxt(txtInit, fontSizeInit, maxWidth);
        } catch (Exception ex) {
            ex.printStackTrace();
            prepTxt("TEXT PARSE ERROR", 12, 200);
        }
    }

    private void prepTxt(CharSequence txtInit, float fontSizeInit, float maxWidth) {
        if (txtInit == null) throw new NullPointerException();

        setFontSize(fontSizeInit);

        txt = txtInit;
        fontSize = fontSizeInit;
        areaWidth = maxWidth - pad;
        lineHeight = getTextAsc() + getTextDesc();

        if (lineList == null) lineList = new ArrayList<CharSequence>();
        else lineList.clear();

        BreakIterator boundary = BreakIterator.getWordInstance();
        boundary.setText(txt.toString());

        int start = boundary.first();
        int end = boundary.next();
        int prevEnd = start;
        while (end != BreakIterator.DONE) {
            CharSequence line = txt.subSequence(start, end);
            CharSequence prevLine = txt.subSequence(start, prevEnd);
            float lineWidth = getTextWidth(line, 0, line.length());

            if (lineWidth > areaWidth) {
                // If the first word is longer than lineWidth
                // prevLine is empty and should be ignored
                if (prevLine.length() > 0) lineList.add(prevLine);

                start = prevEnd;
            }

            prevEnd = end;
            end = boundary.next();
        }
        CharSequence line = txt.subSequence(start, prevEnd);
        lineList.add(line);

        maxLineWidth = 0;
        for (CharSequence seq : lineList) {
        	float lineWidth = getTextWidth(seq, 0 ,seq.length());
            if (maxLineWidth < lineWidth) maxLineWidth = lineWidth;
        }
        areaWidth = maxLineWidth;
        areaHeight = lineHeight * lineList.size();

        width = areaWidth + pad * 2;
        height = areaHeight + pad * 2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paint(Canvas canvas) {
        if (canvas == null) throw new NullPointerException();

        setFontSize(fontSize);

        setFill(true);
        setColor(backgroundColor);
        paintRoundedRect(canvas, 0, 0, width, height);

        setFill(false);
        setColor(borderColor);
        paintRoundedRect(canvas, 0, 0, width, height);

        int i=0;
        for (CharSequence line : lineList) {
            setFill(true);
            setStrokeWidth(0);
            setColor(textColor);
            paintText(canvas, pad, pad + lineHeight * i + getTextAsc(), line, 0, line.length());
            i++;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getWidth() {
        return width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getHeight() {
        return height;
    }
}
