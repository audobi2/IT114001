package client;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class User extends JPanel {
    private String name;
    private JTextField nameField;

    public User(String name) {
	this.name = name;
	nameField = new JTextField(name);
	nameField.setEditable(false);
	this.setLayout(new BorderLayout());
	this.add(nameField);
    }

    public String getName() {
	return name;
    }
    
    public void setBackgroundColor(Color color) {
    	nameField.setBackground(color);
    }
}