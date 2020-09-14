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
	int size=0;//파일의 총크기
	int packet_num_int=0;//패킷의 번호
	int packet_size;//총 패킷의 갯수
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
	public int byteArrayToInt(byte bytes[]) {//바이트화한 숫자를 int로 해주는 메소드
		return ((((int)bytes[0] & 0xff) << 24) |
				(((int)bytes[1] & 0xff) << 16) |
				(((int)bytes[2] & 0xff) << 8) |
				(((int)bytes[3] & 0xff)));
	}
	public boolean Receive(byte[] data){//1460받음

		byte[] receive_file = new byte[1440];
		byte[] packet = new byte[4];
		byte[] file_name_length = new byte[4];
		byte[] file_size = new byte[4];

		System.arraycopy(data, 8, file_name_length, 0, 4);
		System.arraycopy(data, 12, packet, 0, 4);
		System.arraycopy(data, 16, file_size, 0, 4);//파이크기 저장


		int file_name_length_int = byteArrayToInt(file_name_length);
		int packet_num = byteArrayToInt(packet);//몇번째 패킷인지
		size = byteArrayToInt(file_size);//파일크기 정수 형변환
		if(packet_num == 0&&first_pass){//첫번째 패킷
			first_pass=false;
			k=0;
			test.frame.progressBar.setValue(0);
			sum_file = new byte[size+1440];//첫번째 패킷일 경우에만 sum_file 생성 2번째이상 패킷일경우는X
			System.out.println("파일 사이즈는"+size);
			packet_size = (size/1440)+1;// 패킷 갯수
			System.out.println("패킷 사이즈는"+packet_size);
			byte[] file_name_byte = new byte[file_name_length_int];//파일 이름 바이트배열 생성
			System.arraycopy(data, 0, file_name_byte, 0, file_name_length_int); //파일 이름 바이트배열 저장
			file_name = new String(file_name_byte);//파일이름 바이트배열 string 변환
			pass= new boolean[packet_size];
			Arrays.fill(pass,true);
		}
		if(packet_num<packet_size&&pass[packet_num]){
			pass[packet_num]=false;
			System.arraycopy(data, 20, sum_file, 1440*packet_num, 1440);//sum_file에다가 1440*해당 패킷 번호 복사
			k++;//하나받을때마다 ++
			System.out.println(packet_num+" : "+packet_size+ " : " + k);
			test.frame.progressBar.setValue((int)((float)k/(float)((size/1440)+1)*100));//프로그래스바
			//몇개가 들어오는지 알수있는데  1개씩 들어올때 카운트해서 text.frame.progressbar.value
			if(k==packet_size){//마지막 패킷이 들어오면 복사 시작
				byte[] real_sum_file = new byte[size];
				System.arraycopy(sum_file, 0, real_sum_file, 0, size);//파일깨지는것을 방지하기위해 파일크기에 맞는 배열을 생성 후 copy
				Path path = Paths.get("C:\\Users\\Suka\\Music\\"+file_name);//수신되었다고 팝업창.
				try {
					Files.write(path,real_sum_file);//해당 path에 file_name 이름으로 저장
					JOptionPane.showMessageDialog(null, "파일 수신 완료");
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
			//패킷번호 지정
			byte[] file_size = ByteBuffer.allocate(4).putInt((int)file.length()).array();//파일크기 byte화
			byte[] buffer = new byte[1440];//파일을 보낼 바이트 생성
			byte[] file_name = file.getName().getBytes();//파일이름 byte화
			byte[] file_name_length = new byte[4];//파일이름길이 byte화저장
			byte[] packet_num = new byte[4];//패킷 번호 바이트 변수 

			file_name_length = ByteBuffer.allocate(4).putInt(file.getName().length()).array();//파일이름길이 바이트화

			try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))){//패킷단위로 쪼개서 send하기
				int temp_int = 0;
				while((temp_int=bis.read(buffer))>0){
					packet_num = ByteBuffer.allocate(4).putInt(count++).array();//패킷 번호 바이트화
					byte[] send_data = new byte[1460];//TCP로 보낼 바이트배열
					System.arraycopy(file_name, 0, send_data, 0, file.getName().length());//헤더 파일이름부분
					System.arraycopy(file_name_length, 0, send_data, 8, 4);//헤더 파일이름크기
					System.arraycopy(packet_num, 0, send_data, 12, 4);//헤더 패킷번호
					System.arraycopy(file_size,0,send_data,16,4);//헤더 파일크기 헤더
					System.arraycopy(buffer, 0, send_data, 20, temp_int);//파일바이트
					UnderLayer.Send(send_data);//1460보냄   
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
			JOptionPane.showMessageDialog(null, "전송 성공");
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