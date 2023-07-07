const code = `
let x='1'+1
console.log('x',x)
`

const options = {
    compact: false,//压缩代码
    controlFlowFlattening: false, //控制流平坦化
    //变量名混淆
    identifierNamesGenerator:
        'hexadecimal' //十六进制变量名
    // 'mangled' //a，b形式变量名
    ,
    identifiersPrefix: 'aaaaaaa',//增加变量名前缀
    renameGlobals: true, //混淆全局变量和函名称
    //字符串混淆
    // stringArray:true, //默认开启 
    // rotateStringArray:true, //控制数组化后结果的元素顺序
    // stringArrayEncoding:true, //字符串编码格式 默认不开启 true或base64使用base64编码，rc4 使用RC4编码
    // stringArrayThreshold:0.8, //控制启用编码的概率 默认0.8
    //代码自我保护
    selfDefending: true, //开启之后如果对代码格式化则无法运行
    deadCodeInjection:true, //无用代码注入
    transformObjectKeys:true, //键值替换
    disableConsoleOutput:true, //禁止控制台调用
    domainLock:['xxx.com'], //锁定域名 只能在指定域名下运行
}

const obfuscator = require('javascript-obfuscator')
function obfuscate(code, options) {
    return obfuscator.obfuscate(code, options).getObfuscatedCode()
}
console.log(obfuscate(code, options))


