package ipc;

import java.util.ArrayList;

public class ChatAppLayer implements BaseLayer {
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();

	private class _CAPP_HEADER {
		int capp_src;
		int capp_dst;
		byte[] capp_totlen;
		byte[] capp_data;

		public _CAPP_HEADER() {
			this.capp_src = 0x00000000;
			this.capp_dst = 0x00000000;
			this.capp_totlen = new byte[2];
			this.capp_data = null;
		}
	}

	_CAPP_HEADER m_sHeader = new _CAPP_HEADER();

	public ChatAppLayer(String pName) {
		// super(pName);
		// TODO Auto-generated constructor stub
		pLayerName = pName;
		ResetHeader();
	}

	public void ResetHeader() {
		for (int i = 0; i < 2; i++) {
			m_sHeader.capp_totlen[i] = (byte) 0x00;
		}
		m_sHeader.capp_data = null;
	}

	public byte[] ObjToByte(_CAPP_HEADER Header, byte[] input, int length) {
		byte[] buf = new byte[length + 6];
		byte[] srctemp = intToByte2(Header.capp_src);
		byte[] dsttemp = intToByte2(Header.capp_dst);

		buf[0] = dsttemp[0];
		buf[1] = dsttemp[1];
		buf[2] = srctemp[0];
		buf[3] = srctemp[1];
		buf[4] = (byte) (length % 256);
		buf[5] = (byte) (length / 256);

		for (int i = 0; i < length; i++)
			buf[6 + i] = input[i];

		return buf;
	}

	public boolean Send(byte[] input, int length) {
		/*
		 * ����
		 * 
		 */
		return true;
	}

	public byte[] RemoveCappHeader(byte[] input, int length) {
		
	/*
	 * ����
	 * 
	 * 
	 * */	
		return input;// �����ϼ��� �ʿ��Ͻø�
	}

	public synchronized boolean Receive(byte[] input) {
		byte[] data;
		byte[] temp_src = intToByte2(m_sHeader.capp_src);
		for (int i = 0; i < 2; i++) {
			if (input[i] != temp_src[i]) {
				return false;
			}
		}
		data = RemoveCappHeader(input, input.length);
		this.GetUpperLayer(0).Receive(data);
		// �ּҼ���
		return true;
	}

	byte[] intToByte2(int value) {
		byte[] temp = new byte[2];
		temp[1] = (byte) (value >> 8);
		temp[0] = (byte) value;

		return temp;
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

	public void SetEnetSrcAddress(int srcAddress) {
		// TODO Auto-generated method stub
		m_sHeader.capp_src = srcAddress;
	}

	public void SetEnetDstAddress(int dstAddress) {
		// TODO Auto-generated method stub
		m_sHeader.capp_dst = dstAddress;
	}

}
