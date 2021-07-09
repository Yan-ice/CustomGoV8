package fw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileMng {
	  public static void copyDir(File source, File target)
	  {
	    if (source.isDirectory()){
	      if (!target.exists()) {
	        target.mkdirs();
	      }
	      if(!source.getName().equals("playerdata") && !source.getName().equals("stats")){
	    	  for (String el : source.list()){
	  	        if (!el.equals("uid.dat") && !el.equals("session.lock")) {
	  	          copyDir(new File(source, el), new File(target, el));
	  	        }
	  	      }
	      }
	      
	    }else{
	      try{
	        if (!target.getParentFile().exists())
	        {
	          new File(target.getParentFile().getAbsolutePath()).mkdirs();
	          target.createNewFile();
	        }
	        else if (!target.exists())
	        {
	          target.createNewFile();
	        }
	        InputStream in = new FileInputStream(source);
	        Object out = new FileOutputStream(target);
	        
	        byte[] buf = new byte[1024];
	        int len;
	        while ((len = in.read(buf)) > 0) {
	          ((OutputStream)out).write(buf, 0, len);
	        }
	        in.close();
	        ((OutputStream)out).close();
	      }
	      catch (Exception exception)
	      {
	      }
	    }
	  }
	  
	  public static boolean deleteDir(File dir)
	  {
	    if (dir.isDirectory()) {
	      for (File f : dir.listFiles()) {
	        if (!deleteDir(f)) {
	          return false;
	        }
	      }
	    }
	    return dir.delete();
	  }
	  
	  static String basePath;
	  public static void unZip(File srcFile, String destDirPath){
		          if (!srcFile.exists()) {
		              throw new RuntimeException(srcFile.getPath() + "文件不存在");
		          }
		          ZipFile zipFile = null;
		          try {
		              zipFile = new ZipFile(srcFile,Charset.forName("GBK"));
		              Enumeration<?> entries = zipFile.entries();
		              while (entries.hasMoreElements()) {
		                  ZipEntry entry = (ZipEntry) entries.nextElement();
		                  if (entry.isDirectory()) {
		                      String dirPath = destDirPath + "/" + entry.getName();
		                      File dir = new File(dirPath);
		                      dir.mkdirs();
		                  } else {
		                      File targetFile = new File(destDirPath + "/" + entry.getName());

		                      if(!targetFile.getParentFile().exists()){
		                          targetFile.getParentFile().mkdirs();
		                      }
		                      targetFile.createNewFile();

		                      InputStream is = zipFile.getInputStream(entry);
		                      FileOutputStream fos = new FileOutputStream(targetFile);
		                      int len;
		                      byte[] buf = new byte[1024];
		                      while ((len = is.read(buf)) != -1) {
		                          fos.write(buf, 0, len);
		                      }
		                      // 关流顺序，先打开的后关闭
		                      fos.close();
		                      is.close();
		                  }
		              }
		          } catch (Exception e) {
		        	  e.printStackTrace();
		              //throw new RuntimeException("unzip error from ZipUtils", e);
		          } finally {
		              if(zipFile != null){
		                  try {
		                      zipFile.close();
		                  } catch (IOException e) {
		                      e.printStackTrace();
		                  }
		              }
		          }
		      }
}
