import socket
import threading

HOST = "127.0.0.1"
PORT = 8080


def clientHandler(connection, clientAddress):
    with connection:
        print(f"Connected by {clientAdress}")
        while connection :
            data = connection.recv(1024)
            if not data:
                break
            connection.sendall(data)

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.bind((HOST, PORT))
    s.listen()
    while True:
        connection, clientAdress = s.accept()
        thread = threading.Thread(target=clientHandler, args=(connection,clientAdress,))
        thread.start()
