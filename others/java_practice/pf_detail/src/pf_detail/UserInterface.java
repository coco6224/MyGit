package pf_detail;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.omg.CORBA.Current;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class UserInterface {
	public JFrame current = new JFrame();
	
	//UI
	LoginInfo log_info;
	CreAccInfo creAcc_info;
	MainWindowInfo mainWin_info;
	
	//Initialize
	UserInterface(){
		log_info = new LoginInfo();
		current.setVisible(true);
	}
		
	//UI Info
	class MainWindowInfo{
		private JFrame mainWin;
		MainWindowInfo(){
			setMainWindow();
		}
		public void setMainWindow(){
			mainWin=new JFrame("Piggy Bank");
			mainWin.setSize(600,400);
			mainWin.setVisible(true);
			
			JButton temp = new JButton("temp");
			temp.addActionListener(new TempClicked());
			temp.setBounds(118,200,100,40);
			
			Container c = mainWin.getContentPane();
			c.setLayout(null);
			c.add(temp);
		}
		class TempClicked implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				 JLabel a = new JLabel("A");
				 Container c = mainWin.getContentPane();
				 c.add(a);
				 mainWin.revalidate();
				 mainWin.repaint();
				 
			 }  
		}
	}
	class CreAccInfo{
		JTextField account;
		JTextField name;
		JPasswordField password;
		private JFrame createAcc;
		CreAccInfo(){
			setCreAcc();
		}
		public void setCreAcc(){
			createAcc = new JFrame("Sign Up");
			createAcc.setSize(250,500);
			createAcc.setDefaultCloseOperation(createAcc.EXIT_ON_CLOSE);
			
			//title
			JLabel signUp = new JLabel(new ImageIcon("sign up.png"));
			signUp.setBounds(18,15,200,50);
			
			//TextField for acc & pw
			JLabel accLabel = new JLabel("Enter Account");
			account = new JTextField();
			JLabel pwLabel = new JLabel("Enter Password");
			password = new JPasswordField();
			JLabel nameLabel = new JLabel("Enter Name");
			name = new JTextField();
			accLabel.setBounds(18, 90, 200, 20);
			account.setBounds(18,110,200,40);
			pwLabel.setBounds(18, 170, 200, 20);
			password.setBounds(18,190,200,40);
			nameLabel.setBounds(18, 250, 200, 20);
			name.setBounds(18,270,200,40);
			
			//buttons
			JButton toSignUp = new JButton("Sign UP");
			toSignUp.addActionListener(new toSignUpClicked());
			toSignUp.setBounds(118,350,100,40);
			JButton log = new JButton("Login");
			log.addActionListener(new logClicked());
			log.setBounds(18,430,200,20);
			
			//container
			Container c = createAcc.getContentPane();
			c.setLayout(null);
			c.add(signUp);
			c.add(accLabel);
			c.add(account);
			c.add(pwLabel);
			c.add(password);
			c.add(nameLabel);
			c.add(name);
			c.add(toSignUp);
			c.add(log);
			current=createAcc;
		}
		class logClicked implements ActionListener {
			 public void actionPerformed(ActionEvent e) {
				 current.setVisible(false);
				 log_info = new LoginInfo();
				 current.setVisible(true);
			 }  
		}
		class toSignUpClicked implements ActionListener {
			 public void actionPerformed(ActionEvent e) {
				 if(account.getText().length()==0){
					 JOptionPane.showMessageDialog(null,"Please enter account", "Sign up failed",JOptionPane.INFORMATION_MESSAGE);
				 }else if (password.getText().length()==0){
					 JOptionPane.showMessageDialog(null,"Please enter password", "Sign up failed",JOptionPane.INFORMATION_MESSAGE);
				 }else if (name.getText().length()==0){
					 JOptionPane.showMessageDialog(null,"Please enter name", "Sign up failed",JOptionPane.INFORMATION_MESSAGE);
				 }else{
					 try{
						 //connect
						 Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "rita80221");
						 
						 //query
						 PreparedStatement ps = c.prepareStatement("select mpw from pf_detail.memberInfo where mac=?;");
						 ps.setString(1, account.getText());
						 ResultSet r =ps.executeQuery();
						 
						 //check account
						 if(r.next()){
							 JOptionPane.showMessageDialog(null,"Account "+account.getText()+" has already existed", "Sign up failed",JOptionPane.INFORMATION_MESSAGE);
						 }else{
							 //insert
							 ps=c.prepareStatement("insert into pf_detail.memberinfo(mac,mpw,mname) values(?,?,?)");
							 ps.setString(1, account.getText());
							 ps.setString(2, password.getText());
							 ps.setString(3, name.getText());
							 ps.executeUpdate();
							 ps.close();
							 //done
							 JOptionPane.showMessageDialog(null,"Sign up successfully!", "Signed Up",JOptionPane.INFORMATION_MESSAGE);
							 
							 //back to login
							 current.setVisible(false);
							 log_info = new LoginInfo();
							 current.setVisible(true);
						 }
					 }
					 catch(Exception err){
						 System.out.println(err.toString());
					 };
				 }
			 }  
		}
	}
	class LoginInfo{
		JTextField account;
		JPasswordField password;
		private JFrame login;
		LoginInfo(){
			setLogin();
		}
		public void setLogin(){
			login = new JFrame("Login");
			login.setSize(250,500);
			login.setDefaultCloseOperation(login.EXIT_ON_CLOSE);
			
			//logo
			JLabel logo = new JLabel(new ImageIcon("logo.png"));
			logo.setBounds(13,15,210,150);
			
			//TextField for acc & pw
			JLabel accLabel = new JLabel("Enter Account");
			account = new JTextField();
			JLabel pwLabel = new JLabel("Enter Password");
			password = new JPasswordField();
			accLabel.setBounds(18, 170, 200, 20);
			account.setBounds(18,190,200,40);
			pwLabel.setBounds(18, 250, 200, 20);
			password.setBounds(18,270,200,40);
			
			//Button to login/Create Account
			JButton toLogin = new JButton("Login");
			toLogin.addActionListener(new toLoginClicked());
			toLogin.setBounds(118,350,100,40);
			JButton CreAcc = new JButton("Sign Up");
			CreAcc.addActionListener(new CreAccClicked());
			CreAcc.setBounds(18,430,200,20);
			
			//Container
			Container c = login.getContentPane();
			c.setLayout(null);
			c.add(logo);
			c.add(accLabel);
			c.add(account);
			c.add(pwLabel);
			c.add(password);
			c.add(toLogin);
			c.add(CreAcc);
			current=login;
		}
		class CreAccClicked implements ActionListener {
			 public void actionPerformed(ActionEvent e) {
				 current.setVisible(false);
				 creAcc_info = new CreAccInfo();
				 current.setVisible(true);
			 }  
		}
		class toLoginClicked implements ActionListener {
			 public void actionPerformed(ActionEvent e) {
				 try{
					 //connect
					 Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "rita80221");
					 
					 //query
					 PreparedStatement ps = c.prepareStatement("select mpw from pf_detail.memberInfo where mac=?;");
					 ps.setString(1, account.getText());
					 ResultSet r =ps.executeQuery();
					 
					 //found account
					 if(r.next()){
						 //check password
						 if(r.getString("mpw").equals(password.getText())){
							 current.setVisible(false);
							 mainWin_info = new MainWindowInfo();
						 }else{
							 JOptionPane.showMessageDialog(null,"Wrong password!", "Login failed",JOptionPane.INFORMATION_MESSAGE);
						 }
					 }else{
						 JOptionPane.showMessageDialog(null,"Account "+account.getText()+" does not exist", "Login failed",JOptionPane.INFORMATION_MESSAGE);
					 }
				 }
				 catch(Exception err){
					 System.out.println(err.toString());
				 };
			 }  
		}
	}
}
