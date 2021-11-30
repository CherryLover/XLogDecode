package me.monster.xlog_decode;

import me.monster.xlog_decode.tool.XlogFileDecoder;
import me.monster.xlog_decode.tool.ZipUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@RestController
@RequestMapping("/decode")
public class DecodeController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @PostMapping("/decode")
    public String decode(HttpServletRequest request, HttpServletResponse response) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        String prefix = request.getParameter("prefix");
        String isCrypt = request.getParameter("crypt");
        String isZip = request.getParameter("zip");
        if (files.isEmpty()) {
            return "no file";
        }
        if (prefix == null) {
            prefix = "";
        }
        if (isCrypt == null) {
            isCrypt = "1";
        }
        if (isZip == null) {
            isZip = "0";
        }
        final MultipartFile multipartFile = files.get(0);
        String workDir = generateLogDir(prefix);
        checkDirOrCreate(workDir);
        String localFile = workDir + "/" + multipartFile.getOriginalFilename();
        File dest = new File(localFile);
        try {
            multipartFile.transferTo(dest);
        } catch (Exception e) {
            e.printStackTrace();
            return "文件传输失败";
        }
        String decodeFile = localFile + ".log";
        if ("1".equals(isZip)) {
            try {
                String unzipDir = dest.getParent() + "/" + dest.getName().replace(".zip", "");
                ZipUtil.unzip(dest.getAbsolutePath(), unzipDir, false);
                final File unzip = new File(unzipDir);
                final String[] list = unzip.list((dir, name) -> name.endsWith(".xlog"));
                for (String tempFile : list) {
                    String xlogFile = unzipDir + "/" + tempFile;
                    String resultFile = xlogFile + ".log";
                    XlogFileDecoder.ParseFile(xlogFile, resultFile, "1".equals(isCrypt));
                }
                ZipUtil.zip(unzipDir, dest.getParent(), "result.zip");
                decodeFile = dest.getParent() + "/result.zip";
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            decodeFile = localFile + ".log";
            XlogFileDecoder.ParseFile(localFile, decodeFile, "1".equals(isCrypt));
        }
        return returnResult(response, decodeFile);
    }

    private String generateLogDir(String prefix) {
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("user.dir"))
                .append("/decodeLogs/");
        if (prefix.length() != 0) {
            sb.append(prefix)
                    .append("_")
                    .append(System.currentTimeMillis());
        } else {
            sb.append(System.currentTimeMillis());
        }
        sb.append("/");
        String workDir = sb.toString();
        return workDir;
    }

    private String returnResult(HttpServletResponse response, String decodeFile) {
        File resultFile = new File(decodeFile);
        if (resultFile.exists()) {
            response.reset();
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("utf-8");
            response.setContentLength((int) resultFile.length());
            response.setHeader("Content-Disposition", "attachment;filename=" + resultFile.getName());

            try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(resultFile))) {
                byte[] buff = new byte[1024];
                OutputStream os  = response.getOutputStream();
                int i = 0;
                while ((i = bis.read(buff)) != -1) {
                    os.write(buff, 0, i);
                    os.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "下载失败";
            }
        } else {
            return "出错了";
        }
        return "";
    }

    private void checkDirOrCreate(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            if (dir.isFile()) {
                dir.delete();
                dir.mkdirs();
            }
        } else {
            dir.mkdirs();
        }
    }
}
