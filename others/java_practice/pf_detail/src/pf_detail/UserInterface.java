package pf_detail;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.event.*;

import javax.swing.table.DefaultTableModel; 
import javax.swing.*;

import org.omg.CORBA.Current;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class UserInterface {
	public JFrame current = new JFrame();
	//attribute
	String myAccount;
	String myName;
	int myId;
	
	//UI
	LoginInfo log_info;
	CreAccInfo creAcc_info;
	MainWindowInfo mainWin_info;
	JFrame addFrame;
	
	//Initialize
	UserInterface(){
		log_info = new LoginInfo();
		current.setVisible(true);
	}
		
	//UI Info
	class MainWindowInfo{
		JPanel op_pane;
		JPanel ginfo_pane;
		JComboBox group;
		JTextField account;
		int gnum;
		String gname;
		private JFrame mainWin;
		MainWindowInfo(){
			setMainWindow();
		}
		public void setMainWindow(){
			try{
				 //connect
				 Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "rita80221");
				 mainWin=new JFrame("Piggy Bank");
				 mainWin.setSize(700,500);
				 mainWin.setVisible(true);
				 PreparedStatement ps;
				 ResultSet r;
	
				 //Option Panel
				 op_pane = new JPanel();
				 op_pane.setBounds(15,15,130,470);
				 op_pane.setLayout(null);
				 
				 //logo
				 JLabel logo = new JLabel(new ImageIcon("mainWin.png"));
				 logo.setBounds(0,0,130,100);
				 op_pane.add(logo);
				 
				 //label-hello
				 JLabel hello = new JLabel("Hello, "+myName);
				 hello.setBounds(0,110,130,20);
				 op_pane.add(hello);
				 hello=new JLabel("Here are your Groups:");
				 hello.setBounds(0,140,130,20);
				 op_pane.add(hello);
					
				 //combobox-groupName
				 ps=c.prepareStatement("select gname from (pf_detail.groupinfo g join pf_detail.belong b on g.gid=b.gid)"
				 		+ "join pf_detail.memberinfo m on b.mid = m.mid where mac=?");
				 ps.setString(1, myAccount);
				 r=ps.executeQuery();
				 ArrayList<String> n=new ArrayList<String>();
				 while(r.next()){
					 n.add(r.getString("gname"));
				 }
				 group = new JComboBox(n.toArray());
				 group.setBounds(0,160,130,30);
				 op_pane.add(group);
				 
				 //button-ginfo
				 JButton ginfo = new JButton("Group Info");
				 ginfo.addActionListener(new GinfoClicked());
				 ginfo.setBounds(0,210,130,30);
				 op_pane.add(ginfo);
			 	
				 //button-Invite
				 JButton add = new JButton("Add member");
				 add.addActionListener(new AddClicked());
				 add.setBounds(0, 260,130,30);
				 op_pane.add(add);
				 
				 //
				 JButton quit = new JButton("Quit group");
				 quit.addActionListener(new QuitClicked());
				 quit.setBounds(0, 310,130,30);
				 op_pane.add(quit);
				 
			 	 Container cont = mainWin.getContentPane();
			 	 cont.setLayout(null);
				 cont.add(op_pane);
				 
				 //close
				 r.close();
				 ps.close();
				 c.close();
			}
			 catch(Exception err){
				 System.out.println(err.toString());
			 };
			
		}
		class GinfoClicked implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				setGinfoPanel(group.getSelectedItem().toString());
				mainWin.setContentPane(ginfo_pane);
				ginfo_pane.revalidate();
			 }  
		}
		class AddClicked implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				gname = group.getSelectedItem().toString();
				
				addFrame = new JFrame("Add member");
				addFrame.setSize(250, 150);
				
				//label
				JLabel message = new JLabel("Enter account to add him/her to ");
				message.setBounds(10,10,200,20);
				JLabel gname = new JLabel(group.getSelectedItem()+":", SwingConstants.RIGHT);
				gname.setFont(new Font("Arial", Font.PLAIN, 20));
				gname.setBounds(10,30,210,20);
				
				//textfield
				account = new JTextField();
				account.setBounds(10,60,150,30);
				
				//button
				JButton add = new JButton("Add");
				add.setBounds(165,60,60,30);
				add.addActionListener(new AddMemClicked());
				
				//Container
				Container c=addFrame.getContentPane();
				c.setLayout(null);
				c.add(message);
				c.add(gname);
				c.add(account);
				c.add(add);
				
				addFrame.setVisible(true);
			 }  
		}
		class QuitClicked implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				gname = group.getSelectedItem().toString();
				int res = JOptionPane.showConfirmDialog(mainWin,"Are you sure to quit "+gname+"?","Confirm",
                    JOptionPane.OK_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE);
				if(res==JOptionPane.OK_OPTION){
					try{
						Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "rita80221");
						PreparedStatement ps;
						ResultSet r;
						
						int myID;
						ps = c.prepareStatement("select mid from pf_detail.memberinfo where mac=?");
						ps.setString(1, myAccount);
						r=ps.executeQuery();						
						
						if(r.next()){
							myID=r.getInt("mid");
							ps=c.prepareStatement("select gid from pf_detail.groupinfo where gname=?");
							ps.setString(1, gname);
							r=ps.executeQuery();
							if(r.next()){
								gnum=r.getInt("gid");
								ps=c.prepareStatement("delete from pf_detail.belong where gid=? and mid=?");
								ps.setInt(1, gnum);
								ps.setInt(2,myID);
								ps.executeUpdate();
								ps=c.prepareStatement("delete from pf_detail.detail where dbelong=? and dpayer=?");
								ps.setInt(1, gnum);
								ps.setInt(2,myID);
								ps.executeUpdate();
							}
						}
						
						r.close();
						ps.close();
						c.close();
					}catch(Exception err){
						
					}
				}else{
					System.out.println("cancel");
				}
			}  
		}
		class AddMemClicked implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				if(account.getText().equals("")){
					JOptionPane.showMessageDialog(null,"Please enter account", "Add member failed",JOptionPane.INFORMATION_MESSAGE);
				}
				else{
					try{
						Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "rita80221");
						PreparedStatement ps;
						ResultSet r;
						
						int newID;
						ps = c.prepareStatement("select mid from pf_detail.memberinfo where mac=?");
						ps.setString(1, account.getText());
						r=ps.executeQuery();						
						
						if(r.next()){
							newID=r.getInt("mid");
							ps=c.prepareStatement("select gid from pf_detail.groupinfo where gname=?");
							ps.setString(1, gname);
							r=ps.executeQuery();
							if(r.next()){
								gnum=r.getInt("gid");
							}
							ps=c.prepareStatement("select * from pf_detail.belong where gid=? and mid=?");
							ps.setInt(1, gnum);
							ps.setInt(2, newID);
							r=ps.executeQuery();
							if(r.next()){
								JOptionPane.showMessageDialog(null,"Account "+account.getText()+" is already in the group", "Add member failed",JOptionPane.INFORMATION_MESSAGE);
							}
							else{
								ps=c.prepareStatement("insert into pf_detail.belong(gid,mid) values(?,?)");
								ps.setInt(1, gnum);
								ps.setInt(2, newID);
								ps.executeUpdate();
								JOptionPane.showMessageDialog(null,"Done!", "Add member successfully",JOptionPane.INFORMATION_MESSAGE);
								addFrame.setVisible(false);
								setGinfoPanel(gname);
								mainWin.setContentPane(ginfo_pane);
								ginfo_pane.revalidate();
							}
						}
						else{
							JOptionPane.showMessageDialog(null,"Account "+account.getText()+" does not exist.", "Add member failed",JOptionPane.INFORMATION_MESSAGE);
						}
						
						r.close();
						ps.close();
						c.close();
					}catch(Exception err){
						System.out.println(err.toString());
					}
				}
			 }  
		}
		
		private void setGinfoPanel(String gn){
			try{			
				//Var
				int numOfMem=0;
				int totalCost=0;
				int youvePayed=0;
				int youllGet=0;
				
				//connect
				Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "rita80221");
				PreparedStatement ps;
				ResultSet r;
				
				//show panel
				JPanel show_pane = new JPanel();
				show_pane.setBounds(160,15,510,430);
				show_pane.setLayout(null);
				//show_pane.setBackground(Color.white);
				
				//NAME
				JLabel gname=new JLabel(gn,2);
				gname.setFont(new Font("Arial", Font.BOLD+Font.ITALIC,32));
				gname.setBounds(10,0,525,40);
				show_pane.add(gname);
				
				//get gid
				ps=c.prepareStatement("select gid from pf_detail.groupinfo where gname=?");
				ps.setString(1, gn);
				r=ps.executeQuery();
				if(r.next()){
					gnum=r.getInt("gid");
				}
				
				//get group info
				JLabel groupinfo;
				
				//num of members
				ps=c.prepareStatement("select count(*) as c from pf_detail.belong where gid=?;");
				ps.setInt(1, gnum);
				r=ps.executeQuery();
				groupinfo=new JLabel("Number of members: ");
				groupinfo.setBounds(20,50,505,20);
				groupinfo.setFont(new Font("Arial", Font.PLAIN,20));
				show_pane.add(groupinfo);
				if(r.next()){
					numOfMem=r.getInt("c");
					groupinfo = new JLabel(""+numOfMem,SwingConstants.RIGHT);	
				}else{
					groupinfo = new JLabel("Cannot get number of members.",SwingConstants.RIGHT);	
				}
				groupinfo.setBounds(20,50,440,20);
				groupinfo.setFont(new Font("Arial", Font.PLAIN,20));
				show_pane.add(groupinfo);
				
				//total cost
				ps=c.prepareStatement("select sum(dnum) as c from pf_detail.detail where dbelong=?;");
				ps.setInt(1, gnum);
				r=ps.executeQuery();
				groupinfo = new JLabel("Total Cost: ");
				groupinfo.setBounds(20, 80, 505, 20);
				groupinfo.setFont(new Font("Arial", Font.PLAIN,20));
				show_pane.add(groupinfo);
				if(r.next()){
					totalCost = (-r.getInt("c"));
					groupinfo = new JLabel("NT "+totalCost,SwingConstants.RIGHT);
				}else{
					groupinfo = new JLabel("Cannot get total cost",SwingConstants.RIGHT);
				}
				groupinfo.setBounds(20, 80, 440, 20);
				groupinfo.setFont(new Font("Arial", Font.PLAIN,20));
				show_pane.add(groupinfo);
				
				//you've payed
				ps=c.prepareStatement("select sum(dnum) n from pf_detail.detail where dpayer=? and dbelong=?");
				ps.setInt(1, myId);
				ps.setInt(2, gnum);
				r=ps.executeQuery();
				groupinfo = new JLabel("You've payed: ");
				groupinfo.setBounds(20, 110, 505, 20);
				groupinfo.setFont(new Font("Arial", Font.PLAIN,20));
				show_pane.add(groupinfo);
				if(r.next()){
					youvePayed = -r.getInt("n");
					groupinfo = new JLabel("NT "+youvePayed,SwingConstants.RIGHT);
				}else{
					groupinfo = new JLabel("Cannot get the num of money you've payed",SwingConstants.RIGHT);
				}
				groupinfo.setBounds(20, 110, 440, 20);
				groupinfo.setFont(new Font("Arial", Font.PLAIN,20));
				show_pane.add(groupinfo);
				
				//you'll get
				if(youvePayed-(totalCost/2)>0){
					groupinfo = new JLabel("You will get NT "+(youvePayed-(totalCost/2))+" back.");
				}
				else if(youvePayed-(totalCost/2)==0){
					groupinfo = new JLabel("You've payed how much you need to pay");
				}else{
					groupinfo = new JLabel("You have to pay NT "+((totalCost/2)-youvePayed)+" more.");
				}
				groupinfo.setBounds(20, 140, 505, 20);
				groupinfo.setFont(new Font("Arial", Font.PLAIN,20));
				show_pane.add(groupinfo);
				
				//detail
				ps=c.prepareStatement("select ddate,ddetail,-dnum n,mname from "
						+ "pf_detail.detail join pf_detail.memberinfo on dpayer=mid "
						+"where dbelong = ?");
				ps.setInt(1,gnum);
				r=ps.executeQuery();
				ArrayList<String[]> detail=new ArrayList<String[]>();
				while(r.next()){
					ArrayList<String> temp = new ArrayList<String>();
					temp.add(r.getString("ddate"));
					temp.add(r.getString("ddetail"));
					temp.add(r.getString("n"));
					temp.add(r.getString("mname"));
					detail.add(temp.toArray(new String[temp.size()]));
				}
				String[] cols ={"Date", "Detail", "Cost", "Payer"};
				JTable detail_tb = new JTable(detail.toArray(new String[detail.size()][]),cols);
				JScrollPane withTitle =new JScrollPane(detail_tb);
				withTitle.setBounds(20,170,470,260);
				show_pane.add(withTitle);
				
				ginfo_pane = new JPanel();
				ginfo_pane.setLayout(null);
				ginfo_pane.add(op_pane);
				ginfo_pane.add(show_pane);
				
				r.close();
				ps.close();
				c.close();
			 }catch(Exception err){
				 System.out.println(err.toString());
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
						 r.close();
						 ps.close();
						 c.close();
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
					 PreparedStatement ps = c.prepareStatement("select mid, mpw, mname from pf_detail.memberInfo where mac=?;");
					 ps.setString(1, account.getText());
					 ResultSet r =ps.executeQuery();
					 
					 //found account
					 if(r.next()){
						 //check password
						 if(r.getString("mpw").equals(password.getText())){
							 myAccount=account.getText();
							 myName = r.getString("mname");
							 myId = r.getInt("mid");
							 //System.out.println(myName);
							 current.setVisible(false);
							 mainWin_info = new MainWindowInfo();
						 }else{
							 JOptionPane.showMessageDialog(null,"Wrong password!", "Login failed",JOptionPane.INFORMATION_MESSAGE);
							 account.setText("");
							 password.setText("");
						 }
					 }else{
						 JOptionPane.showMessageDialog(null,"Account "+account.getText()+" does not exist", "Login failed",JOptionPane.INFORMATION_MESSAGE);
						 account.setText("");
						 password.setText("");
					 }
					 r.close();
					 ps.close();
					 c.close();
				 }
				 catch(Exception err){
					 System.out.println(err.toString());
				 };
			 }  
		}
	}
}
