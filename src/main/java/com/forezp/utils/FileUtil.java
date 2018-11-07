package com.forezp.utils;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by liangbing on 2018/5/16.
 * Desc:
 */
public class FileUtil {

    public static void splitFileDemo(File src, int m) throws IOException {
        if (src.isFile()) {
            //获取文件的总长度
            long l = src.length();
            //获取文件名
            String fileName = src.getName().substring(0, src.getName().indexOf("."));
            //获取文件后缀
            String endName = src.getName().substring(src.getName().lastIndexOf("."));
            InputStream in = null;
            try {
                in = new FileInputStream(src);
                for (int i = 1; i <= m; i++) {
                    StringBuffer sb = new StringBuffer();
                    sb.append(src.getParent());
                    sb.append(DefineUtils.SEPARATOR);
                    sb.append(fileName);
                    sb.append("_data");
                    sb.append(i);
                    sb.append(endName);
                    System.out.println(sb.toString());

                    File file2 = new File(sb.toString());
                    //创建写文件的输出流
                    OutputStream out = new FileOutputStream(file2);
                    int len = -1;
                    byte[] bytes = new byte[DefineUtils.ONE_MB];
                    while ((len = in.read(bytes)) != -1) {
                        out.write(bytes, 0, len);
                        long fl2 = file2.length();
                        long lm = (l/m);
                        if (file2.length() > (l / m)) {
                            break;
                        }
                    }
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null)
                    in.close();
            }
            System.out.println("--- 文件分割完成 ---");
        }
    }

    public static void joinFileDemo(String[] src) {
        //获取合并文件
        File newFile = new File(src[0].toString());
        //获取文件名后缀
        String fileName = newFile.getName().substring(0, newFile.getName().indexOf("_data"));
        String endName = newFile.getName().substring(newFile.getName().lastIndexOf("."));
        //得到新的文件名
        StringBuffer sb = new StringBuffer();
        sb.append(newFile.getParent());
        sb.append(DefineUtils.SEPARATOR);
        sb.append(fileName);
        sb.append(endName);
        newFile = new File(sb.toString());
        
        // System.out.println("hello world!");
        System.out.println(sb.toString());
        // System.out.println(src[0]);
        // System.out.println(src[1]);
        // System.out.println(src[2]);
        // System.out.println(src[3]);
        // System.out.println(src[4]);
        // log.info("文件合并开始");

        ArrayList<String>  filePathList = new ArrayList<String> (); 

        // String[] filePathList;

        for (int i = 0; i < src.length; i++) {
            for (int j = 0; j < src.length; j++) {
                String subName=src[j];
                //获取文件编号
                int index1=subName.lastIndexOf("_data");
                int index2=subName.indexOf(".");
                if(index2>index1+5){
                    String endName2 =subName.substring(index1+5, index2);
                    // System.out.println(endName2);
                    int index =Integer.parseInt(endName2)-1;
                 if(index==filePathList.size()){
                    filePathList.add(subName);
              }
                }
                // String endName1 = subName.substring(subName.lastIndexOf("_data")+5);
                // String endName2 = subName.substring(subName.indexOf("."));
                // System.out.println(subName);
                // System.out.println(endName1);
                // System.out.println(endName2);
                
            }
         }

         String[] scrnew = filePathList.toArray(new String[filePathList.size()]);

        // System.out.println("hello world33!");
        //  System.out.println(scrnew[0]);
        // System.out.println(scrnew[1]);
        // System.out.println(scrnew[2]);
        // System.out.println(scrnew[3]);
        // System.out.println(scrnew[4]);


        for (int i = 0; i < scrnew.length; i++) {
            File file = new File(scrnew[i]);
            try {
                String fileName11 = file.getName().substring(0, file.getName().indexOf("_data"));
                // System.out.println("fileName11==");
                // System.out.println(fileName11);

                //读取小文件的输入流
                InputStream in = new FileInputStream(file);
                OutputStream out = new FileOutputStream(newFile, true);
                int len = -1;
                // byte[] bytes = new byte[DefineUtils.ONE_MB];
                byte[] bytes = new byte[1024 * 1024 *5];
                while ((len = in.read(bytes)) != -1) {
                    // System.out.println(len);
                    // System.out.println(Arrays.toString(bytes));

                    out.write(bytes, 0, len);
                }
                out.close();
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void union(String dirPath, String toFilePath) {
    File dir = new File(dirPath);
    System.out.println("hello world!");
    if (!dir.exists())
        return;
    File videoPartArr[] = dir.listFiles();
    if (videoPartArr.length == 0)
        return;
    File combineFile = new File(toFilePath);
    try (FileOutputStream writer = new FileOutputStream(combineFile)) {
        byte buffer[] = new byte[1024];
        for (File part : videoPartArr) {
            try (FileInputStream reader = new FileInputStream(part)) {
                while (reader.read(buffer) != -1) {
                    writer.write(buffer);
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}


    public static void main(String[] args) throws Exception {
        //分割文件
        FileUtil.splitFileDemo(new File("f://temp//bbb.mp4"), 5);

        //合并文件
//        FileUtil.union("upload-dir","upload-dir/new.mp4");

        // FileUtil.joinFileDemo(new String[]{
        //     // System.out.println("--- 文件分割完成 ---");
        //         "D://temp//video//zygIphone7_20180523113918_data1.mp4",
        //         "D://temp//video//zygIphone7_20180523113918_data2.mp4",
        //         "D://temp//video//zygIphone7_20180523113918_data3.mp4",
        //         "D://temp//video//zygIphone7_20180523113918_data4.mp4",
        //         "D://temp//video//zygIphone7_20180523113918_data5.mp4"
        // });
    }
}
