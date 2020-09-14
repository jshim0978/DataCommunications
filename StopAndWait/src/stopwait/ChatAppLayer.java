package stopwait;

import java.util.ArrayList;


public class ChatAppLayer implements BaseLayer {
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();

	private class _CAPP_APP {
		byte[] capp_totlen;
		byte capp_type;
		byte capp_unused;
		byte[] capp_data;
		public _CAPP_APP() {
			this.capp_totlen = new byte[2];
			this.capp_type = 0x00;
			this.capp_unused = 0x00;
			this.capp_data = null;
		}
	}
	_CAPP_APP Packet = new _CAPP_APP();
	
	public ChatAppLayer(String pName) {
		// super(pName);
		// TODO Auto-generated constructor stub
		pLayerName = pName;
		ResetHeader();
	}
	public void ResetHeader() {
		for (int i = 0; i < 2; i++) {
			Packet.capp_totlen[i] = (byte) 0x00;
			
		}	
	}
	public byte[] Obj2Byte(_CAPP_APP Packet, byte[] input,int length) {
		byte[] buf = new byte[length + 4];
		byte[] totlen = Packet.capp_totlen;
		buf[0] = totlen[0];
		buf[1] = totlen[1];
		buf[2] = Packet.capp_type;
		buf[3] = Packet.capp_unused;
		for (int i = 0; i < length; i++)
			buf[4 + i] = input[i];
		return buf;
	}

	public boolean Send(byte[] input, int length) {
		
		Packet.capp_data = input;
		Packet.capp_totlen[0] = (byte) (length%256);
		Packet.capp_totlen[1] = (byte) (length/256);
		byte[] buf = new byte[length +4];
		buf[0] = Packet.capp_totlen[0];
		buf[1] = Packet.capp_totlen[1];
		buf[2] = 0x00;
		buf[3] = 0x00;
		for (int i = 0; i < length; i++) {
			buf[i+4] = input[i];
		}
		this.GetUnderLayer().Send(buf, length + 4);
		return true;
	}
	
	public byte[] RemoveCappHeader(byte[] input, int length) {
		for (int i = 0; i < (input.length - 4); i++) {
			input[i] = input[i+4];
		}
		return input;
	}
	public synchronized boolean Receive(byte[] input) {
		byte[] data;
		data = RemoveCappHeader(input, input.length);
		this.GetUpperLayer(0).Receive(data);
		return true;
	}
	@Override
	public String GetLayerName() {
		// TODO Auto-generated method stub
		return pLayerName;
	}
	@Override
	public BaseLayer GetUnderLayer() {
		// TODO Auto-generated method stub
		if (p_UnderLayer == null)
			return null;
		return p_UnderLayer;
	}
	@Override
	public BaseLayer GetUpperLayer(int nindex) {
		// TODO Auto-generated method stub
		if (nindex < 0 || nindex > nUpperLayerCount || nUpperLayerCount < 0)
			return null;
		return p_aUpperLayer.get(nindex);
	}
	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		// TODO Auto-generated method stub
		if (pUnderLayer == null)
			return;
		this.p_UnderLayer = pUnderLayer;
	}
	@Override
	public void SetUpperLayer(BaseLayer pUpperLayer) {
		// TODO Auto-generated method stub
		if (pUpperLayer == null)
			return;
		this.p_aUpperLayer.add(nUpperLayerCount++, pUpperLayer);
		// nUpperLayerCount++;
	}
	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		this.SetUpperLayer(pUULayer);
		pUULayer.SetUnderLayer(this);
	}
}
