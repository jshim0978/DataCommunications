package chat;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

import chat.NILayer.Receive_Thread;

public class FileAppLayer extends BaseLayer {
	byte[] fapp_tolen = new byte[4];
	byte[] fapp_type = new byte[2];
	byte[] fapp_msg_type = new byte[1];
	byte[] unused = new byte[1];
	byte[] fapp_seq_num = new byte[4];
	byte[] fapp_data = new byte[1440];
	byte[] sum_file = new byte[1444];
	boolean[] pass;
	String file_name = null;
	int k =0;
	int count=0;
	boolean first_pass = true;
	int size=0;//������ ��ũ��
	int packet_num_int=0;//��Ŷ�� ��ȣ
	int packet_size;//�� ��Ŷ�� ����
	JFrame dialog = new JFrame();
	Test test = new Test();
	public FileAppLayer(String Layer) {
		super(Layer);
	}
	public synchronized boolean Send(File file){
		k=0;
		count=0;
		Send_Thread thread = new Send_Thread(file);
		Thread obj = new Thread(thread);
		obj.start();
		return false;
	}

	public void setUpperLayer(BaseLayer Layer){
		UpperLayer = Layer;
	}
	public void setUnderLayer(BaseLayer Layer){
		UnderLayer = Layer;
	}
	public int byteArrayToInt(byte bytes[]) {//����Ʈȭ�� ���ڸ� int�� ���ִ� �޼ҵ�
		return ((((int)bytes[0] & 0xff) << 24) |
				(((int)bytes[1] & 0xff) << 16) |
				(((int)bytes[2] & 0xff) << 8) |
				(((int)bytes[3] & 0xff)));
	}
	public boolean Receive(byte[] data){//1460����

		byte[] receive_file = new byte[1440];
		byte[] packet = new byte[4];
		byte[] file_name_length = new byte[4];
		byte[] file_size = new byte[4];

		System.arraycopy(data, 8, file_name_length, 0, 4);
		System.arraycopy(data, 12, packet, 0, 4);
		System.arraycopy(data, 16, file_size, 0, 4);//����ũ�� ����


		int file_name_length_int = byteArrayToInt(file_name_length);
		int packet_num = byteArrayToInt(packet);//���° ��Ŷ����
		size = byteArrayToInt(file_size);//����ũ�� ���� ����ȯ
		if(packet_num == 0&&first_pass){//ù��° ��Ŷ
			first_pass=false;
			k=0;
			test.frame.progressBar.setValue(0);
			sum_file = new byte[size+1440];//ù��° ��Ŷ�� ��쿡�� sum_file ���� 2��°�̻� ��Ŷ�ϰ���X
			System.out.println("���� �������"+size);
			packet_size = (size/1440)+1;// ��Ŷ ����
			System.out.println("��Ŷ �������"+packet_size);
			byte[] file_name_byte = new byte[file_name_length_int];//���� �̸� ����Ʈ�迭 ����
			System.arraycopy(data, 0, file_name_byte, 0, file_name_length_int); //���� �̸� ����Ʈ�迭 ����
			file_name = new String(file_name_byte);//�����̸� ����Ʈ�迭 string ��ȯ
			pass= new boolean[packet_size];
			Arrays.fill(pass,true);
		}
		if(packet_num<packet_size&&pass[packet_num]){
			pass[packet_num]=false;
			System.arraycopy(data, 20, sum_file, 1440*packet_num, 1440);//sum_file���ٰ� 1440*�ش� ��Ŷ ��ȣ ����
			k++;//�ϳ����������� ++
			System.out.println(packet_num+" : "+packet_size+ " : " + k);
			test.frame.progressBar.setValue((int)((float)k/(float)((size/1440)+1)*100));//���α׷�����
			//��� �������� �˼��ִµ�  1���� ���ö� ī��Ʈ�ؼ� text.frame.progressbar.value
			if(k==packet_size){//������ ��Ŷ�� ������ ���� ����
				byte[] real_sum_file = new byte[size];
				System.arraycopy(sum_file, 0, real_sum_file, 0, size);//���ϱ����°��� �����ϱ����� ����ũ�⿡ �´� �迭�� ���� �� copy
				Path path = Paths.get("C:\\Users\\Suka\\Music\\"+file_name);//���ŵǾ��ٰ� �˾�â.
				try {
					Files.write(path,real_sum_file);//�ش� path�� file_name �̸����� ����
					JOptionPane.showMessageDialog(null, "���� ���� �Ϸ�");
					first_pass = true;
				} catch (IOException e) {
					e.printStackTrace();
				}   
			}
		}
		return false;
	}
	public class Send_Thread implements Runnable {
		File file;
		public Send_Thread(File file){
			this.file=file;
		}
		@Override
		public void run() {
			//��Ŷ��ȣ ����
			byte[] file_size = ByteBuffer.allocate(4).putInt((int)file.length()).array();//����ũ�� byteȭ
			byte[] buffer = new byte[1440];//������ ���� ����Ʈ ����
			byte[] file_name = file.getName().getBytes();//�����̸� byteȭ
			byte[] file_name_length = new byte[4];//�����̸����� byteȭ����
			byte[] packet_num = new byte[4];//��Ŷ ��ȣ ����Ʈ ���� 

			file_name_length = ByteBuffer.allocate(4).putInt(file.getName().length()).array();//�����̸����� ����Ʈȭ

			try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))){//��Ŷ������ �ɰ��� send�ϱ�
				int temp_int = 0;
				while((temp_int=bis.read(buffer))>0){
					packet_num = ByteBuffer.allocate(4).putInt(count++).array();//��Ŷ ��ȣ ����Ʈȭ
					byte[] send_data = new byte[1460];//TCP�� ���� ����Ʈ�迭
					System.arraycopy(file_name, 0, send_data, 0, file.getName().length());//��� �����̸��κ�
					System.arraycopy(file_name_length, 0, send_data, 8, 4);//��� �����̸�ũ��
					System.arraycopy(packet_num, 0, send_data, 12, 4);//��� ��Ŷ��ȣ
					System.arraycopy(file_size,0,send_data,16,4);//��� ����ũ�� ���
					System.arraycopy(buffer, 0, send_data, 20, temp_int);//���Ϲ���Ʈ
					UnderLayer.Send(send_data);//1460����   
					try {
						Thread.sleep((long)1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			JOptionPane.showMessageDialog(null, "���� ����");
		}
	}
	public class send_time_Thread implements Runnable{
		byte[] packet_num,end_time_byte;
		send_time_Thread(byte[] packet_num,byte[] end_time_byte){
			this.packet_num=packet_num;
			this.end_time_byte=end_time_byte;
		}
		@Override
		public void run() {
			byte[] send_data= new byte[12];
			System.arraycopy(packet_num, 0, send_data, 0, 4);
			System.arraycopy(end_time_byte,0,send_data,4,8);
			UnderLayer.Send(send_data);
		}
	}
}