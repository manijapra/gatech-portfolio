from struct import *
import hashlib

class CRPPacket:

	DATA_LEN = 3

	def __init__(self, packet=None, src_port=0, dest_port=0, seq_num=0,
		ack_num=None, data=None):
		if packet != None:
			string = packet.split( )
			self.src_port = string[0]
			self.dest_port = string[1]
			self.seq_num = string[2]
			self.ack_num = string[3]
			self.NACK = int(string[4][0])
			self.ACK = int(string[4][1])
			self.SYN = int(string[4][2])
			self.FIN = int(string[4][3])
			self.BEG = int(string[4][4])
			self.checksum = string[5]
			self.data = string[6]
		else:
			self.src_port = src_port
			self.dest_port = dest_port
			self.seq_num = seq_num
			self.ack_num = ack_num
			self.checksum = None
			self.data = data
			self.ACK = False
			self.NACK = False
			self.SYN = False
			self.FIN = False
			self.BEG = False

	def get_flags(self):
		return str(int(self.NACK)) + str(int(self.ACK)) + \
		str(int(self.SYN)) + str(int(self.FIN)) + str(int(self.BEG))

	def get_packet(self):
		l = str(self.src_port) + ' ' + str(self.dest_port) + ' ' + \
		str(self.seq_num) + ' ' + str(self.ack_num) + ' ' + self.get_flags() + \
		' ' + self.calc_checksum() + ' ' + str(self.data)

		return l

	def calc_checksum(self):
		m = hashlib.md5()
		m.update(str(self.src_port))
		m.update(str(self.dest_port))
		m.update(str(self.seq_num))
		m.update(str(self.ack_num))
		m.update(self.get_flags())

		return m.hexdigest()
