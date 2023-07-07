class Person:

    def __init__(self,name,reso):
        self.name=name
        self.reso=reso
class Relesion:
    def __init__(self):
        self.coup=[]
    def relesionship(self,obj1,obj2):
        self.obj1=obj1
        self.obj2=obj2
        self.coup=[self.obj1,self.obj2]
        return self.coup
    def get_parter(self,obj):
        for i in self.coup:
            if i.name!=obj.name:
                print(i.name)
res=Relesion()
p1=Person('yyy',res)
p2=Person('xxx',res)

a=res.relesionship(p1,p2)
res.get_parter(p2)
print(a)