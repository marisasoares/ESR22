from ast import arg
import sys
import socket
import threading
import signal

from argparse import ArgumentParser



HOST = "127.0.0.1"
PORT = 8080

def clientHandler(connection, clientAddress):
    with connection:
        print(f"Connected by {clientAddress}")
        while connection :
            data = connection.recv(1024)
            print(f"Received from [{str(clientAddress[0])}:{str(clientAddress[1])}]: " + str(data,encoding="utf-8"))
            if not data:
                break
            connection.sendall(data)

def respondToRequests(port):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind((HOST, port))
    s.listen()
    while True:
        (connection, clientAdress) = s.accept()
        thread = threading.Thread(target=clientHandler, args=(connection,clientAdress,))
        thread.start()

def sendRequest(destinationIP,destinationPort):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    connected = False
    print("Trying to connect to: " + str(destinationIP) + ":" + str(destinationPort)+" ...")
    while not connected:
        try:
            s.connect((destinationIP,destinationPort))
            connected = True
            print("Connected to " + destinationIP + ":" + destinationPort)
        except Exception as e:
            #print("Error")
            pass 
    while True:
        try:
            string = input("")
        except EOFError:
            break
        s.sendall(bytes(string,"utf-8"))
        data = s.recv(1024)
        print(f"Server on [{destinationIP}:{destinationPort}]: " + str(data,"utf-8"))
    s.close()



def signal_handler(sig, frame):
    print('You pressed Ctrl+C!')
    try:
        sys.exit(0)
    except Exception as e:
        pass


def main():
    signal.signal(signal.SIGINT, signal_handler)
    parser = ArgumentParser("ServidorCliente.py")
    parser.add_argument("-s", "--server", type=int,
                    help="Port the server will listen on", metavar="Server port",required=False)
    parser.add_argument("-c", "--client",
                    help="IP address of the server to connect", metavar="IP",required=False)
    parser.add_argument("-p", "--port", type=int,
                    help="Port of the server to connect to", metavar="port",required=False)
    args = parser.parse_args()
    
    if (args.server != None):
        port = args.server
        serverThread = threading.Thread(target=respondToRequests,args=(port,))
        serverThread.start()
        print("Server hosted on: " + HOST + ":"+ str(port))
        
    if (args.client != None):
        destinationIP = args.client
        destinationPort = args.port
        if(destinationPort == None): destinationPort = 8080
        clientThread = threading.Thread(target=sendRequest,args=(destinationIP,destinationPort,)) 
        clientThread.start()

main()




