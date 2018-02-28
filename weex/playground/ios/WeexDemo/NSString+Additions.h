/**
 * Created by Weex.
 * Copyright (c) 2016, Alibaba, Inc. All rights reserved.
 *
 * This source code is licensed under the Apache Licence 2.0.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

#import <Foundation/Foundation.h>

@interface NSString(Additions)

- (NSDictionary*)queryDictionaryUsingEncoding:(NSStringEncoding)encoding;
- (NSString*)AliWXAdditionsStringByAddingURLEncodedQueryDictionary:(NSDictionary*)query;
- (NSString*)AliWXAdditionsUrlEncoded;
- (NSDictionary*)queryContentsUsingEncoding:(NSStringEncoding)encoding;

/**
 *	inputCharset转换为对应的编码
 *  utf-8对应NSUTF8StringEncoding，gbk和gb2312对应kCFStringEncodingGB_18030_2000, big5对应kCFStringEncodingBig5_HKSCS_1999
 *
 *	@param 	inputCharset 	utf-8 | gbk | gb2312 | big5
 *
 *	@return	对应的编码
 */
- (NSStringEncoding)AliWXAdditionsEncodingForInputCharset:(NSString *)inputCharset;

/**
 *	使用string包含的_input_charset指定的编码。如taobao://go/about?key1=%B4%BA%BD%DA%26%CF%B0%CB%D7&key2=ni%C4%E1ma%C2%EA&_input_charset=gbk&key3%C8%FD%B8%F6key=value%C8%FD%C8%FD
 taobao://go/about?key1=%E6%98%A5%E8%8A%82%26%E4%B9%A0%E4%BF%97&key2=ni%E5%B0%BCma%E7%8E%9B&_input_charset=utf-8&key3%E4%B8%89%E4%B8%AAkey%3Dvalue%E4%B8%89%E4%B8%89
 taobao://go/about?key=%A4%E2%BE%F7&_input_charset=big5&key2=aaa
 http://list.taobao.com/itemlist/default.htm?cat=1512&sort=coefp&sd=1&as=0&viewIndex=1&commend=all&atype=b&style=grid&pcat=food2011&q=%E6%89%8B%E6%9C%BA&same_info=1&tid=0&isnew=2&_input_charset=utf-8
 *  暂时只支持 utf-8 | gbk | gb2312 | big5
 *  若无_input_charset则返回NSUTF8StringEncoding解码后的数据
 *
 *	@return	query中key-value解码后对应的参数字典
 */
- (NSDictionary*)queryDictionaryUsingInnerInputCharsetEncoding;


@end
