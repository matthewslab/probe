/**
 * Copyright 2019 Lin He, Megan L. Matthews
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package edu.upenn.sas.matthews.ms.view;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.CharacterIterator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class MSAnnotator {

    final int maxCharHeight = 18;
    final int minFontSize = 6;
    final Color bg = Color.WHITE;
    final Color fg = Color.BLACK;
    final Color red = Color.RED;
    final Color blue = Color.BLUE;
    final float edgeWidth = 60;
    final float edgeHeight = 60;
    int topNumToShow = 10;
    FontMetrics fontMetrics;

    public static void main(String[] args) throws IOException {
        String outFile = "C:/Users/l22he/tmp.png";
        double[] mz = new double[]{134.3, 345.5, 445.6887, 1840.21, 1856.33};
        double[] h = new double[]{1314.3, 345.5, 1445.6, 6278.5, 2023.33};
        ArrayList<Annotation> alist = new ArrayList<Annotation>();
        AttributedString as = new AttributedString("HexNAc1(307)1");
        alist.add(new Annotation(345.5f, 345.5f * 100 / 6278.5f, as, true, true));
        as = new AttributedString("c2-H");
        alist.add(new Annotation(464.5f, 664.5f * 100 / 6278.5f, as, true, true));
        as = new AttributedString("c5[2+]");
        alist.add(new Annotation(564.5f, 1264.5f * 100 / 6278.5f, as, true, true));
        as = new AttributedString("pep-HexNAc2Hex3");
        alist.add(new Annotation(678.5f, 100, as, false, false));
        as = new AttributedString("pep-HexNAc2Hex3");
        alist.add(new Annotation(1023.3322f, 1445.6f * 100 / 6278.5f, as, false, false));
        as = new AttributedString("pep-HexNAc2Hex5Fuc1");
        alist.add(new Annotation(1840.21f, 6278.5f * 100 / 6278.5f, as, false, false));
        as = new AttributedString("pep-HexNAc2Hex6");
        alist.add(new Annotation(1856.33f, 2023.33f * 100 / 6278.5f, as, false, false));

        BufferedImage bi = new BufferedImage(2000, 600, BufferedImage.TYPE_INT_BGR);
        MSAnnotator msv = new MSAnnotator();
        msv.drawSpectrum(bi, 100, 2, mz, h, alist);        
        ImageIO.write(bi, "png", new File(outFile));
    }

    private FontMetrics pickFont(Graphics2D g2, String longString, int xSpace) {
        boolean fontFits = false;
        Font font = g2.getFont();
        fontMetrics = g2.getFontMetrics();
        int size = font.getSize();
        String name = font.getName();
        int style = font.getStyle();

        while (fontFits) {
            System.out.println(name);
            if ((fontMetrics.getHeight() <= maxCharHeight)
                    && (fontMetrics.stringWidth(longString) <= xSpace)) {
                fontFits = true;
            } else {
                if (size <= minFontSize) {
                    fontFits = true;
                } else {
                    g2.setFont(font = new Font(name, style, --size));
                    fontMetrics = g2.getFontMetrics();
                }
            }
        }

        return fontMetrics;
    }
    

    public void drawSpectrum(BufferedImage bi, double precMz, int precZ, double[] mz,
                             double[] h, ArrayList<Annotation> alist) {
    	drawSpectrum(bi, precMz, precZ, mz, h, alist, 1);
    }

    public void drawSpectrum(BufferedImage bi, double precMz, int precZ, double[] mz,
                             double[] h, ArrayList<Annotation> alist, int scale) {
        Graphics2D g2 = bi.createGraphics();
        g2.setBackground(bg);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        int gridWidth = bi.getWidth();
        int gridHeight = bi.getHeight();

        g2.setPaint(bg);
        g2.fillRect(0, 0, gridWidth, gridHeight);
        g2.setPaint(fg);
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));

        fontMetrics = pickFont(g2, "Filled and Stroked GeneralPath", gridWidth);
        String errMsg = "Enlarge to show the spectrum";
        if (gridWidth <= 2 * edgeWidth + fontMetrics.stringWidth(errMsg)
                || gridHeight <= 4 * edgeHeight) {
            g2.drawString(errMsg, edgeWidth, edgeHeight + fontMetrics.getHeight());
            return;
        }

        float rectWidth = gridWidth - 2 * edgeWidth;
        float rectHeight = gridHeight - 2 * edgeHeight;

        // the origin of the spectrum;
        float originX = edgeWidth;
        float originY = edgeHeight + rectHeight;

        // draw x and y axis;
        Rectangle2D xAxis = new Rectangle2D.Double(originX, originY - 1, rectWidth, 1);
        g2.draw(xAxis);
        g2.fill(xAxis);
        Rectangle2D yAxis = new Rectangle2D.Double(originX, originY - rectHeight, 1, rectHeight);
        g2.draw(yAxis);
        g2.fill(yAxis);
        
        // draw labels for x and y axis;
        float xAxisLabelX = originX + rectWidth + fontMetrics.getWidths()[0] / 2;
        float xAxisLabelY = gridHeight - edgeHeight + fontMetrics.getHeight() / 4;
        g2.drawString("m/z", xAxisLabelX, xAxisLabelY);

        float yAxisLabelX = 6;
        float yAxisLabelY = edgeHeight - 2 * fontMetrics.getHeight() / 3;
        g2.drawString("Intensity (%)", yAxisLabelX, yAxisLabelY);

        // draw x axis tick marks and units;
        int peakNum = mz.length;
        int majorTickUnit = 500;
        int mediaTickUnit = 100;
        int minorTickUnit = 50;
//        int minTickMark = (int) mz[0] / majorTickUnit * 100;
//        minTickMark = minTickMark < majorTickUnit ? majorTickUnit : minTickMark;
        int minTickMark = minorTickUnit;
        int maxTickMark = ((int) mz[peakNum - 1] / majorTickUnit + 1) * majorTickUnit;
        int majorTickMarkLength = 5;
        int mediaTickMarkLength = 3;
        int minorTickMarkLength = 2;
        float xMinorSpaceLength = (rectWidth - edgeWidth) / maxTickMark;
        for (int i = minTickMark; i * xMinorSpaceLength < rectWidth; i++) {
            if (i % majorTickUnit == 0) {
                g2.draw(new Line2D.Double(originX + i * xMinorSpaceLength, originY,
                        originX + i * xMinorSpaceLength, originY + majorTickMarkLength));
                String unitLabel = String.valueOf(i);
                g2.drawString(unitLabel,
                        originX + i * xMinorSpaceLength - fontMetrics.stringWidth(unitLabel) / 2,
                        originY + fontMetrics.getHeight());
            } else if (i % mediaTickUnit == 0) {
                g2.draw(new Line2D.Double(originX + i * xMinorSpaceLength, originY,
                        originX + i * xMinorSpaceLength, originY + mediaTickMarkLength));
            } else if (i % minorTickUnit == 0) {
                g2.draw(new Line2D.Double(originX + i * xMinorSpaceLength, originY,
                        originX + i * xMinorSpaceLength, originY + minorTickMarkLength));
            }
        }

     // draw y axis tick marks and units;
        float yMinorSpaceLength = rectHeight / 100;
        g2.draw(new Line2D.Double(originX, originY - rectHeight / 2,
                originX - majorTickMarkLength, originY - rectHeight / 2));
        String unitLabel = String.valueOf(50 / scale);
        g2.drawString(unitLabel,
                originX - fontMetrics.stringWidth(unitLabel) - fontMetrics.getWidths()[0] / 4 - majorTickMarkLength,
                originY - rectHeight / 2 + fontMetrics.getHeight() / 4);

        g2.draw(new Line2D.Double(originX, originY - rectHeight,
                originX - majorTickMarkLength, originY - rectHeight));
        unitLabel = String.valueOf(100 / scale);
        g2.drawString(unitLabel,
                originX - fontMetrics.stringWidth(unitLabel) - fontMetrics.getWidths()[0] / 4 - majorTickMarkLength,
                originY - rectHeight + fontMetrics.getHeight() / 4);

        // draw precursor m/z and charge;
        g2.setColor(Color.black);
        String precText = "Precursor m/z=" + precMz + " z=" + precZ;
        float textX = originX + 5 + fontMetrics.stringWidth("Intensity (%)");
        float textY = yAxisLabelY;
        g2.drawString(precText, textX, textY);
            
        // calculate relative intensities;
        double[] ri = new double[peakNum];
        double maxH = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < peakNum; i++) {
            if (maxH < h[i]) {
                maxH = h[i];
            }
        }
        maxH = (maxH <= 0) ? 100 : maxH;
        for (int i = 0; i < peakNum; i++) {
            ri[i] = h[i] / maxH * 100 * scale;
            if (ri[i] > 100) {
            	ri[i] = 100;
            }
        }
        int topNum = (topNumToShow < peakNum) ? topNumToShow : peakNum;
        double[] tmpRi = new double[peakNum];
        System.arraycopy(ri, 0, tmpRi, 0, peakNum);
        Arrays.sort(tmpRi);
        double lowestRiToLabel = tmpRi[peakNum - topNum];

        // draw peaks;
        DecimalFormat df = new DecimalFormat("##.##");
        for (int i = 0; i < peakNum; i++) {
            g2.draw(new Line2D.Double(originX + mz[i] * xMinorSpaceLength, originY,
                    originX + mz[i] * xMinorSpaceLength, originY - ri[i] * yMinorSpaceLength));
            if (ri[i] >= lowestRiToLabel) {
                unitLabel = df.format(mz[i]);
                g2.setColor(Color.gray);
                g2.drawString(unitLabel,
                        (float) (originX + mz[i] * xMinorSpaceLength - fontMetrics.stringWidth(unitLabel) / 2),
                        (float) (originY - ri[i] * yMinorSpaceLength - 1));
                g2.setColor(fg);
            }
        }

        // annotate the spectrum;
        for (Annotation anno : alist) {
        	if (anno.text == null) {
        		continue;
        	}
            // re-draw the peak using color pen;
        	if (anno.isNTerm) {
        		g2.setColor(blue);
        	} else {
        		g2.setColor(red);
        	}
            double scaledH = anno.h * scale;
        	scaledH = scaledH > 100 ? 100 : scaledH;
            g2.draw(new Line2D.Double(originX + anno.mz * xMinorSpaceLength, originY,
                    originX + anno.mz * xMinorSpaceLength, originY - scaledH * yMinorSpaceLength));

            // draw the label;
            AttributedString annoText = anno.text;
            //* draw annotation text vertically; 
            {
                double x = originX + anno.mz * xMinorSpaceLength + fontMetrics.getHeight() / 4;
                double y = originY - anno.h * yMinorSpaceLength - fontMetrics.getHeight();
                AttributedCharacterIterator annoIter = annoText.getIterator();
                int prevCloseNum = 0;
                for (char c = annoIter.first(); c != CharacterIterator.DONE; c = annoIter.next()) {
                  	int i = annoIter.getIndex();
                  	if (c == '[' || c == '(') {
                  		prevCloseNum++;
                  	} else if (c == ']' || c == ')') {
                  		prevCloseNum--;
                  	}

              		annoText.addAttribute(TextAttribute.FAMILY, Font.SANS_SERIF, i, i + 1);
              		annoText.addAttribute(TextAttribute.SIZE, new Float(30), i, i + 1);
              		annoText.addAttribute(TextAttribute.TRANSFORM, -Math.PI / 2, i, i + 1);
                  	if (prevCloseNum == 0 && c >= '0' && c <= '9') {
                  		annoText.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, 
                  				i, i + 1);
                  	}
                }
                Rectangle2D annoRec = fontMetrics.getStringBounds(annoIter, 0, annoIter.getEndIndex(), g2);
                if (y < annoRec.getWidth() + edgeHeight) {
                    x -= fontMetrics.getHeight() / 2;
                    y = (float) (1.5f * edgeHeight + annoRec.getWidth());
                }

                g2.rotate(-Math.PI / 2);
                g2.drawString(annoText.getIterator(), (float) (-y), (float) x);
                g2.rotate(Math.PI / 2);
            }

            g2.setColor(fg);
            if (anno.isSpecial) {
                // draw special annotation text;
                g2.setColor(Color.BLUE);
                AffineTransform at = new AffineTransform();
                
                // draw text;
//                textX = originX + anno.mz * xMinorSpaceLength - fontMetrics.stringWidth(anno.text.toString()) / 2;
//                textY = originY + 2 * fontMetrics.getHeight();
//                g2.drawString(annoText.getIterator(), textX, textY);
                
                // draw arrow;
                Polygon arrowHead = new Polygon();  
                arrowHead.addPoint(0, 0);
                arrowHead.addPoint(-15, 15);
                arrowHead.addPoint(15, 15);
                at.translate(originX + anno.mz * xMinorSpaceLength, originY);
                Graphics2D g = (Graphics2D) g2.create();
                g.setTransform(at); 
                g.draw(arrowHead);
                g.fill(arrowHead);
                g.dispose();
            }
        }
        g2.dispose();
    }

}
