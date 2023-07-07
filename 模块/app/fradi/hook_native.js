Java.perform(function(){
    Interceptor.attach(Module.findExportByName('libnative.so',
    'Java_com_germey_appbasic2_MainActivity_getMessage'),{
        onEnter:function(args){
            send('hook onEnter')
            send('arg[1]='+args[2])
            send('arg[2]='+args[3])
        },
        onLeave:function(val){
            send('hook onLeave')
            val.replace(Java.vm.getEnv().newStringUtf('5'))
        }
    })
})