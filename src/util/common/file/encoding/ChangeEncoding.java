package util.common.file.encoding;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
 * Author: beifeng600
 * 批量转换文件编码
 * // UTF-16、UTF-16BE、UTF-16LE编码方式的区别 
 * // http://jiangzhengjun.iteye.com/blog/622589
 */

public class ChangeEncoding {

	public static final String DefaultEncoding = "UTF-8";
	public static final String UTF8 = "UTF-8";
	public static final String GBK = "GBK";
	public static final String UTF16LE = "UTF-16LE";
	public static final String UTF16BE = "UTF-16BE";
	
	public static String in_encoding = ChangeEncoding.UTF8;
	public static String out_encoding = ChangeEncoding.UTF8;
	
	public static HashMap<String, String> encodingHM = new HashMap<String, String>();
	
	static {
		encodingHM.put(UTF8, "yes");
		encodingHM.put(GBK, "yes");
		encodingHM.put(UTF16LE, "yes");
		encodingHM.put(UTF16BE, "yes");
	}
	
	public static String handleFile(String filePath, String resPath, boolean append){
		  
		List<String> sent_list = new ArrayList<String>();
		
        try {   
        	FileInputStream fileInputStream = new FileInputStream(filePath);
        	InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, in_encoding);
        	BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        	
        	if(resPath==null || "".equalsIgnoreCase(resPath)){
    			resPath = filePath.substring(0, filePath.lastIndexOf('.')) + "_output" + filePath.substring(filePath.lastIndexOf('.'));
    		}
    			
    		FileOutputStream fileOutputStream = new FileOutputStream(resPath, append);
    		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, out_encoding);
    		BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            
	        String strLine = null;
	        int lineNum = 0;
			while((strLine=bufferedReader.readLine())!=null){
				lineNum += 1;
//				if("".equalsIgnoreCase(strLine.trim())){
//					continue;
//				}
				sent_list.add(strLine);
			}
			
			for(int index=0; index<sent_list.size(); ++index){
				String transform_encoding = transform_encoding(sent_list.get(index), out_encoding, index);
				bufferedWriter.write(transform_encoding);
				bufferedWriter.newLine();
			}
			
			bufferedWriter.flush();
			bufferedWriter.close();
			outputStreamWriter.close();
			fileOutputStream.close();
			
			
			bufferedReader.close();
	        inputStreamReader.close();
	        fileInputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	} 
	
	public static boolean validStr(String str){
		if("".equalsIgnoreCase(str.trim())){
			return false;
		}
		//中文Unicode编码:\\u4e00-\\u9fa5
		
		return true;
	}
	
	public static String transform_encoding(String UTF16BE_str, String dst_encoding, int lineNum){
		
		boolean is_first_line = false;
		
		if(lineNum == 1){
			is_first_line = true;
		}
		
		try{
			if(ChangeEncoding.GBK.equalsIgnoreCase(dst_encoding)){
				return UTF16BE_str;
			}else if(ChangeEncoding.UTF8.equalsIgnoreCase(dst_encoding)){
				
				return UTF16BE_str;
				
			}else if(ChangeEncoding.UTF16BE.equalsIgnoreCase(dst_encoding)){
				byte[] headByte = {(byte)0xFE, (byte)0xFF};
				
				byte[] lineBytes = UTF16BE_str.getBytes("UTF-16BE");
				
				if(is_first_line){
					return new String(lineBytes, "UTF-16BE");
				}
				
				byte[] copyArray = new byte[lineBytes.length+2];
				
				System.arraycopy(lineBytes, 0, copyArray, 2, lineBytes.length);
				copyArray[0] = headByte[0];
				copyArray[1] = headByte[1];
				
				return new String(copyArray, "UTF-16BE");

			}else if(ChangeEncoding.UTF16LE.equalsIgnoreCase(dst_encoding)){
				byte[] headByte = {(byte)0xFF, (byte)0xFE};
				
				byte[] lineBytes = UTF16BE_str.getBytes("UTF-16BE");
				
				for(int i=0; i<lineBytes.length-1; i+=2){
					byte temp_byte = lineBytes[i];
					lineBytes[i] = lineBytes[i+1];
					lineBytes[i+1] = temp_byte;
				}
				
				if(is_first_line){
					return new String(lineBytes, "UTF-16LE");
				}
				
				byte[] copyArray = new byte[lineBytes.length+2];
				System.arraycopy(lineBytes, 0, copyArray, 2, lineBytes.length);
				copyArray[0] = headByte[0];
				copyArray[1] = headByte[1];
				
				return new String(copyArray, "UTF-16LE");
			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
	
	public static void handleDir(String inputDir, String resDir){
		
		File rootFile = new File(inputDir);
		if (!rootFile.exists()) {
			System.out.println("Please input a Directory!");
		}
		
		if ("".equalsIgnoreCase(resDir)) {
			resDir = inputDir + "_result";
		}
		
		File resFile = new File(resDir);
		if (!resFile.exists()) {
			resFile.mkdir();
		}

		File[] fileList = rootFile.listFiles();
		for (File file : fileList) {
			if(file.isDirectory()){
				handleDir(file.getAbsolutePath(), resDir+"/"+file.getName());
				
			}else{
				handleFile(file.getAbsolutePath(), resDir+"/"+file.getName(), false);
			}
		}
		
		System.out.println("Complete "+inputDir);
	}
	
	public static void handleDir2File(String inputDir, String resPath){
		
		File rootFile = new File(inputDir);
		if (!rootFile.exists()) {
			System.out.println("Please input a Directory!");
		}
		
		if ("".equalsIgnoreCase(resPath)) {
			resPath = inputDir + "_result";
		}
		
		File[] fileList = rootFile.listFiles();
		for (File file : fileList) {
			if(file.isDirectory()){
				handleDir2File(file.getAbsolutePath(), resPath);
				
			}else{
				handleFile(file.getAbsolutePath(), resPath, true);
			}
		}
		
		System.out.println("Complete "+inputDir);
	}
	
	public static void main(String args[]){
		
		if(args.length < 4){
			System.out.println("java ChangeEncoding input output in_encoding out_encoding");
			return;
		}
		
		String inputPath = args[0];
		String resPath = args[1];
		boolean append = false;
		
		String in_encoding = args[2];
		String out_encoding = args[3];
		
		if(ChangeEncoding.encodingHM.containsKey(in_encoding) && ChangeEncoding.encodingHM.containsKey(out_encoding)){
			ChangeEncoding.in_encoding = in_encoding;
			ChangeEncoding.out_encoding = out_encoding;
		}else{
			System.out.println("Please input correct encoding!");
			Iterator iter = ChangeEncoding.encodingHM.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry entry = (Map.Entry)iter.next();
				System.out.println(entry.getKey());
			}
			
			return;
		}
		
		File inputFile = new File(inputPath);
		if(!inputFile.exists()){
			System.out.println(inputPath+" does not exists!");
			return;
		}
		
		System.out.println("Begin!\t"+(new Date()));
		if(inputFile.isFile()){
			ChangeEncoding.handleFile(inputPath, resPath, append);
		}else{
			File outputFile = new File(resPath);
			if(outputFile.isFile()){
				ChangeEncoding.handleDir2File(inputPath, resPath);
			}else{
				ChangeEncoding.handleDir(inputPath, resPath);
			}
			
		}
		
		System.out.println("Complete!\t"+(new Date()));
	}
}
