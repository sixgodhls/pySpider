import {parse} from '@babel/parser'
import fs from 'fs'
import generate from '@babel/generator'
import traverse from '@babel/traverse'
const code=fs.readFileSync('./codes/code1.js','utf-8')
let ast=parse(code)
// traverse(ast,{
//     enter(path){
//         let node=path.node;
//         if (node.type==='NumericLiteral' && node.value===3){
//             node.value=5
//         }
//         if (node.type==='StringLiteral' && node.value==='hello'){
//             node.value='hi'
//         }
//     },
// });
traverse(ast,{
    CallExpression(path){
        let node=path.node;
        if (
            node.callee.object.name==='console'&&
            node.callee.property.name==='log'
        ){
            path.remove()
        }
    }
})
const {code:output}=generate(ast,{
    retainLines:true
})
console.log(output)