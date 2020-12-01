package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.io.Serializable;

import core.GameObject;

public class Projectile extends GameObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7209519805019912495L;
	Color color = Color.WHITE;
	Dimension size = new Dimension(5, 5);
	
	@Override
    public boolean draw(Graphics g) {
	// super
	if (super.draw(g)) {
	    g.setColor(color);
	    g.fillOval(position.x, position.y, size.width, size.height);
	    g.setColor(Color.WHITE);
	}
	return true;
    }
	
}
