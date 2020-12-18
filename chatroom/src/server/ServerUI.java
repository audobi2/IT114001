package server;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import client.User;

public class ServerUI extends JFrame {
	private static final long serialVersionUID = 1L;
	CardLayout card;
	ServerUI self;
	JPanel mainPanel;
    JPanel userPanel;
    JPanel statsPanel;
    static List<User> users = new ArrayList<User>();
    private final static Logger log = Logger.getLogger(ServerUI.class.getName());
    Dimension windowSize = new Dimension(500, 500);
	
    public ServerUI(String title) {
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	setPreferredSize(windowSize);
    	setLocationRelativeTo(null);
    	self = this;
    	setTitle(title);
    	card = new CardLayout();
    	setLayout(card);

    	mainPanel = new JPanel();
    	mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    	mainPanel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
    	JLabel test = new JLabel("Connected Users");
    	mainPanel.add(test);
    	this.add(mainPanel);
    	
    	createConnectedUsersPanel();
    	createServerStatsPanel();
    	
    	showUI();
    }
    
    void createConnectedUsersPanel() {
    	userPanel = new JPanel();
    	userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
    	userPanel.setAlignmentY(Component.TOP_ALIGNMENT);
    	JScrollPane scroll = new JScrollPane(userPanel);
    	scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    	scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

    	Dimension d = new Dimension(100, windowSize.height);
    	scroll.setPreferredSize(d);

    	mainPanel.getParent().getParent().getParent().add(scroll, BorderLayout.WEST);

    	for(int i = 0; i < users.size(); i++) {
    		User u = users.get(i);
    		Dimension p = new Dimension(userPanel.getSize().width, 30);
    		u.setPreferredSize(p);
    		u.setMinimumSize(p);
    		u.setMaximumSize(p);
    		u.setBackgroundColor(Color.decode("#ebeef0"));
    		userPanel.add(u);
    	}
    	
    	this.add(userPanel);
    }
    
    void createServerStatsPanel() {
    	statsPanel = new JPanel();
    	
    }
    
    void showUI() {
    	pack();
    	setVisible(true);
    }
    
	 public static void main(String[] args) {
		 for(int i = 0; i < 10; i++) {
			 users.add(new User(i+""));
		 }
		 
		 ServerUI ui = new ServerUI("Server UI");
		 if (ui != null) {
			    log.log(Level.FINE, "Server Started");
			}
	 }
	
}
