说明文件

java -Dfile.encoding=utf-8 -mx1000m -jar ChangeEncoding-1.0.jar inputPath outPath in_encoding out_encoding

in_encoding 是输入的文本编码
out_encoding 是输出的文本编码

其中输入，可以是单个文件，或者文件夹
输出可以是文件夹，也可以是单个文件（但必须是已存在的文件）。


-------------------------------------
修改日志

1.0		现在能批量处理UTF8(UTF-8)、GBK(GBK)之间的编码转换	20150206 15:51:00
1.1		添加新的编码UTF16LE(UTF-16LE)、UTF16BE(UTF-16BE)支持，	20150312	0:00:00
