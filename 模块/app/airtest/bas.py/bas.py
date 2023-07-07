from airtest.core.android import Android
from airtest.core.api import *
import logging

logging.getLogger('airtest').setLevel(logging.WARNING)

device:Android=init_device('Android')
is_lock=device.is_locked()
print(f'is_locked:{is_lock}')
if is_lock:device.unlock()
device.wake()
# app_list=device.list_app()
# print(f'app_list:{app_list}')
# ip_addr=device.get_ip_address()
# print(f'ip_addr:{ip_addr}')
package="com.goldze.mvvmhabit"
start_app(package)
sleep(10)
stop_app(package)