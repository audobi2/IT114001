package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;

import core.GameObject;

public class Player extends GameObject implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -6088251166673414031L;
    protected String team = "";
	//change color based on team
    Color color = Color.WHITE;
    Point nameOffset = new Point(0, 5);
    int hits = 0;

    /**
     * Gets called by the game engine to draw the current location/size
     */
    @Override
    public boolean draw(Graphics g) {
	if (super.draw(g)) {
		if(team == "magenta") {
		    g.setColor(Color.MAGENTA);
	    } else if(team == "green") {
	    	g.setColor(Color.GREEN);
	    } else {
		    g.setColor(color);
	    }
		if(!isActive) {
			g.setColor(Color.GRAY);
		}
	    g.fillOval(position.x, position.y, size.width, size.height);
	    g.setColor(Color.WHITE);
	    g.setFont(new Font("Monospaced", Font.PLAIN, 12));
	    g.drawString("[" + team + "] " + name, position.x + nameOffset.x, position.y + nameOffset.y);
	}
	return true;
    }

    public void setTeam(String team) {
    	this.team = team;
        }

    public String getTeam() {
    	return this.team;
     }
    
    @Override
    public String toString() {
	return String.format("Name: %s, p: (%d,%d), s: (%d, %d), d: (%d, %d), isAcitve: %s", name, position.x,
		position.y, speed.x, speed.y, direction.x, direction.y, isActive);
    }
}
