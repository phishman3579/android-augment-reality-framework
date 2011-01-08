package com.jwetherell.augmented_reality.ui.objects;

import java.text.BreakIterator;
import java.util.ArrayList;
import android.graphics.Canvas;
import android.graphics.Color;

public class PaintableBoxedText extends PaintableObject {
    private float width=0, height=0;
	private float areaWidth=0, areaHeight=0;
	private String[] lines = null;
	private float[] lineWidths = null;
	private float lineHeight = 0;
	private float maxLineWidth = 0;
	private float pad = 0;

	private String txt = null;
    private float fontSize = 12;
	private int borderColor = Color.rgb(255, 255, 255);
	private int backgroundColor = Color.argb(160, 0, 0, 0);
	private int textColor = Color.rgb(255, 255, 255);

	public PaintableBoxedText(String txtInit, float fontSizeInit, float maxWidth) {
		this(txtInit, fontSizeInit, maxWidth, Color.rgb(255, 255, 255), Color.argb(128, 0, 0, 0), Color.rgb(255, 255, 255));
	}

	public PaintableBoxedText(String txtInit, float fontSizeInit, float maxWidth, int borderColor, int bgColor, int textColor) {
		this.borderColor = borderColor;
		this.backgroundColor = bgColor;
		this.textColor = textColor;
		this.pad = getTextAsc();

		try {
			prepTxt(txtInit, fontSizeInit, maxWidth);
		} catch (Exception ex) {
			ex.printStackTrace();
			prepTxt("TEXT PARSE ERROR", 12, 200);
		}
	}

	private void prepTxt(String txtInit, float fontSizeInit, float maxWidth) {
		setFontSize(fontSizeInit);

		txt = txtInit;
		fontSize = fontSizeInit;
		areaWidth = maxWidth - pad * 2;
		lineHeight = getTextAsc() + getTextDesc();

		ArrayList<String> lineList = new ArrayList<String>();

		BreakIterator boundary = BreakIterator.getWordInstance();
		boundary.setText(txt);

		int start = boundary.first();
		int end = boundary.next();
		int prevEnd = start;
		while (end != BreakIterator.DONE) {
			String line = txt.substring(start, end);
			String prevLine = txt.substring(start, prevEnd);
			float lineWidth = getTextWidth(line);

			if (lineWidth > areaWidth) {
				// If the first word is longer than lineWidth 
				// prevLine is empty and should be ignored
				if(prevLine.length()>0) lineList.add(prevLine);

				start = prevEnd;
			}

			prevEnd = end;
			end = boundary.next();
		}
		String line = txt.substring(start, prevEnd);
		lineList.add(line);

		lines = new String[lineList.size()];
		lineWidths = new float[lineList.size()];
		lineList.toArray(lines);

		maxLineWidth = 0;
		for (int i = 0; i < lines.length; i++) {
			lineWidths[i] = getTextWidth(lines[i]);
			if (maxLineWidth < lineWidths[i])
				maxLineWidth = lineWidths[i];
		}
		areaWidth = maxLineWidth;
		areaHeight = lineHeight * lines.length;

		width = areaWidth + pad * 2;
		height = areaHeight + pad * 2;
	}

	public void paint(Canvas canvas) {
	    setFontSize(fontSize);

		setFill(true);
		setColor(backgroundColor);
		paintRect(canvas, 0, 0, width, height);

		setFill(false);
		setColor(borderColor);
		paintRect(canvas, 0, 0, width, height);
		
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			setFill(true);
			setStrokeWidth(0);
			setColor(textColor);
			paintText(canvas, pad, pad + lineHeight * i + getTextAsc(), line);
		}
	}

	@Override
	public float getWidth() {
		return width;
	}

	@Override
	public float getHeight() {
		return height;
	}
}
