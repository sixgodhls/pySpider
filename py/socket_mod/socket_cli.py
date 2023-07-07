import socket

cli=socket.socket(socket.AF_INET,socket.SOCK_STREAM)

cli.connect(('localhost',6666))

while True:
    msg=input('input msg:')

    if not msg:continue
    if msg=='q':break
    cli.send(msg.encode())
    data=cli.recv(1024).decode()
    print(f'get msg:{data}')
