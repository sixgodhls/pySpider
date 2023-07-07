Java.perform(()=>{
    let MainActivity = Java.use('com.germey.appbasic1.MainActivity')
    console.log('start hook')
    MainActivity.getMessage.implementation=(arg1,arg2)=>{
        send('start hook')
        return '6'
    }
})