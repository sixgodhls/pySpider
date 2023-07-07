# -*- encoding=utf8 -*-
__author__ = "LENOVO"

from airtest.core.api import *

auto_setup(__file__)
touch(Template(r"tpl1660615818415.png", record_pos=(0.355, 0.299), resolution=(1080, 2340)))
wait(Template(r"tpl1660616734692.png", record_pos=(-0.006, -0.607), resolution=(1080, 2340)))

swipe(Template(r"tpl1660615892267.png", record_pos=(-0.008, 0.073), resolution=(1080, 2340)), vector=[-0.0099, -0.4024])
keyevent("HOME")
