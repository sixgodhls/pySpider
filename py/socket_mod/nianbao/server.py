import socket
import subprocess
import struct
def ret_std(cmd):
    data = subprocess.Popen(cmd.decode('utf-8'), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    stdout = data.stdout.read()
    stderr = data.stderr.read()
    return str(len(stdout)+len(stderr)),stdout+stderr

s=socket.socket(socket.AF_INET,socket.SOCK_STREAM)
#可以写本机ip地址 端口随便写
s.bind(('0.0.0.0',6666))

count=0

while True:
    s.listen()
    count+=1
    client_conn,client_addr=s.accept()
    while True:

        cmd=client_conn.recv(1024)
        if not cmd: break
        #可以把报头部分做成字典形式 就是终极版
        total_size,data=ret_std(cmd)
        #发送报头（长度）
        total_len=struct.pack('i',int(total_size))
        client_conn.send(total_len)

        print(f'get msg{data.decode("gbk")}')
        client_conn.send(data)

    client_conn.close()
s.close()

