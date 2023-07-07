import frida
import sys
code=(open('./hook_java.js',encoding='utf-8').read())
process_name='com.germey.appbasic1'

def on_message(message,data):
    print(message)

process=frida.get_usb_device().attach(process_name)
script=process.create_script(code)
script.on('message',on_message)
script.load()
sys.stdin.read()