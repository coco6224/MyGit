package pf_detail;
import java.awt.*;
import java.util.ArrayList;
import java.awt.event.*;
import java.util.regex.*;
import java.sql.Date;
import java.util.* ;

import javax.swing.*;
import javax.swing.table.TableColumnModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class UserInterface {
	private boolean isInteger(String str) {    
	    Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");    
	    return pattern.matcher(str).matches();    
	}
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
				mainWin=new JFrame("Piggy Bank");
				 mainWin.setDefaultCloseOperation(mainWin.EXIT_ON_CLOSE);
				 mainWin.setSize(700,500);
				 mainWin.setVisible(true);
				setOpPane(); 
				
			 	 Container cont = mainWin.getContentPane();
			 	 cont.setLayout(null);
				 cont.add(op_pane);
			}
			 catch(Exception err){
				 System.out.println(err.toString());
			 };
			
		}
		private void setOpPane(){
			try{
				 //connect
				 Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "*");
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
				 hello.setBounds(0,135,130,20);
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
				 group.setBounds(0,155,130,30);
				 op_pane.add(group);
				 
				 //button-ginfo
				 JButton ginfo = new JButton("Group Info");
				 ginfo.addActionListener(new GinfoClicked());
				 ginfo.setBounds(0,225,130,30);
				 op_pane.add(ginfo);
			 	
				 //button-Invite
				 JButton add = new JButton("Add member");
				 add.addActionListener(new AddClicked());
				 add.setBounds(0, 260,130,30);
				 op_pane.add(add);
				 
				 //button-Quit
				 JButton quit = new JButton("Quit group");
				 quit.addActionListener(new QuitClicked());
				 quit.setBounds(0, 295,130,30);
				 op_pane.add(quit);

				 //button-AddDetail
				 JButton addDetail = new JButton("Add Detail");
				 addDetail.addActionListener(new AddDetailClicked());
				 addDetail.setBounds(0, 190,130,30); //315,130
				 op_pane.add(addDetail);
				 

				//button-refresh
				 JButton refresh = new JButton("Refresh");
				 refresh.addActionListener(new RefreshClicked());
				 refresh.setBounds(0, 335,130,30);
				 op_pane.add(refresh);
				 
				//button-createGroup
				 JButton createGroup = new JButton("Create group");
				 createGroup.addActionListener(new CreateGClicked());
				 createGroup.setBounds(0, 370,130,30);
				 op_pane.add(createGroup);
				
				//button-logout
				 JButton logOut = new JButton("log out");
				 logOut.addActionListener(new LogOutClicked());
				 logOut.setBounds(0, 405,130,30);
				 op_pane.add(logOut);
				 
				 //close
				 r.close();
				 ps.close();
				 c.close();
			}
			 catch(Exception err){
				 System.out.println(err.toString());
			 };
		}
		
		class CreateGClicked implements ActionListener{
			public void actionPerformed(ActionEvent e){
				try{
					String newG = JOptionPane.showInputDialog(null,"Enter Group Name:","Create Group",JOptionPane.QUESTION_MESSAGE);
					Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres","postgres","*");
					PreparedStatement ps = c.prepareStatement("insert into pf_detail.groupinfo(gname) values(?);");
					ps.setString(1, newG);
					ps.executeUpdate();
					
					ps=c.prepareStatement("select gid from pf_detail.groupinfo where gname=?;");
					ps.setString(1, newG);
					ResultSet r = ps.executeQuery();
						if(r.next()){
						ps = c.prepareStatement("insert into pf_detail.belong(mid,gid) values(?,?);");
						ps.setInt(1, myId);
						ps.setInt(2, r.getInt("gid"));
						ps.executeUpdate();
					}
					ps=c.prepareStatement("select gname from (pf_detail.groupinfo g join pf_detail.belong b on g.gid=b.gid)"
					 		+ "join pf_detail.memberinfo m on b.mid = m.mid where mac=?");
					 ps.setString(1, myAccount);
					r=ps.executeQuery();
					 ArrayList<String> n=new ArrayList<String>();
					 while(r.next()){
						 n.add(r.getString("gname"));
					 }
					 group.removeAllItems();
					 for(String temp:n){
						 group.addItem(temp);
					 }
					 c.close();
					 ps.close();
					 r.close();
				 }catch(Exception err){
					 System.out.println(err.toString());
				 }
			}
		}
		
		class LogOutClicked implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				mainWin.setVisible(false);
				log_info = new LoginInfo();
				current.setVisible(true);
			 }  
		}
		class AddDetailClicked implements ActionListener{
			JTextField year; 
			JTextField month;
			JTextField date;
			JTextField detail;
			JTextField num;
			JComboBox payer;
			public void actionPerformed(ActionEvent e) {
				gname = group.getSelectedItem().toString();
				
				JPanel addD_pane = new JPanel();
				addD_pane.setBounds(0,0,700,500);
				addD_pane.setLayout(null);
				
				JPanel show_pane = new JPanel();
				show_pane.setLayout(null);
				show_pane.setBounds(160,15,510,430);
				
				//NAME
				JLabel name=new JLabel(gname,2);
				name.setFont(new Font("Arial", Font.BOLD+Font.ITALIC,32));
				name.setBounds(10,0,525,40);
				show_pane.add(name);
								
				//DATE
				JLabel dateMessage = new JLabel("Date (yyyy-mm-dd):");
				dateMessage.setFont(new Font("Avril", Font.PLAIN, 20));
				dateMessage.setBounds(20,60,200,20);
				show_pane.add(dateMessage);
				
				dateMessage = new JLabel("(Blank for current date)");
				dateMessage.setFont(new Font("Avril", Font.PLAIN, 20));
				dateMessage.setBounds(20,85,400,20);
				show_pane.add(dateMessage);
				
				year = new JTextField();
				year.setBounds(210,60,50,20);
				show_pane.add(year);

				JLabel line = new JLabel("-");
				line.setFont(new Font("Avril", Font.PLAIN, 20));
				line.setBounds(265,60,15,20);
				show_pane.add(line);
				 
				month = new JTextField();
				month.setBounds(285,60,50,20);
				show_pane.add(month);
				
				line = new JLabel("-");
				line.setFont(new Font("Avril", Font.PLAIN, 20));
				line.setBounds(340,60,15,20);
				show_pane.add(line);

				date = new JTextField();
				date.setBounds(360,60,50,20);
				show_pane.add(date);
				
				//Detail
				JLabel detailMessage = new JLabel("Detail:");
				detailMessage.setFont(new Font("Avril", Font.PLAIN, 20));
				detailMessage.setBounds(20,125,70,20);
				show_pane.add(detailMessage);
				
				detail = new JTextField();
				detail.setBounds(90,120,350,30);
				show_pane.add(detail);
				
				//Num
				JLabel numMessage = new JLabel("Amount of money: NT ");
				numMessage.setFont(new Font("Avril", Font.PLAIN, 20));
				numMessage.setBounds(20,175,220,20);
				show_pane.add(numMessage);
				
				num = new JTextField();
				num.setBounds(220,170,220,30);
				show_pane.add(num);
				
				//Payer
				JLabel payerMessage = new JLabel("Payer:");
				payerMessage.setFont(new Font("Avril", Font.PLAIN, 20));
				payerMessage.setBounds(20,225,70,20);
				show_pane.add(payerMessage);
				
				try{
					Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "*");
					PreparedStatement ps = c.prepareStatement("select mname from pf_detail.belong natural join pf_detail.memberinfo natural join pf_detail.groupinfo where gname=?");
					ps.setString(1, gname);
					ResultSet r=ps.executeQuery();
					ArrayList<String>mem = new ArrayList<String>();
					while(r.next()){
						mem.add(r.getString("mname"));
					}
					
					payer = new JComboBox(mem.toArray());
					payer.setBounds(90,220,200,30);
					show_pane.add(payer);
					c.close();
					ps.close();
					r.close();
				}
				catch(Exception err){
					System.out.println(err.toString());
				}
				
				//Button addD
				JButton addD = new JButton("Add detail");
				addD.setFont(new Font("Avril", Font.PLAIN, 20));
				addD.setBounds(350,390,150,30);
				addD.addActionListener(new AddDClicked());
				show_pane.add(addD);
				
				addD_pane.add(op_pane);
				addD_pane.add(show_pane);
				mainWin.setContentPane(addD_pane);
				addD_pane.revalidate();
			 } 
  
			class AddDClicked implements ActionListener{
				public void actionPerformed(ActionEvent e) {
					if(detail.getText().equals("")){	//check detail not null
						JOptionPane.showMessageDialog(null,"Please enter detail", "Add detail failed",JOptionPane.INFORMATION_MESSAGE);
					}
					else if(num.getText().equals("")){	//check num not null
						JOptionPane.showMessageDialog(null,"Please enter amount of money", "Add detail failed",JOptionPane.INFORMATION_MESSAGE);
						
					}
					else{
						//check num is an int
						if(isInteger(num.getText())){
							int numint=Integer.parseInt(num.getText());
							if(year.getText().equals("")&&month.getText().equals("")&&date.getText().equals("")){
								try{
									Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres","postgres","*");
									PreparedStatement ps = c.prepareStatement("select gid from pf_detail.groupinfo where gname=?");
									ps.setString(1, gname);
									ResultSet r = ps.executeQuery();
									int gid;
									if(r.next()){
										gid=r.getInt("gid");
										ps = c.prepareStatement("select mid from pf_detail.memberinfo where mname=?");
										ps.setString(1, payer.getSelectedItem().toString());
										r =ps.executeQuery();
										if(r.next()){
											ps=c.prepareStatement("insert into pf_detail.detail(ddetail,dnum,dpayer,dupdateby,dbelong) "
													+"values(?,?,?,?,?)");
											ps.setString(1, detail.getText());
											ps.setInt(2, -numint);
											ps.setInt(3, r.getInt("mid"));
											ps.setInt(4, myId);
											ps.setInt(5, gid);
											ps.executeUpdate();
											setGinfoPanel(gname);
											mainWin.setContentPane(ginfo_pane);
											ginfo_pane.revalidate();
										}
									}
									c.close();
									ps.close();
									r.close();
								}
								catch(Exception err){
									System.out.println(err.toString());
								}
							}
							else if(year.getText().equals("")||month.getText().equals("")||date.getText().equals("")){
								JOptionPane.showMessageDialog(null,"Please enter complete date", "Add detail failed",JOptionPane.INFORMATION_MESSAGE);
							}
							else{
								if(isInteger(year.getText())&&isInteger(month.getText())&&isInteger(date.getText())){
									int yint=Integer.parseInt(year.getText());
									int mint=Integer.parseInt(month.getText());
									int dint=Integer.parseInt(date.getText());
									Boolean flag = false;
									if(yint>=1996&&yint<=9999){
										if(mint<=12&&mint>0){
											if(mint<=7){
												if((mint%2)==1&&dint>0&&dint<=31){
													flag=true;
												}else if(mint==2&&dint>0&&dint<=29){
													flag=true;
												}else if(mint!=2&&mint%2==0&&dint>0&&dint<=30){
													flag=true;
												}
											}else{
												if((mint%2)==0&&dint>0&&dint<=31){
													flag=true;
												}else{
													if(mint%2==1&&dint>0&&dint<=30){
														flag=true;
													}
												}
											}
											if(flag){
												try{
													Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres","postgres","*");
													PreparedStatement ps = c.prepareStatement("select gid from pf_detail.groupinfo where gname=?");
													ps.setString(1, gname);
													ResultSet r = ps.executeQuery();
													int gid;
													if(r.next()){
														gid=r.getInt("gid");
														ps = c.prepareStatement("select mid from pf_detail.memberinfo where mname=?");
														ps.setString(1, payer.getSelectedItem().toString());
														r =ps.executeQuery();
														if(r.next()){
															ps=c.prepareStatement("insert into pf_detail.detail(ddetail,dnum,dpayer,dupdateby,dbelong,ddate) "
																	+"values(?,?,?,?,?,?)");
															ps.setString(1, detail.getText());
															ps.setInt(2, -numint);
															ps.setInt(3, r.getInt("mid"));
															ps.setInt(4, myId);
															ps.setInt(5, gid);
															String startDate="01-02-2013";
															
															java.text.SimpleDateFormat f = new java.text.SimpleDateFormat("yyyyMMdd");
															java.util.Date JDate = f.parse(year.getText()+month.getText()+date.getText());														
															java.sql.Date sqlDate = new java.sql.Date(JDate.getTime());
															
															ps.setDate(6, sqlDate);
															ps.executeUpdate();
															setGinfoPanel(gname);
															mainWin.setContentPane(ginfo_pane);
															ginfo_pane.revalidate();
														}
													}
													c.close();
													ps.close();
													r.close();
												}
												catch(Exception err){
													System.out.println(err.toString());
												}
											}
											else{
												JOptionPane.showMessageDialog(null,"Invalid date value.", "Add detail failed",JOptionPane.INFORMATION_MESSAGE);
											}	
										}else{
											JOptionPane.showMessageDialog(null,"Invalid month value.", "Add detail failed",JOptionPane.INFORMATION_MESSAGE);
										}
									}
									else{
										JOptionPane.showMessageDialog(null,"Invalid year value.", "Add detail failed",JOptionPane.INFORMATION_MESSAGE);
									}
								}
								else{
									JOptionPane.showMessageDialog(null,"Invalid date formate", "Add detail failed",JOptionPane.INFORMATION_MESSAGE);
								}
							}
						}
						else{
							JOptionPane.showMessageDialog(null,"Enter number to amount of money only!", "Add detail failed",JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
			}
		}
		class GinfoClicked implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				setGinfoPanel(group.getSelectedItem().toString());
				mainWin.setContentPane(ginfo_pane);
				ginfo_pane.revalidate();
			 }  
		}
		class RefreshClicked implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				setOpPane();
				JPanel temp = new JPanel();
				temp.setLayout(null);
				temp.add(op_pane);
				mainWin.setContentPane(temp);
				temp.revalidate();
			 }  
		}
		class ResetClicked implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				int res = JOptionPane.showConfirmDialog(mainWin,"Are you sure to reset?","Confirm",
	                    JOptionPane.OK_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE);
					if(res==JOptionPane.OK_OPTION){
						try{
							Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres","postgres","*");
							PreparedStatement ps = c.prepareStatement("select gid from pf_detail.groupinfo where gname=?;");
							ps.setString(1, gname);
							ResultSet r =ps.executeQuery();
							if(r.next()){
								ps = c.prepareStatement("delete from pf_detail.detail where dbelong=?;");
								ps.setInt(1, r.getInt("gid"));
								ps.executeUpdate();
							}
							r.close();
							c.close();
							ps.close();
						}
						catch(Exception err){
							System.out.println(err.toString());
						}
					}
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
			class AddMemClicked implements ActionListener{
				public void actionPerformed(ActionEvent e) {
					if(account.getText().equals("")){
						JOptionPane.showMessageDialog(null,"Please enter account", "Add member failed",JOptionPane.INFORMATION_MESSAGE);
					}
					else{
						try{
							Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "*");
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
		}
		class QuitClicked implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				gname = group.getSelectedItem().toString();
				int res = JOptionPane.showConfirmDialog(mainWin,"Are you sure to quit "+gname+"?","Confirm",
                    JOptionPane.OK_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE);
				if(res==JOptionPane.OK_OPTION){
					try{
						Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "*");
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
								
								ps=c.prepareStatement("select count(*) c from pf_detail.belong where gid=?");
								ps.setInt(1, gnum);
								r=ps.executeQuery();
								
								if(r.next()&&r.getInt("c")==0){
									ps=c.prepareStatement("delete from pf_detail.groupinfo where gid=?;");
									ps.setInt(1, gnum);
									ps.executeUpdate();
								}
								
								ps=c.prepareStatement("select gname from (pf_detail.groupinfo g join pf_detail.belong b on g.gid=b.gid)"
								 		+ "join pf_detail.memberinfo m on b.mid = m.mid where mac=?");
								 ps.setString(1, myAccount);
								 r=ps.executeQuery();
								 ArrayList<String> n=new ArrayList<String>();
								 while(r.next()){
									 n.add(r.getString("gname"));
								 }
								 group.removeAllItems();
								 for(String temp:n){
									 group.addItem(temp);
								 }
								 ginfo_pane.remove(1);
								 ginfo_pane.revalidate();
								 mainWin.repaint();
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
		
		class EditClicked implements ActionListener{
			private int select;
			private JTextField year; 
			private JTextField month;
			private JTextField date;
			private JTextField detail;
			private JTextField num;
			private JComboBox payer;
			public void actionPerformed(ActionEvent e) {
				select=detail_tb.getSelectedRow();
				JPanel edit_pane = new JPanel();
				edit_pane.setBounds(0,0,700,500);
				edit_pane.setLayout(null);
				
				JPanel show_pane = new JPanel();
				show_pane.setLayout(null);
				show_pane.setBounds(160,15,510,430);
				
				//NAME
				JLabel name=new JLabel(gname,2);
				name.setFont(new Font("Arial", Font.BOLD+Font.ITALIC,32));
				name.setBounds(10,0,525,40);
				show_pane.add(name);
								
				//DATE
				JLabel dateMessage = new JLabel("Date (yyyy-mm-dd):");
				dateMessage.setFont(new Font("Avril", Font.PLAIN, 20));
				dateMessage.setBounds(20,60,200,20);
				show_pane.add(dateMessage);
								
				String d=detail_tb.getModel().getValueAt(select, 0).toString();
				
				year = new JTextField(d.substring(0, 4));
				year.setBounds(210,60,50,20);
				show_pane.add(year);

				JLabel line = new JLabel("-");
				line.setFont(new Font("Avril", Font.PLAIN, 20));
				line.setBounds(265,60,15,20);
				show_pane.add(line);
				 
				month = new JTextField(d.substring(5, 7));
				month.setBounds(285,60,50,20);
				show_pane.add(month);
				
				line = new JLabel("-");
				line.setFont(new Font("Avril", Font.PLAIN, 20));
				line.setBounds(340,60,15,20);
				show_pane.add(line);

				date = new JTextField(d.substring(8,10));
				date.setBounds(360,60,50,20);
				show_pane.add(date);
				
				//Detail
				JLabel detailMessage = new JLabel("Detail:");
				detailMessage.setFont(new Font("Avril", Font.PLAIN, 20));
				detailMessage.setBounds(20,125,70,20);
				show_pane.add(detailMessage);
				
				detail = new JTextField(detail_tb.getModel().getValueAt(select, 1).toString());
				detail.setBounds(90,120,350,30);
				show_pane.add(detail);
				
				//Num
				JLabel numMessage = new JLabel("Amount of money: NT ");
				numMessage.setFont(new Font("Avril", Font.PLAIN, 20));
				numMessage.setBounds(20,175,220,20);
				show_pane.add(numMessage);
				
				num = new JTextField(detail_tb.getModel().getValueAt(select, 2).toString());
				num.setBounds(220,170,220,30);
				show_pane.add(num);
				
				//Payer
				JLabel payerMessage = new JLabel("Payer:");
				payerMessage.setFont(new Font("Avril", Font.PLAIN, 20));
				payerMessage.setBounds(20,225,70,20);
				show_pane.add(payerMessage);
				
				try{
					Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "*");
					PreparedStatement ps = c.prepareStatement("select mname from pf_detail.belong natural join pf_detail.memberinfo natural join pf_detail.groupinfo where gname=?");
					ps.setString(1, gname);
					ResultSet r=ps.executeQuery();
					ArrayList<String>mem = new ArrayList<String>();
					while(r.next()){
						mem.add(r.getString("mname"));
					}
					
					payer = new JComboBox(mem.toArray());
					payer.setBounds(90,220,200,30);
					payer.setSelectedItem(detail_tb.getValueAt(select, 3));
					show_pane.add(payer);
					c.close();
					ps.close();
					r.close();
				}
				catch(Exception err){
					System.out.println(err.toString());
				}
				
				//Button editD
				JButton editD = new JButton("Edit detail");
				editD.setFont(new Font("Avril", Font.PLAIN, 20));
				editD.setBounds(350,390,150,30);
				editD.addActionListener(new EditDClicked());
				show_pane.add(editD);
				
				edit_pane.add(op_pane);
				edit_pane.add(show_pane);
				mainWin.setContentPane(edit_pane);
				edit_pane.revalidate();

				/*
				int did = Integer.parseInt(detail_tb.getModel().getValueAt(select, 4).toString());
				setGinfoPanel(gname);
				mainWin.setContentPane(ginfo_pane);
				ginfo_pane.revalidate();
				*/
			}  
			class EditDClicked implements ActionListener{
				public void actionPerformed(ActionEvent e) {
					int did = Integer.parseInt(detail_tb.getModel().getValueAt(select, 4).toString());
					int yint = Integer.parseInt(year.getText());
					int mint = Integer.parseInt(month.getText());
					int dint = Integer.parseInt(date.getText());
					if(yint>=1996&&yint<=9999){
						if(mint<=12&&mint>0){
							Boolean flag = false;
							if(mint<=7){
								if((mint%2)==1&&dint>0&&dint<=31){
									flag=true;
								}else if(mint==2&&dint>0&&dint<=29){
									flag=true;
								}else if(mint!=2&&mint%2==0&&dint>0&&dint<=30){
									flag=true;
								}
							}else{
								if((mint%2)==0&&dint>0&&dint<=31){
									flag=true;
								}else{
									if(mint%2==1&&dint>0&&dint<=30){
										flag=true;
									}
								}
							}
							if(flag){
								try{
								Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "*");
								PreparedStatement ps = c.prepareStatement("select mid from pf_detail.memberinfo where mname =?;");
								ps.setString(1, payer.getSelectedItem().toString());
								ResultSet r=ps.executeQuery();
								if(r.next()){
									ps = c.prepareStatement("update pf_detail.detail set "
											+ "ddetail=?, dnum=?, dpayer=?, dupdateby=?, ddate=? "
											+ "where did=?");
									ps.setString(1, detail.getText());
									ps.setInt(2, -Integer.parseInt(num.getText()));
									ps.setInt(3, r.getInt("mid"));
									ps.setInt(4, myId);
									
									java.text.SimpleDateFormat f = new java.text.SimpleDateFormat("yyyyMMdd");
									java.util.Date JDate = f.parse(year.getText()+month.getText()+date.getText());														
									java.sql.Date sqlDate = new java.sql.Date(JDate.getTime());
									
									ps.setDate(5, sqlDate);
									ps.setInt(6, did);
									ps.executeUpdate();
								}
								r.close();
								c.close();
								ps.close();
								}
								catch(Exception err){
									System.out.println(err.toString());
								}
								setGinfoPanel(gname);
								mainWin.setContentPane(ginfo_pane);
								ginfo_pane.revalidate();
							}
							else{
								JOptionPane.showMessageDialog(null,"Invalid date formate", "Add detail failed",JOptionPane.INFORMATION_MESSAGE);
							}
						}
						else{
							JOptionPane.showMessageDialog(null,"Invalid month formate", "Add detail failed",JOptionPane.INFORMATION_MESSAGE);
							}
					}else{
						JOptionPane.showMessageDialog(null,"Invalid year formate", "Add detail failed",JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		}
		class DeleteClicked implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				int res = JOptionPane.showConfirmDialog(mainWin,"Are you sure to delete this detail?","Confirm",
	                    JOptionPane.OK_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE);
				if(res==JOptionPane.OK_OPTION){
					int did = Integer.parseInt(detail_tb.getModel().getValueAt(detail_tb.getSelectedRow(), 4).toString());
					try{
						Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "*");	
						PreparedStatement ps = c.prepareStatement("delete from pf_detail.detail where did=?;");
						ps.setInt(1, did);
						ps.executeUpdate();
						ps.close();
						c.close();
						setGinfoPanel(gname);
						mainWin.setContentPane(ginfo_pane);
						ginfo_pane.revalidate();
					}catch(Exception err){
						System.out.println(err.toString());
					}
				}
			}  
		}
		
		private JTable detail_tb;
		
		private void setGinfoPanel(String gn){
			gname=gn;
			try{			
				//Var
				int numOfMem=0;
				int totalCost=0;
				int youvePayed=0;
				int youllGet=0;
				
				//connect
				Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "*");
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
				if(youvePayed-(totalCost/numOfMem)>0){
					groupinfo = new JLabel("You will get NT "+(youvePayed-(totalCost/numOfMem))+" back.");
				}
				else if(youvePayed-(totalCost/numOfMem)==0){
					groupinfo = new JLabel("You've payed how much you need to pay");
				}else{
					groupinfo = new JLabel("You have to pay NT "+((totalCost/numOfMem)-youvePayed)+" more.");
				}
				groupinfo.setBounds(20, 140, 505, 20);
				groupinfo.setFont(new Font("Arial", Font.PLAIN,20));
				show_pane.add(groupinfo);
				
				//detail
				ps=c.prepareStatement("select ddate,ddetail,-dnum n,mname,did from "
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
					temp.add(String.valueOf(r.getInt("did")));
					detail.add(temp.toArray(new String[temp.size()]));
				}
				String[] cols ={"Date", "Detail", "Cost", "Payer","did"};
				detail_tb = new JTable(detail.toArray(new String[detail.size()][]),cols);
				TableColumnModel tcm = detail_tb.getColumnModel();
				tcm.removeColumn(tcm.getColumn(4));
				JScrollPane withTitle =new JScrollPane(detail_tb);
				withTitle.setBounds(20,170,470,220);
				show_pane.add(withTitle);
				
				//button-edit
				JButton edit = new JButton("Edit");
				edit.addActionListener(new EditClicked());
				edit.setBounds(400,390,90,30);
				show_pane.add(edit);
				
				//button-delete
				JButton delete = new JButton("Delete");
				delete.addActionListener(new DeleteClicked());
				delete.setBounds(295,390,90,30);
				show_pane.add(delete);
				
				//button-edit
				JButton reset = new JButton("Reset");
				reset.addActionListener(new ResetClicked());
				reset.setBounds(190,390,90,30);
				show_pane.add(reset);
				
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
				 //current.setVisible(false);
				 current.dispose();
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
						 Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "*");
						 
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
							 //current.setVisible(false);
							 current.dispose();
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
				 //current.setVisible(false);
				 current.dispose();
				 creAcc_info = new CreAccInfo();
				 current.setVisible(true);
			 }  
		}
		class toLoginClicked implements ActionListener {
			 public void actionPerformed(ActionEvent e) {
				 try{
					 //connect
					 Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "*");
					 
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
							 //current.setVisible(false);
							 current.dispose();
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
