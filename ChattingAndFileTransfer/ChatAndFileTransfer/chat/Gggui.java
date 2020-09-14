package chat;

import java.awt.BorderLayout;

import java.awt.EventQueue;
import java.awt.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;

public class Gggui extends JFrame  {
	JButton btnFileq;
	public JPanel contentPane;
	public JTextField file_path;
	public JTextField chat_field;
	public JTextField yourmac;
	public JTextField mymac;
	JTextArea chatting_area;
	JButton btnSetq;
	JButton btnSendf;
	JButton btnSendq;
	List chatlist;
	JProgressBar progressBar;
	JComboBox combobox;

	public Gggui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 660, 360);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Chatting", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(0, 0, 381, 214);
		contentPane.add(panel);
		panel.setLayout(null);

		btnSendq = new JButton("Send(Q)");
		btnSendq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//				ChatAchat_field
			}
		});
		btnSendq.setBounds(288, 182, 81, 23);
		panel.add(btnSendq);

		chat_field = new JTextField();
		chat_field.setBounds(12, 183, 264, 21);
		chat_field.setColumns(10);
		panel.add(chat_field);

		//		chatting_area = new JTextArea();
		//		chatting_area.setEditable(false);
		//		chatting_area.setBounds(12, 20, 357, 152);
		//		panel.add(chatting_area);
		chatlist = new List();
		chatlist.setBounds(12,20,357,152);
		panel.add(chatlist);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Source", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(385, 0, 247, 78);
		contentPane.add(panel_1);
		panel_1.setLayout(null);

		JLabel lblNewLabel = new JLabel("Ethernet Address");
		lblNewLabel.setBounds(12, 21, 124, 15);
		panel_1.add(lblNewLabel);

		mymac = new JTextField();
		mymac.setColumns(10);
		mymac.setBounds(12, 46, 223, 21);
		panel_1.add(mymac);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "File Transfer", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_2.setBounds(0, 219, 381, 84);
		contentPane.add(panel_2);
		panel_2.setLayout(null);

		btnFileq = new JButton("File(Q)");
		btnFileq.setBounds(288, 21, 81, 23);
		panel_2.add(btnFileq);

		btnSendf = new JButton("Send(F)");
		btnSendf.setBounds(288, 54, 81, 23);
		panel_2.add(btnSendf);

		file_path = new JTextField();
		file_path.setBounds(12, 22, 264, 21);
		panel_2.add(file_path);
		file_path.setColumns(10);

		progressBar = new JProgressBar();
		progressBar.setBounds(12, 60, 264, 14);
		panel_2.add(progressBar);

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(null, "Destination", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_3.setBounds(385, 83, 247, 69);
		contentPane.add(panel_3);
		panel_3.setLayout(null);

		yourmac = new JTextField();
		yourmac.setBounds(12, 41, 223, 21);
		panel_3.add(yourmac);
		yourmac.setColumns(10);
		yourmac.setText("AAAAAAAAAAAA");

		//yourmac.setText(new Scanner(System.in).next());
		JLabel label = new JLabel("Ethernet Address");
		label.setBounds(12, 20, 124, 15);
		panel_3.add(label);

		btnSetq = new JButton("Set(Q)");
		btnSetq.setBounds(476, 233, 81, 23);
		contentPane.add(btnSetq);

		combobox = new JComboBox();
		combobox.setBounds(395,162,162,21);
		contentPane.add(combobox);
		Test test = new Test();
		for(int i = 0; i<test.NILayer.m_pAdapterList.size(); i++){
			combobox.addItem(test.NILayer.m_pAdapterList.get(i).getDescription());
		}

		combobox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				int adapterNumber = combobox.getSelectedIndex();
				System.out.println(adapterNumber);
				test.NILayer.SetAdapterNumber(adapterNumber);
			}
		});
		btnSendq.addMouseListener(new MouseListener(){            
			public void mouseClicked(MouseEvent e){
				String msg= chat_field.getText(); 
				chatlist.add("º»ÀÎ : "+msg+"\n");
				test.ChatAppLayer.Send(msg.getBytes());
				chat_field.setText("");
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}
}
