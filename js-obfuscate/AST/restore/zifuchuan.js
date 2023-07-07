import traverse from '@babel/traverse'
import generate from '@babel/generator'
import {parse} from '@babel/parser'
import * as types from '@babel/types'
import fs from 'fs'
const code=fs.readFileSync('../codes/code2.js','utf-8');
let ast=parse(code);
traverse(ast,{
    StringLiteral({node}){
        if(node.extra && /\\[ux]/gi.test(node.extra.raw)){
            node.extra.raw=node.extra.rawValue
        }
    },
});

const {code:output}=generate(ast)
console.log(output)