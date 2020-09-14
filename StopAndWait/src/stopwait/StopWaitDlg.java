package stopwait;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;


public class StopWaitDlg extends JFrame implements BaseLayer{
	
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	BaseLayer UnderLayer;

	private static LayerManager m_LayerMgr = new LayerManager();

	private JTextField ChattingWrite;

	Container contentPane;

	JTextArea ChattingArea;
	JTextArea srcAddress;
	JTextArea dstAddress;

	JLabel lblsrc;
	JLabel lbldst;

	JButton Setting_Button;
	JButton Chat_send_Button;

	static JComboBox<String> NICComboBox;

	int adapterNumber = 0;

	String Text;

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		m_LayerMgr.AddLayer(new NILayer("NI"));
		m_LayerMgr.AddLayer(new EthernetLayer("Ethernet"));
		m_LayerMgr.AddLayer(new ChatAppLayer("Chat"));
		m_LayerMgr.AddLayer(new StopWaitDlg("GUI"));
		
		m_LayerMgr.ConnectLayers(" NI ( *Ethernet ( *Chat ( *GUI ) ) ) ");
	}
	

	public StopWaitDlg(String pName) {
		pLayerName = pName;

		setTitle("StopWait");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(250, 250, 644, 425);
		contentPane = new JPanel();
		((JComponent) contentPane).setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel chattingPanel = new JPanel();// chatting panel
		chattingPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "chatting",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		chattingPanel.setBounds(10, 5, 360, 276);
		contentPane.add(chattingPanel);
		chattingPanel.setLayout(null);

		JPanel chattingEditorPanel = new JPanel();// chatting write panel
		chattingEditorPanel.setBounds(10, 15, 340, 210);
		chattingPanel.add(chattingEditorPanel);
		chattingEditorPanel.setLayout(null);

		ChattingArea = new JTextArea();
		ChattingArea.setEditable(false);
		ChattingArea.setBounds(0, 0, 340, 210);
		chattingEditorPanel.add(ChattingArea);// chatting edit

		JPanel chattingInputPanel = new JPanel();// chatting write panel
		chattingInputPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		chattingInputPanel.setBounds(10, 230, 250, 20);
		chattingPanel.add(chattingInputPanel);
		chattingInputPanel.setLayout(null);

		ChattingWrite = new JTextField();
		ChattingWrite.setBounds(2, 2, 250, 20);// 249
		chattingInputPanel.add(ChattingWrite);
		ChattingWrite.setColumns(10);// writing area

		JPanel settingPanel = new JPanel();
		settingPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "setting",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		settingPanel.setBounds(380, 5, 236, 371);
		contentPane.add(settingPanel);
		settingPanel.setLayout(null);

		JPanel sourceAddressPanel = new JPanel();
		sourceAddressPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sourceAddressPanel.setBounds(10, 96, 170, 20);
		settingPanel.add(sourceAddressPanel);
		sourceAddressPanel.setLayout(null);

		lblsrc = new JLabel("Source MAC Address");
		lblsrc.setBounds(10, 75, 170, 20);
		settingPanel.add(lblsrc);

		srcAddress = new JTextArea();
		srcAddress.setBounds(2, 2, 170, 20);
		sourceAddressPanel.add(srcAddress);// src address
		
		JPanel NIC = new JPanel();
		NIC.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		NIC.setBounds(10, 30, 170, 20);
		settingPanel.add(NIC);
		NIC.setLayout(null);

		lblsrc = new JLabel("NIC ����");
		lblsrc.setBounds(10, 15, 170, 20);
		settingPanel.add(lblsrc);

		NICComboBox = new JComboBox();
		NICComboBox.setBounds(2, 2, 170, 20);
		NIC.add(NICComboBox);// src address
		
		for (int i = 0; i < NILayer.m_pAdapterList.size(); i++) {
			NICComboBox.addItem(NILayer.m_pAdapterList.get(i).getDescription());
		}
		NICComboBox.addItemListener(new ItemListener() {			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				adapterNumber = NICComboBox.getSelectedIndex();
				System.out.println(adapterNumber);
				try {
					srcAddress.setText(byteArrayToHexString(((NILayer) m_LayerMgr.GetLayer("NI")).getAdapterList().get(adapterNumber).getHardwareAddress()));
					System.out.println(((NILayer) m_LayerMgr.GetLayer("NI")).getAdapterList().get(adapterNumber).getHardwareAddress());
					System.out.println(byteArrayToHexString(((NILayer) m_LayerMgr.GetLayer("NI")).getAdapterList().get(adapterNumber).getHardwareAddress()));
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		JPanel destinationAddressPanel = new JPanel();
		destinationAddressPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		destinationAddressPanel.setBounds(10, 212, 170, 20);
		settingPanel.add(destinationAddressPanel);
		destinationAddressPanel.setLayout(null);

		lbldst = new JLabel("Destination MAC Address");
		lbldst.setBounds(10, 187, 190, 20);
		settingPanel.add(lbldst);

		dstAddress = new JTextArea();
		dstAddress.setBounds(2, 2, 170, 20);
		destinationAddressPanel.add(dstAddress);// dst address

		Setting_Button = new JButton("Setting");// setting
		Setting_Button.setBounds(80, 270, 100, 20);
		Setting_Button.addActionListener(new setAddressListener());
		settingPanel.add(Setting_Button);// setting

		Chat_send_Button = new JButton("Send");
		Chat_send_Button.setBounds(270, 230, 80, 20);
		Chat_send_Button.addActionListener(new setAddressListener());
		chattingPanel.add(Chat_send_Button);// chatting send button
		setVisible(true);

	}
	
	public int[] splitaddress(String Address) {
		
		
		String[] integerStrings = Address.split("-"); 
		// Splits each spaced integer into a String array.
		
		
		int[] integers = new int[integerStrings.length]; 
		// Creates the integer array.
		for (int i = 0; i < integers.length; i++){
			System.out.println(integerStrings[i]);
		    integers[i] = Integer.parseInt(integerStrings[i],16); 
		//Parses the integer for each string.
		}
		return integers;
	}

	class setAddressListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
		
			if(e.getSource() == Setting_Button){
				if(Setting_Button.getText() == "Reset"){
					
					srcAddress.setText("");
					dstAddress.setText("");
					dstAddress.setEnabled(true);
					srcAddress.setEnabled(true);			
					Setting_Button.setText("Setting");
				}
				else {
					String Ssrc = srcAddress.getText();
					String Sdst = dstAddress.getText();	
					
					int[] src = (splitaddress(Ssrc));
					int[] dst = (splitaddress(Sdst));		
					
					((EthernetLayer) m_LayerMgr.GetLayer("Ethernet")).SetEnetDstAddress(dst);
					((EthernetLayer) m_LayerMgr.GetLayer("Ethernet")).SetEnetSrcAddress(src);
					
					((NILayer) m_LayerMgr.GetLayer("NI")).SetAdapterNumber(adapterNumber);	
									
					Setting_Button.setText("Reset");					
					dstAddress.setEnabled(false);
					srcAddress.setEnabled(false);					
				}
			}			
			if(e.getSource() == Chat_send_Button){				
				if(Setting_Button.getText() == "Reset"){										
					String writtenChat = ChattingWrite.getText();							
					ChattingArea.append("[Send] : " + writtenChat  + "\n");						
					byte[] sendingChat = writtenChat.getBytes();	
					
					((ChatAppLayer) m_LayerMgr.GetLayer("Chat")).Send(sendingChat, sendingChat.length);										
				}
				else {
					ChattingArea.append("�ּ� ���� ����");					
				}			
			}
		}
	}
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	public static String byteArrayToHexString(byte[] bytes){ 
		
		StringBuilder sb = new StringBuilder(); 
		
		for(byte b : bytes){ 
			
			sb.append(String.format("%02X", b&0xff)); 
			sb.append("-");
		} 
		
		return sb.toString(); 
	} 
	
	public boolean Receive(byte[] input) {			
		ChattingArea.append("[Rcvd] : "+ new String(input) + "\n");		
		return true;
	}
	
	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		if (pUnderLayer == null)
			return;
		this.p_UnderLayer = pUnderLayer;
	}
	@Override
	public void SetUpperLayer(BaseLayer pUpperLayer) {
		if (pUpperLayer == null)
			return;
		this.p_aUpperLayer.add(nUpperLayerCount++, pUpperLayer);		
	}
	@Override
	public String GetLayerName() {		
		return pLayerName;
	}
	@Override
	public BaseLayer GetUnderLayer() {
		if (p_UnderLayer == null)
			return null;
		return p_UnderLayer;
	}
	@Override
	public BaseLayer GetUpperLayer(int nindex) {
		if (nindex < 0 || nindex > nUpperLayerCount || nUpperLayerCount < 0)
			
			return null;
		return p_aUpperLayer.get(nindex);
	}
	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		this.SetUpperLayer(pUULayer);
		pUULayer.SetUnderLayer(this);
	}
}
