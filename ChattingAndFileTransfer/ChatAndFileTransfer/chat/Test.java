package chat;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;

import java.*;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Test {
	static String file_path,file_name;
	static Gggui frame;
	static NILayer NILayer;
	static EthernetLayer EthernetLayer;
	static ChatAppLayer ChatAppLayer;
	static IPLayer IPLayer;
	static FileAppLayer FileAppLayer;
	static TCPLayer TCPLayer;
	public static void main(String args[]) throws IOException{


		NILayer = new NILayer("NILayer");
		IPLayer = new IPLayer("IPLayer");
		TCPLayer = new TCPLayer("TCPLayer");

		EthernetLayer = new EthernetLayer("EthernetLayer");
		FileAppLayer = new FileAppLayer("FileAppLayer");
		ChatAppLayer = new ChatAppLayer("ChatAppLayer");

		NILayer.setUpperLayer(EthernetLayer);

		EthernetLayer.setUnderLayer(NILayer);
		EthernetLayer.setUpperLayer(IPLayer);

		IPLayer.setUnderLayer(EthernetLayer);
		IPLayer.setUpperLayer(TCPLayer);

		TCPLayer.setUnderLayer(IPLayer);
		TCPLayer.setUpperLayer(ChatAppLayer);
		TCPLayer.setUpperLayer1(FileAppLayer);

		FileAppLayer.setUnderLayer(TCPLayer);
		ChatAppLayer.setUnderLayer(TCPLayer);


		

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {		
				frame = new Gggui();
				frame.setVisible(true);
				// TODO Auto-generated catch block
			}
		});

		/*mac �ּ�*/
		NetworkInterface network = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());

		byte[] mac = network.getHardwareAddress();
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i<mac.length; i++){
			sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "" : ""));
		}

		frame.btnSetq.addMouseListener(new MouseListener(){            
			public void mouseClicked(MouseEvent e){
				NILayer.SetAdapterNumber(0);
				EthernetLayer.dst_addr=frame.yourmac.getText();
				EthernetLayer.set_addr();
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {	
			}
			@Override
			public void mousePressed(MouseEvent e) {	
			}
			@Override
			public void mouseReleased(MouseEvent e) {	
			}
		}
				);
		
		frame.mymac.setText(sb.toString());
		/*�ؽ�Ʈ ����*/		
		frame.file_path.addMouseListener(new MouseListener(){            
			public void mouseClicked(MouseEvent e){
				//int returnVal = fc.showOpenDialog(parent);
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(chooser.DIRECTORIES_ONLY);
				chooser.showOpenDialog(null);
			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
		});
		frame.btnFileq.addMouseListener(new MouseListener(){            
			public void mouseClicked(MouseEvent e){
				JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				int returnValue = jfc.showOpenDialog(null);
				if(returnValue == JFileChooser.APPROVE_OPTION){
					File selectedFile = jfc.getSelectedFile();
					file_path = selectedFile.getParent().replace("\\", "\\\\");
					file_name = selectedFile.getName();
					frame.file_path.setText(selectedFile.getAbsolutePath().replace("\\", "\\\\"));  //���� ��� \�� \\�� �ٲپ��ִ� �κ�
					frame.progressBar.setValue(0);
					System.out.println(file_path+file_name);
				}
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseReleased(MouseEvent e) {
			}
		});
		frame.btnSendf.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				FileAppLayer.k=0;
				FileAppLayer.Send(new File(file_path,file_name));
			}
		});


	}
}
