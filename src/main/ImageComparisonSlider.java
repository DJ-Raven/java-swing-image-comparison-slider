package main;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ImageComparisonSlider extends JPanel {

    public Icon getImage1() {
        return image1;
    }

    public void setImage1(Icon image1) {
        this.image1 = image1;
        repaint();
    }

    public Icon getImage2() {
        return image2;
    }

    public void setImage2(Icon image2) {
        this.image2 = image2;
        repaint();
    }

    public SliderType getType() {
        return type;
    }

    public void setType(SliderType type) {
        this.type = type;
        repaint();
    }

    public float getSliderPosition() {
        return sliderPosition;
    }

    public void setSliderPosition(float sliderPosition) {
        this.sliderPosition = sliderPosition;
        repaint();
    }

    public int getButtonSize() {
        return buttonSize;
    }

    public void setButtonSize(int buttonSize) {
        this.buttonSize = buttonSize;
        repaint();
    }

    public int getLineSize() {
        return lineSize;
    }

    public void setLineSize(int lineSize) {
        this.lineSize = lineSize;
        repaint();
    }

    public Point getButtonLocation() {
        return buttonLocation;
    }

    public void setButtonLocation(Point buttonLocation) {
        this.buttonLocation = buttonLocation;
        repaint();
    }

    private Icon image1;
    private Icon image2;
    private SliderType type;
    private float sliderPosition = 0.5f;  //  0.5f = 50%
    private int buttonSize = 20;
    private int lineSize = 2;
    private Point buttonLocation;
    private boolean selected;

    public ImageComparisonSlider() {
        //  For remove background color
        setOpaque(false);
        type = SliderType.HORIZONTAL;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                if (SwingUtilities.isLeftMouseButton(me)) {
                    selected = isMouseOver(me.getPoint());
                }
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                if (SwingUtilities.isLeftMouseButton(me)) {
                    selected = false;
                }
            }

        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent me) {
                if (isMouseOver(me.getPoint())) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }

            @Override
            public void mouseDragged(MouseEvent me) {
                if (SwingUtilities.isLeftMouseButton(me)) {
                    if (selected) {
                        changeSliderPosition(me.getPoint());
                    }
                }
            }

        });
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        if (image1 != null && image2 != null) {
            Graphics2D g2 = (Graphics2D) grphcs;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            calculateButtonLocation();
            Rectangle size = getAutoSize(image2);
            int with = getWidth();
            int height = getHeight();
            //  Paint Image 2
            g2.drawImage(toImage(image2), size.x, size.y, size.width, size.height, null);
            //  Paint Image 1
            int position = converSliderposition();
            if (position > 0) {
                if (type == SliderType.HORIZONTAL) {
                    BufferedImage img = new BufferedImage(position, height, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = img.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g.drawImage(toImage(image1), size.x, size.y, size.width, size.height, null);
                    g2.drawImage(img, 0, 0, null);
                } else {
                    BufferedImage img = new BufferedImage(with, position, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = img.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g.drawImage(toImage(image1), size.x, size.y, size.width, size.height, null);
                    g2.drawImage(img, 0, 0, null);
                }
            }
            //  Paint Slide
            int lineLocation = lineSize / 2;
            g2.setColor(new Color(0, 0, 0, 150));
            if (type == SliderType.HORIZONTAL) {
                g2.fillRect(position - lineLocation, 0, lineSize, height);
                g2.setColor(new Color(42, 198, 35, 150));
                g2.fillOval(buttonLocation.x, buttonLocation.y, buttonSize, buttonSize);
            } else {
                g2.fillRect(0, position - lineLocation, with, lineSize);
                g2.setColor(new Color(42, 198, 35, 150));
                g2.fillOval(buttonLocation.x, buttonLocation.y, buttonSize, buttonSize);
            }
        }
        super.paintComponent(grphcs);
    }

    private Rectangle getAutoSize(Icon image) {
        int w = getWidth();
        int h = getHeight();
        int iw = image.getIconWidth();
        int ih = image.getIconHeight();
        double xScale = (double) w / iw;
        double yScale = (double) h / ih;
        double scale = Math.max(xScale, yScale);
        int width = (int) (scale * iw);
        int height = (int) (scale * ih);
        int x = (w - width) / 2;
        int y = (h - height) / 2;
        return new Rectangle(new Point(x, y), new Dimension(width, height));
    }

    private Image toImage(Icon icon) {
        return ((ImageIcon) icon).getImage();
    }

    private int converSliderposition() {
        int size;
        if (type == SliderType.HORIZONTAL) {
            size = getWidth();
        } else {
            size = getHeight();
        }
        return (int) (sliderPosition * size);
    }

    private void calculateButtonLocation() {
        int position = converSliderposition();
        int width = getWidth();
        int height = getHeight();
        if (type == SliderType.HORIZONTAL) {
            buttonLocation = new Point(position - (buttonSize / 2), height / 2 - (buttonSize / 2));
        } else {
            buttonLocation = new Point(width / 2 - (buttonSize / 2), position - (buttonSize / 2));
        }
    }

    private boolean isMouseOver(Point point) {
        if (image1 != null && image2 != null) {
            Ellipse2D.Double circle = new Ellipse2D.Double(buttonLocation.x, buttonLocation.y, buttonSize, buttonSize);
            return circle.contains(point);
        } else {
            return false;
        }
    }

    private void changeSliderPosition(Point point) {
        if (type == SliderType.HORIZONTAL) {
            sliderPosition = (float) point.getX() / getWidth();
        } else {
            sliderPosition = (float) point.getY() / getHeight();
        }
        if (sliderPosition < 0) {
            sliderPosition = 0;
        }
        if (sliderPosition > 1) {
            sliderPosition = 1;
        }
        repaint();
    }

    public static enum SliderType {
        HORIZONTAL, VERTICAl
    }
}
