# -*- encoding=utf8 -*-
__author__ = "LENOVO"

from airtest.core.api import *

auto_setup(__file__)


from  poco.drivers.android.uiautomation import AndroidUiautomationPoco
poco=AndroidUiautomationPoco(use_airtest_input=True,screenshot_each_action=False)
poco("App1").click()
poco("android.support.v7.widget.RecyclerView").wait_for_appearance(10)
poco("android.widget.FrameLayout").child("android.widget.LinearLayout").offspring("com.goldze.mvvmhabit:id/content").offspring("android.support.v7.widget.RecyclerView").child("com.goldze.mvvmhabit:id/item")[3].swipe([0,-0.1])
keyevent('HOME')poco("com.goldze.mvvmhabit:id/action_bar_root")