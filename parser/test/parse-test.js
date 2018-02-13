'use strict';

const chai = require('chai');
const parser = require('../lib/index');

const expect = chai.expect;
const parse = parser.parse;

describe('parse', function(){

  it('should parse a positive variable',function(){
    let result = parse('x+0');
    expect(result).to.be.a('string');
    expect(result).to.be.equal('{"type":"+","children":[{"type":"Identifier","value":"x"},{"type":"NumericLiteral","value":0}]}');
  });

  it('should parse a nagitive variable',function(){
    let x = -1;
    let result = parse(`x+${x}`);
    expect(result).to.be.a('string');
    expect(result).to.be.equal('{"type":"+","children":[{"type":"Identifier","value":"x"},{"type":"NumericLiteral","value":-1}]}');
  });

  it('should parse a nagitive number',function(){
    let result = parse(`-1`);
    expect(result).to.be.a('string');
    expect(result).to.be.equal('{"type":"NumericLiteral","value":-1}');
  });

  it('should parse  min max function ',function(){
    let result = parse(`max(0,min(1,x))`);
    expect(result).to.be.a('string');
    expect(result).to.be.equal('{"type":"NumericLiteral","value":-1}');
  });




})

