import traverse from '@babel/traverse'
import generate from '@babel/generator'
import {parse} from '@babel/parser'
import * as types from '@babel/types'
import fs from 'fs'
import { stringify } from 'querystring'
import { type } from 'os'

const code=`const a=!![];
const b='abc'=='bcd'
const c=(a<<3)|3
const d=parseInt('5'+0)`

let ast=parse(code)

traverse(ast,{
    'UnaryExpression|BinaryExpression|ConditionalExpression|CallExpression':(
        path
    )=>{

        const {confident,value}=path.evaluate();
        console.log(confident,value)
        if(value==Infinity ||value ==-Infinity)return;
        confident && path.replaceWith(types.valueToNode(value));
    },
});

const {code:output}=generate(ast)
console.log(output)