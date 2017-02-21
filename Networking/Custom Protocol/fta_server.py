import sys
import getopt
import threading
from crp import Socket
from crp import Status

PORT = 0
debug = False
WINDOW = 1
sock = Socket()
sock.is_server = True
terminate = False
available = True


"""
def close_server():
    sock.close()
    print "Server Terminated."

def get_user_input():
    global available
    global terminate
    while(terminate != True):
        input_user = raw_input('FTA-server: ')
        input_array = input_user.split(' ')

        if (input_user == 'terminate'):
            print '\nTerminating FTA-Server...'
            terminate = True
        elif (input_array[0] == 'window'):
            WINDOW = int(input_array[1])
            print '\nWindow Size set to: ' + str(WINDOW) + '\n'
        lock.release()
"""

def run_server():
    global terminate
    sock.bind(('0.0.0.0', PORT))
    while(terminate != True):
        if (sock.connection_status == Status.LISTEN):
            print('Listening for a Client...')
            sock.listen()
            sock.accept()
        else:
            while (sock.connection_status != Status.LISTEN):
                print('Connected to Client...')
                sock.rec(sock.recv_window)
    sock.close()
    print "Server Terminated."
    #TODO


print ''
try:
    opts, args = getopt.getopt(sys.argv[1:],"p:w:td", ["port=", "window=", "debug", "terminate"])
except getopt.GetoptError, msg:
    print str(msg) + '\n'
    sys.exit()
for opt, arg in opts:
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
    elif opt in ('-t', '--terminate'):
        print '\nTerminating Server...'
        sys.exit()
    elif opt in ('-d', '--debug'):
        debug = True
        print 'Debug: Enabled'

if PORT == 0:
        s = 'Please enter a '
        missing_s = ''
        if PORT == 0:
            missing_s += '|Port|'
        print s + missing_s + ".\n"
        sys.exit()

print ''

#server_listen_thread = threading.Thread(target = run_server)
#server_listen_thread.setDaemon(1)
#user_io_thread = threading.Thread(target = get_user_input)
#user_io_thread.setDaemon(1)

#server_listen_thread.start()
#user_io_thread.start()

run_server()
sock.close()
print "Server Terminated."
#user_io_thread.join()
