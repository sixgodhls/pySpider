import {parse} from '@babel/parser'
import fs from 'fs'
import generate from '@babel/generator'
const code=fs.readFileSync('./codes/code1.js','utf-8')
let ast=parse(code)
const { code:output }=generate(ast,{
    retainLines:true
})
// console.log(ast)
// console.log(ast.program.body)
// console.log(output) 