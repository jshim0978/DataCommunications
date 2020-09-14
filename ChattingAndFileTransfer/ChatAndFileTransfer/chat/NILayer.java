package chat;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

public class NILayer extends BaseLayer {
	String Layername;
	StringBuilder errbuf=new StringBuilder();
	int m_iNumAdapter;
	public Pcap m_AdapterObject ;
	public PcapIf device;
	public List<PcapIf> m_pAdapterList;
	public NILayer(String Layer) {
		super(Layer);
		Layername = Layer;
		m_pAdapterList = new ArrayList<PcapIf>();
		m_iNumAdapter=0;
		SetAdapterList();
		System.out.println(m_pAdapterList.get(0).getAddresses());
		
	}
	private void SetAdapterList() {
		int r = Pcap.findAllDevs(m_pAdapterList, errbuf);
		if(r == Pcap.NOT_OK||m_pAdapterList.isEmpty()){//not ok 반환값 -1
			System.err.printf("Cant read list of devices, error is %s",errbuf.toString());
			return;
		}
	}
	public void SetAdapterNumber(int iNum){
		m_iNumAdapter = iNum;
		PacketStartDriver();
		Receive();
	}
	public void PacketStartDriver(){
		int snaplen=64*1024;
		int flags = Pcap.MODE_PROMISCUOUS;
		int timeout = 2*1000;
		m_AdapterObject = Pcap.openLive(m_pAdapterList.get(m_iNumAdapter).getName(), snaplen, flags, timeout, errbuf);
	}
	public synchronized boolean Receive(){
		Receive_Thread thread = new Receive_Thread(m_AdapterObject,(EthernetLayer)this.getUpperLayer());
		Thread obj = new Thread(thread);
		obj.start();
		return false;
	}
	public synchronized boolean Send(byte[] input){
		ByteBuffer buf = ByteBuffer.wrap(input);
		if(m_AdapterObject.sendPacket(buf)!=Pcap.OK){//pcap.ok 반환값0
			System.err.println(m_AdapterObject.getErr());
			return false;
		}
		return true;
	}
	public class Receive_Thread implements Runnable{
		byte[] data;
		Pcap AdapterObject;
		BaseLayer UpperLayer;
		public Receive_Thread(Pcap m_AdapterObject,BaseLayer m_UpperLayer){
			AdapterObject = m_AdapterObject;
			UpperLayer = m_UpperLayer;
		}
		@Override
		public void run() {
			while(true){
				PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>(){
					@Override
					public void nextPacket(PcapPacket packet, String user) {				
						data = packet.getByteArray(0, packet.size());
						UpperLayer.Receive(data);
					}
				};
				AdapterObject.loop(100000, jpacketHandler,"");
			}
		}
	}
	public void setUpperLayer(BaseLayer Layer){
		UpperLayer = Layer;
	}
	public void setUnderLayer(BaseLayer Layer){
		UnderLayer = Layer;
	}
}
