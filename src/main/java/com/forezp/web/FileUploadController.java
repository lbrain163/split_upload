package com.forezp.web;

import com.forezp.storage.StorageFileNotFoundException;
import com.forezp.storage.StorageService;
import com.forezp.utils.DefineUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by liangbing on 2018/5/9.
 */

@Controller
@Slf4j
public class FileUploadController {

    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService
                .loadAll()
                .map(path ->
                        MvcUriComponentsBuilder
                                .fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
                                .build().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+file.getFilename()+"\"")
                .body(file);
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        // // 新加的用来测试多线程
        // AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AsyncTaskConfig.class);
        //  AsyncTaskService asyncTaskService = context.getBean(AsyncTaskService.class);
 
        //  List<Future<String>> lstFuture = new ArrayList<Future<String>>();// 存放所有的线程，用于获取结果
 
        //  // 创建100个线程
        //  for (int i = 1; i <= 100; i++) {
        //      while (true) {
        //          try {
        //              // 线程池超过最大线程数时，会抛出TaskRejectedException，则等待1s，直到不抛出异常为止
        //              Future<String> future = AsyncTaskService.asyncInvokeReturnFuture(i);
        //              lstFuture.add(future);
 
        //              break;
        //          } catch (TaskRejectedException e) {
        //              System.out.println("线程池满，等待1S。");
        //              Thread.sleep(1000);
        //          }
        //      }
        //  }
        //  
        // 获取值。get是阻塞式，等待当前线程完成才返回值
         // for (Future<String> future : lstFuture) {
         //     System.out.println(future.get());
         // }
         // 新加的用来测试多线程
 
         // context.close();


        // 文件大小 byte
        long filesize = file.getSize();
        log.info("上传开始");
        log.info("上传文件名：" + file.getOriginalFilename());
        log.info("上传文件大小: " + String.valueOf(filesize) + " 字节");

        String message = "";
        if(storageService.exists(file.getOriginalFilename())) {
            message = "The file " + file.getOriginalFilename() + " is exists!";
        }
        else {
            storageService.store(file);
            message = "You successfully uploaded " + file.getOriginalFilename() + "!";
        }

        redirectAttributes.addFlashAttribute("message", message);

        log.info("上传结束");
        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/mergeFile")
    public String cutFile(Model model) throws IOException {

        model.addAttribute("files", storageService
                .loadAll()
                .map(path ->
                        MvcUriComponentsBuilder
                                .fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
                                .build().toString())
                .collect(Collectors.toList()));

        return "mergeFile";
    }

    @PostMapping("/handleMergeFile")
    public String handleMergeFile(@RequestParam("fileName") String fileName,
                                   RedirectAttributes redirectAttributes) {

        String message = "";
        String file = fileName + DefineUtils.FileTypes.MP4;
        if(storageService.exists(file)) {
            message = "The file " + file + " is exists!";
        }
        else {
            try {
                Map<String, String> map = storageService.doMergeFile(file);
                String code = map.get("code");
                String desc = map.get("desc");

                if("0".equals(code)) {
                    message = "You successfully merged " + file + "!";
                }
                else {
                    message = desc;
                }
            } catch (IOException e) {
                message = "Merged File Error: " + e.getMessage();
                e.getStackTrace();
            }
        }

        redirectAttributes.addFlashAttribute("message", message);
        redirectAttributes.addFlashAttribute("files", storageService
                .loadAll()
                .map(path ->
                        MvcUriComponentsBuilder
                                .fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
                                .build().toString())
                .collect(Collectors.toList()));
        return "redirect:/mergeFile";
    }
}


// // 测试有返回结果
//      private static void testReturn() throws InterruptedException, ExecutionException {
//          AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AsyncTaskConfig.class);
//          AsyncTaskService asyncTaskService = context.getBean(AsyncTaskService.class);
 
//          List<Future<String>> lstFuture = new ArrayList<Future<String>>();// 存放所有的线程，用于获取结果
 
//          // 创建100个线程
//          for (int i = 1; i <= 100; i++) {
//              while (true) {
//                  try {
//                      // 线程池超过最大线程数时，会抛出TaskRejectedException，则等待1s，直到不抛出异常为止
//                      Future<String> future = AsyncTaskService.asyncInvokeReturnFuture(i);
//                      lstFuture.add(future);
 
//                      break;
//                  } catch (TaskRejectedException e) {
//                      System.out.println("线程池满，等待1S。");
//                      Thread.sleep(1000);
//                  }
//              }
//          }
 
//          // 获取值。get是阻塞式，等待当前线程完成才返回值
//          for (Future<String> future : lstFuture) {
//              System.out.println(future.get());
//          }
 
//          context.close();
//      }