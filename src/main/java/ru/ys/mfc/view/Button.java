package ru.ys.mfc.view;

import ru.ys.mfc.util.DrawingUtils;

import java.awt.*;

public class Button {
    private Rectangle bounds; // in Screen coordinates
    private String text;
    private ButtonType buttonType = ButtonType.NAV;
    private String id = "";
    private Graphics2D gfx;
    private Color textColor = Color.BLACK;
    private Color backgroundColor = Color.WHITE;

    public Button(Rectangle bounds, String text, ButtonType buttonType, String id) {
        this.bounds = bounds;
        this.text = text;
        this.buttonType = buttonType;
        this.id = id;
    }

    public Button(Graphics2D gfx, Rectangle bounds, String text, ButtonType buttonType, String id) {
        this.bounds = bounds;
        this.text = text;
        this.buttonType = buttonType;
        this.id = id;
        this.gfx = gfx;
    }

    public Button(Graphics2D gfx, Rectangle bounds, String text, ButtonType buttonType, String id, Color textColor, Color backgroundColor) {
        this.bounds = bounds;
        this.text = text;
        this.buttonType = buttonType;
        this.id = id;
        this.gfx = gfx;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
    }


    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ButtonType getButtonType() {
        return buttonType;
    }

    public void setButtonType(ButtonType buttonType) {
        this.buttonType = buttonType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Graphics2D getGfx() {
        return gfx;
    }

    public void setGfx(Graphics2D gfx) {
        this.gfx = gfx;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void draw() {
        if (gfx == null)
            return;
        Color color = gfx.getColor();
        Color bck = gfx.getBackground();
        gfx.setColor(backgroundColor);
        gfx.fillRoundRect((int) bounds.getX(),
                (int) bounds.getY(),
                (int) bounds.getWidth(),
                (int) bounds.getHeight(),
                40, 40);
        gfx.setColor(textColor);
//        gfx.drawRoundRect((int) bounds.getX(),
//                (int) bounds.getY(),
//                (int) bounds.getWidth(),
//                (int) bounds.getHeight(),
//                40, 40);
        DrawingUtils.drawLongStringBySpliting(gfx, text,
                (int) bounds.getX(),
                (int) bounds.getY(),
                (int) bounds.getWidth(),
                (int) bounds.getHeight(),
                true);
        gfx.setBackground(bck);
        gfx.setColor(color);
    }

    public boolean contains(int x, int y) {
        return bounds.contains(x, y);
    }

    public boolean contains(Point point) {
        return bounds.contains(point);
    }
}
