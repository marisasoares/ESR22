import socket

from pexpect import EOF

HOST = "127.0.0.1"  # The server's hostname or IP address
PORT = 8080  # The port used by the server

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.connect((HOST, PORT))
    while True:
        try:
            string = input()
        except EOFError:
             break
        s.sendall(bytes(string,"utf-8"))
        data = s.recv(1024)
        print("Server response: " + str(data,"utf-8"))
s.close()
