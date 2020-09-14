package chat;

import java.util.Arrays;

public class IPLayer extends BaseLayer {
	int IPLayer_Head_Size=20;
	int IPLayer_Data_Size=1480;
	byte[] ip_verlen = new byte[1];
	byte[] ip_tos = new byte[1];
	byte[] ip_len = new byte[2];
	byte[] ip_id = new byte[2];
	byte[] ip_fragoff = new byte[2];
	byte[] ip_ttl = new byte[1];
	byte[] ip_proto = new byte[1];
	byte[] ip_cksum = new byte[2];
	byte[] ip_src = new byte[4];
	byte[] ip_dst = new byte[4];
	byte[] ip_data = new byte[IPLayer_Data_Size];
	public IPLayer(String Layer) {
		super(Layer);
		// TODO Auto-generated constructor stub
	}
	public boolean Receive(byte[] data){//1500
		byte[] receive_data = new byte[1480];
		System.arraycopy(data, 20, receive_data, 0, 1480);
		UpperLayer.Receive(receive_data);//1480º¸³¿
		return false;
	}
	public synchronized boolean Send(byte[] data){//1480¹Þ¾Æ¼­
		byte[] send_data = new byte[1500];
		System.arraycopy(data, 0, send_data, 20, data.length);
		UnderLayer.Send(send_data); // 1500º¸³¿
		return false;
	}
	public void setUpperLayer(BaseLayer Layer){
		UpperLayer = Layer;
	}
	public void setUnderLayer(BaseLayer Layer){
		UnderLayer = Layer;
	}
}
