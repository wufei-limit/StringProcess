package com.jiaokang.progress;

import com.jiaokang.progress.excel.ExcelWriter;
import com.jiaokang.progress.xmlparse.SAXParse;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class Enter {
    private static final String WORK_DIR = "E:\\apps\\apuslauncher";
    public static final String EXCEL_PATH = "D:\\translate\\launcher.xlsx";
    public static final String NEED_TRANSLATE_EXCEL_PATH = "D:\\translate\\needTranslate.xlsx";


    private static final String PATH_PATTERN = Pattern.quote(File.separator);
    public static AtomicBoolean isParseEnd = new AtomicBoolean(false);

    public static void main(String[] args) {
        SAXParse.init();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                File workDir = new File(WORK_DIR);
                if (!workDir.isDirectory()) return;
                visitModule(workDir);
            }
        });
        thread.start();
        ExcelWriter writer = new ExcelWriter();
        writer.startWrite();
    }

    private static void visitModule(File dir) {
        long start = System.currentTimeMillis();
        LinkedList<File> dirList = new LinkedList<>();
        dirList.offer(dir);
        while (!dirList.isEmpty()) {
            File item = dirList.poll();
            File[] files = item.listFiles();
            if (files == null || files.length == 0) continue;
            for (File f : files) {
                if (f.isDirectory()) {
                    String path = f.getAbsolutePath();
                    if (path.contains("build")) {
                        LogManager.getLogger().error("skip dir " + path);
                    } else {
                        dirList.offer(f);
                    }
                    continue;
                }
                processFile(f);
            }
        }
        LogManager.getLogger().error("all data count is " + TaskDataQueue.getSize());
        isParseEnd.set(true);
        long cast = System.currentTimeMillis() - start;
        LogManager.getLogger().error("visit File cast " + cast + " ms");
    }

    private static void processFile(File file) {
        String path = file.getAbsolutePath();
        String extension = FilenameUtils.getExtension(path);
        if (!"xml".equals(extension)) {
            return;
        }
        String[] split = path.split(PATH_PATTERN);
        if (split.length == 3) {
            System.out.println("---------------未预料的path:" + path);
            return;
        }
        String fileName = split[split.length - 1];
        if (fileName.contains("translate")) {
            LogManager.getLogger().error("skip translate file " + fileName);
            return;
        }
        String lastPath = split[split.length - 2];
        String local;
        if (lastPath.contains("-")) {
            int index = lastPath.indexOf("-");
            local = lastPath.substring(index + 1);
        } else {
            local = LocalConstants.EN;
        }
        String moduleName;
        if ("external".equals(split[3])) {
            moduleName = split[4];
        } else {
            moduleName = "app";
        }
        SAXParse.parse(moduleName, local, file);
    }


}