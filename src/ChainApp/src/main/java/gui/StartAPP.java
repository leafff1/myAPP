package gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;

public class StartAPP extends JFrame{
    public JFrame firstFrame = this;
    	public StartAPP() {
		this.setSize(500, 300);
		this.setTitle("账单管理应用");
		init();
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

    private void init() {
        this.setLayout(new BorderLayout());

		JTabbedPane maintab = new JTabbedPane(JTabbedPane.TOP);
		JPanel  loadPanel = new JPanel();

		loadPanel.setLayout(new BorderLayout());
		JPanel loadContent = new JPanel();
		Box privatekey = Box.createVerticalBox();
		JLabel privatekeyLable = new JLabel("用户名:");
		JTextField privatekeyText = new JTextField(20);
		privatekey.add(Box.createVerticalStrut((int) (this.getHeight()*0.2)));
		privatekey.add(privatekeyLable);
		privatekey.add(privatekeyText);
		privatekey.add(Box.createVerticalGlue());
		loadContent.add(privatekey);
		loadPanel.add(loadContent);
		JButton loadButton = new JButton("登录");
		loadButton.setPreferredSize(new Dimension(100,30));
		Box privatekey2 = Box.createHorizontalBox();
		privatekey2.add(Box.createHorizontalGlue());
		privatekey2.add(loadButton);
		privatekey2.add(Box.createHorizontalGlue());
		loadPanel.add(privatekey2, BorderLayout.SOUTH);
		
		maintab.add("登录", loadPanel);

		this.add(maintab);

		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainAPP mainapp;
				try {
					mainapp = new MainAPP(firstFrame, privatekeyText.getText());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
			}
		});
    }

}