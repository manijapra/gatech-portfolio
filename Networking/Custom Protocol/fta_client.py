import sys
import getopt
import string
import threading
from crp import Socket

IP_ADDR = ''
PORT = 0
WINDOW = 1
FILE = ''
sock = Socket()
disconnect = False

def get_user_input():
    global disconnect
    global PORT
    global WINDOW
    global FILE
    while(disconnect != True):
        input_user = raw_input('FTA-client: ')
        input_array = input_user.split(' ')
        if (input_user == 'disconnect'):
            print '\nDisconnecting from FTA-Server...'
            threading.Thread(target = disconnect()).start()
        elif (input_user == 'connect'):
            print '\nConnecting to FTA-Server'
            threading.Thread(target = connect()).start()
        elif (input_array[0] == 'get'):
            if (len(input_array) == 2):
                FILE = input_array[1]
                threading.Thread(target = get()).start()
            else:
                print 'Did not enter a valid File name. Try Again.\n'
        elif (input_array[0] == 'window'):
            if (len(input_array) == 2):
                try:
                    WINDOW = int(input_array[1])
                    print '\nWindow Size set to: ' + str(WINDOW) + '\n'
                except:
                    print 'Did not enter a valid window size. Try Again.\n'
            else:
                print 'Did not enter a valid window size. Try Again.\n'
        elif (input_array[0] == 'post'):
            if (len(input_array) == 2):
                FILE = input_array[1]
                threading.Thread(target = post()).start()
            else:
                print 'Did not enter a valid File name. Try Again.\n'
            #Extra Credit?

def connect():
    global sock
    sock.bind(('0.0.0.0', PORT))
    sock.connect((IP_ADDR, PORT))
    print 'Connection Successful!\n'

def get():
    print '\nFile name to be downloaded set to: ' + FILE + '\n'
    sock.send("Hello, World!")
    #TODO

def disconnect():
    global sock
    global disconnect
    sock.close()
    print 'Disconnection Successful.\n'
    disconnect = True
    #TODO

def post():
    print '\nFile to be uploaded set to: ' + FILE + '\n'
    #post File
    # Extra Credit?
    #TODO

print ''
try:
    opts, args = getopt.getopt(sys.argv[1:],"a:p:w:", ["address=", "port=", "window="])
except getopt.GetoptError, msg:
    print str(msg) + '\n'
    sys.exit()
for opt, arg, in opts:
    if opt in ('-p', '--port'):
        try:
            PORT = int(arg)
            print 'Port set to: ' + arg
        except:
            print '\'' + str(arg) + '\' is not a valid port.'
            sys.exit()
    elif opt in ('-w', '--window'):
        try:
            WINDOW = int(arg)
            print 'Window Size set to: ' + arg
        except:
            print '\'' + str(arg) + '\' is not a valid window size.'
            sys.exit()
    elif opt in ('-a', '--address'):
        try:
            IP_ADDR = arg
            print 'IP Address of Server set to: ' + arg
        except:
            print '\'' + str(arg) + '\' is not a valid IP address.'
            sys.exit()

if PORT == 0 or IP_ADDR == '':
        s = 'Please enter a '
        missing_s = ''
        if PORT == 0:
            missing_s += '|Port|'
        if IP_ADDR == '':
            missing_s += '|IP Address|'
        print s + missing_s + ".\n"
        sys.exit()

print ''

user_io_thread = threading.Thread(target = get_user_input())

user_io_thread.start()
