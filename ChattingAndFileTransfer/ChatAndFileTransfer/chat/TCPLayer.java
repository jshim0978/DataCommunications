package chat;

import java.io.IOException;
import java.util.Arrays;

public class TCPLayer extends BaseLayer{
	byte[] tcp_sport = new byte[2];
	byte[] tcp_dport = new byte[2];
	byte[] tcp_seq = new byte[4];
	byte[] tcp_ack = new byte[4];
	byte[] tcp_offset = new byte[1];
	byte[] tcp_flag = new byte[1];
	byte[] tcp_window = new byte[2];
	byte[] tcp_cksum = new byte[2];
	byte[] tcp_urgptr = new byte[2];
	//	byte[] Padding = new byte[4];
	byte[] tcp_data = new byte[1460];
	byte[] Max_data = new byte[1480];

	public TCPLayer(String Layer) {
		super(Layer);
	}
	public synchronized boolean Send(byte[] data){//1460 받아서
		byte[] send_data = new byte[1480];
		System.arraycopy(data, 0, send_data, 20, data.length);
		UnderLayer.Send(send_data);//1480 보냄
		return false;
	}
	public boolean Receive(byte[] data){//1480
		byte[] receive_data = new byte[1460];
		System.arraycopy(data, 20, receive_data, 0, 1460);
		try {
			if(data[20]==1){//1460바이트중 첫번째헤더가 1이면 챗앱 레이어로
			UpperLayer.Receive(receive_data);
			}else{//아닐경우 파일앱 레이어로
				UpperLayer1.Receive(receive_data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void setUpperLayer(BaseLayer Layer){
		UpperLayer = Layer;
	}
	public void setUnderLayer(BaseLayer Layer){
		UnderLayer = Layer;
	}
	public void setUpperLayer1(BaseLayer Layer){
		UpperLayer1 = Layer;
	}
}
