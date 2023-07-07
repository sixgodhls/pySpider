import socket
import struct
cli=socket.socket(socket.AF_INET,socket.SOCK_STREAM)

cli.connect(('localhost',6666))

while True:
    cmd=input('input msg:')

    if not cmd:continue
    if cmd=='q':break
    cli.send(cmd.encode('utf-8'))
    #接收报头 长度
    total_len=struct.unpack('i',cli.recv(4))[0]

    recv_len=0
    recv_data=b''
    while recv_len<total_len:
        data=cli.recv(1024)
        recv_data+=data
        recv_len+=len(data)
    print(f'get msg:{recv_data.decode("gbk")}')
