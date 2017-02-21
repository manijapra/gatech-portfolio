import socketF
from crp_exception import CRPException
from crp_packet import CRPPacket
from collections import deque

class Status:
    CLOSED = 0
    SEND = 1
    RECV = 2
    WAIT = 3
    LISTEN = 4
    CONNECTED = 5
    IDLE = 6

class Socket:

    def __init__(self):

        self.socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

        self.recv_window = 65485 #Subject to change

        self.send_window = 1

        self.dest_addr = None

        self.src_addr = None

        self.sequence_num = 0

        self.ack_num = 0

        self.connection_status = Status.CLOSED

        self.is_server = False

        self.resend_limit = 50


    def bind(self, src_addr):
        if src_addr:
            self.src_addr = src_addr

        if self.is_server:
            self.connection_status = Status.LISTEN

        if self.src_addr:
            self.socket.bind(src_addr)
        else:
            raise CRPException('Source address not specified.')

    def connect(self, dest_addr):
        if self.src_addr is None:
            raise CRPException('Socket does not have a source address to bind to.')

        self.dest_addr = dest_addr

        self.sequence_num = 0

        syn_ack = self.send_SYN()

        self.ack_num = int(syn_ack.seq_num) + 1
        #ack_num = syn_ack.header.fields["seq"]
        #self.ack_num.set(ack_num + 1)

        self.send_ACK()

        self.sender = True

        self.connection_status = Status.CONNECTED
        print("Connection successful")

    def listen(self):
        self.connection_status = Status.LISTEN
        recv_pckt = None
        while True:
            try:
                recv_data, recv_addr = self.rec(self.recv_window)
                recv_pckt = CRPPacket(packet=recv_data)
            except CRPException as exception:
                if (exception.type == CRPException.CHECKSUM_INVALID):
                    continue
            else:
                if recv_pckt != None and recv_pckt.SYN:
                    break

 		self.ack_num = int(recv_pckt.seq_num) + 1
        self.dest_addr = recv_addr

    def accept(self):
    	self.sequence_num = 0
    	self.send_SYNACK()
    	self.sender = False
    	self.connection_status = Status.IDLE

    def send(self, message):
      self.socket.sendto(message, self.dest_addr)
      """
    	data_list = deque()
    	packets_list = deque()
    	sent = deque()

    	#chop message into segments
    	for i in range(0, len(message), CRPPacket.DATA_LEN):
    		if i + CRPPacket.DATA_LEN > len(message):
    			data_list.append(message[i:])
    		else:
    			data_list.append(message[i:i + CRPPacket.DATA_LEN])

    	#make each chopped message into a packet
    	for data in data_list:
    		pckt = CRPPacket(data=message, src_port=self.src_addr[1], dest_port=self.dest_addr[1], \
    		seq_num=self.sequence_num)
    		self.sequence_num += 1
    		packets_list.append(pckt)

    	#send each packet
    	resends_rem = self.resend_limit
    	while len(packets_list) > 0 and resends_rem > 0:
    		send_w = self.send_window

    		#send packet until send_w is 0 or all packets sent
    		while send_w > 0 and len(packets_list) > 0:
	    		pckt = packets_list.popleft()

	    		self.send_packet(pckt.get_packet())
	    		#lastSeqNum = packet.header.fields["seq"]

	    		send_w -= 1
	    		sent.append(pckt)

	    	#wait for ack
	    	try:
	    		self.socket.settimeout(5)
	    		recv_data, recv_addr = self.recv_packet(self.recv_window)
	    		recv_pckt = CRPPacket(data=recv_data)
	    	except socket.timeout:
	    		#reset send window and resend
	    		send_w = 1
	    		resends_rem -= 1

	    		#add sent back to front of packets_list and clear sent
	    		sent.reverse()
	    		packets_list.extendleft(sent)
	    		sent.clear()
	    	except CRPException as exception:
	    		if (exception.type == CRPException.CHECKSUM_INVALID):
	    			continue
	    	else:
	    		send_w += 1
	    		#check for ack mismatching
	    		if int(recv_pckt.ack_num) - 1 != sequence_num:
	    			num_off = int(recv_pckt.ack_num) - sequence_num - 1

	    			while num_off < 0:
	    				packets_list.appendleft(sent.pop())
	    				num_off += 1
	    		#if SYNACK, resend everything that was sent
	    		elif recv_pckt != None and recv_pckt.ACK and recv_pckt.SYN:
	    			self.send_ACK()
	    			resends_rem = self.resend_limit

	    			sent.reverse()
	    			packets_list.extendleft(sent)
	    			sent.clear()
	    		#if ACK, send window back to original and remove ack'd packet from sent
	    		elif recv_pckt != None and recv_pckt.ACK:
	    			sequence_num = recv_pckt.ack_num
	    			resends_rem = self.resend_limit
	    			if len(sent) > 0:
	    				sent.popleft()


        # send_pckt = CRPPacket(data=message, dest_port=self.dest_addr[1], src_port=self.src_addr[1])
        # send_packet = send_pckt.get_packet()
        # resends_rem = self.resend_limit
        # recv_pckt = None

        # while resends_rem > 0:
        #     self.socket.settimeout(5)
        #     self.socket.sendto(send_packet, self.dest_addr)

        #     try:
        #         recv_data, recv_addr = self.rec(self.recv_window)
        #         recv_pckt = CRPPacket(packet=recv_data)
        #     except CRPException as exception:
        #         if (exception.type == CRPException.CHECKSUM_INVALID):
        #             continue
        #     except socket.timeout:
        #         resends_rem -= 1
        #     else:
        #         if recv_pckt != None and recv_pckt.ACK:
        #             break

        if resends_rem == 0:
        	raise CRPException(CRPException.TIMEOUT)
         """

    def send_packet(self, packet):
        self.socket.sendto(packet, self.dest_addr)

    def rec(self, recv_window):
    	#if self.connection_status != Status.IDLE:
    	#	raise CRPException("Status is not idle!")

    	resends_rem = self.resend_limit
    	while resends_rem > 0:
    		try:
    			self.socket.settimeout(5)
    			recv_data, recv_addr = self.recv_packet(self.recv_window)
    		except socket.timeout:
    			resends_rem -= 1
    			continue
    		else:
    			recv_pckt = CRPPacket(packet=recv_data)
    			if recv_pckt.calc_checksum() == recv_pckt.checksum and recv_pckt.data != str(None) \
		        and self.connection_status == Status.CONNECTED:
		            print (recv_pckt.data)
		            self.send_ACK()
		        elif recv_pckt.calc_checksum() != recv_pckt.checksum:
		            raise CRPException(CRPException.CHECKSUM_INVALID)

		        if recv_pckt.FIN and not recv_pckt.ACK:
		            self.send_FINACK()
		            print("Disconnected from Client.")
		            self.connection_status = Status.LISTEN
		            return (recv_data, recv_addr)
		        else:
		            return (recv_data, recv_addr)

		if resend_rem == 0:
			raise CRPException(CRPException.TIMEOUT)

        # recv_data, address = self.socket.recvfrom(recv_window)

        # recv_pckt = CRPPacket(packet=recv_data)

        # if recv_pckt.calc_checksum() == recv_pckt.checksum and recv_pckt.data != str(None) \
        # and self.connection_status == Status.CONNECTED:
        #     print (recv_pckt.get_packet())
        #     self.send_ACK()
        # elif recv_pckt.calc_checksum() != recv_pckt.checksum:
        #     raise CRPException(CRPException.CHECKSUM_INVALID)

        # if recv_pckt.FIN and not recv_pckt.ACK:
        #     self.send_FINACK()
        #     print("Disconnected from Client.")
        #     self.connection_status = Status.LISTEN
        #     return (recv_data, address)
        # else:
        #     return (recv_data, address)
    def recv_packet(self, recv_window):
		while True:
			try:
				recv_data, recv_addr = self.socket.recvfrom(recv_window)
				break
			except socket.error as exception:
				if exception.errno == 35:
					continue
				else:
					raise exception
		return (recv_data, recv_addr)

    def close(self):
        if self.src_addr is None:
            raise CRPException('Socket does not have a source address to bind to.')

        self.sequence_num += 1

        fin_ack = None

        if (self.dest_addr is not None):
            fin_ack = self.send_FIN()

        if fin_ack != None and fin_ack.ACK and fin_ack.FIN:
            self.send_ACK()
            self.socket.close()
            self.sender = False

            self.connection_status = Status.CLOSED
            print("Disconnected from server.")
        else:
            self.socket.close()

    def send_SYN(self):
        syn_pckt = CRPPacket(src_port=self.src_addr[1], dest_port=self.dest_addr[1], seq_num=self.sequence_num)
        syn_pckt.SYN = 1
        self.sequence_num += 1
        resends_rem = self.resend_limit
        recv_pckt = None

        while resends_rem > 0:
            self.send_packet(syn_pckt.get_packet())
            try:
                recv_data, recv_addr = self.rec(self.recv_window)
                recv_pckt = CRPPacket(packet=recv_data)
            except CRPException as exception:
                if (exception.type == CRPException.CHECKSUM_INVALID):
                    continue
            else:
                if recv_pckt != None and recv_pckt.SYN and recv_pckt.ACK:
                    break

        if resends_rem == 0:
        	raise CRPException(CRPException.TIMEOUT)

        return recv_pckt

    def send_FIN(self):
        fin_pckt = CRPPacket(src_port=self.src_addr[1], dest_port=self.dest_addr[1], seq_num=0)
        fin_pckt.FIN = 1
        self.sequence_num += 1
        resends_rem = self.resend_limit
        recv_pckt = None

        while resends_rem > 0:
            self.send_packet(fin_pckt.get_packet())
            try:
                recv_data, recv_addr = self.rec(self.recv_window)
                recv_pckt = CRPPacket(packet=recv_data)
            except CRPException as exception:
                if (exception.type == CRPException.CHECKSUM_INVALID):
                    continue
            else:
                if recv_pckt != None and recv_pckt.FIN and recv_pckt.ACK:
                    break

        if resends_rem == 0:
        	raise CRPException(CRPException.TIMEOUT)

        return recv_pckt

    def send_ACK(self):
    	ack_pckt = CRPPacket(src_port=self.src_addr[1], dest_port=self.dest_addr[1], ack_num=self.ack_num)
        ack_pckt.ACK = 1
        self.send_packet(ack_pckt.get_packet())

    def send_SYNACK(self):
        syn_ack_pckt = CRPPacket(src_port=self.src_addr[1], dest_port=self.dest_addr[1], \
        	seq_num=self.sequence_num, ack_num=self.ack_num)
        syn_ack_pckt.SYN = 1
        syn_ack_pckt.ACK = 1
        self.sequence_num += 1
        resends_rem = self.resend_limit
        recv_pckt = None

        while resends_rem > 0:
            self.send_packet(syn_ack_pckt.get_packet())
            try:
                recv_data, recv_addr = self.rec(self.recv_window)
                recv_pckt = CRPPacket(packet=recv_data)
            except CRPException as exception:
                if (exception.type == CRPException.CHECKSUM_INVALID):
                    continue
            else:
                if recv_pckt != None and recv_pckt.SYN:
                    resends_rem = self.resend_limit
                elif recv_pckt != None and recv_pckt.ACK:
                    break

        if resends_rem == 0:
        	raise CRPException(CRPException.TIMEOUT)

    def send_FINACK(self):
        fin_ack_pckt = CRPPacket(src_port=self.src_addr[1], dest_pstevejobsort=self.dest_addr[1], \
        	seq_num=self.sequence_num, ack_num=self.ack_num)
        fin_ack_pckt.FIN = 1
        fin_ack_pckt.ACK = 1
        self.sequence_num += 1
        resends_rem = self.resend_limit
        recv_pckt = None

        while resends_rem > 0:
            self.send_packet(fin_ack_pckt.get_packet())
            try:
                recv_data, recv_addr = self.rec(self.recv_window)
                recv_pckt = CRPPacket(packet=recv_data)
            except CRPException as exception:
                if (exception.type == CRPException.CHECKSUM_INVALID):
                    continue
            else:
                if recv_pckt != None and recv_pckt.ACK:
                    break
                elif recv_pckt != None and recv_pckt.FIN:
                    resends_rem = self.resend_limit

        if resends_rem == 0:
        	raise CRPException(CRPException.TIMEOUT)
