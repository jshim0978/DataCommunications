package chat;

import java.net.InetAddress;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class EthernetLayer extends BaseLayer {
	int enet_MAX_SIZE=1514;
	int enet_MAX_HEAD_SIZE=14;
	int enet_MAX_DATA_SIZE=1500;

	byte[] enet_type = new byte[2];
	byte[] enet_srcaddr=new byte[6];
	byte[] enet_dstaddr=new byte[6];
	byte[] enet_data=new byte[enet_MAX_DATA_SIZE];
	String dst_addr;
	BaseLayer UpperLayer,UnderLayer;
	String Layername;

	public EthernetLayer(String Layer) {
		super(Layer);
		Layername = Layer;
	}
	public void setUpperLayer(BaseLayer Layer){
		UpperLayer = Layer;
	}
	public void setUnderLayer(BaseLayer Layer){
		UnderLayer = Layer;
	}
	public boolean Receive(byte[] data){//1514
		if(data.length==1514){
			byte[] dst_address= new byte[6];
			System.arraycopy(data, 8, dst_address, 0, 6);//자신의 맥주소가 목적지 맥주소라면 더이상 receive하지않는다.
			if(new String(dst_address).equals(new String(enet_srcaddr))){
				byte[] receive_data = new byte[1500];
				System.arraycopy(data, 14, receive_data, 0, receive_data.length);
				UpperLayer.Receive(receive_data);//1500보냄
			}
		}
		return false;
	}
	public synchronized boolean Send(byte[] data){//1500받아서
		byte[] send_data = new byte[1514];
		System.arraycopy(enet_srcaddr, 0, send_data, 2, 6);
		System.arraycopy(enet_dstaddr, 0, send_data, 8, 6);
		System.arraycopy(data, 0, send_data, 14, data.length);//
		UnderLayer.Send(send_data);//1514보냄
		return false;
	}
	public void set_addr(){ 
		InetAddress ip;
		StringBuilder sb = new StringBuilder();
		try {
			NetworkInterface network = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());

			byte[] mac = network.getHardwareAddress();

			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "" : ""));
			}
			String mac_addr = sb.toString();
			for(int i = 0, j = 0; i<12; i+=2, j++){
				enet_srcaddr[j]=Integer.valueOf(mac_addr.substring(i,i+2),16).byteValue();
				enet_dstaddr[j]=Integer.valueOf(dst_addr.substring(i,i+2),16).byteValue();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e){
			e.printStackTrace();
		}
	}
	public boolean check(byte[] a, byte[] b){
		int count=0;
		for(int i = 0 ; i<a.length; i++){
			if(a[i]==b[i])count++;
		}
		if(count==a.length-1)return true;
		return false;
	}
}