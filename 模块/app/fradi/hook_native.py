import frida
import sys
code=open('./hook_native.js',encoding='utf-8').read()
process_name='com.germey.appbasic2'

def get_message(meassage,data):
    print(meassage)

process=frida.get_usb_device().attach(process_name)
script=process.create_script(code)
script.on('message',get_message)
script.load()
sys.stdin.read()