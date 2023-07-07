import socket

s=socket.socket(socket.AF_INET,socket.SOCK_STREAM)

s.bind(('0.0.0.0',6666))
s.listen()
count=0
while True:
    count+=1
    client_conn,client_addr=s.accept()
    print(count)
    while True:
        data=client_conn.recv(1024)
        if not data:break
        print(f'get msg{data}')
        client_conn.send(data.decode().upper().encode())

