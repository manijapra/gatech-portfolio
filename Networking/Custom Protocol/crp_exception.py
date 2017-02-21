class CRPException(Exception):

	CHECKSUM_INVALID = 1
	TIMEOUT = 2
	UNEXPECTED_PACKET = 3
	MISMATCH = 4
	RESEND_LIMIT = 5

	DEFAULT_MESSAGE = {
		CHECKSUM_INVALID: "Invalid Checksum",
		TIMEOUT: "Connection timeout",
		UNEXPECTED_PACKET: "Unexpected packet type",
		MISMATCH: "Sequence mismatch",
		RESEND_LIMIT: "Maximum reset limit reached"
	}

	def __init__(self, type_, message=None, innerException=None):
		self.type = type_
		self.inner = innerException
		if message is None:
			self.message = CRPException.DEFAULT_MESSAGE[type_]
		else:
			self.message = message

	def __str__(self):
		return self.message
